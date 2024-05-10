package com.elastic.search.common;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ElasticSearchUtil {
	
	public static void post(String apiUrl, String paramStr) throws MalformedURLException, IOException, ElasticSearchException {
		URL url = new URL(apiUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setRequestMethod("POST"); // POST method 설정
		
		conn.addRequestProperty("charset", "UTF-8");	// header 추가
	    conn.addRequestProperty("Content-Type", "application/json; utf-8");
	    conn.addRequestProperty("Accept", "application/json");
		
		conn.setDoOutput(true);

		try (OutputStream os = conn.getOutputStream();) {
		    os.write(paramStr.getBytes("UTF-8"));
		    conn.connect();
		    
		    int statusCode = conn.getResponseCode();

		    if (statusCode >= 400) {
		        throw new ElasticSearchException("엘라스틱서치 연산 처리 실패했습니다. cause: " + statusCode + " error.");
		    }
		} catch (IOException e) {
			throw new ElasticSearchException("엘라스틱서치 접속 실패했습니다. cause: " + e.toString());
		} finally {
			conn.disconnect();
		}
	}
}
