package com.elastic.search.service.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

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
import com.elastic.search.service.ElasticSearchService;

@Service("elasticSearchService")
public class ElasticSearchServiceImp implements ElasticSearchService{

	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchServiceImp.class);
	
	@Value("${file.loc}")
	private String fileLoc;
	
	@Value("${elasticSearch.host}")
	private String host;
	
	@Value("${elasticSearch.index}")
	private String index;
	
	@Value("${elasticSearch.bulk.path}")
	private String bulkPath;

	@Value("${elasticSearch.bulk.file}")
	private String bulkFileName;
	
	@Value("${elasticSearch.insert.file.path}")
	private String insertFilePath;

	@Value("${elasticSearch.delete.query.path}")
	private String deleteQueryPath;
	
	@Resource(name = "multipartResolver")
    private StandardServletMultipartResolver multipartResolver;
	
	private String bulkUrl;
	
	private String insertFileUrl;

	private String deleteUrl;
	
	@Autowired
	public void init() {	// 엘라스틱서치 url 초기화
		bulkUrl = host + bulkPath;
		insertFileUrl = host + "/" + index + insertFilePath;
		deleteUrl = host + "/" + index + deleteQueryPath;
	}
	
	@Override
	public void insertFile(HttpServletRequest request) throws MalformedURLException, IOException, ElasticSearchException, Exception {
		MultipartHttpServletRequest mRequest = multipartResolver.resolveMultipart(request);
		List<MultipartFile> fileList = mRequest.getFiles("fileList");
		String uuid = UUID.randomUUID().toString();

		File fileDir = new File(fileLoc + File.separator + uuid);
		fileDir.mkdirs();
		
		List<File> resFileList = saveFileToDisk(fileDir, fileList);
		
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
			// 파일 저장 롤백 코드 추가
			fileDir.delete();
			throw new IOException("엘라스틱 첨부파일 디스크에 저장 중에 예외 발생했습니다.");
		}
		
		return resList;
	}
	
	private void bulkFileElastic(String uuid, File fileDir, List<File> fileList) throws MalformedURLException, IOException, ElasticSearchException, Exception{
		try {
			StringBuilder sb = new StringBuilder();
			
			for(File fileItem : fileList) {
				sb.append("{\"index\": {\"_index\": \"").append(index).append("\",")
				.append("\"_type\":\"_doc\",")
				.append("\"pipeline\":\"attachment\"}}\n");
				
				sb.append("{\"uuid\": \"").append(uuid).append("\",")
				.append("\"fileName\": \"").append(fileItem.getName()).append("\",")
				.append("\"fileContent\": \"").append(convertFile(fileItem)).append("\"}\n");
			}
			
			LOGGER.debug(bulkUrl + " " + sb.toString());
			ElasticSearchUtil.post(bulkUrl, sb.toString());
		} catch (Exception e) {
			LOGGER.debug(e.toString());
			deleteFileElastic(uuid);
			throw new ElasticSearchException("엘라스틱서치 다중 첨부파일 등록 중에 예외 발생했습니다.");
		}
	}
	
	private void saveFileElastic(String uuid, File fileDir, List<File> fileList) throws MalformedURLException, IOException, ElasticSearchException, Exception{
		try {
			for(File fileItem : fileList) {
				String fileContent = convertFile(fileItem);
				
				JSONObject jsonObj = new JSONObject();
				
				jsonObj.put("uuid", uuid);
				jsonObj.put("fileName", fileItem.getName());
				jsonObj.put("fileContent", fileContent);
				
				LOGGER.debug(insertFileUrl + " " + jsonObj.toString());
				ElasticSearchUtil.post(insertFileUrl, jsonObj.toString());
			}
		} catch (Exception e) {
			LOGGER.debug(e.toString());
			deleteFileElastic(uuid);
			throw new ElasticSearchException("엘라스틱서치 단일 첨부파일 등록 중에 예외 발생했습니다.");
		}
	}
	
	private String convertFile(File file) throws ElasticSearchException{
		String res = null;
		
		try (FileInputStream fis = new FileInputStream(file)){
			byte[] byteFile = fis.readAllBytes();
			byte[] enFile = Base64.getEncoder().encode(byteFile);
			
			res = new String(enFile);
		} catch (Exception e) {
			LOGGER.debug(e.toString());
			throw new ElasticSearchException("엘라스틱서치 첨부파일 인코딩 변환 중에 예외 발생했습니다.");
		}
		
		return res;
	}
	
	private void deleteFileElastic(String uuid) throws MalformedURLException, IOException, ElasticSearchException, Exception{
		try {
			String queryStr = "{\"query\":{\"match\":{\"uuid\":" + uuid + "}}}";
			ElasticSearchUtil.post(deleteUrl, queryStr);
		} catch (Exception e) {
			LOGGER.debug(e.toString());
			throw new ElasticSearchException("엘라스틱서치 첨부파일 예외 처리 실패했습니다.");
		}
	}
}
