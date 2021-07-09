package com.scube.edu.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@ToString
public class DisputeResponse {
	
	private long id;
	private String created_by;
	private long application_id;
	private String emailid;
	private String phone_no;
	private String reason;
	private long verification_id;
	private String first_name;
	private String last_name;
	private String vrStatus;
	private Long semId;
	private String prnNo;
	private String request_type_id;
	private String courierAddress;
	private String resultCollectionType;

}
