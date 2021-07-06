package com.scube.edu.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@ToString
public class UniversityStudDocResponse {
	
	private long id;
	private String CollegeName;
	private String Stream;
	private String EnrollmentNo;
	private String PassingYear;
	private String reason;
	private String branch_nm;
	private String semester;
	private String monthOfPassing;
	private String dataType;


}
