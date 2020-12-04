CREATE TABLE wechat.simcard
(
    id SERIAL,
    card_number text NOT NULL,
    simcard_type integer NOT NULL DEFAULT 0,
    simcard_status integer NOT NULL DEFAULT 0,
    inserttime timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT private_simcard_pk PRIMARY KEY (id)
)