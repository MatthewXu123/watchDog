-- 2020.11.26
CREATE SEQUENCE public.private__registeration_info_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

-- Table: public.private_simcard

CREATE TABLE public.private_registeration_info
(
    id integer NOT NULL DEFAULT nextval('public.private__registeration_info_seq'::regclass),
    card_number text NOT NULL,
    simcard_type integer NOT NULL DEFAULT 0,
    simcard_status integer NOT NULL DEFAULT 0,
    inserttime timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT private_simcard_pk PRIMARY KEY (id)
)