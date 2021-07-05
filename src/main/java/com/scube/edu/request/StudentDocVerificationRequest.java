package com.scube.edu.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter

public class StudentDocVerificationRequest {
	
	private String firstname; 
	private String lastname;
	private Long streamid;
	private String docname;
	private Long requesttype;
	private String enrollno;
	private Long yearofpassid;
	private Long collegenameid;
	private Long applicationid;
	private Long uniid;
	private Long userid;
//	private String requesttypeid;
	private Long verreqid;
	private String uploaddocpath;
	private String filepath;
	private String paymentId;
	private Long branchId;
	private Long semId;
	private String createby;
	private String altEmail;
	
	private String monthOfPassing;
	
	private String prnNo;
	private String courierAdrr;
	private Long resultCollectionType;



}
