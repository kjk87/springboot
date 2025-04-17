/**
 * 
 */
package kr.co.pplus.store.util.secure;

import java.io.Serializable;

/**
 * @author user
 *
 */
public class RsaKeyInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2880006845941080961L;

	/**
	 * 기본 생성자
	 */
	public RsaKeyInfo() {

	}

	/**
	 * @param keyLength
	 * @param modulus
	 * @param exponent
	 */
	public RsaKeyInfo(int keyLength, String modulus, String exponent) {
		super();
		this.keyLength = keyLength;
		this.modulus = modulus;
		this.exponent = exponent;
	}

	/**
	 * RSA 키 길이
	 */
	private int keyLength;
	/**
	 * 계수(16진수 문자열)
	 */
	private String modulus;
	/**
	 * 공개 지수(16진수 문자열)
	 */
	private String exponent;

	/**
	 * @return the keyLength
	 */
	public int getKeyLength() {
		return keyLength;
	}
	/**
	 * @param keyLength the keyLength to set
	 */
	public void setKeyLength(int keyLength) {
		this.keyLength = keyLength;
	}
	/**
	 * @return the modulus
	 */
	public String getModulus() {
		return modulus;
	}
	/**
	 * @param modulus the modulus to set
	 */
	public void setModulus(String modulus) {
		this.modulus = modulus;
	}
	/**
	 * @return the exponent
	 */
	public String getExponent() {
		return exponent;
	}
	/**
	 * @param exponent the exponent to set
	 */
	public void setExponent(String exponent) {
		this.exponent = exponent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RsaKeyInfo [keyLength=" + keyLength 
               + ", modulus=" + modulus
               + ", exponent=" + exponent
               + "]";
	}

}
