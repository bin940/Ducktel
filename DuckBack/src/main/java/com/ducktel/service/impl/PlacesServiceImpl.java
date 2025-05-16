package com.ducktel.service.impl;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.domain.entity.AccommodationImage;
import com.ducktel.domain.entity.Room;
import com.ducktel.domain.entity.RoomImage;
import com.ducktel.domain.repository.*;
import com.ducktel.dto.AccommodationDTO;
import com.ducktel.dto.PlacesDTO;
import com.ducktel.dto.RoomDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.service.PlacesService;
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
        log.debug("Place 조회 요청: accommodationId={}, checkInDate={}, checkOutDate={}", accommodationId, checkInDate, checkOutDate);

        // 숙소 정보
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> {
                    log.warn("유효하지 않은 숙소 ID: {}", accommodationId);
                    return new CustomException(404, "NOT_FOUND", "유효하지 않은 숙소 ID입니다: " + accommodationId);
                });
        log.debug("숙소 조회 성공: accommodationId={}", accommodationId);

        List<String> accommodationImages = accommodationImageRepository.findByAccommodation_AccommodationId(accommodationId)
                .stream()
                .map(AccommodationImage::getImage)
                .toList();
        log.debug("숙소 이미지 조회 성공: accommodationId={}, imageCount={}", accommodationId, accommodationImages.size());

        // 객실 정보
        List<Room> rooms = roomRepository.findAllByAccommodationId(accommodation);
        log.debug("객실 조회 성공: accommodationId={}, roomCount={}", accommodationId, rooms.size());

        List<Long> roomIds = rooms.stream().map(Room::getRoomId).toList();
        log.debug("객실 ID 목록: {}", roomIds);

        // 예약 정보
        List<Object[]> bookedRoomCounts = bookingRepository.findBookedRooms(roomIds, checkInDate, checkOutDate);
        log.debug("예약된 객실 수 조회 성공: count={}", bookedRoomCounts.size());

        Map<Long, Integer> bookedRoomsMap = new HashMap<>();
        for (Object[] result : bookedRoomCounts) {
            Long roomId = (Long) result[0];
            Integer count = ((Long) result[1]).intValue();
            bookedRoomsMap.put(roomId, count);
            log.debug("객실 예약 정보: roomId={}, bookedCount={}", roomId, count);
        }

        // 객실 이미지
        List<RoomImage> allRoomImages = roomImageRepository.findByRoom_RoomIdIn(roomIds);
        log.debug("객실 이미지 조회 성공: roomImageCount={}", allRoomImages.size());

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
                    log.debug("객실 정보: roomId={}, totalRooms={}, bookedCount={}, available={}",
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
        log.debug("객실 DTO 변환 성공: roomDTOCount={}", roomDTOs.size());

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
        log.debug("숙소 DTO 생성 성공: accommodationId={}", accommodation.getAccommodationId());

        PlacesDTO placesDTO = PlacesDTO.builder()
                .accommodation(accommodationDTO)
                .rooms(roomDTOs)
                .build();
        log.info("Place 조회 성공: accommodationId={}", accommodationId);

        return placesDTO;
    }
}
