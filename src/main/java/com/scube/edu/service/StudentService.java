package com.scube.edu.service;

import java.util.HashMap;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.scube.edu.request.StudentDocVerificationRequest;
import com.scube.edu.request.paymensReqFlagRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.PriceMasterResponse;
import com.scube.edu.response.StudentVerificationDocsResponse;
import com.scube.edu.response.VerificationListPojoResponse;
import com.scube.edu.response.VerificationResponse;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public interface StudentService {

	public List<VerificationResponse> getVerificationDocsDataByUserid(long userId) throws Exception;

	public List<VerificationResponse> getClosedRequests(long userId);

	public HashMap<String, List<Long>> saveVerificationDocAndCalculateAmount(List<StudentDocVerificationRequest> studentDocReq, HttpServletRequest request);

	public HashMap<String, Long> saveStudentSingleVerificationDoc(StudentDocVerificationRequest studentDocReq,
			HttpServletRequest request);

	public String saveDocument(MultipartFile file);
	
	public String UpdatePaymentFlag(List<paymensReqFlagRequest> id);
	public HashMap<String, List< Long>> CalculateDocAmount(List<StudentDocVerificationRequest> studentDocReq);

}
