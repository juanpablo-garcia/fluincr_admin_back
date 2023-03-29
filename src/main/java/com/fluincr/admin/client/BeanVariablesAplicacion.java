package com.fluincr.admin.client;

import java.util.concurrent.ConcurrentHashMap;

import com.fluincr.admin.utilidades.SocketSessionObject;

import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Named(value = "BeanVariablesAplicacion")
@Singleton
public class BeanVariablesAplicacion {
	private ConcurrentHashMap<String, SocketSessionObject> socketSessions = new ConcurrentHashMap<String, SocketSessionObject>();
	private ConcurrentHashMap<String, SocketSessionObject> socketSessionsApp = new ConcurrentHashMap<String, SocketSessionObject>();

	public BeanVariablesAplicacion() {
		super();
	}

	public ConcurrentHashMap<String, SocketSessionObject> getSocketSessions() {
		return socketSessions;
	}

	public void setSocketSessions(ConcurrentHashMap<String, SocketSessionObject> socketSessions) {
		this.socketSessions = socketSessions;
	}

	public ConcurrentHashMap<String, SocketSessionObject> getSocketSessionsApp() {
		return socketSessionsApp;
	}

	public void setSocketSessionsApp(ConcurrentHashMap<String, SocketSessionObject> socketSessionsApp) {
		this.socketSessionsApp = socketSessionsApp;
	}
}
