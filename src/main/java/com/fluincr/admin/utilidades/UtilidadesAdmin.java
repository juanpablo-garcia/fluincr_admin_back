package com.fluincr.admin.utilidades;

import java.security.PrivateKey;
import java.util.Calendar;
import java.util.Set;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.security.Keys;

public class UtilidadesAdmin {
	public static String generateJWTToken(String mail, String username, Long idUsuario, Set<String> grupos) {
		Calendar c = Calendar.getInstance();
		// Calendar c1 = Calendar.getInstance();
//		c1.add(Calendar.HOUR_OF_DAY, 24);
		String s = Jwts.builder().setIssuer("tmsolutions").setSubject("authland").claim("token", mail)
				.claim("username", username).claim("id_usuario", String.valueOf(idUsuario)).claim("groups", grupos)
				// Fri Jun 24 2016 15:33:42 GMT-0400 (EDT)
				.setIssuedAt(c.getTime())
				// Sat Jun 24 2116 15:33:42 GMT-0400 (EDT)
				.setExpiration(null)
				.signWith(SignatureAlgorithm.HS256,
						TextCodec.BASE64.decode("7AfJJNfJdfMcH5c+4dznjotmLnR+qyagJmGdFpiVXCswCutRmj+fsloWSlOK0yHT"))
				.compact();
		return s;

	}
}
