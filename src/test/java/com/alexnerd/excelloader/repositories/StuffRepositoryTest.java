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

package com.alexnerd.excelloader.repositories;

import com.alexnerd.excelloader.PostgreSQLContainerExtension;
import com.alexnerd.excelloader.repository.StuffRepository;
import com.alexnerd.excelloader.repository.dao.Stuff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(PostgreSQLContainerExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class StuffRepositoryTest {

    @Autowired
    private StuffRepository stuffRepository;

    private Stuff stuff;

    @BeforeEach
    private void init() {
        stuff = new Stuff();
        stuff.setName("iphone");
        stuff.setDescription("смартфон");
    }

    @Test
    @DisplayName("Validate stuff mapping")
    public void shouldGetStuffTest() {
        stuffRepository.findAll();
    }

    @Test
    @DisplayName("Should save stuff")
    public void shouldSuccessfulSaveTest() {
        Stuff savedStuff = stuffRepository.save(stuff);
        assertNotEquals(0L, savedStuff.getId());
    }
}
