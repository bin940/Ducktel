package com.ducktel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeResponseDTO {

    private List<AccommodationDTO> discountAccommodations;
    private List<AccommodationDTO> favoriteAccommodations;
    private List<AccommodationDTO> seasonalAccommodations;
}
