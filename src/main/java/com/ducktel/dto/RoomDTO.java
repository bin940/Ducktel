package com.ducktel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {

    private Long roomId;
    private String name;
    private int maxCapacity;
    private int minCapacity;
    private int price;
    private String explanation;
    private String serviceInfo;
    private String tag;
    private List<String> images;
    private int availableRooms;
}
