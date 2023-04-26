package com.fluincr.admin.client;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;

import com.fluincr.admin.utilidades.UtilidadesAdmin;
import com.fluincr.admin.utilidades.UtilidadesJWT;

import co.com.tmsolutions.fluincr.model.Colaboration;
import co.com.tmsolutions.fluincr.model.EnumIFStatus;
import co.com.tmsolutions.fluincr.model.Influencer;
import co.com.tmsolutions.fluincr.model.LanguageInfluincr;
import co.com.tmsolutions.fluincr.model.RolBanda;
import co.com.tmsolutions.fluincr.model.RolFormulario;
import co.com.tmsolutions.fluincr.model.RolGrupoDeSeguridad;
import co.com.tmsolutions.fluincr.model.RolGrupoDeSeguridadUsuario;
import co.com.tmsolutions.fluincr.model.RolPrivilegioGrupoDeSeguridad;
import co.com.tmsolutions.fluincr.model.RolRole;
import co.com.tmsolutions.fluincr.model.Users;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/client/user")
@Stateless
public class UserController {

	@Inject
	private EntityManager em;

//	@Inject
//	private BeanVariablesAplicacion beanVariablesAplicacion;
//	@Inject
//	private CommonController commonController;

	@GET
	@Path("/validateUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateUser(@QueryParam("usuario") String usuario, @QueryParam("password") String password) {
		String ret = null;
		String sql = "select u from Users u where u.email = :mail and u.status = true";
		String tok = null;
		Optional<Users> optult = em.createQuery(sql, Users.class).setParameter("mail", usuario).getResultList().stream()
				.findFirst();
		Users us = null;
		if (optult.isPresent()) {
			us = optult.get();
			String encoded = us.getPassword();
			if (encoded.startsWith("$2y")) {
				encoded = encoded.replace("$2y", "$2a");
			}
			if (BCrypt.checkpw(password, encoded)) {
				Set<String> grupos = new HashSet<String>();

				grupos.add("admin");
				ret = UtilidadesAdmin.generateJWTToken(us.getEmail(), us.getName(), us.getId(), grupos);
				tok = "token";
//				Faces.addResponseCookie("token_app", tok, "/", -1);
			} else {
				ret = "contrasena_invalida";
			}
		} else {
			ret = "usuario_no_existe";
		}
		tok = StringUtils.defaultIfBlank(tok, "error");
//		if(ret==null) { 
//			ret
//		}
//		if (ret != null) {
//			FacesContext.getCurrentInstance().addMessage(null,
//					new FacesMessage(FacesMessage.SEVERITY_ERROR, Messages.getString("error"), ret));
//		} else {
//			if (us != null) {
//				bean_User.setFoto(us.getFoto());
//				bean_User.setNombre(us.getNombre());
//				Faces.redirect("secured/dashboard.xhtml");
//			}
//		}
//		
		HashMap<String, String> map = new HashMap<>();
		map.put(tok, ret);
		return Response.status(Status.OK).entity(map).build();

	}

	@GET
	@Path("/getUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@QueryParam("usuario") Long id) {
		String sql = "select u from Users u where u.id = :id";
		Optional<Users> optult = em.createQuery(sql, Users.class).setParameter("id", id).getResultList().stream()
				.findFirst();
		Users us = null;
		if (optult.isPresent()) {
			us = optult.get();

			return Response.status(Status.OK).entity(us).build();
		}

		return Response.status(Status.OK).build();

	}

	@GET
	@Path("/getPrivileges")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPrivileges(@QueryParam("usuario") Long id) {
		String sql = "select u from Users u where u.id = :id";
		Optional<Users> optult = em.createQuery(sql, Users.class).setParameter("id", id).getResultList().stream()
				.findFirst();
		if (optult.isPresent()) {
			String sqlpriv = " select distinct   rol.nombre as rol,  banda.nombre as banda,   form.nombre as formulario,rol.icono as ic_rol,  banda.icono as ic_banda,   form.icono as ic_formulario,form.orden as orden  from rol_grupo_de_seguridad_usuario  inner join rol_grupo_de_seguridad grupo on grupo.id = grupo_de_seguridad   inner join rol_privilegio_grupo_de_seguridad rl on rl.grupodeseguridad = grupo.id inner join rol_formulario form on form.id = rl.formulario_id inner join rol_banda banda on banda.id = form.banda inner join rol_role rol on rol.id = banda.role  where usuario = %s order by rol,banda,orden";
			sqlpriv = String.format(sqlpriv, id);
			List<Object[]> ls = em.createNativeQuery(sqlpriv).getResultList();
			return Response.status(Status.OK).entity(ls).build();
		}

		return Response.status(Status.NOT_FOUND).build();

	}

	@GET
	@Path("/getLanguages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLanguages(@QueryParam("usuario") Long id) {
		String sql = "select u from LanguageInfluincr u join u.influincrId infl where infl.id = :id";
		List<LanguageInfluincr> ls = em.createQuery(sql, LanguageInfluincr.class).setParameter("id", id)
				.getResultList();
		List<String> lgs = ls.stream().map(o -> o.getLanguageId().getName()).collect(Collectors.toList());
		return Response.status(Status.OK).entity(lgs).build();

	}

	@POST
	@Path("/verificar")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response verificar(HashMap<String, Object> mapParam) {
		BigDecimal id = (BigDecimal) mapParam.get("id");
		Boolean ver = (Boolean) mapParam.get("verified");
		Influencer inf = em.find(Influencer.class, id.longValue());
		if (inf != null) {
			inf.getAttributes().put("verified", ver);
			em.merge(inf);
		}
		return Response.status(Status.OK).build();

	}

	@POST
	@Path("/featured")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response featured(HashMap<String, Object> mapParam) {
		BigDecimal id = (BigDecimal) mapParam.get("id");
		Boolean ver = (Boolean) mapParam.get("featured");
		Influencer inf = em.find(Influencer.class, id.longValue());
		if (inf != null) {
			inf.setFeatured(ver);
			em.merge(inf);
		}
		return Response.status(Status.OK).build();

	}

	@POST
	@Path("/changeStatus")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	public Response changeStatus(HashMap<String, Object> mapParam) {
		BigDecimal id = (BigDecimal) mapParam.get("id");
		String ver = (String) mapParam.get("status");
		System.out.println(ver);
		if (id != null && ver != null) {
			EnumIFStatus enstatus = EnumIFStatus.valueOf(ver);
			Influencer inf = em.find(Influencer.class, id.longValue());
			if (inf != null) {
				inf.getAttributes().put("status", ver);
				Boolean status = enstatus.equals(EnumIFStatus.ACTIVO);
				inf.getAttributes().put("active", status);
				inf.setDeleted(status ? "0" : "1");
				em.merge(inf);
			}
		}
		return Response.status(Status.OK).build();

	}

	@GET
	@Path("/loadPrivilege")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadPrivilege(@QueryParam("usuario") Long idUsuario,
			@QueryParam("formulario") String nombreFormulario) {
		if (nombreFormulario != null) {
			String sql = "select  coalesce(cancreate,false) as cancreate ,  coalesce(candelete,false) as candelete, coalesce(canread,false)  as canread, coalesce(canupdate,false) as canupdate,  coalesce(canprint ,false) as canprint  from rol_privilegio_grupo_de_seguridad  inner join rol_formulario form on form.id = formulario_id where grupodeseguridad in ( select grupo_de_seguridad from rol_grupo_de_seguridad_usuario where usuario = %s) and form.nombre = '%s';";
			sql = String.format(sql, idUsuario, nombreFormulario);
			List<Object[]> ls = em.createNativeQuery(sql).getResultList();
			if (!ls.isEmpty()) {
				Object[] ob = ls.get(0);
				return Response.status(Status.OK).entity(ob).build();
			}
		}
		return Response.status(Status.NOT_FOUND).build();

	}

	@GET
	@Path("/getRoles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRoles() {
		List<RolRole> roles = em.createQuery("select c from RolRole c join fetch c.bands b order by c.nombre")
				.getResultList();
		return Response.status(Status.OK).entity(roles).build();
	}

	@DELETE
	@Path("/deleteRolPrivilegioGrupoDeSeguridad")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response deleteRolPrivilegioGrupoDeSeguridad(@QueryParam("id") Long id) {
		em.createQuery("delete from RolPrivilegioGrupoDeSeguridad c where  c.grupodeseguridad.id = :id")
				.setParameter("id", id).executeUpdate();
		return Response.status(Status.OK).build();
	}

	@GET
	@Path("/getRolPrivilegioGrupoDeSeguridad")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRolPrivilegioGrupoDeSeguridad(@QueryParam("grupodeseguridad") Long grupodeseguridad) {
		List<RolPrivilegioGrupoDeSeguridad> privs = em
				.createQuery(
						"select c from RolPrivilegioGrupoDeSeguridad c where c.grupodeseguridad.id = :grupodeseguridad")
				.setParameter("grupodeseguridad", grupodeseguridad).getResultList();
		privs.stream().forEach(o -> {
			if (o.getFormulario() != null) {
				o.setId_formulario(o.getFormulario().getId());
			}
		});
		return Response.status(Status.OK).entity(privs).build();
	}

	@GET
	@Path("/getBandsByRole")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBandsByRole(@QueryParam("role") Long role) {
		List<RolBanda> privs = em.createQuery("select b from RolBanda b where b.role.id = :role")
				.setParameter("role", role).getResultList();
		return Response.status(Status.OK).entity(privs).build();
	}

	@GET
	@Path("/getFormsByBand")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFormsByBand(@QueryParam("banda") Long banda) {
		List<RolFormulario> privs = em.createQuery("select b from RolFormulario b where b.banda.id = :banda")
				.setParameter("banda", banda).getResultList();
		return Response.status(Status.OK).entity(privs).build();
	}

	@POST
	@Path("/guardarRoles")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response guardarRoles(HashMap<String, Object> map) {

		BigDecimal id = (BigDecimal) map.get("user_id");
		Users usuario = em.find(Users.class, id.longValue());
		List<BigDecimal> roles_id = (List<BigDecimal>) map.get("roles_id");
		if (roles_id != null) {

			System.out.println(roles_id);

			em.createQuery("delete from RolGrupoDeSeguridadUsuario r where r.usuario = ?1").setParameter(1, usuario)
					.executeUpdate();

			Session session = em.unwrap(Session.class);

			MultiIdentifierLoadAccess<RolGrupoDeSeguridad> multiLoadAccess = session
					.byMultipleIds(RolGrupoDeSeguridad.class);
			List<Long> roles_id_long = roles_id.stream().map(o -> o.longValue()).collect(Collectors.toList());
			List<RolGrupoDeSeguridad> gruposDeseguridad = multiLoadAccess.multiLoad(roles_id_long);

//			List<RolGrupoDeSeguridad> gruposDeseguridad = em
//					.createQuery("select c from RolGrupoDeSeguridad c where c.id IN :ids").setParameter("ids", roles_id)
//					.getResultList();
			for (RolGrupoDeSeguridad gs : gruposDeseguridad) {
				RolGrupoDeSeguridadUsuario r = new RolGrupoDeSeguridadUsuario();
				r.setUsuario(usuario);
				r.setGrupo_de_seguridad(gs);
				em.merge(r);
			}
		}
		return Response.status(Status.OK).build();

	}

	@GET
	@Path("/getRolesIds")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRolesIds(@QueryParam("usuario") Long id) {
		String sql = "select u from Users u where u.id = :id";
		Optional<Users> optult = em.createQuery(sql, Users.class).setParameter("id", id).getResultList().stream()
				.findFirst();
		if (optult.isPresent()) {
			String sqlpriv = "select distinct grupo_de_seguridad from rol_grupo_de_seguridad_usuario where usuario = %s";
			sqlpriv = String.format(sqlpriv, id);
			List<Long> ls = em.createNativeQuery(sqlpriv).getResultList();
			return Response.status(Status.OK).entity(ls).build();
		}

		return Response.status(Status.NOT_FOUND).build();

	}

	@GET
	@Path("/getToken")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getToken(@QueryParam("usuario") Long id) {
		Influencer inf = em.find(Influencer.class, id);
		HashMap<String, Object> map = new HashMap<>();

		if (inf != null && inf.getAttributes().get("active") != null && (Boolean) inf.getAttributes().get("active")) {
			String token = UtilidadesJWT.generateJWTToken(inf.getMail(), inf.getId(), "influencer");
			map.put("token", token);

		}
		return Response.status(Status.OK).entity(map).build();

	}

	@GET
	@Path("/getTokenColaboration")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTokenColaboration(@QueryParam("id") Long id) {
		Colaboration col = em.find(Colaboration.class, id);
		HashMap<String, Object> map = new HashMap<>();
		Influencer inf = col.getInfluincr();
		if (inf != null && inf.getAttributes().get("active") != null && (Boolean) inf.getAttributes().get("active")) {
			String token = UtilidadesJWT.generateJWTToken(inf.getMail(), inf.getId(), "influencer");
			map.put("token", token);

		}
		return Response.status(Status.OK).entity(map).build();

	}

}
