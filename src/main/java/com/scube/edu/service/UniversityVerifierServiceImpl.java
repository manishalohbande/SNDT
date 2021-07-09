package com.scube.edu.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lowagie.text.BadElementException;
import com.scube.edu.model.BranchMasterEntity;
import com.scube.edu.model.CutomizationEntity;
import com.scube.edu.model.DocumentMaster;
import com.scube.edu.model.PassingYearMaster;
import com.scube.edu.model.SemesterEntity;
import com.scube.edu.model.StreamMaster;
import com.scube.edu.model.UserMasterEntity;
import com.scube.edu.model.VerificationRequest;
import com.scube.edu.repository.CustomizationRepository;
import com.scube.edu.repository.UniversityVerifierRepository;
import com.scube.edu.repository.UserRepository;
import com.scube.edu.repository.VerificationRequestRepository;
import com.scube.edu.request.StatusChangeRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.RequestTypeResponse;
import com.scube.edu.response.StudentVerificationDocsResponse;
import com.scube.edu.response.UniversityVerifierResponse;
import com.scube.edu.response.UserResponse;
import com.scube.edu.response.VerificationResponse;

@Service
public class UniversityVerifierServiceImpl implements UniversityVerifierService {
	private static final Logger logger = LoggerFactory.getLogger(UniversityStudentDocServiceImpl.class);

	BaseResponse baseResponse = null;

	@Autowired
	UniversityVerifierRepository universityVerifierRepository;

	@Autowired
	VerificationRequestRepository verificationReqRepository;

	@Autowired
	StreamService streamService;

	@Autowired
	RequestTypeService reqTypeService;

	@Autowired
	YearOfPassingService yearOfPassService;

	@Autowired
	DocumentService documentService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	EmailService emailService;

	@Autowired
	UserService userService;
	
	@Autowired
	CustomizationRepository customizationRepository;
	
	@Autowired
	SemesterService semesterService;
	
	@Autowired
	BranchMasterService branchMasterService;

	@Override
	public List<UniversityVerifierResponse> getUniversityVerifierRequestList() throws Exception {
		logger.info("*******UniversityVerifierServiceImpl getUniversityVerifierRequestList*******");

		List<UniversityVerifierResponse> responseList = new ArrayList<>();

		List<VerificationRequest> list = universityVerifierRepository.findByStatus();
		logger.info("uvlist" + list);
		for (VerificationRequest req : list) {

			UniversityVerifierResponse resp = new UniversityVerifierResponse();
			
			resp.setSemId(req.getSemId());

			PassingYearMaster year = yearOfPassService.getYearById(req.getYearOfPassingId());

			DocumentMaster doc = documentService.getNameById(req.getDocumentId());

			StreamMaster stream = streamService.getNameById(req.getStreamId());
			
			 SemesterEntity sem=semesterService.getSemById(req.getSemId());
				
			  BranchMasterEntity branch=branchMasterService.getbranchById(req.getBranchId());
				

			// Date date=new Date();
			Date closedDate = null;

			Integer days = null;
			Date date = new Date();
			
			if (req.getSvActionDate() != null || req.getVerActionDate()!=null) {
			
				if(req.getSvActionDate()!=null) {
				if(req.getSvActionDate().compareTo(req.getVerActionDate())>0) {
				closedDate = req.getSvActionDate();
				}
				else
				{
				 closedDate=req.getVerActionDate();
				}
				}	else{
					if(req.getVerActionDate()!=null) {
					 closedDate=req.getVerActionDate();
					}

				}
				logger.info("date" + closedDate);
				long difference_In_Time = date.getTime() - closedDate.getTime();
				logger.info("time" + difference_In_Time);
				long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
				days = (int) (999 - difference_In_Days);
			}
			
			resp.setBranch_nm(branch.getBranchName());
			  resp.setSemester(sem.getSemester());
			  
			if (days != null) {
				resp.setNoOfDays(days);
			}

			if (doc != null) {
				resp.setStatus(req.getDocStatus());
				resp.setDocName(doc.getDocumentName());
			}
			if (year != null) {
				resp.setYearofPassing(year.getYearOfPassing());
			}
			if (stream != null) {
				resp.setStream(stream.getStreamName());
			}
			resp.setFullName(req.getFirstName() + " " + req.getLastName());
			resp.setId(req.getId());
			resp.setFilePath(req.getUploadDocumentPath());
			resp.setMonthOfPassing(req.getMonthOfPassing());
			resp.setPrnNo(req.getPrnNo());
			
			RequestTypeResponse reqMaster = reqTypeService.getNameById(req.getRequestType());
			resp.setRequest_type_id(reqMaster.getRequestType());
			
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

	@Value("${file.imagepath-dir}")
    private String imageLocation;
	
	@Override
	public List<StudentVerificationDocsResponse> setStatusForUniversityDocument(
			StatusChangeRequest statusChangeRequest) throws Exception {

		logger.info("*******UniversityVerifierServiceImpl setStatusForUniversityDocument*******");

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		String currentDate = formatter.format(date);

		Long roleid = Long.parseLong(statusChangeRequest.getRoleid());
		
		VerificationRequest entt = verificationReqRepository.findById(statusChangeRequest.getId());

		System.out.println("------------" + entt.getDocStatus() + entt.getApplicationId());

		entt.setDocStatus(statusChangeRequest.getStatus());
//		entt.setVerifiedBy(statusChangeRequest.getVerifiedby());
		if (statusChangeRequest.getStatus().equalsIgnoreCase("UN_Rejected")) {
			if(entt.getRemark()!=null) {
			entt.setRemark(entt.getRemark() + " UN_" + currentDate + "-" + statusChangeRequest.getRemark());
		}
			else
			{
				entt.setRemark(" UN_" + currentDate + "-" + statusChangeRequest.getRemark());

			}
		}
		entt.setClosedDate(date);
		entt.setUnVerId(statusChangeRequest.getVerifiedby());
		entt.setUnActionDate(new Date());
		entt.setUniVerStatus(statusChangeRequest.getStatus());
		verificationReqRepository.save(entt);
				
		if(statusChangeRequest.getStatus().equalsIgnoreCase("UN_Approved_Pass") || statusChangeRequest.getStatus().equalsIgnoreCase("UN_Approved_Fail") || 
				statusChangeRequest.getStatus().equalsIgnoreCase("UN_Rejected")) {
			
			UserResponse ume = userService.getUserInfoById(entt.getUserId());
			CutomizationEntity cutomizationEntity=customizationRepository.findByRoleId(roleid);
			if(cutomizationEntity!=null)
			{
				if(cutomizationEntity.getEmailFlag().equals("Y")) {
					
					String altEmail = entt.getAltEmail();
					emailService.sendStatusMail(altEmail, ume.getEmail(), entt.getId() , statusChangeRequest.getStatus(), imageLocation);
			
			
				}
			}
			}
	

		return null;
	}

	@Override
	public List<UniversityVerifierResponse> getUniversityVerifierPhysicalCollectionRequestList(String fromDate,
			String toDate) {
		
		logger.info("**UniversityVerifierServiceImpl getUniversityVerifierPhysicalCollectionRequestList**"+ fromDate +"-- "+ toDate);
		
		List<UniversityVerifierResponse> responseList = new ArrayList<>();
		List<VerificationRequest> list = universityVerifierRepository.findByRequestTypeAndDocStatusAndResultCollectionType(fromDate, toDate);
		
		for(VerificationRequest req: list) {
			
			UniversityVerifierResponse resp = new UniversityVerifierResponse();
			
			PassingYearMaster year = yearOfPassService.getYearById(req.getYearOfPassingId());

			DocumentMaster doc = documentService.getNameById(req.getDocumentId());

			StreamMaster stream = streamService.getNameById(req.getStreamId());
			
			 SemesterEntity sem=semesterService.getSemById(req.getSemId());
				
			  BranchMasterEntity branch=branchMasterService.getbranchById(req.getBranchId());
			
			resp.setApplicationId(req.getApplicationId());
			
			if (doc != null) {
				resp.setStatus(req.getDocStatus());
				resp.setDocName(doc.getDocumentName());
			}
			if (year != null) {
				resp.setYearofPassing(year.getYearOfPassing());
			}
			if (stream != null) {
				resp.setStream(stream.getStreamName());
			}
			resp.setFullName(req.getFirstName() + " " + req.getLastName());
			resp.setId(req.getId());
			resp.setFilePath(req.getUploadDocumentPath());
			resp.setMonthOfPassing(req.getMonthOfPassing());
			resp.setPrnNo(req.getPrnNo());
			resp.setSemester(sem.getSemester());
			resp.setSemId(req.getSemId());
			
			RequestTypeResponse reqMaster = reqTypeService.getNameById(req.getRequestType());
			resp.setRequest_type_id(reqMaster.getRequestType());
			
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

}
