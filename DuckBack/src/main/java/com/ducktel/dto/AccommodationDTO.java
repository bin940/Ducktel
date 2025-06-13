package com.ducktel.dto;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private String category;
    private int likeCount;

}
