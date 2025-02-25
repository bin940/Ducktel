package com.ducktel.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlacesDTO {
    AccommodationDTO accommodation;
    List<RoomDTO> rooms;

}
