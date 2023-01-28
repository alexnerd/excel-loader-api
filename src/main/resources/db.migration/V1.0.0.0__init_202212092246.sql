-- Init migration

CREATE TABLE stuff (
    id bigserial PRIMARY KEY,
    name varchar NOT NULL UNIQUE,
    description varchar,
    create_time TIMESTAMP NOT NULL DEFAULT now()
)

CREATE UNIQUE INDEX stuff_name_idx ON stuff (name);
CREATE INDEX stuff_description_idx ON stuff (description);

COMMENT ON TABLE  stuff                  IS 'Таблица с данными';
COMMENT ON COLUMN stuff.id               IS 'Primary key';
COMMENT ON COLUMN stuff.name             IS 'Название';
COMMENT ON COLUMN stuff.description      IS 'Описание';
COMMENT ON COLUMN stuff.create_time      IS 'Дата создания';