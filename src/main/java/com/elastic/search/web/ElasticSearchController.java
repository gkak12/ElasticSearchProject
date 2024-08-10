package com.elastic.search.web;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elastic.search.common.ElasticSearchException;
import com.elastic.search.dto.PagingDto;
import com.elastic.search.dto.SearchDto;
import com.elastic.search.service.ElasticSearchService;

@RestController
public class ElasticSearchController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchController.class);
	
	@Resource(name = "elasticSearchService")
	private ElasticSearchService elasticSearchService;
	
	@GetMapping(value="/selectFile.json")
	public ResponseEntity<String> selectFile(@RequestParam(name = "keyword", required = true) String keyword){
		ResponseEntity<String> res = null;
		
		try {
			String val = elasticSearchService.selectFile(keyword);
			res = ResponseEntity.status(HttpStatus.OK).body(val);
		} catch(MalformedURLException e) {
			res = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch(IOException | ElasticSearchException e) {
			res = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (Exception e) {
			res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return res;
	}
	
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
	
	@PostMapping(value="/restoreSnapshot.json")
	public ResponseEntity<String> restoreSnapshot(@RequestParam(name = "snapshotName", required = true) String snapshotName){
		ResponseEntity<String> res = null;
		
		try {
			elasticSearchService.restoreSnapshot(snapshotName);
			res = ResponseEntity.status(HttpStatus.OK).build();
		} catch(MalformedURLException e) {
			res = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch(IOException | ElasticSearchException e) {
			res = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} catch (Exception e) {
			res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return res;
	}
	
	@PostMapping(value="/selectListPaging.json")
	public ResponseEntity<PagingDto> selectListPaging(@RequestBody SearchDto searchDto){
		ResponseEntity<PagingDto> res = null;
		
		try {
			PagingDto pagingDto = elasticSearchService.selectListPaging(searchDto);
			res = ResponseEntity.status(HttpStatus.OK).body(pagingDto);
		} catch (Exception e) {
			res = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return res;
	}
}
