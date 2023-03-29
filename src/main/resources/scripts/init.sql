 
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

insert into rol_formulario (id,nombre,banda,deleted,icono) values 
(1,'roles',1,'0','fa fa-dot-circle-o'),
(2,'usuarioAdmin',1,'0','fa fa-dot-circle-o'),
(3,'legal_text',1,'0','fa fa-dot-circle-o'),
(4,'influencers',1,'0','fa fa-dot-circle-o'),
(5,'companies',1,'0','fa fa-dot-circle-o'),
(6,'staticpage',1,'0','fa fa-dot-circle-o'),
(7,'cuponcode',1,'0','fa fa-dot-circle-o'),
(8,'services',1,'0','fa fa-dot-circle-o'),
(9,'colaboration',1,'0','fa fa-dot-circle-o'),
(10,'emailtemplates',1,'0','fa fa-dot-circle-o'),
(11,'colaboration_report',2,'0','fa fa-dot-circle-o'),
(12,'business_report',2,'0','fa fa-dot-circle-o'),
(13,'influencer_report',2,'0','fa fa-dot-circle-o');

  

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

