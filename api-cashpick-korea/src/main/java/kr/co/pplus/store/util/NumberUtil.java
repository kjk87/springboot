package kr.co.pplus.store.util;

public class NumberUtil {
	  private final static char[] lowerHexArray = "0123456789abcdef".toCharArray();

	  private final static char[] upperHexArray = "0123456789ABCDEF".toCharArray();

	  public static void copyIntToByteArray(byte[] bytes, int offset, int d) {
	    for (int i = 0; i < 4; i++) {
	      bytes[offset + i] = (byte) (d >> (4 - i - 1 << 3));
	    }
	  }
	  
	  public static void copyLongToByteArray(byte[] bytes, int offset, long l) {
	    for (int i = 0; i < 8; i++) {
	      bytes[offset + i] = (byte) (l >> (8 - i - 1 << 3));
	    }
	  }
	  
	  public static String bytesToHexString(byte[] bytes, boolean upper) {
	    final char[] hexArray = upper ? upperHexArray : lowerHexArray;
	    
	    char[] hexChars = new char[bytes.length * 2];
	    
	    for (int i = 0; i < bytes.length; i++) {
	      int v = bytes[i] & 0xFF;
	      hexChars[i * 2] = hexArray[v >>> 4];
	      hexChars[i * 2 + 1] = hexArray[v & 0x0F];
	    }
	    
	    return new String(hexChars);
	  }
	  
	  public static String bytesToHexString(byte[] bytes) {
	    return bytesToHexString(bytes, false);
	  }
	  
	  public static boolean isDigit(String string) {
	    
	    for (char c : string.toCharArray()) {
	      if (Character.isDigit(c) == false)
	        return false;
	    }
	    
	    return true;
	  }
}
