package com.scube.edu.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scube.edu.request.StatusChangeRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.StudentVerificationDocsResponse;
import com.scube.edu.response.UniversityResponse;
import com.scube.edu.response.VerificationResponse;
import com.scube.edu.service.VerifierService;
import com.scube.edu.util.FileStorageService;
import com.scube.edu.util.StringsUtils;

import lombok.var;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/verifier")
public class VerifierController {
	
	private static final Logger logger = LoggerFactory.getLogger(VerifierController.class);

	BaseResponse response = null;
	
	@Autowired
	VerifierService	verifierService;
	
	
	@Autowired
	 private FileStorageService fileStorageService;
	
	@GetMapping("/getVerifierRequestList/{userid}")
	public  ResponseEntity<Object> getVerifierRequestList(@PathVariable long userid) {
		
		response = new BaseResponse();
		
		    try {
		    	List<VerificationResponse> list = verifierService.getVerifierRequestList(userid);
					// this list has FIFO mechanism for getting records for verifier (limit 5)
					response.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
					response.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
					response.setRespData(list);
					
					return ResponseEntity.ok(response);
						
				}catch (Exception e) {
					
					logger.error(e.getMessage()); //BAD creds message comes from here
					
					response.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
					response.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
					response.setRespData(e.getMessage());
					
					return ResponseEntity.badRequest().body(response);
					
				}
		    
		    
			
   }
	
	
	@GetMapping("/getverifydocument/{id}")
	public  ResponseEntity<Object> verifydocument(@PathVariable Long id) {
		
		response = new BaseResponse();
		
		logger.info("getVerifyDoc");
		    try {
		    	logger.info("---getVerifyDoc");
		    	List<StudentVerificationDocsResponse> list = verifierService.verifyDocument(id);

		    	    response.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
					response.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
					response.setRespData(list);
					
					return ResponseEntity.ok(response);
						
				}catch (Exception e) {
					
					logger.error(e.getMessage()); //BAD creds message comes from here
					
					response.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
					response.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
					response.setRespData(e.getMessage());
					
					return ResponseEntity.badRequest().body(response);
					
				}
			
   }

	@Value("${file.awsORtest}")
    private String awsORtest;
	
	//This is to get dynamic(Actual) file from the drive location
	 @GetMapping("/getimage/{UserFor}/{id}")
	 public ResponseEntity<byte[]> getFileFromStorageSelection(@PathVariable String UserFor, @PathVariable Long id ) throws Exception {
		 
		 if(awsORtest.equalsIgnoreCase("TEST") || awsORtest.equalsIgnoreCase("LOCAL")) {
			 
			 Resource res =  fileStorageService.loadFileAsResource(UserFor,id);
			 
			 byte[] bytes = StreamUtils.copyToByteArray(res.getInputStream());
			 
			 MediaType mediaType;
			 String ext = FilenameUtils.getExtension(res.getFilename());
		        
		        if(ext.equalsIgnoreCase("pdf")|| ext == "PDF" ) {
		        
		        	mediaType = MediaType.APPLICATION_PDF ;
		        }
		        else {
		        	mediaType = MediaType.IMAGE_JPEG ;
		        }
		        	
		        
		        return ResponseEntity.ok()
		                             .contentType(mediaType)
		                             .body(bytes);
			 
		 }else {

	  //      var imgFile = new ClassPathResource("image/ha-img.jpg");
	        
	       // Resource res =  fileStorageService.loadFileAsResource("file/ha-img.jpg");
		 
		 
		 
//		 byte[] res =  fileStorageService.loadFileAsResourceFromAws(UserFor,id);
	     
		 HashMap<String, Object> res =  fileStorageService.loadFileAsResourceFromAws(UserFor,id);
		 
		 //File file = new File(res.getFile());
		 
		 
		 
		 
//	        byte[] bytes = StreamUtils.copyToByteArray(res.getInputStream());

	        byte[] ret = (byte[]) res.get("byteArray");
	        String ext = (String) res.get("extension");
	        MediaType mediaType;
		 
	        
	        if(ext.equalsIgnoreCase("pdf")|| ext == "PDF" ) {
	        
	        	mediaType = MediaType.APPLICATION_PDF ;
	        }
	        else {
	        	mediaType = MediaType.IMAGE_JPEG ;
	        }
	        	
	        
	        return ResponseEntity.ok()
	                             .contentType(mediaType)
	                             .body(ret);
		 }
	    }
	 
		@PostMapping("/setStatusForVerifierDocument")
		public  ResponseEntity<Object> setStatusForVerifierDocument(@RequestBody StatusChangeRequest statusChangeRequest) {
			
			response = new BaseResponse();
			System.out.println("*****VerifierController setStatusForVerifierDocument*****"+statusChangeRequest.getRemark());
			    try {
			    	
			    	List<StudentVerificationDocsResponse> list = verifierService.setStatusForVerifierDocument(statusChangeRequest);

			    	    response.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
						response.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
						response.setRespData(null);
						
						return ResponseEntity.ok(response);
							
					}catch (Exception e) {
						
						logger.error(e.getMessage()); //BAD creds message comes from here
						
						response.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
						response.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
						response.setRespData(e.getMessage());
						
						return ResponseEntity.badRequest().body(response);
						
					}
				
	   }
		
		@GetMapping("/getUniversityTabularFormData/{prnNo}/{semesterId}")
		public  ResponseEntity<Object> getUniversityTabularFormData(@PathVariable String prnNo, @PathVariable long semesterId) {
			
			response = new BaseResponse();
			
			    try {
			    	List<UniversityResponse> list = verifierService.getUniTabularData(prnNo, semesterId);
						// this list has FIFO mechanism for getting records for verifier (limit 5)
						response.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
						response.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
						response.setRespData(list);
						
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
