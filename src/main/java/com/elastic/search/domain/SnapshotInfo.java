package com.elastic.search.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Data
@Entity
@Table(schema="ELASTIC", name="FILE_INFO")
@SequenceGenerator(
		name="ELASTIC_SNAPSHOT_SEQ_GENERATOR",
		schema="ELASTIC",
		sequenceName="ELASTIC_SNAPSHOT_SEQ",
		initialValue=1
	)
public class SnapshotInfo {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ELASTIC_SNAPSHOT_SEQ_GENERATOR")
	@Column(name="id")
	private long id;
	
	@Column(name="snapshot_name")
	private String snapshotName;
	
	@Temporal(TemporalType.TIME)
	@Column(name="create_time")
	private Date createTime;
}
