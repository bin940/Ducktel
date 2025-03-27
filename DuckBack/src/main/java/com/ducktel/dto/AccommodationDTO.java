package com.ducktel.dto;


import com.ducktel.domain.entity.Accommodation;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
