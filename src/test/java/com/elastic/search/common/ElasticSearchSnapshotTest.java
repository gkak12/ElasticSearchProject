package com.elastic.search.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import javax.annotation.Resource;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {ElasticSearchSnapshot.class, ElasticSearchUtil.class})
@TestPropertySource(locations="classpath:application.yml")
class ElasticSearchSnapshotTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchSnapshotTest.class);
	
	@Value("${elasticSearch.host}")
	private String host;
	
	@Value("${elasticSearch.index}")
	private String index;
	
	@Value("${elasticSearch.snapshot.path}")
	private String path;
	
	@Value("${elasticSearch.snapshot.restore}")
	private String restore;
	
	@Resource(name="elasticSearchUtil")
	private ElasticSearchUtil elasticSearchUtil;
	
	@Test
	public void 엘라스틱_스냅샷_생성_테스트() throws JSONException {
		String uuid = UUID.randomUUID().toString();
		String snapshotName = "snapshot_"+uuid;
		
		StringBuilder sb = new StringBuilder();
		String url = sb.append(host).append(path).append("/").append(snapshotName).toString();
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("indices", index);
		jsonObj.put("ignore_unavailable", true);
		jsonObj.put("include_global_state ", false);
		
		try {
			elasticSearchUtil.put(url, jsonObj.toString());
			LOGGER.debug("ElasticSearch snapshot is successfully created.");
		} catch (MalformedURLException e) {
			LOGGER.debug(e.toString());
		} catch (IOException | ElasticSearchException e) {
			LOGGER.debug(e.toString());
		} catch (Exception e) {
			LOGGER.debug(e.toString());
		}
	}
	
	@Test
	public void 엘라스틱_스냅샷_복원_테스트() throws MalformedURLException, IOException, ElasticSearchException, JSONException, Exception {
		String snapshotName = "snapshot_29cbe1cf-9b9a-4168-9f4a-e0edbf5b45ab";
		StringBuilder sb = new StringBuilder();
		String url = sb.append(host).append(path).append("/").append(snapshotName).append(restore).toString();
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("indices", index);
		jsonObj.put("ignore_unavailable", true);
		jsonObj.put("include_global_state", false);
		
		elasticSearchUtil.post(url, jsonObj.toString());
	}
}
