package com.ducktel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null은 json으로 반환되지않음
public class HomeResponseDTO {

    private List<AccommodationDTO> discountAccommodations;
    private List<AccommodationDTO> favoriteAccommodations;
    private List<AccommodationDTO> seasonalAccommodations;
    private List<AccommodationDTO> categoryAccommodations;
}
