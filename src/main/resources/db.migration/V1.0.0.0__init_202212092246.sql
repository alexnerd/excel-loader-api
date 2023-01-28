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

-- Init migration

CREATE TABLE stuff (
    id bigserial PRIMARY KEY,
    name varchar NOT NULL UNIQUE,
    description varchar,
    create_time TIMESTAMP NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX stuff_name_idx ON stuff (name);
CREATE INDEX stuff_description_idx ON stuff (description);

COMMENT ON TABLE  stuff                  IS 'Таблица с данными';
COMMENT ON COLUMN stuff.id               IS 'Primary key';
COMMENT ON COLUMN stuff.name             IS 'Название';
COMMENT ON COLUMN stuff.description      IS 'Описание';
COMMENT ON COLUMN stuff.create_time      IS 'Дата создания';