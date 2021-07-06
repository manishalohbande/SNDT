package com.scube.edu.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@ToString
public class UniversityResponse {
	
	private String prnNo;
	private String name;
	private String seatNo;
	private String stream;// will have to get name By id from streamService
	private String branch;// same as above from respective service
	private String semester;// same as above from respective service
	private String yearOfPassing;
	private int semTotMax;
	private int semGrdTot;
	private String semGrace;
	private String semClass;
	private int semGpa;
	private String semGrade;
	private String resDesc;
	
	

}
