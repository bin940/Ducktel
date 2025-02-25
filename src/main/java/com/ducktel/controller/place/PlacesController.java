package com.ducktel.controller.place;

import com.ducktel.dto.PlacesDTO;
import com.ducktel.dto.ResponseDTO;
import com.ducktel.service.PlacesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class PlacesController {

    private final PlacesService placesService;

    @GetMapping("/api/places/{accommodationId}/{checkInData}/{checkOutData}")
    public ResponseEntity<ResponseDTO<?>> getPlaces(@PathVariable("accommodationId") Long accommodationId,
                                                    @PathVariable("checkInData") LocalDate checkInDate,
                                                    @PathVariable("checkOutData") LocalDate checkOutDate) {

        PlacesDTO placesData= placesService.getPlaces(accommodationId, checkInDate, checkOutDate);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "Place 조회 성공", placesData));
    }
}
