package com.elastic.search.repository.imp;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.elastic.search.domain.FileInfo;
import com.elastic.search.domain.QFileInfo;
import com.elastic.search.dto.FileInfoDto;
import com.elastic.search.repository.ElasticSearchRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository("elasticSearchRepository")
@RequiredArgsConstructor
public class ElasticSearchRepositoryImp implements ElasticSearchRepository {

	private final JPAQueryFactory queryFactory;
	
	private final EntityManager entityManager;

	@Override
	public List<FileInfo> selectListAll() throws Exception {
		QFileInfo qFileInfo = QFileInfo.fileInfo;
		List<FileInfo> list = queryFactory.select(qFileInfo).fetch();
		
		return list;
	}

	@Override
	public void insert(FileInfoDto fileInfoDto) throws Exception {
		FileInfo fileInfo = new FileInfo();
		fileInfo.setFileName(fileInfoDto.getFileName());
		fileInfo.setFileDir(fileInfoDto.getFileDir());
		fileInfo.setElasticUuid(fileInfoDto.getUuid());
		
		entityManager.persist(fileInfo);
	}

	@Override
	public void insertBatch(List<FileInfoDto> list) throws Exception {
		int cnt = 0;
		int batchSize = 5;
		int listSize = list.size();
		
		for(FileInfoDto fileInfoDto : list) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.setFileName(fileInfoDto.getFileName());
			fileInfo.setFileDir(fileInfoDto.getFileDir());
			fileInfo.setElasticUuid(fileInfoDto.getUuid());
			
			entityManager.persist(fileInfo);
			cnt++;
			
			if(cnt % batchSize == 0 || cnt == listSize) {
				entityManager.flush();
				entityManager.clear();
			}
		}
	}
}
