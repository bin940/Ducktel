package com.ducktel.service.impl;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.domain.entity.AccommodationImage;
import com.ducktel.domain.repository.AccommodationImageRepository;
import com.ducktel.domain.repository.AccommodationRepository;
import com.ducktel.dto.AccommodationDTO;
import com.ducktel.dto.HomeResponseDTO;
import com.ducktel.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeServiceImpl implements HomeService {
    private final AccommodationRepository accommodationRepository;
    private  final AccommodationImageRepository accommodationImageRepository;

    public HomeResponseDTO getHomeData() {
        log.debug("홈 데이터 조회 요청");

        Integer discount = 0;
        String season = "겨울";

        List<Accommodation> discountData = accommodationRepository.findByDiscountGreaterThan(discount);
        log.debug("할인 숙소 데이터 조회 성공: count={}", discountData.size());

        List<Accommodation> favoriteData = accommodationRepository.findAllByOrderByLikeCountDesc();
        log.debug("인기 숙소 데이터 조회 성공: count={}", favoriteData.size());

        List<Accommodation> seasonData = accommodationRepository.findBySeason(season);
        log.debug("시즌 숙소 데이터 조회 성공: count={}", seasonData.size());

        HomeResponseDTO response = HomeResponseDTO.builder()
                .discountAccommodations(convertToDTO(discountData))
                .favoriteAccommodations(convertToDTO(favoriteData))
                .seasonalAccommodations(convertToDTO(seasonData))
                .build();

        log.info("홈 데이터 조회 성공: {}", response);
        return response;
    }

    @Override
    public HomeResponseDTO getSubHomeData(String category) {
        log.debug("카테고리별 홈 데이터 조회 요청: category={}", category);

        List<Accommodation> categoryData = accommodationRepository.findByCategory(category);
        log.debug("카테고리별 숙소 데이터 조회 성공: count={}", categoryData.size());

        HomeResponseDTO response = HomeResponseDTO.builder()
                .categoryAccommodations(convertToDTO(categoryData))
                .build();

        log.info("카테고리별 홈 데이터 조회 성공: {}", response);
        return response;
    }

    @Override
    public HomeResponseDTO getLocationHomeData(String category, String location) {
        log.debug("카테고리 및 위치별 홈 데이터 조회 요청: category={}, location={}", category, location);

        List<Accommodation> locationData = accommodationRepository.findByCategoryAndLocation(category, location);
        log.debug("카테고리 및 위치별 숙소 데이터 조회 성공: count={}", locationData.size());

        HomeResponseDTO response = HomeResponseDTO.builder()
                .locationAccommodations(convertToDTO(locationData))
                .build();

        log.info("카테고리 및 위치별 홈 데이터 조회 성공: {}", response);
        return response;
    }

    public List<AccommodationDTO> convertToDTO(List<Accommodation> accommodations) {
        log.debug("숙소 데이터를 DTO로 변환 요청: count={}", accommodations.size());

        List<AccommodationDTO> accommodationDTOs = accommodations.stream()
                .map(accommodation -> {
                    List<String> images = accommodationImageRepository.findByAccommodation_AccommodationId(accommodation.getAccommodationId())
                            .stream()
                            .map(AccommodationImage::getImage)
                            .toList();

                    log.debug("숙소 이미지 조회 성공: accommodationId={}, imageCount={}", accommodation.getAccommodationId(), images.size());

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

        log.info("숙소 데이터 DTO 변환 성공: count={}", accommodationDTOs.size());
        return accommodationDTOs;
    }
}
