package com.mob41.sakura.servlets.old;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.spec.KeySpec;
import java.util.Random;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.rpi.ha.Conf;

import sun.misc.*;

public class HashKey {
	
	private static final String ALGO = "AES";
    private static byte[] keyValue = new byte[16];
    
    private static final String s = Conf.base641;
    private static final String h = Conf.base642;
    protected static final String y = Conf.base643;
    private static final String v = Conf.base644;
    
    protected static boolean cleartypeSaltAuth(String clearType) throws Exception{
    	String original = decrypt(h, v);
    	return clearType.equals(original);
    }
    
    
    protected static boolean auth(String pwdhash) throws Exception{
    	keyValue = decode(y);
    	String original = decrypt(s, y);
    	String pwd = decrypt(pwdhash, y);
    	return pwd.equals(original);
    }
    
    protected static String getExchangeKey(){
    	Random rand = new Random();
    	byte[] key = new byte[16];
    	rand.nextBytes(key);
    	return Base64.encodeBase64String(key);
    }
    
    protected static String getRandomSalt(){
    	Random rand = new Random();
    	byte[] key = new byte[16];
    	rand.nextBytes(key);
    	return Base64.encodeBase64String(key);
    }
    
    protected static String getRandom(int amount){
    	Random rand = new Random();
    	byte[] key = new byte[amount];
    	rand.nextBytes(key);
    	return Base64.encodeBase64String(key);
    }
    
    
    protected static byte[] decode(String buffer) throws Exception {
    	return Base64.decodeBase64(buffer);
    }
    
    protected static String encrypt(String Data, String salt) throws Exception {
    	keyValue = decode(salt);
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeBase64String(encVal);
        return encryptedValue;
    }

    protected static String decrypt(String encryptedData, String salt) throws Exception {
    	keyValue = decode(salt);
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = Base64.decodeBase64(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }
    
    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
}
	
}
