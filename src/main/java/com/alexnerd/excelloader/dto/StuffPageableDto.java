package com.alexnerd.excelloader.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StuffPageableDto {
    private List<StuffDto> stuff;
    private long totalItems;
}
