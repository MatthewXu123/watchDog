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
    vpn_address text NOT NULL,
    registeration_date timestamp without time zone NOT NULL,
    purchaser text NOT NULL,
    end_user text NOT NULL,
    service_period integer NOT NULL,
    product_code text NOT NULL,
    product_mac text NOT NULL,
    original_version text NOT NULL,
    is_connected boolean NOT NULL DEFAULT false,
    is_updated boolean NOT NULL DEFAULT false,
    router_mac text NOT NULL,
    router_manufacturer text NOT NULL,
    simcard_id integer,
    comment text NOT NULL,
    inserttime timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT private_registeration_info__pk PRIMARY KEY (id)
)