--alter table private_wechat_receiver alter column tag_id type varchar(300) ; 
--alter table private_wechat_receiver add column tag_id2 varchar(300)  null;
--alter table private_wechat_receiver add column tag_id3 varchar(300)  null;
--alter table private_wechat_receiver add column channel int not null default 1;
--alter table private_wechat_receiver add column send_tag2_delay int not null default 60;

CREATE TABLE public.private_wechat_receiver
(
    id integer DEFAULT nextval('private_wechat_receiver_id_seq'::regclass),
    supervisor_id integer NOT NULL,
    deadline date,
    checknetwork boolean NOT NULL DEFAULT false,
    agent_id character varying(10) COLLATE pg_catalog."default",
    tag_id character varying(300) COLLATE pg_catalog."default",
    tag_id2 character varying(300) COLLATE pg_catalog."default",
    tag_id3 character varying(300) COLLATE pg_catalog."default",
    send_tag2_delay integer NOT NULL DEFAULT 60,
    channel integer NOT NULL DEFAULT 1,
    CONSTRAINT private_wechat_receiver_pkey PRIMARY KEY (id)
);
CREATE TABLE public.private_alarm_important
(
    idsite integer NOT NULL,
    idalarm integer NOT NULL,
    insert_time timestamp without time zone,
    CONSTRAINT private_alarm_important_pkey PRIMARY KEY (idsite, idalarm)
);
CREATE TABLE public.private_property
(
    key character varying(100) COLLATE pg_catalog."default" NOT NULL,
    value text COLLATE pg_catalog."default",
    "time" timestamp without time zone,
    CONSTRAINT private_property_pkey PRIMARY KEY (key)
);
drop table private_shorturl;
CREATE TABLE public.private_shorturl
(
    long_url character varying(500) COLLATE pg_catalog."default" NOT NULL,
    url character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT private_shorturl_pkey PRIMARY KEY (long_url)
);

create table private_sla_site_result
(
type varchar(1) not null,
startday date not null,
idsite int not null,
ht_tot int,
ht_outsla int,
ht_reset int,
vh_tot int,
vh_outsla int,
vh_reset int,
h_tot int,
h_outsla int,
h_reset int,
m_tot int,
m_outsla int,
m_reset int,
l_tot int,
l_outsla int,
l_reset int,
CONSTRAINT private_sla_site_result_pkey PRIMARY KEY (type,startday,idsite)
);


CREATE TABLE public.private_ack_site_result
(
    type varchar(1) not null,
    startday date not null,
    idsite integer not null,
    m_tot int,
    m_ack int,
    m_avg double precision,
    a_tot int,
    a_ack int,
    a_avg double precision,
    e_tot int,
    e_ack int,
    e_avg double precision,
    n_tot int,
    n_ack int,
    n_avg double precision,
    CONSTRAINT private_ack_site_result_pkey PRIMARY KEY (type,startday,idsite)
);


--alter table private_wechat_receiver add column channel int not null default 1;


CREATE TABLE public.private_temperature_kpi_result
(
    type character varying(1) COLLATE pg_catalog."default" NOT NULL,
    startday date NOT NULL,
    idsite integer NOT NULL,
    iddevice integer NOT NULL,
    devicedescription character varying(300) COLLATE pg_catalog."default",
    t1 integer,
    t2 integer,
    t3 integer,
    t4 integer,
    t5 integer,
    t6 integer,
    defrost integer,
    defrostminutes integer,
    CONSTRAINT private_temperature_kpi_result_pkey PRIMARY KEY (type,startday,idsite,iddevice)
);



--2-19-5-23
alter table private_wechat_receiver
drop column id;
alter table private_wechat_receiver
add primary key (supervisor_id);

--5-28
alter table private_wechat_receiver
add column tag_id_ character varying(300) null;
alter table private_wechat_receiver
add column tag_id2_ character varying(300) null;
alter table private_wechat_receiver
add column tag_id3_ character varying(300) null;
update private_wechat_receiver set tag_id_=tag_id,tag_id2_=tag_id2,tag_id3_=tag_id3;

--2020.5.18
alter table private_property alter column value type text;