package com.scube.edu.service;

import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.StudentVerificationDocsResponse;


public interface StudentService {

	List<StudentVerificationDocsResponse> getVerificationDataByUserid(long userId) throws Exception;

	List<StudentVerificationDocsResponse> getClosedRequests(long userId);

}
