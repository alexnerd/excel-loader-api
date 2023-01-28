package com.alexnerd.excelloader;

import org.apache.commons.lang3.ArrayUtils;

public abstract class StuffMappingServiceApplicationTests {
    protected final String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    protected final String fileName = "test_data.xlsx";
    protected byte[] createFileContent(byte[] data, String boundary, String contentType) {
        String start = "--" + boundary + "\r\n Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n"
                + "Content-type: " + contentType + "\r\n\r\n";

        String end = "\r\n--" + boundary + "--";
        return ArrayUtils.addAll(start.getBytes(), ArrayUtils.addAll(data, end.getBytes()));
    }
}

