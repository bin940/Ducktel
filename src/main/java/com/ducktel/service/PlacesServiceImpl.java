package com.ducktel.service;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.domain.entity.AccommodationImage;
import com.ducktel.domain.entity.Room;
import com.ducktel.domain.entity.RoomImage;
import com.ducktel.domain.repository.*;
import com.ducktel.dto.AccommodationDTO;
import com.ducktel.dto.PlacesDTO;
import com.ducktel.dto.RoomDTO;
import com.ducktel.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlacesServiceImpl implements PlacesService {

    private final AccommodationRepository accommodationRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final RoomImageRepository roomImageRepository;
    private final AccommodationImageRepository accommodationImageRepository;
    //숙소정보, 객실정보, 잔여 객실 정보, 객실 할인 정보
    @Override
    public PlacesDTO getPlaces(Long accommodationId, LocalDate checkInDate, LocalDate checkOutDate) {
        // 숙소 정보
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new CustomException("INVALID_ACCOMMODATION_ID", "Invalid accommodation ID: " + accommodationId));
        List<String> accommodationImages = accommodationImageRepository.findByAccommodation_AccommodationId(accommodationId)
                .stream()
                .map(AccommodationImage::getImage)
                .toList();

        // 객실 정보
        List<Room> rooms = roomRepository.findAllByAccommodationId(accommodation);
        log.info("Found {} rooms", rooms.size());
        List<Long> roomIds = rooms.stream().map(Room::getRoomId).toList();
        log.info("Room IDs: {}", roomIds);

        // 예약 정보
        List<Object[]> bookedRoomCounts = bookingRepository.findBookedRooms(roomIds, checkInDate, checkOutDate);
        log.info("Booked room counts: {}", bookedRoomCounts);
        Map<Long, Integer> bookedRoomsMap = new HashMap<>();
        for (Object[] result : bookedRoomCounts) {
            Long roomId = (Long) result[0];
            Integer count = ((Long) result[1]).intValue();
            bookedRoomsMap.put(roomId, count);
            log.info("Room ID: {}, Booked count: {}", roomId, count);
        }

        // 객실 이미지
        List<RoomImage> allRoomImages = roomImageRepository.findByRoom_RoomIdIn(roomIds);
        Map<Long, List<String>> roomImagesMap = allRoomImages.stream()
                .collect(Collectors.groupingBy(
                        roomImage -> roomImage.getRoom().getRoomId(),
                        Collectors.mapping(RoomImage::getImage, Collectors.toList())
                ));

        // 객실 DTO
        List<RoomDTO> roomDTOs = rooms.stream()
                .map(room -> {
                    int totalRooms = room.getTotalRooms();
                    int bookedCount = bookedRoomsMap.getOrDefault(room.getRoomId(), 0);
                    int available = totalRooms - bookedCount;
                    log.info("Room ID: {}, Total: {}, Booked: {}, Available: {}",
                            room.getRoomId(), totalRooms, bookedCount, available);
                    return RoomDTO.builder()
                            .roomId(room.getRoomId())
                            .name(room.getName())
                            .maxCapacity(room.getMaxCapacity())
                            .minCapacity(room.getMinCapacity())
                            .price((int) (room.getPrice() * (1 - accommodation.getDiscount() / 100.0)))
                            .images(roomImagesMap.getOrDefault(room.getRoomId(), new ArrayList<>()))
                            .availableRooms(available)
                            .build();
                })
                .collect(Collectors.toList());

        // 숙소 DTO
        AccommodationDTO accommodationDTO = AccommodationDTO.builder()
                .accommodationId(accommodation.getAccommodationId())
                .name(accommodation.getAccommodationName())
                .location(accommodation.getLocation())
                .explanation(accommodation.getExplanation())
                .serviceInfo(accommodation.getServiceInfo())
                .discount(accommodation.getDiscount())
                .likeCount(accommodation.getLikeCount())
                .image(accommodationImages)
                .build();

        return PlacesDTO.builder()
                .accommodation(accommodationDTO)
                .rooms(roomDTOs)
                .build();
    }
}
