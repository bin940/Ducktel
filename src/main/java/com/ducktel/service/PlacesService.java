package com.ducktel.service;

import com.ducktel.dto.PlacesDTO;

import java.time.LocalDate;

public interface PlacesService {

    PlacesDTO getPlaces(Long accommodationId, LocalDate checkInDate, LocalDate checkOutDate);
}
