package com.elastic.search.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SnapshotInfoDto {

	private String indices;
	
	private boolean ignoreUnavailable;
	
	private boolean includeGlobalState;
}
