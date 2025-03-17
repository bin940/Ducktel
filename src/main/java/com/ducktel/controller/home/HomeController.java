package com.ducktel.controller.home;

import com.ducktel.dto.HomeResponseDTO;
import com.ducktel.dto.ResponseDTO;
import com.ducktel.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private final HomeService homeService;

    @GetMapping("/api/home")
    public ResponseEntity<ResponseDTO<?>> getHomeData() {

        HomeResponseDTO homeData= homeService.getHomeData();
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "Home Data 조회 성공", homeData));
    }

    @GetMapping("/api/sub-home/{category}")
    public ResponseEntity<ResponseDTO<?>> getSubHomeData(@PathVariable("category") String category) {

        HomeResponseDTO subHomeData = homeService.getSubHomeData(category);
        log.info(subHomeData.toString());
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "Sub-Home Data 조회 성공", subHomeData));
    }
    @GetMapping("/api/sub-home/{category}/{location}")
    public ResponseEntity<ResponseDTO<?>> getLocationHomeData(@PathVariable("category") String category,
                                                                 @PathVariable("location") String location) {
        log.info("getLocationHomeData category: {}, location: {}", category, location);
        HomeResponseDTO LocationHomeData = homeService.getLocationHomeData(category, location);
        return ResponseEntity.ok(new ResponseDTO<>(200, null, "Location Home Data 조회 성공", LocationHomeData));
    }
}
