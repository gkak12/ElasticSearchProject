package com.elastic.search.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("elasticSearchSnapshot")
public class ElasticSearchSnapshot {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchSnapshot.class);

	@Value("${elasticSearch.host}")
	private String host;
	
	@Value("${elasticSearch.index}")
	private String index;
	
	@Value("${elasticSearch.snapshot.path}")
	private String path;
	
	@Resource(name="elasticSearchUtil")
	private ElasticSearchUtil elasticSearchUtil;
	
	@Scheduled(cron = "0 0 2 * * *")
	public void cronSnapshot() {
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
}
