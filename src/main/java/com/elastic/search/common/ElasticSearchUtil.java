package com.elastic.search.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("elasticSearchUtil")
public class ElasticSearchUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchUtil.class);
	
	public String get(String apiUrl, String paramStr, boolean bodyFlag) throws MalformedURLException, IOException, ElasticSearchException {
		String res = null;
		URL url = new URL(apiUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setRequestMethod("GET"); // GET method 설정
		
		conn.addRequestProperty("charset", "UTF-8");	// header 추가
	    conn.addRequestProperty("Content-Type", "application/json; utf-8");
	    conn.addRequestProperty("Accept", "application/json");
		
		conn.setDoOutput(true);
		String errMsg = "엘라스틱서치 조회 실패했습니다. cause: ";

		try (OutputStream os = conn.getOutputStream();) {
		    if(bodyFlag && paramStr != null && !paramStr.isBlank()) {	// http body에 데이터 쓰는 경우
		    	os.write(paramStr.getBytes("UTF-8"));
		    }

		    conn.connect();
		    int statusCode = conn.getResponseCode();

		    if (statusCode >= 400) {
		        throw new ElasticSearchException(errMsg + statusCode + " error.");
		    }

		    try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
		    	BufferedReader br = new BufferedReader(isr);){
		    	StringBuilder sb = new StringBuilder();
		    	String tmp = null;
		    	
		    	while((tmp = br.readLine()) != null) {
		    		sb.append(tmp);
		    	}
		    	
		    	res = sb.toString();
			} catch (Exception e) {
				LOGGER.debug(e.toString());
				throw new ElasticSearchException(errMsg + e.toString());
			}
		} catch (Exception e) {
			LOGGER.debug(e.toString());
			throw new ElasticSearchException(errMsg + e.toString());
		} finally {
			conn.disconnect();
		}
		
		return res;
	}
	
	public void post(String apiUrl, String paramStr) throws MalformedURLException, IOException, ElasticSearchException {
		URL url = new URL(apiUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setRequestMethod("POST"); // POST method 설정
		
		conn.addRequestProperty("charset", "UTF-8");	// header 추가
	    conn.addRequestProperty("Content-Type", "application/json; utf-8");
	    conn.addRequestProperty("Accept", "application/json");
		
		conn.setDoOutput(true);
		String errMsg = "엘라스틱서치 연산 처리 실패했습니다. cause: ";

		try (OutputStream os = conn.getOutputStream();) {
		    if(paramStr != null && !paramStr.isBlank()) {
		    	os.write(paramStr.getBytes("UTF-8"));
		    }
		    
		    conn.connect();
		    int statusCode = conn.getResponseCode();

		    if (statusCode >= 400) {
		        throw new ElasticSearchException(errMsg + statusCode + " error.");
		    }
		} catch (Exception e) {
			LOGGER.debug(e.toString());
			throw new ElasticSearchException(errMsg + e.toString());
		} finally {
			conn.disconnect();
		}
	}
}
