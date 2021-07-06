package com.scube.edu.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.scube.edu.controller.MasterController;
import com.scube.edu.model.BranchMasterEntity;
import com.scube.edu.model.CollegeMaster;
import com.scube.edu.model.PassingYearMaster;
import com.scube.edu.model.SemesterEntity;
import com.scube.edu.model.StreamMaster;
import com.scube.edu.model.UniversityStudentDocument;
import com.scube.edu.repository.CollegeRepository;
import com.scube.edu.repository.StreamRepository;
import com.scube.edu.repository.UniversityStudentDocRepository;
import com.scube.edu.repository.YearOfPassingRepository;
import com.scube.edu.request.UniversityStudentRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.StudentVerificationDocsResponse;
import com.scube.edu.response.UniversityStudentDocumentResponse;
import com.scube.edu.response.VerificationResponse;
import com.scube.edu.util.FileStorageService;

@Service
public class AssociateSupervisorServiceImpl implements AssociateSupervisorService{
	
	private static final Logger logger = LoggerFactory.getLogger(AssociateSupervisorServiceImpl.class);
	
	@Autowired
	UniversityStudentDocRepository 	universityStudentDocRepository ;
	
	@Autowired
	CollegeRepository collegeRespository;
	 
	@Autowired
	StreamRepository  streamRespository;
	
	@Autowired
	UniversityStudentDocService  stuDocService;
	
	@Autowired
	YearOfPassingRepository yearOfPassingRespository;
	
	@Autowired
	private FileStorageService fileStorageService;

	 
	 @Autowired
	SemesterService semesterService;
		
	@Autowired
	BranchMasterService branchMasterService;
		
	
	@Override
	public boolean deleteRecordById(long id, HttpServletRequest request) throws Exception {
		
		logger.info("*******AssociateSupervisorServiceImpl deleteRecordById*******");
		
		Optional<UniversityStudentDocument> usd = universityStudentDocRepository.findById(id);
		UniversityStudentDocument uniRecord = usd.get();
		
		if(uniRecord == null) {
			   
			throw new Exception(" Invalid ID");
			
		}else {
			
			universityStudentDocRepository.deleteById(id);
			return true;
			
		}
		
		
	}

	@Override
	public boolean updateRecordById(UniversityStudentRequest universityStudentRequest, HttpServletRequest request) throws Exception {
		
		logger.info("*******AssociateSupervisorServiceImpl updateRecordById*******");
		
		Optional<UniversityStudentDocument> usd = universityStudentDocRepository.findById(universityStudentRequest.getId());
		UniversityStudentDocument ogRecord = usd.get();
		
		if(ogRecord != null) {
			logger.info("NOT NULL---");
			
			UniversityStudentDocument editRecord = new UniversityStudentDocument();
			
			editRecord.setId(universityStudentRequest.getId());
			//editRecord.setFirstName(ogRecord.getFirstName());
			//editRecord.setLastName(ogRecord.getLastName());

			editRecord.setEnrollmentNo(universityStudentRequest.getEnrollmentNo());
			if(universityStudentRequest.getFilePath() != "") {
			//editRecord.setOriginalDOCuploadfilePath(universityStudentRequest.getFilePath());
			}else{
				//editRecord.setOriginalDOCuploadfilePath(ogRecord.getOriginalDOCuploadfilePath());
			}
			editRecord.setCollegeId( ogRecord.getCollegeId());
			editRecord.setPassingYearId( Long.parseLong(universityStudentRequest.getPassingYearId()));
			editRecord.setStreamId( Long.parseLong(universityStudentRequest.getStreamId()));
			editRecord.setBranchId(ogRecord.getBranchId());
			editRecord.setSemId(Long.parseLong(universityStudentRequest.getSemId()));
			editRecord.setMonthOfPassing(universityStudentRequest.getMonthOfPassing());
			editRecord.setUpdatedate(new Date());
			editRecord.setUpdateby(universityStudentRequest.getUserId());
			universityStudentDocRepository.save(editRecord);
			
			return true;
			
		}else {
			throw new Exception("Request Body cannot be empty.");
		}
		
		
	}

	@Override
	public UniversityStudentDocumentResponse getRecordById(long id, HttpServletRequest request) {
		
		logger.info("*******AssociateSupervisorServiceImpl getRecordById*******");
		
		Optional<UniversityStudentDocument> usd = universityStudentDocRepository.findById(id);
		UniversityStudentDocument ogRecord = usd.get();
		
		UniversityStudentDocumentResponse resp = new UniversityStudentDocumentResponse();
		
//		Optional<CollegeMaster> cm = collegeRespository.findById(ogRecord.getCollegeId());
//		CollegeMaster college = cm.get();
		 
		Optional<StreamMaster> streaminfo = streamRespository.findById(ogRecord.getStreamId());
		StreamMaster stream = streaminfo.get();
		 
		Optional<PassingYearMaster> passingyrinfo = yearOfPassingRespository.findById(ogRecord.getPassingYearId());
		PassingYearMaster passingyr = passingyrinfo.get();
		
		 SemesterEntity sem=semesterService.getSemById(ogRecord.getSemId());
			
//		 BranchMasterEntity branch=branchMasterService.getbranchById(ogRecord.getBranchId());
		 
		resp.setId(ogRecord.getId());
//		resp.setCollegeName(college.getCollegeName());
		resp.setEnrollmentNo(ogRecord.getEnrollmentNo());
	//	resp.setFirstName(ogRecord.getFirstName());
		//resp.setLastName(ogRecord.getLastName());
		//resp.setOriginalDOCuploadfilePath(ogRecord.getOriginalDOCuploadfilePath());
		resp.setPassingYear(passingyr.getYearOfPassing());
		resp.setStream(stream.getStreamName());
		resp.setPassingYearId(ogRecord.getPassingYearId());
		resp.setStreamId(ogRecord.getStreamId());
		resp.setCollegeId(ogRecord.getCollegeId());
//		resp.setBranchNm(branch.getBranchName());
		resp.setBranchId(ogRecord.getBranchId());
		resp.setSemester(sem.getSemester());
		resp.setSemesterId(ogRecord.getSemId());
		resp.setMonthOfPassing(ogRecord.getMonthOfPassing());
		return resp;
	}
	
	@Value("${file.awsORtest}")
    private String awsORtest;
	
	public String saveDocument (MultipartFile file) {
		String fileSubPath = "file/";
		String flag = "2";
		String filePath;
		
		if(awsORtest.equalsIgnoreCase("TEST") || awsORtest.equalsIgnoreCase("LOCAL")) {
			 
			 filePath = fileStorageService.storeFile(file, fileSubPath, flag);
		 }else {
			 filePath = fileStorageService.storeFileOnAws(file , flag);
		 }
		
		return filePath;
		
	}

	@Override
	public VerificationResponse verifyDocument(Long id) {
		
		logger.info("*******AssociateSupervisorServiceImpl verifyDocument*******");
		
		VerificationResponse resEntity = new VerificationResponse();
		 
		 UniversityStudentDocument doc = stuDocService.getUniversityDocDataById(id);
		 
		 if(doc != null) {
		 resEntity.setOriginalDocUploadFilePath("/verifier/getimage/U/"+doc.getId());
		 }
		 logger.info("University upload path set just above");
		
		return resEntity;
	}

}
