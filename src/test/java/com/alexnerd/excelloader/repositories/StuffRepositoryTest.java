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
