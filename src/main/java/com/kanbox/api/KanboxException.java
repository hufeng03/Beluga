package com.kanbox.api;

public class KanboxException extends Exception {

	private static final long serialVersionUID = 475022994858770424L;

	/*
	 * 401未授权或授权过期
	 * 404 当前路径不存在 
	 * 406 帐号未激活
	 * 
	 * 10723 路径不存在
	 * 14112 退组时， 用户已解除共享，但是仍然传入退组
	 */
	private int statusCode = -1;

	public KanboxException() {
		super();
	}
	
	public KanboxException(String msg) {
		super(msg);
	}

	public KanboxException(Exception cause) {
		super(cause);
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public KanboxException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
