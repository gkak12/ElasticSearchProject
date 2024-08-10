package com.elastic.search.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.elastic.search.domain.FileInfo;
import com.elastic.search.dto.FileInfoDto;
import com.elastic.search.dto.SearchDto;

public interface ElasticSearchRepository {
	
	public List<FileInfo> selectListAll() throws Exception;
	
	public CompletableFuture<List<FileInfo>> selectListPaging(SearchDto searchDto) throws Exception;
	
	public CompletableFuture<Long> selectListPagingCnt(SearchDto searchDto) throws Exception;
	
	public void insert(FileInfoDto fileInfoDto) throws Exception;

	public void insertBatch(List<FileInfoDto> list) throws Exception;
}
