package com.fluincr.admin.utilidades;

import java.util.Calendar;
import java.util.TimeZone;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

public class UtilidadesJWT {

	public static String validateJWT(String jwt) {
		if (jwt != null) {
			try {
				Claims claims = Jwts.parser()
						.setSigningKey(TextCodec.BASE64
								.decode("7AfJJNfJdfMcH5c+4dznjotmLnR+qyagJmGdFpiVXCswCutRmj+fsloWSlOK0yHT"))
						.parseClaimsJws(jwt).getBody();
				if (claims.getExpiration() != null) {
					if (claims.getExpiration().before(Calendar.getInstance(TimeZone.getDefault()).getTime())) {
						return null;
					}
				}
				return (String) claims.get("id_usuario");
			} catch (io.jsonwebtoken.ExpiredJwtException e) {
				return null;
			} catch (Exception e) {
				return null;
			}
		}
		return null;

	}

	public static String generateJWTToken(String mail, Long idUsuario, String userType) {

		Calendar c = Calendar.getInstance();
//		Calendar c1 = Calendar.getInstance();
//		c1.add(Calendar.HOUR_OF_DAY, 24);

		String s = Jwts.builder().setIssuer("sigma").setSubject("auth").claim("token", mail)
				.claim("id_usuario", String.valueOf(idUsuario)).claim("userType", userType).claim("admin", Boolean.TRUE)
				// Fri Jun 24 2016 15:33:42 GMT-0400 (EDT)
				.setIssuedAt(c.getTime())
				// Sat Jun 24 2116 15:33:42 GMT-0400 (EDT)

				.setExpiration(null)
				.signWith(SignatureAlgorithm.HS256, "92b6d1a6b833c00cbe0a052a8eb7262325560257c137c86ad8c978d70f142263")
				.compact();
		return s;

	}

	public static boolean validarToken(String tokenjwt, String token) {
		if (tokenjwt == null || token == null) {
			return false;
		}
		return true;
	}
}
