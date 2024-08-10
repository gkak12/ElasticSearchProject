package com.elastic.search.service.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import com.elastic.search.common.ElasticSearchException;
import com.elastic.search.common.ElasticSearchUtil;
import com.elastic.search.domain.FileInfo;
import com.elastic.search.dto.FileInfoDto;
import com.elastic.search.dto.PagingDto;
import com.elastic.search.dto.SearchDto;
import com.elastic.search.repository.ElasticSearchRepository;
import com.elastic.search.service.ElasticSearchService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("elasticSearchService")
public class ElasticSearchServiceImp implements ElasticSearchService{

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchServiceImp.class);
	
	@Value("${file.loc}")
	private String fileLoc;
	
	@Value("${elasticSearch.host}")
	private String host;
	
	@Value("${elasticSearch.index}")
	private String index;
	
	@Value("${elasticSearch.select.query.path}")
	private String selectPath;
	
	@Value("${elasticSearch.bulk.path}")
	private String bulkPath;

	@Value("${elasticSearch.bulk.file}")
	private String bulkFileName;
	
	@Value("${elasticSearch.insert.file.path}")
	private String insertFilePath;

	@Value("${elasticSearch.delete.query.path}")
	private String deleteQueryPath;
	
	@Value("${elasticSearch.snapshot.path}")
	private String path;
	
	@Value("${elasticSearch.snapshot.restore}")
	private String restore;
	
	@Resource(name="multipartResolver")
    private StandardServletMultipartResolver multipartResolver;
	
	@Resource(name="elasticSearchUtil")
	private ElasticSearchUtil elasticSearchUtil;
	
	@Resource(name="elasticSearchRepository")
	private ElasticSearchRepository elasticSearchRepository;

	private String selectUrl;
	
	private String bulkUrl;
	
	private String insertFileUrl;

	private String deleteUrl;
	
	@Autowired
	public void init() {	// 엘라스틱서치 url 초기화
		selectUrl = host + "/" + index + selectPath; 
		bulkUrl = host + bulkPath;
		insertFileUrl = host + "/" + index + insertFilePath;
		deleteUrl = host + "/" + index + deleteQueryPath;
	}
	
	@Override
	public String selectFile(String keyword) throws MalformedURLException, IOException, ElasticSearchException, Exception {
		String res = null;

		String paramStr = "{\"query\": {\"wildcard\": {\"attachment.content\": \"*" + keyword + "*\"}}}";
		String data = elasticSearchUtil.get(selectUrl, paramStr, true);

		ObjectMapper objMapper = new ObjectMapper();
		JsonNode root = objMapper.readTree(data);
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		
		for(JsonNode node : root.get("hits").get("hits")) {
			JsonNode source = node.get("_source");
			String fileName = source.get("fileName").toString();
			String fileContent = source.get("attachment").get("content").toString();
			
			sb.append("{\"fileName\": ").append(fileName).append(", \"fileContent\": ").append(fileContent).append("},");
		}
		
		res = sb.deleteCharAt(sb.length()-1).append("]").toString();
		return res;
	}
	
	@Override
	public void insertFile(HttpServletRequest request) throws MalformedURLException, IOException, ElasticSearchException, Exception {
		MultipartHttpServletRequest mRequest = multipartResolver.resolveMultipart(request);
		List<MultipartFile> fileList = mRequest.getFiles("fileList");
		String uuid = UUID.randomUUID().toString();

		File fileDir = new File(fileLoc + File.separator + uuid);
		fileDir.mkdirs();
		
		List<File> resFileList = saveFileToDisk(fileDir, fileList);	// 첨부파일 디스크 저장
		
		if(resFileList.size() > 1) {	// 파일이 2건 이상인 경우
			bulkFileElastic(uuid, fileDir, resFileList);
		} else {	// 파일이 1건인 경우
			saveFileElastic(uuid, fileDir, resFileList);
		}
	}

	private List<File> saveFileToDisk(File fileDir, List<MultipartFile> fileList) throws IOException {
		List<File> resList = new ArrayList<File>();
		
		try {
			for(MultipartFile fileItem : fileList) {
				File sFile = new File(fileDir + File.separator + fileItem.getOriginalFilename());
				fileItem.transferTo(sFile);
				
				resList.add(sFile);
			}
		} catch (IOException e) {
			LOGGER.debug(e.toString());
			fileDir.delete();
			throw new IOException("엘라스틱 첨부파일 디스크에 저장 중에 예외 발생했습니다.");
		}
		
		return resList;
	}
	
	private void bulkFileElastic(String uuid, File fileDir, List<File> fileList) throws MalformedURLException, IOException, ElasticSearchException, Exception{
		try {
			StringBuilder sb = new StringBuilder();
			List<FileInfoDto> list = new ArrayList<FileInfoDto>();
			
			for(File fileItem : fileList) {
				sb.append("{\"index\": {\"_index\": \"").append(index).append("\",")
				.append("\"_type\":\"_doc\",")
				.append("\"pipeline\":\"attachment\"}}\n");
				
				sb.append("{\"uuid\": \"").append(uuid).append("\",")
				.append("\"fileName\": \"").append(fileItem.getName()).append("\",")
				.append("\"fileContent\": \"").append(convertFile(fileItem)).append("\"}\n");
				
				FileInfoDto fileInfoDto = FileInfoDto.builder()
												.fileName(fileItem.getName())
												.fileDir(fileItem.getAbsolutePath())
												.uuid(uuid)
												.build();
				
				list.add(fileInfoDto);
			}
			
			LOGGER.debug(bulkUrl + " " + sb.toString());
			elasticSearchUtil.post(bulkUrl, sb.toString());	// 엘라스틱서치 인덱스 2건 이상 저장, bulk api
			
			elasticSearchRepository.insertBatch(list);	// DB 테이블 2건 이상 저장, batch
		} catch (Exception e) {
			LOGGER.debug(e.toString());
			rollbackFileElastic(uuid);
			throw new ElasticSearchException("엘라스틱서치 다중 첨부파일 등록 중에 예외 발생했습니다.");
		}
	}
	
	private void saveFileElastic(String uuid, File fileDir, List<File> fileList) throws MalformedURLException, IOException, ElasticSearchException, Exception {
		try {
			for(File fileItem : fileList) {
				String fileContent = convertFile(fileItem);
				
				JSONObject jsonObj = new JSONObject();
				
				jsonObj.put("uuid", uuid);
				jsonObj.put("fileName", fileItem.getName());
				jsonObj.put("fileContent", fileContent);
				
				FileInfoDto fileInfoDto = FileInfoDto.builder()
						.fileName(fileItem.getName())
						.fileDir(fileItem.getAbsolutePath())
						.uuid(uuid)
						.build();
				
				LOGGER.debug(insertFileUrl + " " + jsonObj.toString());
				elasticSearchUtil.post(insertFileUrl, jsonObj.toString());	// 엘라스틱서치 인덱스 1건 저장
				
				elasticSearchRepository.insert(fileInfoDto);	// DB 테이블 1건 저장
			}
		} catch (Exception e) {
			LOGGER.debug(e.toString());
			rollbackFileElastic(uuid);
			throw new ElasticSearchException("엘라스틱서치 단일 첨부파일 등록 중에 예외 발생했습니다.");
		}
	}
	
	private String convertFile(File file) throws ElasticSearchException{
		String res = null;
		
		try (FileInputStream fis = new FileInputStream(file)){
			byte[] byteFile = fis.readAllBytes();
			byte[] enFile = Base64.getEncoder().encode(byteFile);
			
			res = new String(enFile);
		} catch(FileNotFoundException e) {
			LOGGER.debug(e.toString());
			throw new ElasticSearchException("엘라스틱서치 첨부파일이 디스크에 저장되지 않았습니다.");
		} catch (IOException e) {
			LOGGER.debug(e.toString());
			throw new ElasticSearchException("엘라스틱서치 첨부파일 인코딩 변환 중에 예외 발생했습니다.");
		}
		
		return res;
	}
	
	private void rollbackFileElastic(String uuid) throws MalformedURLException, IOException, ElasticSearchException, Exception{
		try {
			String queryStr = "{\"query\":{\"match\":{\"uuid\":" + uuid + "}}}";
			elasticSearchUtil.post(deleteUrl, queryStr);
		} catch (Exception e) {
			LOGGER.debug(e.toString());
			throw new ElasticSearchException("엘라스틱서치 첨부파일 예외 처리 실패했습니다.");
		}
	}

	@Override
	public void restoreSnapshot(String snapshotName) throws MalformedURLException, IOException, ElasticSearchException, Exception {
		StringBuilder sb = new StringBuilder();
		String url = sb.append(host).append(path).append("/").append(snapshotName).append(restore).toString();
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("indices", index);
		jsonObj.put("ignore_unavailable", true);
		jsonObj.put("include_global_state", false);
		
		elasticSearchUtil.post(url, jsonObj.toString());
	}

	@Override
	public PagingDto selectListPaging(SearchDto searchDto) throws Exception {
		CompletableFuture<List<FileInfo>> futureList = elasticSearchRepository.selectListPaging(searchDto);
		CompletableFuture<Long> futureCnt = elasticSearchRepository.selectListPagingCnt(searchDto);
		
		CompletableFuture.allOf(futureList, futureCnt).join();
		
		List<FileInfo> list = futureList.get();
		Long cnt = futureCnt.get();
		
		PagingDto pagingDto = PagingDto.builder().list(list).cnt(cnt).build();
		return pagingDto;
	}
}
