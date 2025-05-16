package com.ducktel.service;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.domain.entity.AccommodationImage;
import com.ducktel.domain.entity.Room;
import com.ducktel.domain.entity.RoomImage;
import com.ducktel.domain.repository.*;
import com.ducktel.dto.PlacesDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.service.impl.PlacesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlacesServiceImplTest {

    @Mock private AccommodationRepository accommodationRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private RoomImageRepository roomImageRepository;
    @Mock private AccommodationImageRepository accommodationImageRepository;

    @InjectMocks
    private PlacesServiceImpl placesService;

    private Accommodation accommodation;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;

    @BeforeEach
    void setUp() {
        accommodation = new Accommodation();
        accommodation.setAccommodationId(1L);
        accommodation.setAccommodationName("Test Hotel");
        accommodation.setLocation("Seoul");
        accommodation.setExplanation("Nice place");
        accommodation.setServiceInfo("Free WiFi");
        accommodation.setDiscount(10);
        accommodation.setLikeCount(100);

        room = new Room();
        room.setRoomId(2L);
        room.setName("Deluxe Room");
        room.setMaxCapacity(4);
        room.setMinCapacity(2);
        room.setPrice(200000);
        room.setTotalRooms(10);
        room.setAccommodationId(accommodation);

        checkIn = LocalDate.now();
        checkOut = checkIn.plusDays(1);
    }

    @Test
    void getPlaces_PlacesDTO() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.of(accommodation));
        when(accommodationImageRepository.findByAccommodation_AccommodationId(1L))
                .thenReturn(Collections.singletonList(createAccommodationImage("image1.jpg")));
        when(roomRepository.findAllByAccommodationId(accommodation)).thenReturn(List.of(room));
        when(bookingRepository.findBookedRooms(List.of(2L), checkIn, checkOut)).thenReturn(Collections.emptyList());
        when(roomImageRepository.findByRoom_RoomIdIn(List.of(2L)))
                .thenReturn(List.of(createRoomImage(2L, "room1.jpg")));

        PlacesDTO places = placesService.getPlaces(1L, checkIn, checkOut);

        assertThat(places.getAccommodation().getName()).isEqualTo("Test Hotel");
        assertThat(places.getRooms()).hasSize(1);
        assertThat(places.getRooms().get(0).getAvailableRooms()).isEqualTo(10);
    }

    @Test
    void getPlaces_AccommodationNotFound() {
        when(accommodationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomException.class, () -> placesService.getPlaces(1L, checkIn, checkOut));
    }

    private RoomImage createRoomImage(Long roomId, String image) {
        Room room = new Room();
        room.setRoomId(roomId);

        RoomImage roomImage = new RoomImage();
        roomImage.setRoom(room);
        roomImage.setImage(image);
        return roomImage;
    }

    private AccommodationImage createAccommodationImage(String image) {
        AccommodationImage img = new AccommodationImage();
        img.setImage(image);
        return img;
    }
}
