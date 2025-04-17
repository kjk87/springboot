/**
 * 
 */
package kr.co.pplus.store.util.secure;

import java.io.*;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import kr.co.pplus.store.api.jpa.converter.JpaConverterEncryptJson;
import kr.co.pplus.store.api.jpa.repository.PrivateKeyRepository;
import kr.co.pplus.store.api.util.AppUtil;
import kr.co.pplus.store.util.RedisUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author user
 *
 */
public class CryptoUtil {

	private static final Logger logger = LoggerFactory.getLogger(JpaConverterEncryptJson.class);

	@Deprecated
	public static final int RSA_KEY_LENGTH_512 = 512;
	public static final int RSA_KEY_LENGTH_1024 = 1024;
	public static final String PRIKEY_SAVING_DIR = "/app/privateKeys";
	public static final String PRIKEY_SAVING_EXT = "pse";
	public static final String TOKEN_SAVING_EXT = "tkn";

	/**
	 * 기본 생성자
	 */
	public CryptoUtil() {

	}

    /**
     * UUID를 생성한다.
     * 
     * @return
     */
	public static String generateUuid() {
		return UUID.randomUUID().toString();
	}
	
    /**
     * RSA 공개키와 개인키를 생성한 후 개인키를 파일로 저장한다.
     * 
     * @param keyLength
     * @param uuid
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws FileNotFoundException
     * @throws IOException
     */
	public static RsaKeyInfo generateRsaKey(int keyLength, String uuid, String yyyymm) throws NoSuchAlgorithmException, InvalidKeySpecException, FileNotFoundException, IOException, Exception {


		SecureRandom sr = new SecureRandom();
        sr.nextInt();
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(keyLength, sr);

//		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
//		kpg.initialize(keyLength);
		
		KeyPair kp = kpg.genKeyPair();
		KeyFactory kf = KeyFactory.getInstance("RSA");
		
		// RSA에서 PublicKey의 byte[]은 modulus와 exponent의 조합으로 이루어진 ASN.1 포맷(publicKey.getEncoded())
		PublicKey publicKey = kp.getPublic(); // 공개키(사용자에게 발급)
		PrivateKey privateKey = kp.getPrivate(); // 개인키(서버에 저장)
		
		RSAPublicKeySpec rpks = (RSAPublicKeySpec)kf.getKeySpec(publicKey, RSAPublicKeySpec.class);
		String publicKeyModulus = rpks.getModulus().toString(16); // 계수, 16진수, BigInteger의 바이트 배열에서 2의 보수를 제거한 후 16진수 문자열을 반환
		String publicKeyExponent = rpks.getPublicExponent().toString(16); // 공개 지수, 16진수, 여기서는 10001로 항상 동일

		return new RsaKeyInfo(keyLength, publicKeyModulus, publicKeyExponent);

	}


	public static PrivateKey loadPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, FileNotFoundException, IOException, Exception {

		PrivateKey privateKey = null ;
		try {
			try {
				privateKey = RedisUtil.getInstance().getOpsHash("pplus-private-key", "RSA");
			}
			catch(Exception e){

			}
			if( privateKey == null ) {
				InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("pplus.pse") ;

				ObjectInputStream ois = new ObjectInputStream(is);
				privateKey = (PrivateKey)ois.readObject();
				ois.close();
				is.close() ;
				RedisUtil.getInstance().putOpsHash("pplus-private-key", "RSA", privateKey);
			}
		}
		catch(Exception e){
			logger.error("PrivateKey Error : " + AppUtil.excetionToString(e));
		}
		finally {
			return privateKey ;
		}
	}

	/**
	 * RSA 개인키를 파일로 저장한다.
	 * 
	 * @param uuid
	 * @param privateKey
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void saveRsaPrivateKey(String uuid, String yyyymm, PrivateKey privateKey) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRIKEY_SAVING_DIR + File.separator + yyyymm + File.separator  + uuid + "." + PRIKEY_SAVING_EXT));
        oos.writeObject(privateKey);
        oos.close();

//        kr.co.pplus.store.api.jpa.model.PrivateKey pKey = new  kr.co.pplus.store.api.jpa.model.PrivateKey() ;
//		ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
//		ObjectOutputStream oos = new ObjectOutputStream(bos) ;
//		oos.writeObject(privateKey) ;
//
//        pKey.setUuid(uuid);
//        pKey.setPrivateKey(bos.toByteArray());
//        pKey.setRegDatetime(AppUtil.localDatetimeNowString());
//
//        privateKeyRepository.save(pKey) ;
//		oos.close() ;
//		bos.close() ;
	}
	/**
	 * Token 객체를 파일로 저장한다.
	 * 
	 * @param token
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void saveToken(Token token) throws FileNotFoundException, IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PRIKEY_SAVING_DIR + File.separator + token.getUuid() + "." + TOKEN_SAVING_EXT));
        oos.writeObject(token);
        oos.close();
	}

	/**
	 * 파일로 저장한 RSA 개인키를 읽어 반환한다.
	 * 별도 개인키 파일 삭제 스케줄링 기능 필요
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey readRsaPrivateKey(String uuid, String yyyymm) throws Exception {
		long cdt = new Date().getTime();
		
		File f = null;
		PrivateKey privateKey = null;
		
		try {
			f = new File(PRIKEY_SAVING_DIR + File.separator + yyyymm + File.separator + uuid + "." + PRIKEY_SAVING_EXT);
			long fcdt = f.lastModified();

			if ((cdt - fcdt) > 10000) { // 생성된지 10초가 지난 개인키라면 파기후 예외 처리!
				throw new Exception("* PrivateKey is timeover!");
			}
			
	        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
	        privateKey = (PrivateKey)ois.readObject();
	        ois.close();
		}
		catch(Exception e) {
			throw e;
		}
		finally {
			if (f != null && f.exists()) {
				f.delete();
			}
		}

        return privateKey;
	}
	
	/**
	 * 파일로 저장한 Token를 읽어 반환한다.
	 * 별도 Token 파일 삭제 스케줄링 기능 필요
	 * 
	 * @param uuid
	 * @return
	 * @throws Exception
	 */
	public static Token readToken(String uuid) throws Exception {
		long cdt = new Date().getTime();
		
		File f = null;
		Token token = null;
		
		try {
			f = new File(PRIKEY_SAVING_DIR + File.separator + uuid + "." + TOKEN_SAVING_EXT);
			long fcdt = f.lastModified();

			if ((cdt - fcdt) > 10000) { // 생성된지 10초가 지난 token라면 파기후 예외 처리!
				throw new Exception("* Token is timeover!");
			}
			
	        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
	        token = (Token)ois.readObject();
	        ois.close();
		}
		catch(Exception e) {
			throw e;
		}
		finally {
			if (f != null && f.exists()) {
				f.delete();
			}
		}

        return token;
	}

	/**
	 * 16진수 문자열로 인코딩된 RSA로 암호화된 문자열을 복호화한다.
	 * 
	 * @param privateKey PrivateKey
	 * @param encStr 16진수 문자열로 인코딩된 암호화된 문자열
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws DecoderException 
	 */
    public static String decryptWithRSA(PrivateKey privateKey, String encStr) throws NoSuchAlgorithmException, NoSuchPaddingException, FileNotFoundException, IOException, ClassNotFoundException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, DecoderException {
    	Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

		byte[] decryptedBytes = cipher.doFinal(encStr.getBytes());
		String decStr = new String(decryptedBytes, "UTF-8");

		return decStr;
    }

	/**
	 * Base64로 인코딩된 RSA로 암호화된 문자열을 복호화한다.
	 * 
	 * @param uuid UUID
	 * @param encStr Base64로 인코딩된 암호화된 문자열
	 * @return
	 * @throws Exception
	 */
    public static String decryptWithRSA(String uuid, String yyyymm, String encStr) throws Exception {
    	Cipher cipher = Cipher.getInstance("RSA");
        PrivateKey privateKey = readRsaPrivateKey(uuid, yyyymm);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

		byte[] decryptedBytes = cipher.doFinal(Base64.decodeBase64(encStr.getBytes()));
		String decStr = new String(decryptedBytes, "UTF-8");
		
		return decStr;
    }

    /*
	public static PrivateKey getPrivateKey(String uuid) throws Exception {

    	kr.co.pplus.store.api.jpa.model.PrivateKey pKey = privateKeyRepository.findByUuid(uuid) ;
		ByteArrayInputStream bis = new ByteArrayInputStream(pKey.getPrivateKey()) ;
    	ObjectInputStream ois = new ObjectInputStream(bis) ;
    	PrivateKey privateKey = (PrivateKey)ois.readObject() ;
    	ois.close() ;
    	bis.close() ;
    	return privateKey ;
	}
	*/

    /**
     * 문자열을 RSA로 암호화한 후 BASE64 인코딩된 문자열을 반환한다.
     * 
     * @param modulus 계수의 16진수 문자열
     * @param exponent 공개 지수의 16진수 문자열
     * @param plainText 암호화할 평문
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException 
     * @throws UnsupportedEncodingException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     */
    public static String encryptWithRSA(String modulus, String exponent, String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {




    	KeyFactory kf = KeyFactory.getInstance("RSA");
    	PublicKey publicKey = kf.generatePublic(new RSAPublicKeySpec(new BigInteger(modulus, 16), new BigInteger(exponent, 16)));

    	Cipher cipher = Cipher.getInstance("RSA");
    	cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    	byte[] bytesOfPlainText = new String(plainText).getBytes("UTF-8");
    	byte[] encryptedBytes = cipher.doFinal(bytesOfPlainText);

    	return new String(Base64.encodeBase64(encryptedBytes));
    }

	public static String encryptWithRSA(PrivateKey privateKey, String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {


		RSAPrivateCrtKey privk = (RSAPrivateCrtKey)privateKey;

		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privk.getModulus(), privk.getPublicExponent());

		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey publicKey = kf.generatePublic(publicKeySpec);

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] bytesOfPlainText = new String(plainText).getBytes("UTF-8");
		byte[] encryptedBytes = cipher.doFinal(bytesOfPlainText);

		//return new String(Base64.encodeBase64(encryptedBytes));
		return new String(encryptedBytes);
	}



    /**
     * 비밀키를 생성하는 데 쓰일 salt(32bytes)를 생성한다.
     * 
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String generateSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salts = new byte[32];
        sr.nextBytes(salts);
        return new String(Hex.encodeHex(salts));
    }

    /**
     * 암호화에 쓰일 iv(16bytes)를 생성한다.
     * 
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String generateIv() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] ivs = new byte[16];
        sr.nextBytes(ivs);
        return new String(Hex.encodeHex(ivs));
    }
    
    /**
     * PBKDF2 방식의 비밀키를 생성한다.
     * 
     * @param iterationCount
     * @param keyLength
     * @param password
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws DecoderException 
     * @throws InvalidKeySpecException 
     */
    public static SecretKey generateSecretKey(int iterationCount, int keyLength, String password, String salt) throws NoSuchAlgorithmException, DecoderException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), Hex.decodeHex(salt.toCharArray()), iterationCount, keyLength);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return secretKey;
    }

    /**
     * 암호화된 평문을 복호화한다.
     * 
     * @param secretKey
     * @param algo
     * @param iv(Hex)
     * @param encStr(Base64)
     * @return
     * @throws IllegalArgumentException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws DecoderException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     */
    public static String decrypt(SecretKey secretKey, String algo, String iv, String encStr) throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, DecoderException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
    	SecretKey sk = new SecretKeySpec(secretKey.getEncoded(), algo);
        Cipher cipher = Cipher.getInstance(algo + "/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sk, new IvParameterSpec(Hex.decodeHex(iv.toCharArray())));
        byte[] decryptedBytes = cipher.doFinal(Base64.decodeBase64(encStr.getBytes()));
        return new String(decryptedBytes, "UTF-8");
    }

    /**
     * 
     * @param secretKey
     * @param algo
     * @param plainText
     * @return
     * @throws IllegalArgumentException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidParameterSpecException
     * @throws UnsupportedEncodingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static String[] encrypt(SecretKey secretKey, String algo, String plainText) throws IllegalArgumentException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
    	SecretKey sk = new SecretKeySpec(secretKey.getEncoded(), algo);
        Cipher c = Cipher.getInstance(algo + "/CBC/PKCS5Padding"); // AES/CBC/PKCS7Padding
        c.init(Cipher.ENCRYPT_MODE, sk);
        AlgorithmParameters ap = c.getParameters();
        byte[] iv = ap.getParameterSpec(IvParameterSpec.class).getIV();
        String ivStr = new String(Hex.encodeHex(iv));
        byte[] bytesOfPlainText = new String(plainText).getBytes("UTF-8");
        byte[] encryptedBytes = c.doFinal(bytesOfPlainText);
        String encStr = new String(Base64.encodeBase64(encryptedBytes));
        
        String[] temps = new String[2];
        temps[0] = ivStr;
        temps[1] = encStr;
    	return temps;
    }



	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String uuid = generateUuid();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM") ;
		String yyyymm = sdf.format(new Date()) ;
				RsaKeyInfo rki = generateRsaKey(RSA_KEY_LENGTH_1024, uuid, yyyymm);
		System.out.println(rki.toString());

		String plainText = "안녕하세요? 전강욱입니다.";
		String encStr = encryptWithRSA(rki.getModulus(), rki.getExponent(), plainText);
		System.out.println("* RSA 암호화로 암호화된 평문:   " + encStr);

		String decStr = decryptWithRSA(uuid, yyyymm, encStr);
		System.out.println("* RSA 복호화로 복호화된 암호문: " + decStr);


		
        String salt = generateSalt();
        SecretKey secretKey = generateSecretKey(1024, 128, "!@#s1982761", salt);
        System.out.println("* salt:        " + salt);
        System.out.println("* secretKey:   " + new String(Hex.encodeHex(secretKey.getEncoded())));



        String[] temps = encrypt(secretKey, "AES", plainText);
        System.out.println("* iv(Hex):        " + temps[0]);
        System.out.println("* encStr(Base64): " + temps[1]);

        plainText = decrypt(secretKey, "AES", temps[0], temps[1]);
        System.out.println("* plainText:      " + plainText);
        
        
        
//        * salt:       408ae3626b6bfee3d9508f10e544fd58ba7355b1bbbec4822fc48dc39df31fba
//        * key128Bits: 90cd606577f7828836e7544e64b374b5
//        * iv:         2ac9975c0f5e0abce4e158632f0222f8
//        FLUQ8t493fXHe0TdVyZTLefw2xdRzaveskH83wt9HbWFQ+T6wFE7ryvp7OA+9eB2 
        secretKey = generateSecretKey(1024, 128, "!@#s1982761", "408ae3626b6bfee3d9508f10e544fd58ba7355b1bbbec4822fc48dc39df31fba");
        System.out.println("* secretKey:   " + new String(Hex.encodeHex(secretKey.getEncoded())));
        
        plainText = decrypt(secretKey, "AES", "2ac9975c0f5e0abce4e158632f0222f8", "FLUQ8t493fXHe0TdVyZTLefw2xdRzaveskH83wt9HbWFQ+T6wFE7ryvp7OA+9eB2");
        System.out.println("* plainText:      " + plainText);
	}

}
