package com.elastic.search.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(schema="ELASTIC", name="FILE_INFO")
@SequenceGenerator(
		name="ELASTIC_FILE_SEQ_GENERATOR",
		schema="ELASTIC",
		sequenceName="ELASTIC_FILE_SEQ",
		initialValue=1,
		allocationSize=5
	)
public class FileInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ELASTIC_FILE_SEQ_GENERATOR")
	@Column(name="id")
	private long id;
	
	@Column(name="file_name")
	private String fileName;
	
	@Column(name="file_dir")
	private String fileDir;
	
	@Column(name="elastic_uuid")
	private String elasticUuid;
}
