package com.ducktel.service;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.dto.AccommodationDTO;
import com.ducktel.dto.HomeResponseDTO;

import java.util.List;

public interface HomeService {
    HomeResponseDTO getHomeData();
    List<AccommodationDTO> convertToDTO(List<Accommodation> accommodations);
    HomeResponseDTO getSubHomeData(String category);
}
