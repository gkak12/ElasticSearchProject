package com.elastic.search.dto;

import com.sun.istack.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchDto {

	@NotNull
	private Long offSet;
	
	@NotNull
	private Long limit;
}
