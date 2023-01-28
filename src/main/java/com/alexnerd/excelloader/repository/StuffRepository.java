package com.alexnerd.excelloader.repository;

import com.alexnerd.excelloader.repository.dao.Stuff;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface StuffRepository extends CrudRepository<Stuff, Long>, JpaSpecificationExecutor<Stuff> {
}
