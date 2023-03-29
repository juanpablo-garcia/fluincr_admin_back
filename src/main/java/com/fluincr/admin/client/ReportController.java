package com.fluincr.admin.client;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/client/reports")
@Stateless
public class ReportController {

	@Inject
	private EntityManager em;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getTotalColaboration")
	public Response getTotalColaboration(HashMap<String, Object> map) {
		try {
			String fini = (String) map.get("fini");
			String ffin = (String) map.get("ffin");
			if (fini != null && ffin != null) {
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

				List<Object[]> ls = em.createNativeQuery(
						"select count(1), cast(creation as date)  from Colaboration where creation between ?1 and ?2  and deleted = '0' group by cast(creation as date) order by cast(creation as date)")
						.setParameter(1, df.parse(fini)).setParameter(2, df.parse(ffin)).getResultList();
				return Response.status(Status.OK).entity(ls).build();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.NOT_FOUND).build();

	}

}
