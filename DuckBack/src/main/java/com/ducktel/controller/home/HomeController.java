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
        log.debug("홈 데이터 조회 요청");

        HomeResponseDTO homeData= homeService.getHomeData();
        log.info("홈 데이터 조회 성공: {}", homeData);

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "Home Data 조회 성공", homeData));
    }

    @GetMapping("/api/home/{category}")
    public ResponseEntity<ResponseDTO<?>> getSubHomeData(@PathVariable("category") String category) {
        log.debug("카테고리별 홈 데이터 조회 요청: category={}", category);

        HomeResponseDTO categoryHomeData = homeService.getSubHomeData(category);
        log.info("카테고리별 홈 데이터 조회 성공: {}", categoryHomeData);

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "Sub-Home Data 조회 성공", categoryHomeData));
    }
    @GetMapping("/api/home/{category}/{location}")
    public ResponseEntity<ResponseDTO<?>> getLocationHomeData(@PathVariable("category") String category,
                                                                 @PathVariable("location") String location) {
        log.debug("카테고리 및 위치별 홈 데이터 조회 요청: category={}, location={}", category, location);

        HomeResponseDTO locationHomeData = homeService.getLocationHomeData(category, location);
        log.info("카테고리 및 위치별 홈 데이터 조회 성공: {}", locationHomeData);

        return ResponseEntity.ok(new ResponseDTO<>(200, null, "Location Home Data 조회 성공", locationHomeData));
    }
}
