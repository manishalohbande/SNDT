package com.scube.edu.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scube.edu.controller.MasterController;
import com.scube.edu.model.UniversityStudentDocument;
import com.scube.edu.repository.UniversityStudentDocRepository;
import com.scube.edu.util.FileStorageService;

@Service
public class AssociateManagerServiceImpl implements AssociateManagerService{

	private static final Logger logger = LoggerFactory.getLogger(MasterController.class);

	@Autowired
	 UniversityStudentDocRepository 	 universityStudentDocRepository ;
	
	 @Autowired
	 private FileStorageService fileStorageService;
	
	private XSSFWorkbook workbook;
	
	
	@Override
	public  HashMap<String,List<UniversityStudentDocument>> saveStudentInfo(List<UniversityStudentDocument> list) throws IOException {
		
		 List<UniversityStudentDocument> studentDataList = new ArrayList<UniversityStudentDocument>();
		 UniversityStudentDocument docEntity =null;
		 List<UniversityStudentDocument> rejectedData=new ArrayList<UniversityStudentDocument>();
		 HashMap<String,List<UniversityStudentDocument>> datalist=new HashMap<String,List<UniversityStudentDocument>>();
		 for(UniversityStudentDocument Data:list) {
			 			
			 docEntity=universityStudentDocRepository.findByEnrollmentNo(Data.getEnrollmentNo());
			
			 if(docEntity==null) {	
				 UniversityStudentDocument studentData=new UniversityStudentDocument();
				 studentData.setFirstName(Data.getFirstName());
		        studentData.setLastName(Data.getLastName());
		        studentData.setCollegeName(Data.getCollegeName());	
		        studentData.setStream(Data.getStream());	
		        studentData.setEnrollmentNo(Data.getEnrollmentNo());
		        studentData.setPassingYear(Data.getPassingYear());	
		        studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
		        studentDataList.add(studentData);    
			 }
			 else
			 {	 UniversityStudentDocument studentData=new UniversityStudentDocument();
			     studentData.setFirstName(Data.getFirstName());
		        studentData.setLastName(Data.getLastName());
		        studentData.setCollegeName(Data.getCollegeName());	
		        studentData.setStream(Data.getStream());	
		        studentData.setEnrollmentNo(Data.getEnrollmentNo());
		        studentData.setPassingYear(Data.getPassingYear());	
		        studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
		        //studentDataList.add(studentData);   			
				rejectedData.add(studentData);				
			 }
			
		 }
		 logger.info("rejectedData : "+rejectedData);
		 universityStudentDocRepository.saveAll(studentDataList);
		 datalist.put("savedStudentData", studentDataList);
		 datalist.put("RejectedData", rejectedData);
		 return datalist;
		 
	}


	@Override
	public List<UniversityStudentDocument> ReviewStudentData(MultipartFile excelfile, MultipartFile datafile) throws IOException {
		
		 workbook = new XSSFWorkbook(excelfile.getInputStream());
		 XSSFSheet worksheet = workbook.getSheetAt(0);
		 List<UniversityStudentDocument> studentDataReviewList = new ArrayList<UniversityStudentDocument>();
		 List<UniversityStudentDocument> response = new ArrayList<UniversityStudentDocument>();
		
		 String fileSubPath = "file/";
		 String filePath = fileStorageService.storeFile(datafile , fileSubPath);
		 for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
			 
			 XSSFRow row = worksheet.getRow(i);
			 UniversityStudentDocument docEntity =null;
			 
			 
				 UniversityStudentDocument studentData = new UniversityStudentDocument();	            
		        
		        studentData.setFirstName(row.getCell(0).getStringCellValue());
		        studentData.setLastName(row.getCell(1).getStringCellValue());
		        studentData.setCollegeName(row.getCell(2).getStringCellValue());	
		        studentData.setStream(row.getCell(3).getStringCellValue());	
		        studentData.setEnrollmentNo(row.getCell(4).getStringCellValue());
		        studentData.setPassingYear((int) row.getCell(5).getNumericCellValue());	
		        studentData.setOriginalDOCuploadfilePath(filePath);
		        studentDataReviewList.add(studentData);    
			 }
	
		 return studentDataReviewList;
	}

	@Override
	public List<UniversityStudentDocument> getStudentData(Integer yrOfPassing,String stream,String clgNm ) {
		
		logger.info("********AssociateManagerServiceImpl getStudentData********");

		 List<UniversityStudentDocument> studentDataList = new ArrayList<UniversityStudentDocument>();
		 
		 studentDataList=universityStudentDocRepository.findAllByCollegeNameOrStreamOrPassingYear( clgNm, stream,yrOfPassing );
		return studentDataList;
	}

}
