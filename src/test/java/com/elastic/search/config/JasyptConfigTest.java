package com.elastic.search.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

class JasyptConfigTest {

	@Test
	public void jasypt_테스트() {
		String url = "jdbc:postgresql://localhost:5432/test";
		String username = "user";
		String password = "password";

		System.out.println(jasyptEncoding(url));
		System.out.println(jasyptEncoding(username));
		System.out.println(jasyptEncoding(password));
	}

	public String jasyptEncoding(String value) {
		String key = System.getenv("JASYPT_KEY");
		StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
		pbeEnc.setAlgorithm("PBEWithMD5AndDES");
		pbeEnc.setPassword(key);
		return pbeEnc.encrypt(value);
	}
}
