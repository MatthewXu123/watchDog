-- SCHEMA: wechat

-- DROP SCHEMA wechat ;

CREATE SCHEMA wechat
    AUTHORIZATION postgres;
    
-- Table: wechat.calling_log

-- DROP TABLE wechat.calling_log;

CREATE TABLE wechat.calling_log
(
    idsupervisor integer NOT NULL,
    idalarm integer NOT NULL,
    insert_time timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT calling_log_pkey PRIMARY KEY (idsupervisor, idalarm, insert_time)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wechat.calling_log
    OWNER to postgres;

CREATE SEQUENCE wechat.ping_log_id_seq
    INCREMENT 1
    START 1649918
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

ALTER SEQUENCE wechat.ping_log_id_seq
    OWNER TO postgres;    

-- Table: wechat.ping_log

-- DROP TABLE wechat.ping_log;

CREATE TABLE wechat.ping_log
(
    id integer NOT NULL DEFAULT nextval('wechat.ping_log_id_seq'::regclass),
    ip character varying(20) COLLATE pg_catalog."default" NOT NULL,
    inserttime timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ping_log_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wechat.ping_log
    OWNER to postgres;

CREATE SEQUENCE wechat.suggestion_id_seq
    INCREMENT 1
    START 5
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

ALTER SEQUENCE wechat.suggestion_id_seq
    OWNER TO postgres;
    
-- Table: wechat.suggestion

-- DROP TABLE wechat.suggestion;

CREATE TABLE wechat.suggestion
(
    id integer NOT NULL DEFAULT nextval('wechat.suggestion_id_seq'::regclass),
    user_id character varying(100) COLLATE pg_catalog."default",
    suggestion text COLLATE pg_catalog."default",
    insert_time timestamp without time zone DEFAULT now()
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE wechat.suggestion
    OWNER to postgres;