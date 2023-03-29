package com.fluincr.admin.utilidades;

import java.io.Serializable;

import jakarta.websocket.Session;

public class SocketSessionObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String usuario;
	private Session session;

	public SocketSessionObject(String usuario, Session session) {
		super();
		this.usuario = usuario;
		this.session = session;
	}

	public SocketSessionObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}
