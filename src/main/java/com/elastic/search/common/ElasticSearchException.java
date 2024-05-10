package com.elastic.search.common;

public class ElasticSearchException extends Exception{

	private static final long serialVersionUID = 1L;

	public ElasticSearchException() {
		super();
	}
	
	public ElasticSearchException(String msg) {
		super(msg);
	}
}
