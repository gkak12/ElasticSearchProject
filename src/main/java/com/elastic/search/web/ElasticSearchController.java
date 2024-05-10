package com.elastic.search.web;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elastic.search.common.ElasticSearchException;
import com.elastic.search.service.ElasticSearchService;

@RestController
public class ElasticSearchController {

	@Resource(name = "elasticSearchService")
	private ElasticSearchService elasticSearchService;
	
	@PostMapping(value="/insertFile.json")
	public ResponseEntity<Void> insertFile(HttpServletRequest request){
		ResponseEntity<Void> res = null;
		
		try {
			elasticSearchService.insertFile(request);
			res = ResponseEntity.status(HttpStatus.CREATED).build();
		} catch(MalformedURLException e) {
			res = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch(IOException | ElasticSearchException e) {
			res = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (Exception e) {
			res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return res;
	}
}
