package com.alexnerd.excelloader.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;

@Data
@AllArgsConstructor
public class ExcelRowMapper {
    private String dbName;
    private Collection<String> rowValues;
}
