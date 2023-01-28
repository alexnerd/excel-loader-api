/*
 * Copyright 2023 Aleksey Popov <alexnerd.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.alexnerd.excelloader.controllers;

import com.alexnerd.excelloader.PostgreSQLContainerExtension;
import com.alexnerd.excelloader.StuffMappingServiceApplicationTests;
import com.alexnerd.excelloader.dto.LoadResponseDto;
import com.alexnerd.excelloader.dto.StuffDto;
import com.alexnerd.excelloader.dto.StuffPageableDto;
import com.alexnerd.excelloader.services.StuffMappingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(PostgreSQLContainerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StuffControllerTest extends StuffMappingServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StuffMappingService stuffMappingService;

    private final String apiGetStuff = "/api/v1/stuff";
    private final String apiImportStuff = "/api/v1/upload";

    @Test
    @DisplayName("Should return 200 and list of stuff for get stuff")
    public void shouldReturnListOfStuffForGetStuffTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        doReturn(getStuffPageableDto()).when(stuffMappingService).getStuff(1, 5, null, null);
        mockMvc.perform(get(apiGetStuff)
                        .param("page", "1")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(getStuffPageableDto())))
                .andExpect(header().string("Vary", "Origin"));
    }

    @Test
    @DisplayName("Should return 200 for import stuff")
    public void shouldReturnOkForImportStuffTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = ResourceUtils.getFile("classpath:test-data/test_data.xlsx");

        byte[] data = Files.readAllBytes(file.toPath());
        MockMultipartFile multipartFile = new MockMultipartFile(fileName, fileName,
                contentType, data);
        String boundary = "q1w2e3r4t5y6u7i8o9";
        LoadResponseDto responseDto = new LoadResponseDto(5);
        doReturn(responseDto).when(stuffMappingService).upload(any());
        mockMvc.perform(multipart(apiImportStuff)
                        .file(multipartFile)
                        .contentType("multipart/form-data; boundary=" + boundary)
                        .content(createFileContent(data, boundary, MediaType.MULTIPART_FORM_DATA_VALUE)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(responseDto)))
                .andExpect(header().string("Vary", "Origin"));

    }

    @Test
    @DisplayName("Should return 200 for options")
    public void shouldReturnOkForOptionsTest() throws Exception {
        mockMvc.perform(options(apiImportStuff)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private StuffPageableDto getStuffPageableDto() {
        StuffPageableDto stuffPageableDto = new StuffPageableDto();
        List<StuffDto> stuffDto = Arrays.asList(
                new StuffDto("apple macbook pro", "Ноутбуки"),
                new StuffDto("xiaomi redmi note 12", "Телефоны/Гаджеты/Аксессуары"),
                new StuffDto("айфон 10", "Телефоны/Гаджеты/Аксессуары"),
                new StuffDto("nvidia rtx 3080", "Компьютеры и комплектующие"),
                new StuffDto("asus zen phone", "Телефоны/Гаджеты/Аксессуары")
        );
        stuffPageableDto.setStuff(stuffDto);
        stuffPageableDto.setTotalItems(5);
        return stuffPageableDto;
    }
}

