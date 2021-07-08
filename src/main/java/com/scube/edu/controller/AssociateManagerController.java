package com.scube.edu.controller;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scube.edu.model.UniversityStudentDocument;
import com.scube.edu.request.AssociateExcelRequest;
import com.scube.edu.request.UniversityStudentRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.UniversityResponse;
import com.scube.edu.response.UniversityStudDocResponse;
import com.scube.edu.service.AssociateManagerService;
import com.scube.edu.util.StringsUtils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/AssociateManager")
public class AssociateManagerController {
	
	private static final Logger logger = LoggerFactory.getLogger(AssociateManagerController.class);

	BaseResponse response = null;
	
	 @Autowired
	 AssociateManagerService associateManagerService;
	 
	
	@PostMapping(value = "/uploadStudentInfoFromFile/{userid}" )
	public ResponseEntity<BaseResponse> saveStudentData (@RequestBody List<AssociateExcelRequest> list,@PathVariable Long userid) {
		
		System.out.println("*******AssociateManagerController saveStudentData********"+ list);
		
		response = new BaseResponse();
		
	    try {
	    	
	    	 HashMap<String,List<UniversityStudDocResponse>> List = associateManagerService.saveStudentInfo(list,userid);
				
				response.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response.setRespData(List);
				
				return ResponseEntity.ok(response);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response);
				
			}
	}
	@PostMapping(value = "/reviewUploadStudentInfo" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BaseResponse> reviewStudentData (@RequestParam MultipartFile excelfile,@RequestParam MultipartFile datafile) {
		
		System.out.println("*******AssociateManagerController reviewUploadStudentInfo********"+ datafile);
		
		response = new BaseResponse();
		
	    try {
	    	
	    	List<UniversityStudDocResponse> List = associateManagerService.ReviewStudentData(excelfile, datafile);
				
				response.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response.setRespData(List);
				
				return ResponseEntity.ok(response);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response);
				
			}
	}
	@PostMapping(value = "/getUniversityStudenData")
	public ResponseEntity<BaseResponse> getstudentData (@RequestBody UniversityStudentRequest stuentData) {
		
		System.out.println("*******AssociateManagerController getstudentData********");
		
		response = new BaseResponse();
		
	    try {
	    	
	    	  List<UniversityResponse> List=associateManagerService.getStudentData(stuentData) ;
				
				response.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response.setRespData(List);
				
				return ResponseEntity.ok(response);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response);
				
			}
	}
	@PostMapping(value = "/associateFileSave" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BaseResponse> UploadFile (@RequestParam MultipartFile file) {
		
		System.out.println("*******AssociateManagerController associateFileSave********");
		
		response = new BaseResponse();
		
	    try {
	    	
	    	 String List = associateManagerService.saveassociatefilepath(file);
				
				response.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response.setRespData(List);
				
				return ResponseEntity.ok(response);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response);
				
			}
	}
}

