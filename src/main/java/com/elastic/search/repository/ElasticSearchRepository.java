package com.elastic.search.repository;

import java.util.List;

import com.elastic.search.domain.FileInfo;
import com.elastic.search.dto.FileInfoDto;

public interface ElasticSearchRepository {
	
	public List<FileInfo> selectListAll() throws Exception;
	
	public void insert(FileInfoDto fileInfoDto) throws Exception;

	public void insertBatch(List<FileInfoDto> list) throws Exception;
}
