package com.fluincr.admin;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class ApplicationResources {
	@Produces
	@PersistenceContext
	private EntityManager em;

	public ApplicationResources() {
		super();
	}

	public void init() {

	}
}
