package com.scube.edu.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import com.lowagie.text.BadElementException;
import com.scube.edu.request.StatusChangeRequest;
import com.scube.edu.response.StudentVerificationDocsResponse;
import com.scube.edu.response.UniversityVerifierResponse;

public interface UniversityVerifierService {
	
	public List<UniversityVerifierResponse> getUniversityVerifierRequestList() throws Exception;

	public List<StudentVerificationDocsResponse> setStatusForUniversityDocument(
			StatusChangeRequest statusChangeRequest) throws Exception ;

	public List<UniversityVerifierResponse> getUniversityVerifierPhysicalCollectionRequestList(String fromDate,
			String toDate);



}
