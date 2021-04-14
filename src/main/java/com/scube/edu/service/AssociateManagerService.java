package com.scube.edu.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.scube.edu.model.UniversityStudentDocument;

public interface AssociateManagerService {
	
	public List<String>  saveStudentInfo( List<UniversityStudentDocument> list) throws IOException;
	public List<UniversityStudentDocument> ReviewStudentData(MultipartFile excelfile,MultipartFile datafile) throws IOException;

	//UniversityStudentDocument  getStudentDataByenrollmentNo(String enrollmentNo);


}
