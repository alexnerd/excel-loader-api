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

package com.alexnerd.excelloader.repository;

import com.alexnerd.excelloader.repository.dao.Stuff;
import com.alexnerd.excelloader.repository.dao.Stuff_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Order;

public class StuffSpecification {


    public static <StuffSpecification> SpecBuilder<StuffSpecification> specBuilder(Specification<StuffSpecification> spec) {
        return new SpecBuilder<>(spec);
    }

    public static class SpecBuilder<StuffSpecification> {
        private Specification<StuffSpecification> specification;

        private SpecBuilder(Specification<StuffSpecification> specification) {
            this.specification = specification;
        }

        public SpecBuilder<StuffSpecification> and(Specification<StuffSpecification> spec) {
            specification = specification == null ? spec : specification.and(spec);
            return this;
        }

        public Specification<StuffSpecification> build() {
            return specification;
        }
    }

    public static Specification<Stuff> sortSpec(String field) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Order orderByName = criteriaBuilder.asc(root.get(Stuff_.name));

            if (field == null) {
                criteriaQuery.orderBy(orderByName);
                return null;
            }

            switch (field.toLowerCase()) {
                case "name":
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Stuff_.name)));
                    break;
                case "description":
                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get(Stuff_.description)));
                    break;
                default:
                    criteriaQuery.orderBy(orderByName);
            }
            return null;
        };
    }

    public static Specification<Stuff> likeSpec(String like) {
        if (like == null || like.isBlank()) return null;

        String likePattern = '%' + like.toLowerCase().trim() + '%';
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Stuff_.name)), likePattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get(Stuff_.description)), likePattern)
        );
    }
}
