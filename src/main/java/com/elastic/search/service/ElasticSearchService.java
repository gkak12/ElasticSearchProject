package com.elastic.search.service;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpServletRequest;

import com.elastic.search.common.ElasticSearchException;

public interface ElasticSearchService {

	/**
	 * 엘라스틱서치 첨부파일 검색
	 * @param keyword
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ElasticSearchException
	 * @throws Exception
	 */
	public String selectFile(String keyword) throws MalformedURLException, IOException, ElasticSearchException, Exception;

	/**
	 * 엘라스틱서치 첨부파일 색인
	 * @param request
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ElasticSearchException
	 * @throws Exception
	 */
	public void insertFile(HttpServletRequest request) throws MalformedURLException, IOException, ElasticSearchException, Exception;
	
	/**
	 * 엘라스틱서치 스냅샷 복원
	 * @param snapshotName
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ElasticSearchException
	 * @throws Exception
	 */
	public void restoreSnapshot(String snapshotName) throws MalformedURLException, IOException, ElasticSearchException, Exception;
}
