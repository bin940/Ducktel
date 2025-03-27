package com.ducktel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlacesDTO {
    AccommodationDTO accommodation;
    List<RoomDTO> rooms;

}
