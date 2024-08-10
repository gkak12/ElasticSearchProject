package com.elastic.search.dto;

import java.util.List;

import com.elastic.search.domain.FileInfo;
import com.sun.istack.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PagingDto {

	@NotNull
	private List<FileInfo> list;
	
	@NotNull
	private Long cnt;
}
