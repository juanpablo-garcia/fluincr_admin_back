package com.fluincr.admin.client;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.tmsolutions.fluincr.formulariosautomaticos.FieldId;
import co.com.tmsolutions.fluincr.formulariosautomaticos.IModel;
import co.com.tmsolutions.fluincr.model.PageCriteria;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/client/generic")
@Stateless
public class GenericController {

	@Inject
	private EntityManager em;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/find")
	public Response find(@QueryParam("id") Long id, @QueryParam("clase") String clase) {
		System.out.println("find " + id);
		Class cl;
		try {
			cl = Class.forName(clase);
			Object ob = em.find(cl, id);
			if (ob != null) {
				setIdRelaciones(cl, ob);
				return Response.status(Status.OK).entity(ob).build();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

		}
		return Response.status(Status.NOT_FOUND).build();

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/save")
	@Transactional
	public Response save(Object o, @QueryParam("clase") String clase) {
		ObjectMapper mapper = new ObjectMapper();
		Class<?> cls;
		try {
			cls = Class.forName(clase);
			Object pojo = mapper.convertValue((java.util.HashMap) o, cls);
			// Cargar las relaciones para ser guardada
			guardarRelaciones(cls, pojo);
			pojo = em.merge(pojo);
			return Response.status(Status.CREATED).entity(pojo).build();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR).build();

	}

	private void setIdRelaciones(Class<?> cls, Object pojo) {
		Field[] fs1 = cls.getDeclaredFields();
		List<Field> fields = new ArrayList<Field>();
		for (Field field : fs1) {
			fields.add(field);
		}
		for (Field fieldId : fs1) {
			if (Collection.class.isAssignableFrom(fieldId.getType())) {
			} else {
				if (fieldId.isAnnotationPresent(FieldId.class)) {
					FieldId an = fieldId.getAnnotation(FieldId.class);
					fieldId.setAccessible(true);
					String campo = an.field();
					if (campo != null) {
						Optional<Field> optrelacion = fields.stream().filter(o -> o.getName().equals(campo))
								.findFirst();
						if (optrelacion.isPresent()) {
							try {
								Field fieldRelacion = optrelacion.get();
								fieldRelacion.setAccessible(true);
								Class<?> classRelacion = fieldRelacion.getType();
								if (IModel.class.isAssignableFrom(classRelacion)) {
									IModel relacion = (IModel) fieldRelacion.get(pojo);
									if (relacion != null) {
										fieldId.set(pojo, relacion.getId());
									}
								}
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				}
			}

		}

	}

	private void guardarRelaciones(Class<?> cls, Object pojo) {
		Field[] fs1 = cls.getDeclaredFields();
		List<Field> fields = new ArrayList<Field>();
		for (Field field : fs1) {
			fields.add(field);
		}
		for (Field fieldId : fs1) {
			if (Collection.class.isAssignableFrom(fieldId.getType())) {
			} else {
				if (fieldId.isAnnotationPresent(FieldId.class)) {
					FieldId an = fieldId.getAnnotation(FieldId.class);
					fieldId.setAccessible(true);
					String campo = an.field();

					if (campo != null) {
						Optional<Field> optrelacion = fields.stream().filter(o -> o.getName().equals(campo))
								.findFirst();
						if (optrelacion.isPresent()) {
							try {
								Field fieldRelacion = optrelacion.get();
								fieldRelacion.setAccessible(true);
								Class<?> classRelacion = fieldRelacion.getType();
								// Se coge el id del campo
								Object id = fieldId.get(pojo);
								if (id != null) {
									Object objeto = em.find(classRelacion, id);
									if (objeto != null) {
										fieldRelacion.set(pojo, objeto);
									}
								}
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				}
			}

		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/update")
	@Transactional
	public Response update(Object o, @QueryParam("clase") String clase) {
		ObjectMapper mapper = new ObjectMapper();
		Class<?> cls;
		try {
			cls = Class.forName(clase);
			Object pojo = mapper.convertValue((java.util.HashMap) o, cls);

			// Cargar las relaciones para ser guardada
			guardarRelaciones(cls, pojo);
			pojo = em.merge(pojo);
			return Response.status(Status.CREATED).entity(pojo).build();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR).build();

	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/delete")
	@Transactional
	public Response delete(@QueryParam("id") Long id, @QueryParam("clase") String clase) {
		Class<?> cls;
		try {
			cls = Class.forName(clase);
			Object ob = em.find(cls, id);
			em.remove(ob);
			return Response.status(Status.OK).build();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Response.status(Status.INTERNAL_SERVER_ERROR).build();

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/findAll")
	// List<Object>
	public Response findAll(PageCriteria pc, @QueryParam("clase") String clase) {
		Class cl;
		try {
			cl = Class.forName(clase);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery criteriaQuery = cb.createQuery(cl);
			Root root = criteriaQuery.from(cl);

			if (pc.getSortedBy() != null) {
				criteriaQuery.orderBy(
						pc.getIsAscending() ? cb.asc(root.get(pc.getSortedBy())) : cb.desc(root.get(pc.getSortedBy())));
			}
			generarFiltros(pc, cb, criteriaQuery, root);
			List result = new ArrayList<>();
			if (pc.getSize() != -1) {
				result = em.createQuery(criteriaQuery).setMaxResults(pc.getSize()).setFirstResult(pc.getFirst())
						.getResultList();
			} else {
				result = em.createQuery(criteriaQuery).getResultList();

			}
			System.out.println(result.size() + " *****************************");
			return Response.status(Status.OK).entity(result).build();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.NOT_FOUND).build();

	}

	private void generarFiltros(PageCriteria pc, CriteriaBuilder cb, CriteriaQuery criteriaQuery, Root root) {
		int paramn = 0;
		List<Predicate> predicates = new ArrayList<>();
		if (pc.getFilteredBy() != null && !pc.getFilteredBy().isEmpty()) {
			for (Iterator iterator = pc.getFilteredBy().keySet().iterator(); iterator.hasNext();) {
				String type = (String) iterator.next();
				if (!type.contains("inList*")) {
					if (type.contains(".")) {
						String[] vals = type.split("\\.");
						if (vals.length > 1) {
							HashMap<String, String> params = new HashMap<>();
							String ant = "";
							String val = "";
							Join<?, ?> join = null;
							for (int j = 0; j < vals.length; j++) {
								// ESTE ES EL PRIMERO
								if (j == 0) {
									if (params.containsKey(vals[j])) {
										ant = params.get(vals[j]);
									} else {
										join = root.join(vals[j], JoinType.LEFT);
										join.alias("param" + paramn);
//									criteria.createAlias(vals[j], "param" + paramn);
										params.put(vals[j], "param" + paramn);
										ant = "param" + paramn;
										paramn++;
									}
								}
								// ESTO SON LOS DEMAS
								else if (j < vals.length - 1) {
									join = root.join(ant + "." + vals[j], JoinType.LEFT);
									join.alias("param" + paramn);
//								criteria.createAlias(ant + "." + vals[j], "param" + paramn);
									params.put(vals[j], "param" + paramn);
									ant = "param" + paramn;
									paramn++;
								}
								// ESTE ES EL ULTIMO
								else {
									val = ant + "." + vals[j];
								}
							}
							if (join != null) {
								String prop = val.split("\\.")[1];
								Object value = pc.getFilteredBy().get(type);
								if (prop.startsWith("integer_")) {
									prop = prop.replace("integer_", "");
									value = Integer.valueOf((String) value);
								}
								if (value instanceof String) {
									Expression upper = cb.upper(join.get(prop));
									Predicate ctfPredicate = cb.like(upper,
											"%" + pc.getFilteredBy().get(type).toString().toUpperCase() + "%");
									predicates.add(ctfPredicate);
//								criteriaQuery.where(cb.and(ctfPredicate));
								} else if (value instanceof Boolean) {
									Predicate ctfPredicate = cb.equal(join.get(prop), value);
									predicates.add(ctfPredicate);
//								criteriaQuery.where(cb.and(ctfPredicate));

								} else if (value instanceof Integer || value instanceof BigDecimal) {
									Predicate ctfPredicate = cb.equal(join.get(prop), value);
									predicates.add(ctfPredicate);
//								criteriaQuery.where(cb.and(ctfPredicate));

								}

//							criteriaQuery.where(cb.and(ctfPredicate));

							}
						}

					} else {
						Object value = pc.getFilteredBy().get(type);
						if (type.startsWith("integer_")) {
							type = type.replace("integer_", "");
							value = Integer.valueOf((String) value);
						}
						if (value instanceof String) {
							Expression upper = cb.upper(root.get(type));
							Predicate ctfPredicate = cb.like(upper,
									"%" + pc.getFilteredBy().get(type).toString().toUpperCase() + "%");
							predicates.add(ctfPredicate);
//						criteriaQuery.where(cb.and(ctfPredicate));
						} else if (value instanceof Boolean) {
							Predicate ctfPredicate = cb.equal(root.get(type), value);
							predicates.add(ctfPredicate);
//						criteriaQuery.where(cb.and(ctfPredicate));

						} else if (value instanceof Integer || value instanceof BigDecimal) {
							Predicate ctfPredicate = cb.equal(root.get(type), value);
							predicates.add(ctfPredicate);
//						criteriaQuery.where(cb.and(ctfPredicate));

						}
					}
				} else {
					Object value = pc.getFilteredBy().get(type);
					String tipo = type.replace("inList*", "");
//					if (tipo.equals("order_source")) {
//						ArrayList<Object> conditionColumnValues = (ArrayList<Object>) value;
//						jakarta.persistence.criteria.Path<Object> path = root.get(tipo);
//						In<Object> in = cb.in(path);
//						for (Object conditionColumnValue : conditionColumnValues) {
//							String s = (String) conditionColumnValue;
//							in.value(EnumOrderSource.valueOf(s));
//						}
//						predicates.add(in);
//					}

				}
			}
		}
		Predicate[] ps = new Predicate[predicates.size()];
		int i = 0;
		for (Predicate predicate : predicates) {
			ps[i++] = predicate;
		}
		cb.and(ps);
		criteriaQuery.where(ps);

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/count")
	// Long
	public Response count(PageCriteria pc, @QueryParam("clase") String clase) {
		Class cl;
		try {
			cl = Class.forName(clase);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
			Root root = criteriaQuery.from(cl);
			generarFiltros(pc, cb, criteriaQuery, root);
			criteriaQuery.select(cb.count(root));

			Long result = em.createQuery(criteriaQuery).getSingleResult();
			return Response.status(Status.OK).entity(result).build();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Response.status(Status.NOT_FOUND).build();

	}

}
