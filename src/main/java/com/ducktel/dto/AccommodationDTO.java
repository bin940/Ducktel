package com.ducktel.dto;


import com.ducktel.domain.entity.Accommodation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccommodationDTO {
    private Long accommodationId;
    private String name;
    private String location;
    private String tag;
    private String explanation;
    private String serviceInfo;
    private List<String> image;
    private Integer discount;
    private String season;

}
