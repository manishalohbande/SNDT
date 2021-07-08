package com.scube.edu.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import com.lowagie.text.BadElementException;
import com.scube.edu.request.StatusChangeRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.StudentVerificationDocsResponse;
import com.scube.edu.response.UniversityResponse;
import com.scube.edu.response.VerificationResponse;


public interface VerifierService {

	public List<VerificationResponse> getVerifierRequestList(long id) throws Exception;

	public List<StudentVerificationDocsResponse> verifyDocument(Long id);

	public List<StudentVerificationDocsResponse> setStatusForVerifierDocument(StatusChangeRequest statusChangeRequest) throws BadElementException, MessagingException, IOException, Exception;

	public Integer  updateListonLogout (long id) throws Exception;

	public List<UniversityResponse> getUniTabularData(String prnNo, long semesterId) throws Exception;

}
