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
        //숙소 정보
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new CustomException("INVALID_ACCOMMODATION_ID", "Invalid accommodation ID: " + accommodationId));
        //숙소 이미지
        List<String> accommodationImages = accommodationImageRepository.findByAccommodation_AccommodationId(accommodationId)
                .stream()
                .map(AccommodationImage::getImage)
                .toList();
        //객실정보
        List<Room> rooms = roomRepository.findAllByAccommodationId(accommodation);
        log.info("Found {} rooms", rooms.size());
        //객실 아이디 추출
        List<Long> roomIds = rooms.stream().map(Room::getRoomId).toList();
        log.info("Room IDs: {}", roomIds);
        //해당 날짜에 객실 예약 정보
        List<Object[]> bookedRoomCounts = bookingRepository.findBookedRooms(roomIds, checkInDate, checkOutDate);
        //해당 날짜 객색 예약 수
        Map<Long, Integer> bookedRoomsMap = new HashMap<>();
        for (Object[] result : bookedRoomCounts) {
            bookedRoomsMap.put((Long) result[0], ((Long) result[1]).intValue());
        }
        // 모든 RoomImage 조회
        List<RoomImage> allRoomImages = roomImageRepository.findByRoom_RoomIdIn(roomIds);

        // roomId 기준으로 이미지 그룹
        Map<Long, List<String>> roomImagesMap = allRoomImages.stream()
                .collect(Collectors.groupingBy(
                        roomImage -> roomImage.getRoom().getRoomId(),
                        Collectors.mapping(RoomImage::getImage, Collectors.toList())
                ));

        //객실 정보 entity -> dto
        List<RoomDTO> roomDTOs = rooms.stream()
                .map(room -> RoomDTO.builder()
                        .roomId(room.getRoomId())
                        .name(room.getName())
                        .maxCapacity(room.getMaxCapacity())
                        .minCapacity(room.getMinCapacity())
                        .price((int) (room.getPrice() * (1 - accommodation.getDiscount() / 100.0)))
                        .images(roomImagesMap.getOrDefault(room.getRoomId(), new ArrayList<>()))
                        .availableRooms(room.getTotalRooms() - bookedRoomsMap.getOrDefault(room.getRoomId(), 0))
                        .build()
                )
                .collect(Collectors.toList());

        //숙소 정보 entity -> dto
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
