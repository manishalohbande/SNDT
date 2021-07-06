package com.scube.edu.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scube.edu.model.BranchMasterEntity;
import com.scube.edu.model.DocumentMaster;
import com.scube.edu.model.PassingYearMaster;
import com.scube.edu.model.SemesterEntity;
import com.scube.edu.model.StreamMaster;
import com.scube.edu.model.VerificationRequest;
import com.scube.edu.repository.VerificationRequestRepository;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.EmployerVerificationDocResponse;
import com.scube.edu.response.RequestTypeResponse;
import com.scube.edu.response.StudentVerificationDocsResponse;
import com.scube.edu.response.VerificationResponse;

@Service
public class EmployerServiceImpl implements EmployerService {
	
private static final Logger logger = LoggerFactory.getLogger(EmployerServiceImpl.class);
	
	BaseResponse	baseResponse	= null;  
	
	@Autowired
	VerificationRequestRepository verificationReqRepository;
	
	@Autowired 
	StreamService streamService;
	 
	@Autowired 
	RequestTypeService reqTypeService;
	
	@Autowired
	YearOfPassingService yearOfPassService;
	
	@Autowired
	DocumentService	documentService;
	
	@Autowired
	SemesterService semesterService;
	
	@Autowired
	BranchMasterService branchMasterService;
	@Override
	public List<VerificationResponse> getOneMonthVerificationDocsDataByUserid(long userId, String fromDate, String toDate ) throws Exception {
		
		logger.info("********EmployerServiceImpl getOneMonthVerificationDocsDataByUserid");
		logger.info("------------"+ userId + fromDate + toDate);
		
		List<VerificationResponse> responseList = new ArrayList<>();
		
		List<VerificationRequest> list = verificationReqRepository.findByUserIdAndDates(userId , fromDate , toDate);
		
		//Date should be in yyyy-MM-dd format
		
		for(VerificationRequest req: list) {
			
			VerificationResponse resp = new VerificationResponse();
			
			PassingYearMaster year = yearOfPassService.getYearById(req.getYearOfPassingId());
			
			DocumentMaster doc = documentService.getNameById(req.getDocumentId());
		
			StreamMaster stream = streamService.getNameById(req.getStreamId());
			
			SemesterEntity sem=semesterService.getSemById(req.getSemId());
			
			BranchMasterEntity branch=branchMasterService.getbranchById(req.getBranchId());
			
			if(req.getRequestType() != null) {
			RequestTypeResponse request = reqTypeService.getNameById(req.getRequestType());
			resp.setRequest_type_id(request.getRequestType());
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
			String strDate= formatter.format(req.getCreatedate());
			
			resp.setDoc_status(req.getDocStatus());
			resp.setId(req.getId());
			resp.setApplication_id(req.getApplicationId());
//			closedDocResp.setCollege_name_id(req.getCollegeId());
			resp.setDoc_name(doc.getDocumentName()); //
			resp.setEnroll_no(req.getEnrollmentNumber());
			resp.setFirst_name(req.getFirstName());
			resp.setLast_name(req.getLastName());
			resp.setBranch_nm(branch.getBranchName());
			resp.setSemester(sem.getSemester());
////			resp.setRequest_type_id(req.get);
//			resp.setStream_id(req.getStreamId());
//			resp.setUni_id(req.getUniversityId());
			resp.setUser_id(req.getUserId());
			resp.setVer_req_id(req.getVerRequestId());
			resp.setYear(year.getYearOfPassing());	
			resp.setUpload_doc_path(req.getUploadDocumentPath());
			resp.setStream_name(stream.getStreamName());
			resp.setReq_date(strDate);
			resp.setMonthOfPassing(req.getMonthOfPassing());
			resp.setMonthOfPassing(req.getMonthOfPassing());
			resp.setPrnNo(req.getPrnNo());
			responseList.add(resp);
			
		}
		
		return responseList;
	}
	
	

}
