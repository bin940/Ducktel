package com.ducktel.controller.home;

import com.ducktel.dto.HomeResponseDTO;
import com.ducktel.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;
    @GetMapping("/api/home")
    public ResponseEntity<HomeResponseDTO> getHomeData() {
            HomeResponseDTO homeData= homeService.getHomeData();

        return ResponseEntity.ok(homeData);
    }
}
