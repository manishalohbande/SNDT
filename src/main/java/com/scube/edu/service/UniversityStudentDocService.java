package com.scube.edu.service;

import com.scube.edu.model.PassingYearMaster;

import com.scube.edu.model.UniversityStudentDocument;

public interface UniversityStudentDocService {
	
	
	
	UniversityStudentDocument getDocDataByFourFields(String  enrollNo, String yearOfPassing, Long semId, Long streamId, String monthOfPassing);

	UniversityStudentDocument getUniversityDocDataById(Long id);

	UniversityStudentDocument getRecordByPrnNoAndSemId(String prnNo, Long semId);

}
