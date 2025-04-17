package kr.co.pplus.store.util.secure;

import java.io.Serializable;

/**
 * @description : 
 * @author : user
 * @date  : 2013. 4. 26. 
 * @email : kwjun1304@kico.co.kr
 */
public class Token implements Serializable {

	/**  */
	private static final long serialVersionUID = -3485861251243154264L;

	/**
	 * 
	 */
	public Token() {

	}

	/**
	 * @param keyLength
	 * @param iterationCount
	 * @param algo
	 * @param uuid
	 */
	public Token(int keyLength, int iterationCount, String algo, String uuid) {
		super();
		this.keyLength = keyLength;
		this.iterationCount = iterationCount;
		this.algo = algo;
		this.uuid = uuid;
	}

	private int keyLength;
	
	private int iterationCount;
	
	private String algo;
	
	private String uuid;

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
	 * @return the iterationCount
	 */
	public int getIterationCount() {
		return iterationCount;
	}

	/**
	 * @param iterationCount the iterationCount to set
	 */
	public void setIterationCount(int iterationCount) {
		this.iterationCount = iterationCount;
	}

	/**
	 * @return the algo
	 */
	public String getAlgo() {
		return algo;
	}

	/**
	 * @param algo the algo to set
	 */
	public void setAlgo(String algo) {
		this.algo = algo;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @date : 2013. 4. 26.
	 * @description : 
	 * @author user
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Token [keyLength=" + keyLength 
		       + ", iterationCount=" + iterationCount
		       + ", algo=" + algo
		       + ", uuid=" + uuid + "]";
	}

}
