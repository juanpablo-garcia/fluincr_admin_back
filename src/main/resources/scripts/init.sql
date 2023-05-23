 
 insert into users (id,name,email,password,status)
 values (-1,'Admin','admin@fluincr.com','$2y$10$ode4PwEzjYIoAXDcXs97zuJoSbpk2ax4lNYcpGUUisYPO6zmHCBXS','Y');

 
delete from rol_grupo_de_seguridad_usuario;
delete from rol_privilegio_grupo_de_seguridad;
delete from rol_grupo_de_seguridad;
delete from rol_formulario;
delete from rol_banda;
delete from rol_role;

insert into rol_role (id,nombre,deleted,icono) values (1,'maestros','0','pi pi-briefcase'),(2,'reportes','0','pi pi-chart-line');

 insert into rol_banda (id,nombre,role,deleted,icono) values (1,'general',1,'0','pi pi-home'),
(2,'general',2,'0','pi pi-home');

insert into rol_formulario (id,nombre,banda,deleted,icono,orden) values 
(1,'roles',1,'0','fa fa-dot-circle-o',3),
(2,'usuarioAdmin',1,'0','fa fa-dot-circle-o',4),
(3,'legal_text',1,'0','fa fa-dot-circle-o',5),
(4,'influencers',1,'0','fa fa-dot-circle-o',1),
(5,'companies',1,'0','fa fa-dot-circle-o',2),
(6,'staticpage',1,'0','fa fa-dot-circle-o',7),
(7,'cuponcode',1,'0','fa fa-dot-circle-o',8),
(8,'services',1,'0','fa fa-dot-circle-o',9),
(9,'colaboration',1,'0','fa fa-dot-circle-o',10),
(10,'emailtemplates',1,'0','fa fa-dot-circle-o',6),
(11,'colaboration_report',2,'0','fa fa-dot-circle-o',10),
(12,'business_report',2,'0','fa fa-dot-circle-o',11),
(13,'influencer_report',2,'0','fa fa-dot-circle-o',12),
(14,'fluincrconfiguration',1,'0','fa fa-dot-circle-o',0);

  

insert into rol_grupo_de_seguridad (id,nombre,deleted) values  (1,'Admin','0');
 
INSERT INTO rol_privilegio_grupo_de_seguridad(
	  deleted, cancreate, candelete, canprint, canread, canupdate, formulario_id, grupodeseguridad)
     
	(select   '0' as deleted,
true as cancreate,
true as candelete,
true as canprint,
true as canread,
true as canupdate,
rf.id as formulario_id,
gs.id as grupodeseguridad
from rol_grupo_de_seguridad gs
inner join rol_formulario rf on 1 = 1);

insert into rol_grupo_de_seguridad_usuario (id,deleted,grupo_de_seguridad,usuario) values (1,'0',1,-1);



CREATE OR REPLACE VIEW influencer_view AS
select account,
givenname,
familyname,
imageprofile,
birthday,
gender,mail,secondmail,
inf.id,username,
attributes ->> 'active' = 'true' active,
attributes ->> 'verified' = 'true' verified,
coalesce(attributes ->> 'securityQuestion',attributes ->> 'preguntaSeguridad') securityQuestion,
coalesce(attributes ->> 'securityAnswer') securityAnswer,
coalesce (inf.indicative||'-','') || coalesce(phonenumber,'') phonenumber, 
address,city,c.name country,
attributes ->> 'zipcode' zipcode,
notification ->> 'promotions' promotions,
attributes ->> 'primarylanguage' primarylanguage,
(select STRING_AGG(convention,',') from 
languageinfluincr inner join language l on l.id=languageid where influincrid = inf.id) as language,
rating ,
(select STRING_AGG(name,',') from categoryinfluincr inner join category l on l.id=categoryid where influincrid = inf.id
) category_en ,
(select STRING_AGG(nombre,',') from categoryinfluincr inner join category l on l.id=categoryid where influincrid = inf.id
) category_es,
description,
cast(followers ->> 'instagram' as bigint) instagram,
cast(followers ->> 'facebook' as bigint) facebook,
cast(followers ->> 'tiktok' as bigint) tiktok,
cast(followers ->> 'youtube' as bigint) youtube,
cast(followers ->> 'twitter' as bigint) twitter
from influencer inf
left outer join country c on c.id = inf.country;


CREATE OR REPLACE VIEW item_view AS
select
item.id as id,
col.creation as "date",
col.id AS "col_id",
(CASE col.status when 0 then 'OS_CREADA' when 1 then 'OS_PAGADA' when 2 then 'OS_DECLINADA' when 3 then 'OS_ACEPTADA' when 4 then 'OS_ESPERA' when 5 then 'OS_CERRADA' end)
AS "col_status",
col.comment AS "col_comments",
col.document AS "col_file",
col.publicProfileInfluencer AS "influencer_public_profile",
col.publicProfileCompany AS "company_public_profile",
comp.account AS "company_account",
comp.namecompany AS "company_name",
comp.rating AS "company_rating",
inf.account AS "influencer_account",
inf.givenname|| ' '|| inf.familyname AS "influencer_name",
inf.rating AS "influencer_rating",
prod.name AS "product_name",
prod.description AS "product_description",
cast(prod.price ->> 'USD' as numeric(19,2)) AS "product_price",
col.giftvalue as "col_giftvalue",
item.price  as item_subtotal,
coalesce(item.discount,0.0) as item_discount,
item.price - coalesce(item.discount,0.0) as item_total,

 cast(item.title as jsonb) ->> 'name' as "item_name",
( WHEN 0 THEN 'creado'
            WHEN 1 THEN 'aceptacion_pendiente'
            WHEN 2 THEN 'declinado'
            WHEN 3 THEN 'sube_contenido'
            WHEN 4 THEN 'aprueba_contenido'
            WHEN 5 THEN 'publica_contenido'
            WHEN 6 THEN 'ventan_de_disputa'
            WHEN 7 THEN 'en_espera'
            WHEN 8 THEN 'terminado'
	    WHEN 9 THEN 'cancelado'
	    ELSE NULL
            END AS item_status) as "item_status"
 from item
inner join colaboration col on col.id = item.colaboration
left outer join company comp on comp.id = col.company
left outer join influencer inf on inf.id = col.influincr
left outer join product prod on prod.id = col.product;

create or replace view business_report as 
select account as account,
namecompany as name,
imageprofile as imageprofile,
mail as mail,
secondmail as secondmail,
comp.id as id,
username as username,
cast(attributes ->> 'emailverified' as boolean) as emailverified,
cast(attributes ->> 'active' as boolean) as active,
cast(attributes ->> 'verified' as boolean) as verified,
attributes ->> 'securityQuestion' as securityQuestion,
attributes ->> 'preguntaSeguridad' as preguntaSeguridad,
attributes ->> 'securityAnswer' as securityAnswer,
comp.indicative||' '||phonenumber as phone,
city as city,
pais.name as country,
attributes ->> 'zipcode' as zipcode,
cast(notification ->> 'promotions' as boolean) as  marketing_emails,
attributes ->> 'primarylanguage' as primarylanguage,
(select STRING_AGG ( name, '-' ) from languagecompany inner join language l on l.id = languageid
where companyid = comp.id) as languages,
rating as rating,
(select STRING_AGG ( name, '-' ) from categorycompany inner join category l on l.id = categoryid
where companyid = comp.id) as category,
description as bio,
cast(followers ->> 'instagram' as int) as followers_instagram,
cast(followers ->> 'facebook' as int) as followers_facebook,
cast(followers ->> 'tiktok' as int) as followers_tiktok,
cast(followers ->> 'youtube' as int) as followers_youtube,
cast(followers ->> 'twitter' as int) as followers_twitter

from company comp
left outer join country pais on pais.id = country;

insert into fluincrconfiguration (id,deleted,company_rate,paypal_rate,refund_rate) values (1,'0',5,7,10);