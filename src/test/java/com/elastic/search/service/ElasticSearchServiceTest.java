package com.elastic.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.annotation.Resource;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.elastic.search.common.ElasticSearchException;
import com.elastic.search.common.ElasticSearchUtil;

@SpringBootTest(classes = ElasticSearchUtil.class)
@TestPropertySource(locations="classpath:application.yml")
class ElasticSearchServiceTest {

	@Value("${file.loc}")
	private String fileLoc;
	
	@Value("${elasticSearch.host}")
	private String host;
	
	@Value("${elasticSearch.index}")
	private String index;
	
	@Value("${elasticSearch.select.query.path}")
	private String selectPath;
	
	@Value("${elasticSearch.bulk.path}")
	private String bulkPath;

	@Value("${elasticSearch.bulk.file}")
	private String bulkFileName;
	
	@Value("${elasticSearch.insert.file.path}")
	private String insertFilePath;

	@Value("${elasticSearch.delete.query.path}")
	private String deleteQueryPath;
	
	@Resource(name = "elasticSearchUtil")
	private ElasticSearchUtil elasticSearchUtil;
	
	@ParameterizedTest
	@CsvSource({
		"엘라스틱서치, true",
		"abc, true",
		"01234, true",
		"~!@#$%, true"
	})
	public void 엘라스틱서치_첨부파일_검색_테스트(String keyword, boolean expRes) throws MalformedURLException, IOException, ElasticSearchException, Exception {
		String selectUrl = host + "/" + index + selectPath; 
		
		String paramStr = "{\"query\": {\"wildcard\": {\"attachment.content\": \"*" + keyword + "*\"}}}";
		String str = elasticSearchUtil.get(selectUrl, paramStr, true);
		
		boolean res =  str != null ? true : false;
		assertEquals(expRes, res);
	}
}
