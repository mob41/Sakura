package com.github.mob41.sakura.hash;

import java.security.Key;
import java.security.MessageDigest;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AES_SHA512 {
	
	private static final String ALGO1 = "AES";
	private static final String ALGO2 = "SHA-512";
    private static byte[] keyValue = new byte[16];
    
    public static String getRandomSalt(){
    	Random rand = new Random();
    	byte[] key = new byte[16];
    	rand.nextBytes(key);
    	return Base64.encodeBase64String(key);
    }
    
    private static byte[] decode(String buffer) throws Exception {
    	return Base64.decodeBase64(buffer);
    }
    
    public static String encrypt(String Data, String salt) throws Exception {
    	//First layer
    	keyValue = decode(salt);
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO1);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        
        //Second Layer (Unable to decrypt)
        MessageDigest digest = MessageDigest.getInstance(ALGO2);
        byte[] output = digest.digest(encVal);
        
        //Encode
        String encryptedValue = Base64.encodeBase64String(output);
        return encryptedValue;
    }
    
    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO1);
        return key;
    }
}
