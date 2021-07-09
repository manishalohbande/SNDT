package com.scube.edu.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scube.edu.model.BranchMasterEntity;
import com.scube.edu.model.CollegeMaster;
import com.scube.edu.model.CutomizationEntity;
import com.scube.edu.model.DocumentMaster;
import com.scube.edu.model.PassingYearMaster;
import com.scube.edu.model.RaiseDespute;
import com.scube.edu.model.SemesterEntity;
import com.scube.edu.model.StreamMaster;
import com.scube.edu.model.UserMasterEntity;
import com.scube.edu.model.VerificationRequest;
import com.scube.edu.repository.CustomizationRepository;
import com.scube.edu.repository.RaiseDisputeRepository;
import com.scube.edu.repository.UserRepository;
import com.scube.edu.repository.VerificationRequestRepository;
import com.scube.edu.request.StatusChangeRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.CollegeResponse;
import com.scube.edu.response.DisputeResponse;
import com.scube.edu.response.EmployerVerificationDocResponse;
import com.scube.edu.response.RequestTypeResponse;
import com.scube.edu.response.StudentVerificationDocsResponse;
import com.scube.edu.response.UserResponse;
import com.scube.edu.response.VerificationResponse;

@Service
public class SuperVerifierServiceImpl implements SuperVerifierService {
	
private static final Logger logger = LoggerFactory.getLogger(EmployerServiceImpl.class);
	
	BaseResponse	baseResponse	= null;  
	
	@Autowired
	VerificationRequestRepository verificationReqRepository;
	
	@Autowired 
	StreamService streamService;
	
	@Autowired
	RaiseDisputeRepository disputeRepo;
	 
	@Autowired 
	RequestTypeService reqTypeService;
	
	@Autowired
	YearOfPassingService yearOfPassService;
	
	@Autowired
	DocumentService	documentService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	CollegeSevice	collegeServices;
	
	@Autowired
	CustomizationRepository customizationRepository;
	
	@Autowired
	UserService userService;

	@Autowired
	SemesterService semesterService;
	
	@Autowired
	BranchMasterService branchMasterService;
	
	@Override
	public List<VerificationResponse> getVerificationDocList(String fromDate, String toDate) {
		
		logger.info("*******SuperVerifierServiceImpl getVerificationDocList*******");
		
		List<VerificationResponse> responseList = new ArrayList<>();
		
		List<VerificationRequest> list = verificationReqRepository.findByStatus(fromDate, toDate);
		
		for(VerificationRequest req: list) {
			
			VerificationResponse resp = new VerificationResponse();
			
			PassingYearMaster year = yearOfPassService.getYearById(req.getYearOfPassingId());
			
			DocumentMaster doc = documentService.getNameById(req.getDocumentId());
			
			StreamMaster stream = streamService.getNameById(req.getStreamId());
			
			
			Optional<UserMasterEntity> user = userRepository.findById(req.getVerifiedBy());
			UserMasterEntity userr = user.get();
			
			if(req.getRequestType() != null) {
			RequestTypeResponse request = reqTypeService.getNameById(req.getRequestType());
			resp.setRequest_type_id(request.getRequestType());
			}
			
			 SemesterEntity sem=semesterService.getSemById(req.getSemId());
				
			 BranchMasterEntity branch=branchMasterService.getbranchById(req.getBranchId());
				
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
			String strDate= formatter.format(req.getCreatedate());
			
			resp.setDoc_status(req.getDocStatus());
			resp.setSemId(req.getSemId());
			resp.setId(req.getId());
			resp.setApplication_id(req.getApplicationId());
//			closedDocResp.setCollege_name_id(req.getCollegeId());
			resp.setDoc_name(doc.getDocumentName()); //
			resp.setEnroll_no(req.getEnrollmentNumber());
			resp.setFirst_name(req.getFirstName());
			resp.setLast_name(req.getLastName());
			resp.setRemark(req.getRemark());
////			resp.setRequest_type_id(req.get);
//			resp.setStream_id(req.getStreamId());
//			resp.setUni_id(req.getUniversityId());
			resp.setUser_id(req.getUserId());
			resp.setVer_req_id(req.getVerRequestId());
			resp.setYear(year.getYearOfPassing());	
			resp.setUpload_doc_path(req.getUploadDocumentPath());
			resp.setStream_name(stream.getStreamName());
			resp.setReq_date(strDate);
			resp.setVerifier_name(userr.getFirstName() + " " + userr.getLastName());
			resp.setBranch_nm(branch.getBranchName());
			resp.setSemester(sem.getSemester());
			resp.setMonthOfPassing(req.getMonthOfPassing());
			resp.setPrnNo(req.getPrnNo());
			
			if(req.getCourierAddr() != null) {
			  resp.setCourierAddress(req.getCourierAddr());  
			}
			if(req.getResultCollectionType() != null) {
				if(req.getResultCollectionType().equalsIgnoreCase("1")) {
				  resp.setResultCollectionType("Email");
				}
				if(req.getResultCollectionType().equalsIgnoreCase("2")) {
				  resp.setResultCollectionType("Courier");
				}
				if(req.getResultCollectionType().equalsIgnoreCase("3")) {
				  resp.setResultCollectionType("Self Collection");
				}
			}
			responseList.add(resp);
			
		}
		
		return responseList;
	}

	@Override
	public List<StudentVerificationDocsResponse> setStatusForSuperVerifierDocument(StatusChangeRequest statusChangeRequest) throws Exception {
		
		logger.info("*******SuperVerifierServiceImpl setStatusForSuperVerifierDocument*******");
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
	    Date date = new Date();  
	    String currentDate = formatter.format(date);  
//	    Long roleId = Long.parseLong(statusChangeRequest.getRoleid());
		VerificationRequest veriReq =  verificationReqRepository.findById(statusChangeRequest.getId());
//		VerificationRequest veriReq = vr.get();
		
		System.out.println(veriReq.getDocStatus() + veriReq.getApplicationId());
		
		veriReq.setDocStatus(statusChangeRequest.getStatus());
		//veriReq.setClosedDate(date);
		if (statusChangeRequest.getRemark() != null) {
			if(veriReq.getRemark()!=null) {
		veriReq.setRemark(veriReq.getRemark()+" SVR_"+currentDate+"-"+statusChangeRequest.getRemark());
			}
			else {
				veriReq.setRemark(" SVR_"+currentDate+"-"+statusChangeRequest.getRemark());

			}
		}
		if (statusChangeRequest.getStatus().equalsIgnoreCase("Requested")) {
			veriReq.setAssignedTo((long) 0);
		}
		veriReq.setSvVerId(statusChangeRequest.getVerifiedby());
		veriReq.setSvActionDate(new Date());
		veriReq.setSuperVerStatus(statusChangeRequest.getStatus());
		
		verificationReqRepository.save(veriReq);
		
		if(statusChangeRequest.getStatus().equalsIgnoreCase("Approved") || 
				statusChangeRequest.getStatus().equalsIgnoreCase("SV_Approved") || 
				statusChangeRequest.getStatus().equalsIgnoreCase("Rejected") || 
				statusChangeRequest.getStatus().equalsIgnoreCase("SV_Rejected")) {
			
		UserResponse ume = userService.getUserInfoById(veriReq.getUserId());
		 
//		CutomizationEntity cutomizationEntity=customizationRepository.findByRoleId(roleId);
//		if(cutomizationEntity!=null)
//		{
//			if(cutomizationEntity.getEmailFlag().equals("Y")) {
//		
//		emailService.sendStatusMail(ume.getEmail(), veriReq.getId(), statusChangeRequest.getStatus());
//			}
//		}
		}

		
		return null;
	}

	@Override
	public List<DisputeResponse> getDisputeList() {
		
		logger.info("*******superVerifierServiceImpl getDisputeList*******");
		
		List<RaiseDespute> rd = disputeRepo.findByStatus("1");
		
		List<DisputeResponse> responses = new ArrayList<>();
		
		for(RaiseDespute res: rd) {
			
			DisputeResponse resp = new DisputeResponse();
			
			Optional<VerificationRequest> vr=verificationReqRepository.findById(res.getVerificationId());

			VerificationRequest vr1=vr.get();
			Long id = Long.parseLong(res.getCreateby());
			
			Optional<UserMasterEntity> ume = userRepository.findById(id);
			UserMasterEntity umee = ume.get();
			
			resp.setId(res.getId());
			resp.setCreated_by(res.getCreateby());
			resp.setEmailid(res.getContactPersonEmail());
			resp.setApplication_id(res.getApplicationId());
			resp.setPhone_no(res.getContactPersonPhone());
			resp.setReason(res.getReasonForDispute());
			resp.setVerification_id(res.getVerificationId());
			resp.setFirst_name(umee.getFirstName());
			resp.setLast_name(umee.getLastName());
			resp.setVrStatus(vr1.getDocStatus());
			resp.setSemId(vr1.getSemId());
			resp.setPrnNo(vr1.getPrnNo());
			
			RequestTypeResponse reqMaster = reqTypeService.getNameById(vr1.getRequestType());
			resp.setRequest_type_id(reqMaster.getRequestType());
			
			if(vr1.getCourierAddr() != null) {
				  resp.setCourierAddress(vr1.getCourierAddr());
			  }
			  
			  if(vr1.getResultCollectionType() != null) {
				  if(vr1.getResultCollectionType().equalsIgnoreCase("1")) {
					  resp.setResultCollectionType("Email");
				  }
				  if(vr1.getResultCollectionType().equalsIgnoreCase("2")) {
					  resp.setResultCollectionType("Courier");
				  }
				  if(vr1.getResultCollectionType().equalsIgnoreCase("3")) {
					  resp.setResultCollectionType("Self Collection");
				  }
			  }
			
			
			responses.add(resp);
			
			
		}
		
		return responses;
	}

	@Override
	public VerificationResponse getVerificationRequestDetails(long verification_id) {
		
		logger.info("*******DisputeServiceImpl getVerificationRequestDetails*******"+ verification_id);
		
		VerificationRequest vr = verificationReqRepository.findById(verification_id);
		
		VerificationResponse resp = new VerificationResponse();
		
		PassingYearMaster year = yearOfPassService.getYearById(vr.getYearOfPassingId());
		
		DocumentMaster doc = documentService.getNameById(vr.getDocumentId());
		
		StreamMaster stream = streamService.getNameById(vr.getStreamId());
		
		CollegeResponse college = collegeServices.getNameById(vr.getCollegeId());
		
		Optional<UserMasterEntity> user = userRepository.findById(vr.getVerifiedBy());
		UserMasterEntity userr = user.get();
		
		 SemesterEntity sem=semesterService.getSemById(vr.getSemId());
			
		 BranchMasterEntity branch=branchMasterService.getbranchById(vr.getBranchId());
		
		if(vr.getRequestType() != null) {
			RequestTypeResponse request = reqTypeService.getNameById(vr.getRequestType());
			resp.setRequest_type_id(request.getRequestType());
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
			String strDate= formatter.format(vr.getCreatedate());
			
			resp.setDoc_status(vr.getDocStatus());
			resp.setId(vr.getId());
			resp.setApplication_id(vr.getApplicationId());
//			resp.setCollege_name_id(college.getCollegeName());
			resp.setCollege_name(college.getCollegeName());
			resp.setDoc_name(doc.getDocumentName()); //
			resp.setEnroll_no(vr.getEnrollmentNumber());
			resp.setFirst_name(vr.getFirstName());
			resp.setLast_name(vr.getLastName());
			resp.setSemester(sem.getSemester());
			resp.setBranch_nm(branch.getBranchName());
////			resp.setRequest_type_id(vr.get);
//			resp.setStream_id(vr.getStreamId());
//			resp.setUni_id(vr.getUniversityId());
			resp.setUser_id(vr.getUserId());
			resp.setVer_req_id(vr.getVerRequestId());
			resp.setYear(year.getYearOfPassing());	
			resp.setUpload_doc_path(vr.getUploadDocumentPath());
			resp.setStream_name(stream.getStreamName());
			resp.setReq_date(strDate);
			resp.setMonthOfPassing(vr.getMonthOfPassing());
			resp.setPrnNo(vr.getPrnNo());
			resp.setVerifier_name(userr.getFirstName() + " " + userr.getLastName());
			if(vr.getRemark()==null||vr.getRemark().equals(""))
			{
				resp.setRemark("");

			}
			else {
			resp.setRemark(vr.getRemark());
			}
		return resp;
	}
	
	

}
