package com.elastic.search.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = ElasticSearchUtil.class)
@TestPropertySource(locations="classpath:application.yml")
class ElasticSearchUtilTest {

	@Value("${elasticSearch.host}")
	private String host;
	
	@Test
	public void 엘라스틱서치_접속_테스트() {
		boolean expRes = true;
		
		try {
			String apiUrl = host+"/_search";
			String queryStr = "{\"query\": {\"match_all\": {}}}";
			JSONObject jsonObject = new JSONObject(queryStr);
			
			String str = ElasticSearchUtil.get(apiUrl, jsonObject.toString(), true);
			System.out.println(str);
			
			boolean res = str != null ? true : false;
			assertEquals(expRes, res);
		} catch(JSONException e) {
			System.out.println(e.toString());
		} catch(MalformedURLException e) {
			System.out.println(e.toString());
		} catch(IOException e) {
			System.out.println(e.toString());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
