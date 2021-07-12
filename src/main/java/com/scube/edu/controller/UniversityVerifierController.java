package com.scube.edu.controller;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scube.edu.request.StatusChangeRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.StudentVerificationDocsResponse;
import com.scube.edu.response.UniversityVerifierResponse;
import com.scube.edu.response.VerificationResponse;
import com.scube.edu.service.UniversityVerifierService;
import com.scube.edu.util.StringsUtils;
import com.scube.edu.util.TranscriptPdf;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/universityVerifier")
public class UniversityVerifierController {

	private static final Logger logger = LoggerFactory.getLogger(UniversityVerifierController.class);

	BaseResponse response = null;
	
	@Autowired
	UniversityVerifierService universityVerifierService;
	
	@Autowired
	TranscriptPdf	writeApprovalPdf;

	
	@GetMapping(value="/getUniversityVerifierRequestList")
	public  ResponseEntity<Object> getVerifierRequestList() {
		
		response = new BaseResponse();
		
		    try {
		    	List<UniversityVerifierResponse> list = universityVerifierService.getUniversityVerifierRequestList();
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
	
	@PostMapping("/setStatusForUniversityDocument")
	public  ResponseEntity<Object> setStatusForUniversityDocument(@RequestBody StatusChangeRequest statusChangeRequest) {
		
		response = new BaseResponse();
		System.out.println("*****VerifierController setStatusForVerifierDocument*****"+statusChangeRequest.getRemark());
		    try {
		    	
		    	List<StudentVerificationDocsResponse> list = universityVerifierService.setStatusForUniversityDocument(statusChangeRequest);

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
	
	@GetMapping(value="/getUniPhysicalCollectionRequestList/{fromDate}/{toDate}")
	public  ResponseEntity<Object> getUniPhysicalCollectionRequestList(@PathVariable String fromDate, @PathVariable String toDate) {
		
		response = new BaseResponse();
		
		    try {
		    	List<UniversityVerifierResponse> list = universityVerifierService.getUniversityVerifierPhysicalCollectionRequestList(fromDate,toDate);
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
	@GetMapping("/getTranscriptPdf/{vrId}")
    public void exportToPDF(@PathVariable String vrId, HttpServletResponse response ,HttpServletRequest request) throws Exception {

		logger.info("-----imageLocation---------------");
    	
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        
        String currentDate = dateFormatter.format(new Date());
		ByteArrayOutputStream outputStream = null;
		outputStream = new ByteArrayOutputStream();


        String headerKey =  "Content-Disposition";
        String headerValue = "attachment; filename= "+vrId+"_" + currentDate + ".pdf";
        response.setHeader(headerKey, headerValue);
        try {        
        logger.info("before export");
     //   InvoicePDFExporter exporter = new InvoicePDFExporter(pdfData,applicationId, imageLocation);
        
        writeApprovalPdf.writeTranscriptPdf(response,Long.valueOf(vrId));
        logger.info("after export"+ response.toString());
        }catch(Exception e) {
        	
        	logger.info("-----imageLocation---------------"+e.getMessage());
        }
         
    }
	
}
