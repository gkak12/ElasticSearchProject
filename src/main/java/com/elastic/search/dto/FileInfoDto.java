package com.elastic.search.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInfoDto {

	private String fileName;
	
	private String fileDir;
	
	private String uuid;
}
