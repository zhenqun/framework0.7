package com.fosung.framework.common.secure.password;

import com.fosung.framework.common.secure.password.support.AppRandomBytesKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.util.EncodingUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 密码加密机制
 * @Author : liupeng
 * @Date : 2018-12-15
 * @Modified By
 */
@Slf4j
public class AppPasswordEncoderDefault implements PasswordEncoder {
    private static final int DEFAULT_ITERATIONS = 512 ;

    private final Digester digester;

    private final byte[] secret;

    private final BytesKeyGenerator saltGenerator;

    public AppPasswordEncoderDefault() {
        this("");
    }

    public AppPasswordEncoderDefault(CharSequence secret) {
        this("SHA-256", secret);
    }

    private AppPasswordEncoderDefault(String algorithm, CharSequence secret) {
        this.digester = new Digester(algorithm, DEFAULT_ITERATIONS) ;
        this.secret = Utf8.encode(secret) ;
        // 随机的key生成器
        this.saltGenerator = new AppRandomBytesKeyGenerator() ;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return encode(rawPassword, saltGenerator.generateKey());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] digested = decode(encodedPassword);
        // 获取加密字符中的盐
        byte[] salt = EncodingUtils.subArray(digested, 0, saltGenerator.getKeyLength());

        return matches(digested, digest(rawPassword, salt));
    }

    private String encode(CharSequence rawPassword, byte[] salt) {
        byte[] digest = digest(rawPassword, salt);

        // 保存前使用base64加密
        digest = Base64.getEncoder().encode( digest ) ;

        return new String( digest );
    }

    private byte[] digest(CharSequence rawPassword, byte[] salt) {
        byte[] digest = digester.digest(EncodingUtils.concatenate(salt, secret,
                Utf8.encode(rawPassword))) ;
        return EncodingUtils.concatenate(salt, digest);
    }

    private byte[] decode(CharSequence encodedPassword) {
        if( encodedPassword==null || encodedPassword.toString().getBytes().length % 2 ==1 ){
            log.error("base64 decode内容二进制长度为: {}" , encodedPassword.toString().getBytes().length );
        }
        return Base64.getDecoder().decode( encodedPassword.toString() ) ;
    }

    /**
     * Constant time comparison to prevent against timing attacks.
     */
    private boolean matches(byte[] expected, byte[] actual) {
        if (expected.length != actual.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < expected.length; i++) {
            result |= expected[i] ^ actual[i];
        }
        return result == 0;
    }

    class Digester {

        private final String algorithm;

        private int iterations;

        public Digester(String algorithm, int iterations) {
            createDigest(algorithm);
            this.algorithm = algorithm;
            setIterations(iterations);
        }

        public byte[] digest(byte[] value) {
            MessageDigest messageDigest = createDigest(algorithm);
            for (int i = 0; i < iterations; i++) {
                value = messageDigest.digest(value);
            }
            return value;
        }

        final void setIterations(int iterations) {
            if(iterations <= 0) {
                throw new IllegalArgumentException("Iterations value must be greater than zero");
            }
            this.iterations = iterations;
        }

        private MessageDigest createDigest(String algorithm) {
            try {
                return MessageDigest.getInstance(algorithm);
            }
            catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("No such hashing algorithm", e);
            }
        }
    }

    public static void main(String[] args) {

        PasswordEncoder passwordEncoder = new AppPasswordEncoderDefault() ;

//        for (int i=0;i<10000;i++){
//            long start = System.currentTimeMillis() ;
//            passwordEncoder.encode(RandomStringUtils.random(10)) ;
//            System.out.println( i+"-----"+ ( System.currentTimeMillis() - start )/1000.0  );
//        }
//        String password = "lp769812xhy" ;
//
//        String encoded = passwordEncoder.encode( password ) ;
//
//        System.out.println( encoded +"  长度： "+encoded.length() ) ;
//
//        System.out.println( passwordEncoder.matches( password , encoded ) );

//        LgaQealLoqu8G0BAUF2lbdpQIyK0CHVxKg5qByiy2d/5RC98m9tdDZLmfkQym012

        String passwordEncoded = passwordEncoder.encode("123qwe") ;

//        passwordEncoded = "vvheSdOXH1Sx4WtIXg0x6DEdz+ZqkwZdWuaNm/TE/pqjrho4hx1B6ww+a5r59RDT" ;

        System.out.println( passwordEncoded );

        try {
            Thread.sleep(1000) ;
        } catch (InterruptedException e) {
            e.printStackTrace() ;
        }

        PasswordEncoder passwordEncoder2 = new AppPasswordEncoderDefault() ;

        System.out.println( passwordEncoder2.matches( "123qwe" , passwordEncoded ) );

    }

}
