package com.elastic.search.service;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import com.elastic.search.common.ElasticSearchException;

public interface ElasticSearchService {

	public void insertFile(HttpServletRequest request) throws MalformedURLException, IOException, ElasticSearchException, Exception;
}
