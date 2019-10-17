package com.fosung.framework.common.secure.password.support;

import org.springframework.security.crypto.keygen.BytesKeyGenerator;

import java.util.Random;

public class AppRandomBytesKeyGenerator implements BytesKeyGenerator {

	private static final int DEFAULT_KEY_LENGTH = 16 ;

	private final Random random;

	private final int keyLength;

	/**
	 * Creates a secure random key generator using the defaults.
	 */
	public AppRandomBytesKeyGenerator() {
		this(DEFAULT_KEY_LENGTH);
	}

	public AppRandomBytesKeyGenerator(int keyLength) {
		this.random = new Random( System.currentTimeMillis() );
		this.keyLength = keyLength;
	}

	@Override
	public int getKeyLength() {
		return keyLength;
	}

	@Override
	public byte[] generateKey() {
		byte[] bytes = new byte[keyLength];
		random.nextBytes(bytes);
		return bytes;
	}

}
