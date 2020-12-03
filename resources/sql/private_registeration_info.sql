-- 2020.11.26
CREATE SEQUENCE public.private_registeration_info_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

-- Table: public.private_simcard

CREATE TABLE public.private_registeration_info
(
    id integer NOT NULL DEFAULT nextval('public.private_registeration_info_seq'::regclass),
    vpn_address text,
    registeration_date timestamp without time zone,
    purchaser text,
    project text,
    service_period integer,
    product_code text,
    product_mac text,
    original_version text,
    is_connected boolean DEFAULT false,
    is_updated boolean DEFAULT false,
    router_mac text,
    router_manufacturer text,
    simcard_id integer,
    comment text,
    is_deleted boolean DEFAULT false,
    inserttime timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT private_registeration_info__pk PRIMARY KEY (id)
)