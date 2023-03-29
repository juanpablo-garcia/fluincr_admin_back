package com.fluincr.admin.client;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.fluincr.admin.utilidades.SocketSessionObject;

import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/socket/{appName}/{usuario}")
@Singleton
@Lock(LockType.READ)
public class ChatServer {

	@Inject
	private BeanVariablesAplicacion beanVariablesAplicacion;

	@OnOpen
	public void onOpen(Session session, @PathParam("usuario") String usuario, @PathParam("appName") String appName) {

		if (usuario != null) {
			crearSession(session, usuario, appName);
		}
	}

	private void crearSession(Session session, String usuario, String appName) {
		if (appName.equals("administrativa")) {
			beanVariablesAplicacion.getSocketSessions().put(usuario, new SocketSessionObject(usuario, session));
		} else {
			beanVariablesAplicacion.getSocketSessionsApp().put(usuario, new SocketSessionObject(usuario, session));
		}
	}

	@OnClose
	public void OnClose(Session session, @PathParam("usuario") String usuario, @PathParam("appName") String appName) {
		String key = usuario;
		if (appName.equals("administrativa")) {
			beanVariablesAplicacion.getSocketSessions().remove(key);
		} else {
			beanVariablesAplicacion.getSocketSessionsApp().remove(key);
		}
	}

	@OnMessage
	public void onMessage(String message, Session session, @PathParam("usuario") String usuario,
			@PathParam("appName") String appName) throws IOException {
		//
		String key = usuario;
		if (message.equals("heartbeatLand")) {
			try {
				if (appName.equals("administrativa")) {
					if (beanVariablesAplicacion.getSocketSessions().containsKey(key)) {
						session.getBasicRemote().sendText("heartbeatLand");
					}
				} else {
					if (beanVariablesAplicacion.getSocketSessionsApp().containsKey(key)) {
						session.getBasicRemote().sendText("heartbeatLand");
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@OnError
	public void onError(Session sess, Throwable e) {
		/* normal handling... */
		try {
			// Likely EOF (i.e. user killed session)
			// so just Close the input stream as instructed
			sess.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {

		}
	}

	public void pingMessage() {
		sendAll(beanVariablesAplicacion.getSocketSessions());
		sendAll(beanVariablesAplicacion.getSocketSessionsApp());

	}

	private void sendAll(ConcurrentHashMap<String, SocketSessionObject> concurrentHashMap) {

		for (Iterator iterator = concurrentHashMap.keySet().iterator(); iterator.hasNext();) {
			String type = (String) iterator.next();
			SocketSessionObject obSession = concurrentHashMap.get(type);
			Session session = obSession.getSession();
			if (session.isOpen()) {
				try {
					session.getBasicRemote().sendText("PING");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				concurrentHashMap.remove(type);
			}
		}

	}

}
