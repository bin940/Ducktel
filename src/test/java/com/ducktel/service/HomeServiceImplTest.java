package com.ducktel.service;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.domain.entity.AccommodationImage;
import com.ducktel.domain.repository.AccommodationImageRepository;
import com.ducktel.domain.repository.AccommodationRepository;
import com.ducktel.dto.HomeResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class HomeServiceImplTest {

    @Mock private AccommodationRepository accommodationRepository;
    @Mock private AccommodationImageRepository accommodationImageRepository;

    @InjectMocks
    private HomeServiceImpl homeService;

    private Accommodation accommodation;

    @BeforeEach
    void setup() {
        accommodation = new Accommodation();
        accommodation.setAccommodationId(1L);
        accommodation.setAccommodationName("테스트 숙소");
        accommodation.setLocation("서울");
        accommodation.setTag("편안함");
        accommodation.setExplanation("아늑한 공간");
        accommodation.setServiceInfo("조식 제공");
        accommodation.setDiscount(15);
        accommodation.setSeason("겨울");
        accommodation.setCategory("호텔");
        accommodation.setLikeCount(88);
    }

    @Test
    void getHomeData_HomeResponse() {
        when(accommodationRepository.findByDiscountGreaterThan(0)).thenReturn(List.of(accommodation));
        when(accommodationRepository.findAllByOrderByLikeCountDesc()).thenReturn(List.of(accommodation));
        when(accommodationRepository.findBySeason("겨울")).thenReturn(List.of(accommodation));
        when(accommodationImageRepository.findByAccommodation_AccommodationId(1L))
                .thenReturn(List.of(createImage("img1.jpg")));

        HomeResponseDTO result = homeService.getHomeData();

        assertThat(result.getDiscountAccommodations()).hasSize(1);
        assertThat(result.getFavoriteAccommodations()).hasSize(1);
        assertThat(result.getSeasonalAccommodations()).hasSize(1);
        assertThat(result.getDiscountAccommodations().get(0).getImage()).contains("img1.jpg");
    }

    @Test
    void getSubHomeData_CategoryFilteredList() {
        when(accommodationRepository.findByCategory("호텔")).thenReturn(List.of(accommodation));
        when(accommodationImageRepository.findByAccommodation_AccommodationId(1L))
                .thenReturn(List.of(createImage("img2.jpg")));

        HomeResponseDTO result = homeService.getSubHomeData("호텔");

        assertThat(result.getCategoryAccommodations()).hasSize(1);
        assertThat(result.getCategoryAccommodations().get(0).getImage()).contains("img2.jpg");
    }

    @Test
    void getLocationHomeData_FilteredListByCategoryAndLocation() {
        when(accommodationRepository.findByCategoryAndLocation("호텔", "서울"))
                .thenReturn(List.of(accommodation));
        when(accommodationImageRepository.findByAccommodation_AccommodationId(1L))
                .thenReturn(List.of(createImage("img3.jpg")));

        HomeResponseDTO result = homeService.getLocationHomeData("호텔", "서울");

        assertThat(result.getLocationAccommodations()).hasSize(1);
        assertThat(result.getLocationAccommodations().get(0).getImage()).contains("img3.jpg");
    }

    private AccommodationImage createImage(String img) {
        AccommodationImage image = new AccommodationImage();
        image.setImage(img);
        image.setAccommodation(accommodation);
        return image;
    }
}

