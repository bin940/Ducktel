package com.ducktel.service.impl;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.domain.entity.AccommodationImage;
import com.ducktel.domain.repository.AccommodationImageRepository;
import com.ducktel.domain.repository.AccommodationRepository;
import com.ducktel.dto.AccommodationDTO;
import com.ducktel.dto.HomeResponseDTO;
import com.ducktel.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {
    private final AccommodationRepository accommodationRepository;
    private  final AccommodationImageRepository accommodationImageRepository;

    public HomeResponseDTO getHomeData(){
        Integer discount = 0;
        String season = "겨울";

        List<Accommodation> discountData = accommodationRepository.findByDiscountGreaterThan(discount);
        List<Accommodation> favoriteData = accommodationRepository.findAllByOrderByLikeCountDesc();
        List<Accommodation> seasonData = accommodationRepository.findBySeason(season);
        return HomeResponseDTO.builder()
                .discountAccommodations(convertToDTO(discountData))
                .favoriteAccommodations(convertToDTO(favoriteData))
                .seasonalAccommodations(convertToDTO(seasonData))
                .build();
    }
    @Override
    public HomeResponseDTO getSubHomeData(String category) {
        List<Accommodation> categoryData = accommodationRepository.findByCategory(category);
        return HomeResponseDTO.builder()
                .categoryAccommodations(convertToDTO(categoryData))
                .build();
    }

    @Override
    public HomeResponseDTO getLocationHomeData(String category, String location) {
        List<Accommodation> locationData = accommodationRepository.findByCategoryAndLocation(category, location);
        return HomeResponseDTO.builder()
                .locationAccommodations(convertToDTO(locationData))
                .build();
    }

    //Accommodation_image 조회 및 Entity -> DTO 변환
    public List<AccommodationDTO> convertToDTO(List<Accommodation> accommodations) {
        return accommodations.stream()
                .map(accommodation -> {
                    List<String> images = accommodationImageRepository.findByAccommodation_AccommodationId(accommodation.getAccommodationId())
                            .stream()
                            .map(AccommodationImage::getImage)
                            .toList();

                    return AccommodationDTO.builder()
                            .accommodationId(accommodation.getAccommodationId())
                            .name(accommodation.getAccommodationName())
                            .location(accommodation.getLocation())
                            .tag(accommodation.getTag())
                            .explanation(accommodation.getExplanation())
                            .serviceInfo(accommodation.getServiceInfo())
                            .image(images)
                            .discount(accommodation.getDiscount())
                            .season(accommodation.getSeason())
                            .category(accommodation.getCategory())
                            .likeCount(accommodation.getLikeCount())
                            .build();
                })
                .collect(Collectors.toList());
    }



}
