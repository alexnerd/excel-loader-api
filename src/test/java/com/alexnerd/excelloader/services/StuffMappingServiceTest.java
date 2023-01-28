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

package com.alexnerd.excelloader.services;

import com.alexnerd.excelloader.PostgreSQLContainerExtension;
import com.alexnerd.excelloader.StuffMappingServiceApplicationTests;
import com.alexnerd.excelloader.dto.StuffPageableDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PostgreSQLContainerExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
public class StuffMappingServiceTest extends StuffMappingServiceApplicationTests {

    @Autowired
    private StuffMappingService stuffMappingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @BeforeEach
    public void setUp() {
        jdbcTemplate.update("INSERT INTO stuff (name, description) VALUES ('apple macbook pro', 'Ноутбуки')");
        jdbcTemplate.update("INSERT INTO stuff (name, description) VALUES ('xiaomi redmi note 12', 'Телефоны/Гаджеты/Аксессуары')");
        jdbcTemplate.update("INSERT INTO stuff (name, description) VALUES ('айфон 10', 'Телефоны/Гаджеты/Аксессуары')");
        jdbcTemplate.update("INSERT INTO stuff (name, description) VALUES ('nvidia rtx 3080', 'Компьютеры и комплектующие')");
        jdbcTemplate.update("INSERT INTO stuff (name, description) VALUES ('asus zen phone', 'Телефоны/Гаджеты/Аксессуары')");
    }

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM stuff");
    }

    @Test
    @DisplayName("Should get stuff with limit")
    public void shouldGetStuffTest() {
        StuffPageableDto stuff = stuffMappingService.getStuff(1, 6, null, null);
        assertEquals(5, stuff.getTotalItems());
        assertEquals(5, stuff.getStuff().size());
        stuff = stuffMappingService.getStuff(1, 3, null, null);
        assertEquals(5, stuff.getTotalItems());
        assertEquals(3, stuff.getStuff().size());
        stuff = stuffMappingService.getStuff(2, 3, null, null);
        assertEquals(5, stuff.getTotalItems());
        assertEquals(2, stuff.getStuff().size());
    }

    @Test
    @DisplayName("Should search stuff by name")
    public void shouldSearchStuffByName() {
        StuffPageableDto stuff = stuffMappingService.getStuff(1, 5, null, "apple macbook pro");
        assertEquals(1, stuff.getTotalItems());
        assertEquals(1, stuff.getStuff().size());
        stuff = stuffMappingService.getStuff(1, 5, null, "apple macbook");
        assertEquals(1, stuff.getTotalItems());
        assertEquals(1, stuff.getStuff().size());
        stuff = stuffMappingService.getStuff(1, 5, null, "macbook");
        assertEquals(1, stuff.getTotalItems());
        assertEquals(1, stuff.getStuff().size());
    }

    @Test
    @DisplayName("Should search stuff by description")
    public void shouldSearchStuffByDescription() {
        StuffPageableDto stuff = stuffMappingService.getStuff(1, 5, null, "Ноутбуки");
        assertEquals(1, stuff.getTotalItems());
        assertEquals(1, stuff.getStuff().size());
        stuff = stuffMappingService.getStuff(1, 5, null, "ноут");
        assertEquals(1, stuff.getTotalItems());
        assertEquals(1, stuff.getStuff().size());
        stuff = stuffMappingService.getStuff(1, 5, null, "гаджеты");
        assertEquals(3, stuff.getTotalItems());
        assertEquals(3, stuff.getStuff().size());
    }
}
