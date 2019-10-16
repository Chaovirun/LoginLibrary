package com.virun.loginlibrary.secure;

public class XorCrypto {
	
	private static byte[] XorEnc(byte[] b){
		int l = b.length;
	    for( int i = 0; i < l; i++) {
	    	b[i] = ( byte ) (b[i] ^ 0x9f);
	    	b[i] = ( byte ) ((b[i] & 0xff) >> 4 | (b[i] & 0x0f) << 4);
	    }
	    return b;
	}
	
	private static byte[] XorDec(byte[] b){
		int l = b.length;
	    for(int i = 0; i < l; i++) {
	       b[i] = ( byte ) ((b[i] & 0xff) >> 4 | (b[i] & 0x0f) << 4);
	       b[i] = ( byte ) (b[i] ^ 0x9f);
	    }
	    return b;
	}
	
	private static String byteArrayToHex(byte[] a) {
	    if (a == null || a.length == 0) {
	        return null;
	    }		
	    StringBuilder sb = new StringBuilder();
	    for(final byte b: a)
	        sb.append(String.format("%02x", b&0xff).toUpperCase());
	    return sb.toString();
	}
	
	private static byte[] hexToByteArray(String h) {
	    if (h == null || h.length() == 0) {
	        return null;
	    }
	    byte[] b = new byte[h.length() / 2];
	    for (int i = 0; i < b.length; i++) {
	        b[i] = (byte) Integer.parseInt(h.substring(2 * i, 2 * i + 2), 16);
	    }
	    return b;
	}
	
	public static String encoding(String t){
		if(t == null || t.length() == 0){
			return "";
		}
		return byteArrayToHex(XorEnc(t.getBytes()));
	}
	
	public static String decoding(String t){
		if(t == null || t.length() == 0){
			return "";
		}
		return new String(XorDec(hexToByteArray(t)));
	}

}
