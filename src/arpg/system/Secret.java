package arpg.system;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Secret {

	private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final String SECRET_KEY = "AiriJonouchiKey1";
	private static final String VECTER  = "AiriJonouchiIV01";
	private static final String CHARSET = "UTF-8";

	private IvParameterSpec iv;
	private SecretKeySpec key;

	public Secret() {

		try {			
			iv = new IvParameterSpec(VECTER.getBytes(CHARSET));
			key = new SecretKeySpec(SECRET_KEY.getBytes(CHARSET) , "AES");
		} 
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String encrypt(String text) {
	
		try {
			Cipher encryptCipher = Cipher.getInstance(ALGORITHM);
			encryptCipher.init(Cipher.ENCRYPT_MODE, this.key, this.iv);
			byte[] byteText = encryptCipher.doFinal(text.getBytes(CHARSET));
			
			return new String(Base64.getEncoder().encode(byteText), CHARSET);
		} 
		catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
	 	catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new RuntimeException(e);
		} 
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public String decrypt(String encryptText) {

		try{
			Cipher decryptCipher = Cipher.getInstance(ALGORITHM);
			decryptCipher.init(Cipher.DECRYPT_MODE, this.key, this.iv);
			byte[] byteText = Base64.getDecoder().decode(encryptText);
			
			return new String(decryptCipher.doFinal(byteText), CHARSET);
		}	
		catch(NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
		catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new RuntimeException(e);
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}	
	}
}
