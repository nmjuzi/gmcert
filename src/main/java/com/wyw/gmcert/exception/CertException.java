package com.wyw.gmcert.exception;

/**
 * @author 王亚雯
 * 2020年9月9日 上午9:47:00
 */
public class CertException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8250692513033055564L;

	public CertException(String message, Throwable cause) {
		super(message, cause);
	}

	public CertException(String message) {
		super(message);
	}
	
	
}
