package com.portal.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class SecurityPortal {
	private static final String ALGORITHM = "HmacSHA1";
	private static final String KEY = "AE09F72B007CAAB5";

	public static String getHmacSHA1(String texto) {
		try {
			Mac hmacSHA1 = Mac.getInstance(ALGORITHM);
			SecretKeySpec hmacSHA1Key = new SecretKeySpec(
					BaseEncoder.decode(KEY), ALGORITHM);
			hmacSHA1.init(hmacSHA1Key);
			return Base64.encodeBase64String(
					hmacSHA1.doFinal(texto.getBytes("UTF-16LE"))).trim();
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
