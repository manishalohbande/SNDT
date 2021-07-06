package com.scube.edu.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.scube.edu.model.UniversityStudentDocument;
import com.scube.edu.request.UniversityStudentRequest;
import com.scube.edu.response.UniversityResponse;
import com.scube.edu.response.UniversityStudDocResponse;

public interface AssociateManagerService {
	

	public  HashMap<String,List<UniversityStudDocResponse>>  saveStudentInfo( List<UniversityStudDocResponse> list,Long userid) throws IOException;

	public List<UniversityStudDocResponse> ReviewStudentData(MultipartFile excelfile,MultipartFile datafile) throws IOException;

	public List<UniversityResponse>  getStudentData(UniversityStudentRequest universityStudData);


	public String saveassociatefilepath (MultipartFile datafile);


public  HashMap<String,List<UniversityStudDocResponse>> AutosaveStudentInfo(List<UniversityStudDocResponse> list, Long userid) throws IOException ;
}