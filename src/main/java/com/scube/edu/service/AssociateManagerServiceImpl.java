package com.scube.edu.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import com.scube.edu.controller.MasterController;
import com.scube.edu.model.BranchMasterEntity;
import com.scube.edu.model.CollegeMaster;
import com.scube.edu.model.MonthOfPassing;
import com.scube.edu.model.PassingYearMaster;
import com.scube.edu.model.SemesterEntity;
import com.scube.edu.model.StreamMaster;
import com.scube.edu.model.UniversityStudentDocument;
import com.scube.edu.repository.CollegeRepository;
import com.scube.edu.repository.MonthOfPassingRepository;
import com.scube.edu.repository.StreamRepository;
import com.scube.edu.repository.UniversityStudentDocRepository;
import com.scube.edu.repository.YearOfPassingRepository;
import com.scube.edu.request.UniversityStudentRequest;
import com.scube.edu.response.UniversityStudDocResponse;
import com.scube.edu.response.UniversityStudentDocumentResponse;
import com.scube.edu.util.FileStorageService;

@Service
public class AssociateManagerServiceImpl implements AssociateManagerService{

	private static final Logger logger = LoggerFactory.getLogger(AssociateManagerServiceImpl.class);

	@Autowired
	 UniversityStudentDocRepository 	 universityStudentDocRepository ;
	
	@PersistenceUnit
	 private EntityManagerFactory emf;
	
	 @Autowired
	 private FileStorageService fileStorageService;
	 
	 @Autowired
	CollegeRepository collegeRespository;
	 
	 @Autowired
		StreamRepository  streamRespository;
	 @Autowired
		YearOfPassingRepository yearOfPassingRespository;
	 @Autowired
	 MonthOfPassingRepository monthOfPassingRepository;
	 
	 @Autowired
		SemesterService semesterService;
		
		@Autowired
		BranchMasterService branchMasterService;
		
	
	private XSSFWorkbook workbook;
	
	
	@Override
	public  HashMap<String,List<UniversityStudDocResponse>> saveStudentInfo(List<UniversityStudDocResponse> list, Long userid) throws IOException {
		
		 List<UniversityStudentDocument> studentDataList = new ArrayList<UniversityStudentDocument>();
		 List<UniversityStudDocResponse> savedStudDataList = new ArrayList<UniversityStudDocResponse>();

		 UniversityStudentDocument docEntity =null;
		 List<UniversityStudDocResponse> rejectedData=new ArrayList<UniversityStudDocResponse>();
		 HashMap<String,List<UniversityStudDocResponse>> datalist=new HashMap<String,List<UniversityStudDocResponse>>();
		
		 for(UniversityStudDocResponse Data:list) {
			 
			    Long clgnm = null;
				Long passyr = null;
				Long strm = null;
				Long semId = null;
				Long branchId = null;
				String monthnm=null;
			/*
			 * CollegeMaster collegeEntities =
			 * collegeRespository.findByCollegeName(Data.getCollegeName()); */
				MonthOfPassing month=monthOfPassingRepository.findByMonthOfPassing(Data.getMonthOfPassing());
			 StreamMaster stream =streamRespository.findByStreamName(Data.getStream());
			
			 if(month!=null) {
				 monthnm=month.getMonthOfPassing();
			 }
			 if(stream!=null) 
			  { 
				  strm=stream.getId(); 
			  logger.info("strm"+strm);
			  }
			  PassingYearMaster passingyr=yearOfPassingRespository.findByYearOfPassing(Data.getPassingYear());
			 
				 SemesterEntity sem=semesterService.getSemIdByNm(Data.getSemester(),strm);					
				//  BranchMasterEntity branch=branchMasterService.getbranchIdByname(Data.getBranch_nm(),strm);
				

			 			String enrollNo=Data.getEnrollmentNo();
			 			//String fnm=Data.getFirstName();
			 			//String lnm=Data.getLastName();
			 			
			/*
			 * if(collegeEntities!=null) { clgnm=collegeEntities.getId();
			 * logger.info("clgnm"+clgnm); }*/
			   if(passingyr!=null) {
			 
			  passyr=passingyr.getId();
			  logger.info("passyr"+passyr); }
			 
			 			if(sem!=null) {

				 			 semId=sem.getId();
				 			logger.info("semId"+semId);
				 			}
			/*
			 * if(branch!=null) {
			 * 
			 * branchId=branch.getId(); logger.info("branchId"+branchId); }
			 */
			 			
			 			
			 docEntity=universityStudentDocRepository.findByEnrollmentNoAndStreamIdAndPassingYearIdAndSemIdAndMonthOfPassing(enrollNo,strm,passyr,semId,monthnm);
			String reason = null;
			 if(docEntity==null) {	
				
				
				if(passingyr!=null&&sem!=null && !Data.getEnrollmentNo().equals("")&&month!=null&&!Data.getOriginalDOCuploadfilePath().equals("ImageNotAvailable")) {
					//if(Data.getOriginalDOCuploadfilePath().equals("FileNotAvailable")) {
				UniversityStudentDocument studentData=new UniversityStudentDocument();
				UniversityStudDocResponse SavestudentData=new UniversityStudDocResponse();

			//	studentData.setFirstName(Data.getFirstName());
		      //  studentData.setLastName(Data.getLastName());
		       // studentData.setCollegeId(collegeEntities.getId());	
		        studentData.setStreamId(stream.getId());	
		        studentData.setEnrollmentNo(Data.getEnrollmentNo());
		        studentData.setPassingYearId(passingyr.getId());	
		        //studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
		        studentData.setSemId(sem.getId());
		       // studentData.setBranchId(branch.getId());
		        studentData.setCreatedate(new Date());
		        studentData.setCreateby(userid.toString());
		        studentData.setMonthOfPassing(monthnm);
		        studentDataList.add(studentData);    
		        
		        //SavestudentData.setFirstName(Data.getFirstName());
		       // SavestudentData.setLastName(Data.getLastName());
		      //  SavestudentData.setCollegeName(Data.getCollegeName());	
		        SavestudentData.setStream(Data.getStream());	
		        SavestudentData.setEnrollmentNo(Data.getEnrollmentNo());
		        SavestudentData.setPassingYear(Data.getPassingYear());	
		        SavestudentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
		      //  SavestudentData.setReason(reason);
		        //SavestudentData.setBranch_nm(Data.getBranch_nm());
		        SavestudentData.setSemester(Data.getSemester());
		        SavestudentData.setMonthOfPassing(monthnm);

		        savedStudDataList.add(SavestudentData);	
		        
				
				}
				
					 else
					 {	
						 UniversityStudDocResponse studentData=new UniversityStudDocResponse();
						 if(passingyr==null|| sem==null||stream==null||month==null) {
						 reason="Invalid";
						  if(stream==null) { 
					    	   
					    	   reason=reason+" Stream";
						      if(passingyr==null||sem==null||month==null) { 
						    	  
							  reason=reason+",";
						  
						  } }
						 
							  if(passingyr==null)
							 {
								 reason=reason+" Year of Passing";
								 if(sem==null||month==null)
								 {
									 reason=reason+",";

								 }

							 }
							  if(sem==null)
							 {
								 reason=reason+" Semester";
							/*
							 * if(branch==null) { reason=reason+",";
							 * 
							 * }
							 * 
							 * } if(branch==null) { reason=reason+" Branch";
							 * 
							 */
								 if(month==null)
								 {
									 reason=reason+",";

								 }
							 }
							  if(month==null) {
									
									 reason=reason+" Month Of Passing";

								 }
						 }
					/*
					 * if(collegeEntities==null) { reason=reason+" College Name";
					  if(stream==null||passingyr==null||sem==null||branch==null) {
					  reason=reason+",";
					 * 
					  }
					  } */
				     
						 if( Data.getOriginalDOCuploadfilePath().equals("ImageNotAvailable")) {
							
							 if(reason!=null) {
								 reason=reason+", ImageNotAvailable";

							 }
							 else {
								 reason=" ImageNotAvailable";

							 }
						 }
						
						 if(Data.getEnrollmentNo()==null || Data.getEnrollmentNo().equals("") )
						 {
							 if(reason!=null) {
								 reason=reason+" & Blank";

							 }
							 else {
							 reason="Blank";
							 }
						 }
						 
						 
					/*
					 * if(Data.getFirstName()==null || Data.getFirstName().equals("")) {
					 * reason=reason+" First name"; if (Data.getLastName() == null ||
					 * Data.getLastName().equals("") || Data.getLastName() == null ||
					 * Data.getLastName().equals("") || Data.getEnrollmentNo() == null ||
					 * Data.getEnrollmentNo().equals("")) { reason=reason+","; } }
					 * if(Data.getLastName()==null || Data.getLastName().equals("") ) {
					 * reason=reason+" Last name"; if(Data.getEnrollmentNo()==null ||
					 * Data.getEnrollmentNo().equals("")) { reason=reason+","; } }
					 */
						 if(Data.getEnrollmentNo()==null || Data.getEnrollmentNo().equals("") )
						 {
							 reason=reason+" Seat No";
							
						 }
						
					   // studentData.setFirstName(Data.getFirstName());
				       // studentData.setLastName(Data.getLastName());
				       // studentData.setCollegeName(Data.getCollegeName());
				       // studentData.setBranch_nm(Data.getBranch_nm());
				        studentData.setSemester(Data.getSemester());
				        studentData.setStream(Data.getStream());	
				        studentData.setEnrollmentNo(Data.getEnrollmentNo());
				        studentData.setPassingYear(Data.getPassingYear());	
				        studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
				        studentData.setMonthOfPassing(Data.getMonthOfPassing());
				        studentData.setReason(reason);
				        rejectedData.add(studentData);				
					 }
			 }
			 else
			 {	 UniversityStudDocResponse studentData=new UniversityStudDocResponse();
			 	reason="Duplicate record";
			    //studentData.setFirstName(Data.getFirstName());
		        //studentData.setLastName(Data.getLastName());
		       // studentData.setCollegeName(Data.getCollegeName());	
		        studentData.setStream(Data.getStream());	
		       // studentData.setBranch_nm(Data.getBranch_nm());
		        studentData.setSemester(Data.getSemester());
		        studentData.setEnrollmentNo(Data.getEnrollmentNo());
		        studentData.setPassingYear(Data.getPassingYear());	
		        studentData.setMonthOfPassing(Data.getMonthOfPassing());	
		        studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
		        studentData.setReason(reason);

		        rejectedData.add(studentData);				
			 }
			
		 }
		 logger.info("rejectedData : "+rejectedData);
		 universityStudentDocRepository.saveAll(studentDataList);
		 datalist.put("savedStudentData", savedStudDataList);
		 datalist.put("RejectedData", rejectedData);
		 return datalist;
		 
	}

	@Value("${file.awsORtest}")
    private String awsORtest;
	
	@Override
	public List<UniversityStudDocResponse> ReviewStudentData(MultipartFile excelfile, MultipartFile datafile) throws IOException {
		
		logger.info("********AssociateManagerServiceImpl ReviewStudentData********");

		 workbook = new XSSFWorkbook(excelfile.getInputStream());
		 XSSFSheet worksheet = workbook.getSheetAt(0);
		 List<UniversityStudDocResponse> studentDataReviewList = new ArrayList<UniversityStudDocResponse>();
		int rowcnt=worksheet.getLastRowNum();
		
		int clomncnt=worksheet.getRow(0).getLastCellNum() ;
		
		int noOfColumns = worksheet.getRow(0).getPhysicalNumberOfCells();

		logger.info("noOfColumns="+noOfColumns);

		logger.info("clomncnt="+clomncnt);
		 String fileSubPath = "file/";
		 String filePath;
		 
		 String flag = "2";
		 if(awsORtest.equalsIgnoreCase("TEST") || awsORtest.equalsIgnoreCase("LOCAL")) {
			 
			 filePath = fileStorageService.storeFile(datafile, fileSubPath, flag);
		 }else {
			 filePath = fileStorageService.storeFileOnAws(datafile , flag);
		 }
		 if(clomncnt==5) {
		 for(int i=1;i<=rowcnt;i++) {
			 
			 XSSFRow row = worksheet.getRow(i);		 
			

			 UniversityStudDocResponse studentData = new UniversityStudDocResponse();	            
		     if(row!=null) {
		    	 int rowcell= row.getPhysicalNumberOfCells();
					logger.info("rowcell="+rowcell);
					
		     
		     if(row.getCell(0)!=null){
					 
					 studentData.setStream(row.getCell(0).getStringCellValue()); 
					 }
					 else {
					 studentData.setStream(""); }
					 
			 if(row.getCell(1)!=null){

		        studentData.setSemester(row.getCell(1).getStringCellValue());	
			 }
			 else {
				 studentData.setSemester(""); 
			 }

			 if(row.getCell(2)!=null){
		        int cellType=row.getCell(2).getCellType();
		        
		 logger.info("celltype "+cellType);
		 if(cellType==1)
		 {
		        studentData.setEnrollmentNo(row.getCell(2).getStringCellValue());
		 }
		 else
		 {
		        Integer enrollNo=(int) row.getCell(2).getNumericCellValue();		        
		        studentData.setEnrollmentNo(enrollNo.toString());			 
		 }
			 }
			 else {
				 studentData.setEnrollmentNo("");
			 }
			 
			 
			 if(row.getCell(3)!=null){

		        studentData.setMonthOfPassing(row.getCell(3).getStringCellValue());	
			 }
			 else {
				 studentData.setMonthOfPassing(""); 
			 }

		 if(row.getCell(4)!=null){
	        int yearcellType=row.getCell(4).getCellType();

		 if(yearcellType==1)
		 {
		        studentData.setPassingYear(row.getCell(4).getStringCellValue());
		 }
		 else {
		        Integer yearofPassing=(int) row.getCell(4).getNumericCellValue();
		        studentData.setPassingYear(yearofPassing.toString());		        
		 }
			 }
			 else {
				 studentData.setPassingYear("");
			 }
		        studentData.setOriginalDOCuploadfilePath(filePath);
		        studentDataReviewList.add(studentData);    
			 }
		 }
		 }
		 
		 return studentDataReviewList;
	}

	@Override
	public List<UniversityStudDocResponse> getStudentData(UniversityStudentRequest universityStudData ) {
		
		logger.info("********AssociateManagerServiceImpl getStudentData********");

		 List<UniversityStudentDocument> studentDataList = new ArrayList<UniversityStudentDocument>();
		 
		List<UniversityStudentDocument> usdr = universityStudentDocRepository.searchByEnrollmentNoLikeAndPassingYearIdLikeAndStreamIdLikeAndSemIdLikeAndMonthOfPassingLike( 
				universityStudData.getEnrollmentNo(), universityStudData.getPassingYearId(), universityStudData.getStreamId(),universityStudData.getSemId(),universityStudData.getMonthOfPassing());
		List<UniversityStudDocResponse> studData=new ArrayList<>();
		
		logger.info("********AssociateManagerServiceImpl getStudentData********");
		 
		 for(UniversityStudentDocument studentData:usdr) {
			 
			 UniversityStudDocResponse resp = new UniversityStudDocResponse();
			/*
			 * Optional<CollegeMaster> cm =
			 * collegeRespository.findById(studentData.getCollegeId()); CollegeMaster
			 * college = cm.get();
			 */
			 
			 Optional<StreamMaster> streaminfo = streamRespository.findById(studentData.getStreamId());
			 StreamMaster stream = streaminfo.get();
			 
			 Optional<PassingYearMaster> passingyrinfo = yearOfPassingRespository.findById(studentData.getPassingYearId());
			 PassingYearMaster passingyr = passingyrinfo.get();
			 
			 SemesterEntity sem=semesterService.getSemById(studentData.getSemId());
				
			// BranchMasterEntity branch=branchMasterService.getbranchById(studentData.getBranchId());
				
			 
			 resp.setId(studentData.getId());
			// resp.setFirstName(studentData.getFirstName());
			// resp.setLastName(studentData.getLastName());
			// resp.setCollegeName(college.getCollegeName());
			 resp.setEnrollmentNo(studentData.getEnrollmentNo());
			 resp.setStream(stream.getStreamName());
			 resp.setPassingYear(passingyr.getYearOfPassing());
			// resp.setBranch_nm(branch.getBranchName());
			 resp.setSemester(sem.getSemester());
			 resp.setMonthOfPassing(studentData.getMonthOfPassing());
			 //String Path=
			 resp.setOriginalDOCuploadfilePath("/verifier/getimage/U/"+studentData.getId());
			 studData.add(resp);
			 
		 }

		return studData;
	}
	@Override
	public String saveassociatefilepath(MultipartFile datafile) {
		 String fileSubPath = "file/";
		 String filePath;
		 
		 String flag = "2";
		 if(awsORtest.equalsIgnoreCase("TEST") || awsORtest.equalsIgnoreCase("LOCAL")) {
			 
			 filePath = fileStorageService.storeFile(datafile, fileSubPath, flag);
		 }else {
			 filePath = fileStorageService.storeFileOnAws(datafile , flag);
		 }				 
		 return filePath;
	}
	
	
	@Override
	public  HashMap<String,List<UniversityStudDocResponse>> AutosaveStudentInfo(List<UniversityStudDocResponse> list, Long userid) throws IOException {
		
		 List<UniversityStudentDocument> studentDataList = new ArrayList<UniversityStudentDocument>();
		 List<UniversityStudDocResponse> savedStudDataList = new ArrayList<UniversityStudDocResponse>();

		 UniversityStudentDocument docEntity =null;
		 List<UniversityStudDocResponse> rejectedData=new ArrayList<UniversityStudDocResponse>();
		 HashMap<String,List<UniversityStudDocResponse>> datalist=new HashMap<String,List<UniversityStudDocResponse>>();
		
		 for(UniversityStudDocResponse Data:list) {
			 
			    Long clgnm = null;
				Long passyr = null;
				Long strm = null;
				Long semId = null;
				Long branchId = null;
				String monthnm=null;
			/*
			 * CollegeMaster collegeEntities =
			 * collegeRespository.findByCollegeName(Data.getCollegeName()); */
				MonthOfPassing month=monthOfPassingRepository.findByMonthOfPassing(Data.getMonthOfPassing());
			 StreamMaster stream =streamRespository.findByStreamName(Data.getStream());
			
			 if(month!=null) {
				 monthnm=month.getMonthOfPassing();
			 }
			 if(stream!=null) 
			  { 
				  strm=stream.getId(); 
			  logger.info("strm"+strm);
			  }
			  PassingYearMaster passingyr=yearOfPassingRespository.findByYearOfPassing(Data.getPassingYear());
			 
				 SemesterEntity sem=semesterService.getSemIdByNm(Data.getSemester(),strm);					
				//  BranchMasterEntity branch=branchMasterService.getbranchIdByname(Data.getBranch_nm(),strm);
				

			 			String enrollNo=Data.getEnrollmentNo();
			 			//String fnm=Data.getFirstName();
			 			//String lnm=Data.getLastName();
			 			
			/*
			 * if(collegeEntities!=null) { clgnm=collegeEntities.getId();
			 * logger.info("clgnm"+clgnm); }*/
			   if(passingyr!=null) {
			 
			  passyr=passingyr.getId();
			  logger.info("passyr"+passyr); }
			 
			 			if(sem!=null) {

				 			 semId=sem.getId();
				 			logger.info("semId"+semId);
				 			}
			/*
			 * if(branch!=null) {
			 * 
			 * branchId=branch.getId(); logger.info("branchId"+branchId); }
			 */
			 			
			 			
			 docEntity=universityStudentDocRepository.findByEnrollmentNoAndStreamIdAndPassingYearIdAndSemIdAndMonthOfPassing(enrollNo,strm,passyr,semId,monthnm);
			String reason = null;
			 if(docEntity==null) {	
				
				
				if(passingyr!=null&&sem!=null && !Data.getEnrollmentNo().equals("")&&month!=null&&!Data.getOriginalDOCuploadfilePath().equals("ImageNotAvailable")) {
					//if(Data.getOriginalDOCuploadfilePath().equals("FileNotAvailable")) {
				UniversityStudentDocument studentData=new UniversityStudentDocument();
				UniversityStudDocResponse SavestudentData=new UniversityStudDocResponse();

			//	studentData.setFirstName(Data.getFirstName());
		      //  studentData.setLastName(Data.getLastName());
		       // studentData.setCollegeId(collegeEntities.getId());	
		        studentData.setStreamId(stream.getId());	
		        studentData.setEnrollmentNo(Data.getEnrollmentNo());
		        studentData.setPassingYearId(passingyr.getId());	
		      //  studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
		        studentData.setSemId(sem.getId());
		       // studentData.setBranchId(branch.getId());
		        studentData.setCreatedate(new Date());
		        studentData.setCreateby(userid.toString());
		        studentData.setMonthOfPassing(monthnm);
		        studentDataList.add(studentData);    
		        
		        //SavestudentData.setFirstName(Data.getFirstName());
		       // SavestudentData.setLastName(Data.getLastName());
		      //  SavestudentData.setCollegeName(Data.getCollegeName());	
		        SavestudentData.setStream(Data.getStream());	
		        SavestudentData.setEnrollmentNo(Data.getEnrollmentNo());
		        SavestudentData.setPassingYear(Data.getPassingYear());	
		        SavestudentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
		      //  SavestudentData.setReason(reason);
		        //SavestudentData.setBranch_nm(Data.getBranch_nm());
		        SavestudentData.setSemester(Data.getSemester());
		        SavestudentData.setMonthOfPassing(monthnm);

		        savedStudDataList.add(SavestudentData);	
		        
				
				}
				
					 else
					 {	
						 UniversityStudDocResponse studentData=new UniversityStudDocResponse();
						 if(passingyr==null|| sem==null||stream==null||month==null) {
						 reason="Invalid";
						  if(stream==null) { 
					    	   
					    	   reason=reason+" Stream";
						      if(passingyr==null||sem==null||month==null) { 
						    	  
							  reason=reason+",";
						  
						  } }
						 
							  if(passingyr==null)
							 {
								 reason=reason+" Year of Passing";
								 if(sem==null||month==null)
								 {
									 reason=reason+",";

								 }

							 }
							  if(sem==null)
							 {
								 reason=reason+" Semester";
							/*
							 * if(branch==null) { reason=reason+",";
							 * 
							 * }
							 * 
							 * } if(branch==null) { reason=reason+" Branch";
							 * 
							 */
								 if(month==null)
								 {
									 reason=reason+",";

								 }
							 }
							  if(month==null) {
									
									 reason=reason+" Month Of Passing";

								 }
						 }
					/*
					 * if(collegeEntities==null) { reason=reason+" College Name";
					  if(stream==null||passingyr==null||sem==null||branch==null) {
					  reason=reason+",";
					 * 
					  }
					  } */
				     
						 if( Data.getOriginalDOCuploadfilePath().equals("ImageNotAvailable")) {
							
							 if(reason!=null) {
								 reason=reason+", ImageNotAvailable";

							 }
							 else {
								 reason=" ImageNotAvailable";

							 }
						 }
						
						 if(Data.getEnrollmentNo()==null || Data.getEnrollmentNo().equals("") )
						 {
							 if(reason!=null) {
								 reason=reason+" & Blank";

							 }
							 else {
							 reason="Blank";
							 }
						 }
						 
						 
					/*
					 * if(Data.getFirstName()==null || Data.getFirstName().equals("")) {
					 * reason=reason+" First name"; if (Data.getLastName() == null ||
					 * Data.getLastName().equals("") || Data.getLastName() == null ||
					 * Data.getLastName().equals("") || Data.getEnrollmentNo() == null ||
					 * Data.getEnrollmentNo().equals("")) { reason=reason+","; } }
					 * if(Data.getLastName()==null || Data.getLastName().equals("") ) {
					 * reason=reason+" Last name"; if(Data.getEnrollmentNo()==null ||
					 * Data.getEnrollmentNo().equals("")) { reason=reason+","; } }
					 */
						 if(Data.getEnrollmentNo()==null || Data.getEnrollmentNo().equals("") )
						 {
							 reason=reason+" Seat No";
							
						 }
						
					   // studentData.setFirstName(Data.getFirstName());
				       // studentData.setLastName(Data.getLastName());
				       // studentData.setCollegeName(Data.getCollegeName());
				       // studentData.setBranch_nm(Data.getBranch_nm());
				        studentData.setSemester(Data.getSemester());
				        studentData.setStream(Data.getStream());	
				        studentData.setEnrollmentNo(Data.getEnrollmentNo());
				        studentData.setPassingYear(Data.getPassingYear());	
				        studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
				        studentData.setMonthOfPassing(Data.getMonthOfPassing());
				        studentData.setReason(reason);
				        rejectedData.add(studentData);				
					 }
			 }
			 else
			 {	 UniversityStudDocResponse studentData=new UniversityStudDocResponse();
			 	reason="Duplicate record";
			    //studentData.setFirstName(Data.getFirstName());
		        //studentData.setLastName(Data.getLastName());
		       // studentData.setCollegeName(Data.getCollegeName());	
		        studentData.setStream(Data.getStream());	
		       // studentData.setBranch_nm(Data.getBranch_nm());
		        studentData.setSemester(Data.getSemester());
		        studentData.setEnrollmentNo(Data.getEnrollmentNo());
		        studentData.setPassingYear(Data.getPassingYear());	
		        studentData.setMonthOfPassing(Data.getMonthOfPassing());	
		        studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
		        studentData.setReason(reason);

		        rejectedData.add(studentData);				
			 }
			
		 }
		 logger.info("rejectedData : "+rejectedData);
		 universityStudentDocRepository.saveAll(studentDataList);
		 datalist.put("savedStudentData", savedStudDataList);
		 datalist.put("RejectedData", rejectedData);
		 return datalist;
		 
	}
}
