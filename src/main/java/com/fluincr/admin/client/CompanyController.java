package com.fluincr.admin.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.fluincr.admin.utilidades.UtilidadesJWT;

import co.com.tmsolutions.fluincr.model.Colaboration;
import co.com.tmsolutions.fluincr.model.Company;
import co.com.tmsolutions.fluincr.model.EnumIFStatus;
import co.com.tmsolutions.fluincr.model.Influencer;
import co.com.tmsolutions.fluincr.model.LanguageCompany;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/client/company")
@Stateless
public class CompanyController {

	@Inject
	private EntityManager em;

	@GET
	@Path("/getLanguages")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLanguages(@QueryParam("usuario") Long id) {
		String sql = "select u from LanguageCompany u join u.companyId infl where infl.id = :id";
		List<LanguageCompany> ls = em.createQuery(sql, LanguageCompany.class).setParameter("id", id).getResultList();
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
		Company inf = em.find(Company.class, id.longValue());
		if (inf != null) {
			inf.getAttributes().put("verified", ver);
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
		System.out.println(ver + " ******");
		System.out.println(id);
		if (id != null && ver != null) {
			EnumIFStatus enstatus = EnumIFStatus.valueOf(ver);
			Company inf = em.find(Company.class, id.longValue());
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
	@Path("/getToken")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getToken(@QueryParam("usuario") Long id) {
		Company inf = em.find(Company.class, id);
		HashMap<String, Object> map = new HashMap<>();

		if (inf != null && inf.getAttributes().get("active") != null && (Boolean) inf.getAttributes().get("active")) {
			String token = UtilidadesJWT.generateJWTToken(inf.getMail(), inf.getId(), "company");
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
		Company inf = col.getCompany();
		if (inf != null && inf.getAttributes().get("active") != null && (Boolean) inf.getAttributes().get("active")) {
			String token = UtilidadesJWT.generateJWTToken(inf.getMail(), inf.getId(), "company");
			map.put("token", token);

		}
		return Response.status(Status.OK).entity(map).build();

	}


}
