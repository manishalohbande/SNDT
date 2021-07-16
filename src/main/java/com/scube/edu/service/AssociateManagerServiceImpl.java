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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
import com.scube.edu.repository.SemesterRepository;
import com.scube.edu.repository.StreamRepository;
import com.scube.edu.repository.UniversityStudentDocRepository;
import com.scube.edu.repository.YearOfPassingRepository;
import com.scube.edu.request.AssociateExcelRequest;
import com.scube.edu.request.UniversityStudentRequest;
import com.scube.edu.response.UniversityResponse;
import com.scube.edu.response.UniversityStudDocResponse;
import com.scube.edu.response.UniversityStudentDocumentResponse;
import com.scube.edu.util.FileStorageService;

@Service
public class AssociateManagerServiceImpl implements AssociateManagerService {

	private static final Logger logger = LoggerFactory.getLogger(AssociateManagerServiceImpl.class);

	@Autowired
	UniversityStudentDocRepository universityStudentDocRepository;

	@PersistenceUnit
	private EntityManagerFactory emf;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	CollegeRepository collegeRespository;

	@Autowired
	StreamRepository streamRespository;
	@Autowired
	YearOfPassingRepository yearOfPassingRespository;
	@Autowired
	MonthOfPassingRepository monthOfPassingRepository;

	@Autowired
	SemesterRepository semesterRepository;
	@Autowired
	SemesterService semesterService;

	@Autowired
	BranchMasterService branchMasterService;

	private XSSFWorkbook workbook;

	@Override
	public HashMap<String, List<UniversityStudDocResponse>> saveStudentInfo(List<AssociateExcelRequest> list,
			Long userid) throws IOException {

		List<UniversityStudentDocument> studentDataList = new ArrayList<UniversityStudentDocument>();
		List<UniversityStudDocResponse> savedStudDataList = new ArrayList<UniversityStudDocResponse>();

		UniversityStudentDocument docEntity = null;
		List<UniversityStudDocResponse> rejectedData = new ArrayList<UniversityStudDocResponse>();
		HashMap<String, List<UniversityStudDocResponse>> datalist = new HashMap<String, List<UniversityStudDocResponse>>();

		for (AssociateExcelRequest Data : list) {

			Long clgnm = null;
			Long passyr = null;
			Long strm = null;
			Long semId = null;
			Long branchId = null;
			String monthnm = null;
			Long clgId = null;
			CollegeMaster collegeEntities = collegeRespository.findByCollegeNameAndIsdeleted(Data.getCollegeName(),
					"N");
			MonthOfPassing month = monthOfPassingRepository.findByMonthOfPassing(Data.getMonthOfPassing());
			StreamMaster stream = streamRespository.findByStreamNameAndIsdeleted(Data.getStream(), "N");

			if (month != null) {
				monthnm = month.getMonthOfPassing();
			}
			if (stream != null) {
				strm = stream.getId();
				logger.info("strm" + strm);
			}
			PassingYearMaster passingyr = yearOfPassingRespository
					.findByYearOfPassingAndIsdeleted(Data.getPassingYear(), "N");

			SemesterEntity sem = semesterRepository.findBySemesterAndStreamIdAndIsdeleted(Data.getSemester(), strm,
					"N");
			// BranchMasterEntity
			// branch=branchMasterService.getbranchIdByname(Data.getBranch_nm(),strm);

			String enrollNo = Data.getEnrollmentNo();
			// String fnm=Data.getFirstName();
			// String lnm=Data.getLastName();

			if (collegeEntities != null) {
				clgnm = collegeEntities.getId();
				logger.info("clgnm" + clgnm);
			}
			if (passingyr != null) {

				passyr = passingyr.getId();
				logger.info("passyr" + passyr);
			}

			if (sem != null) {

				semId = sem.getId();
				logger.info("semId" + semId);
			}

			/*
			 * if(branch!=null) {
			 * 
			 * branchId=branch.getId(); logger.info("branchId"+branchId); }
			 */

			// docEntity=universityStudentDocRepository.findByEnrollmentNoAndStreamIdAndPassingYearIdAndSemIdAndMonthOfPassing(enrollNo,strm,passyr,semId,monthnm);
			docEntity = universityStudentDocRepository.findByPrnNoAndSemIdAndPassingYearId(Data.getPrnNo(), semId,
					passyr);
			String reason = null;
			if (docEntity == null) {

				if (passingyr != null && sem != null && !Data.getPrnNo().equals("") && month != null
						&& collegeEntities != null && Data.getPrnNo() != null) {
					UniversityStudentDocument studentData = new UniversityStudentDocument();
					UniversityStudDocResponse SavestudentData = new UniversityStudDocResponse();

					// studentData.setFirstName(Data.getFirstName());
					// studentData.setLastName(Data.getLastName());
					try {
						studentData.setCollegeId(clgnm);
						studentData.setStreamId(stream.getId());
						studentData.setEnrollmentNo(Data.getEnrollmentNo());
						studentData.setPassingYearId(passingyr.getId());
						// studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
						studentData.setSemId(sem.getId());
						// studentData.setBranchId(branchId);
						studentData.setCreatedate(new Date());
						studentData.setIsdeleted("N");
						studentData.setCreateby(userid.toString());
						studentData.setMonthOfPassing(monthnm);

						studentData.setPrnNo(Data.getPrnNo());
						studentData.setCenter(Data.getCenter());
						studentData.setMedDesc(Data.getMedDesc());
						studentData.setStudentName(Data.getStudentName());
						studentData.setSemTotalMax(Integer.valueOf(Data.getSemTotalMax()));
						studentData.setSemGradeTotal(Integer.valueOf(Data.getSemGradeTotal()));
						studentData.setSemGrace(Data.getSemGrace());
						studentData.setSemclass(Data.getSemclass());
						studentData.setSemGpa(Double.valueOf(Data.getSemGpa()));
						studentData.setSemGrade(Data.getSemGrade());
						studentData.setGradeTotal(Integer.valueOf(Data.getGradeTotal()));
						studentData.setClassGrace(Data.getClassGrace());
						studentData.setMaxGrTotal(Integer.valueOf(Data.getMaxGrTotal()));
						studentData.setCredTotal(Integer.valueOf(Data.getCredTotal()));
						studentData.setGpa(Double.valueOf(Data.getGpa()));
						studentData.setFgrade(Data.getFgrade());
						studentData.setResultDesc(Data.getResultDesc());
						studentData.setInceDesc(Data.getInceDesc());
						studentData.setSubOne(Data.getSubOne());
						studentData.setSubNmOne(Data.getSubNmOne());
						studentData.setCreditSubOne(Integer.valueOf(Data.getCreditSubOne()));
						studentData.setIntSubOne(Integer.valueOf(Data.getIntSubOne()));
						studentData.setExtSubOne(Integer.valueOf(Data.getExtSubOne()));
						studentData.setTotalSubOne(Integer.valueOf(Data.getTotalSubOne()));
						studentData.setGraceSubOne(Data.getGraceSubOne());
						studentData.setPrvFlgSubOne(Data.getPrvFlgSubOne());
						studentData.setMaxExtSubOne(Integer.valueOf(Data.getMaxExtSubOne()));
						studentData.setMaxIntSubOne(Integer.valueOf(Data.getMaxIntSubOne()));
						studentData.setMaxTotalSubOne(Integer.valueOf(Data.getMaxTotalSubOne()));
						studentData.setGradeSubOne(Data.getGradeSubOne());

						studentData.setSubTwo(Data.getSubTwo());
						studentData.setSubNmTwo(Data.getSubNmTwo());
						studentData.setCreditSubTwo(Integer.valueOf(Data.getCreditSubTwo()));
						studentData.setIntSubTwo(Integer.valueOf(Data.getIntSubTwo()));
						studentData.setExtSubTwo(Integer.valueOf(Data.getExtSubTwo()));
						studentData.setTotalSubTwo(Integer.valueOf(Data.getTotalSubTwo()));
						studentData.setGraceSubTwo(Data.getGraceSubTwo());
						studentData.setPrvFlgSubTwo(Data.getPrvFlgSubTwo());
						studentData.setMaxExtSubTwo(Integer.valueOf(Data.getMaxExtSubTwo()));
						studentData.setMaxIntSubTwo(Integer.valueOf(Data.getMaxIntSubTwo()));
						studentData.setMaxTotalSubTwo(Integer.valueOf(Data.getMaxTotalSubTwo()));
						studentData.setGradeSubTwo(Data.getGradeSubTwo());

						studentData.setSubThree(Data.getSubThree());
						studentData.setSubNmThree(Data.getSubNmThree());
						studentData.setCreditSubThree(Integer.valueOf(Data.getCreditSubThree()));
						studentData.setIntSubThree(Integer.valueOf(Data.getIntSubThree()));
						studentData.setExtSubThree(Integer.valueOf(Data.getExtSubThree()));
						studentData.setTotalSubThree(Integer.valueOf(Data.getTotalSubThree()));
						studentData.setGraceSubThree(Data.getGraceSubThree());
						studentData.setPrvFlgSubThree(Data.getPrvFlgSubThree());
						studentData.setMaxExtSubThree(Integer.valueOf(Data.getMaxExtSubThree()));
						studentData.setMaxIntSubThree(Integer.valueOf(Data.getMaxIntSubThree()));
						studentData.setMaxTotalSubThree(Integer.valueOf(Data.getMaxTotalSubThree()));
						studentData.setGradeSubThree(Data.getGradeSubThree());

						studentData.setSubFour(Data.getSubFour());
						studentData.setSubNmFour(Data.getSubNmFour());
						studentData.setCreditSubFour(Integer.valueOf(Data.getCreditSubFour()));
						studentData.setIntSubFour(Integer.valueOf(Data.getIntSubFour()));
						studentData.setExtSubFour(Integer.valueOf(Data.getExtSubFour()));
						studentData.setTotalSubFour(Integer.valueOf(Data.getTotalSubFour()));
						studentData.setGraceSubFour(Data.getGraceSubFour());
						studentData.setPrvFlgSubFour(Data.getPrvFlgSubFour());
						studentData.setMaxExtSubFour(Integer.valueOf(Data.getMaxExtSubFour()));
						studentData.setMaxIntSubFour(Integer.valueOf(Data.getMaxIntSubFour()));
						studentData.setMaxTotalSubFour(Integer.valueOf(Data.getMaxTotalSubFour()));
						studentData.setGradeSubFour(Data.getGradeSubFour());

						studentData.setSubFive(Data.getSubFive());
						studentData.setSubNmFive(Data.getSubNmFive());
						studentData.setCreditSubFive(Integer.valueOf(Data.getCreditSubFive()));
						studentData.setIntSubFive(Integer.valueOf(Data.getIntSubFive()));
						studentData.setExtSubFive(Integer.valueOf(Data.getExtSubFive()));
						studentData.setTotalSubFive(Integer.valueOf(Data.getTotalSubFive()));
						studentData.setGraceSubFive(Data.getGraceSubFive());
						studentData.setPrvFlgSubFive(Data.getPrvFlgSubFive());
						studentData.setMaxExtSubFive(Integer.valueOf(Data.getMaxExtSubFive()));
						studentData.setMaxIntSubFive(Integer.valueOf(Data.getMaxIntSubFive()));
						studentData.setMaxTotalSubFive(Integer.valueOf(Data.getMaxTotalSubFive()));
						studentData.setGradeSubFive(Data.getGradeSubFive());

						studentData.setSubSix(Data.getSubSix());
						studentData.setSubNmSix(Data.getSubNmSix());
						studentData.setCreditSubSix(Integer.valueOf(Data.getCreditSubSix()));
						studentData.setIntSubSix(Integer.valueOf(Data.getIntSubSix()));
						studentData.setExtSubSix(Integer.valueOf(Data.getExtSubSix()));
						studentData.setTotalSubSix(Integer.valueOf(Data.getTotalSubSix()));
						studentData.setGraceSubSix(Data.getGraceSubSix());
						studentData.setPrvFlgSubSix(Data.getPrvFlgSubSix());
						studentData.setMaxExtSubSix(Integer.valueOf(Data.getMaxExtSubSix()));
						studentData.setMaxIntSubSix(Integer.valueOf(Data.getMaxIntSubSix()));
						studentData.setMaxTotalSubSix(Integer.valueOf(Data.getMaxTotalSubSix()));
						studentData.setGradeSubSix(Data.getGradeSubSix());
						studentDataList.add(studentData);

						studentData.setPrnNo(Data.getPrnNo());
						studentData.setCenter(Data.getCenter());
						studentData.setMedDesc(Data.getMedDesc());
						studentData.setStudentName(Data.getStudentName());
						studentData.setSemTotalMax(Integer.valueOf(Data.getSemTotalMax()));
						studentData.setSemGradeTotal(Integer.valueOf(Data.getSemGradeTotal()));
						studentData.setSemGrace(Data.getSemGrace());
						studentData.setSemclass(Data.getSemclass());
						studentData.setSemGpa(Double.valueOf(Data.getSemGpa()));
						studentData.setSemGrade(Data.getSemGrade());
						studentData.setGradeTotal(Integer.valueOf(Data.getGradeTotal()));
						studentData.setClassGrace(Data.getClassGrace());
						studentData.setMaxGrTotal(Integer.valueOf(Data.getMaxGrTotal()));
						studentData.setCredTotal(Integer.valueOf(Data.getCredTotal()));
						studentData.setGpa(Double.valueOf(Data.getGpa()));
						studentData.setFgrade(Data.getFgrade());
						studentData.setResultDesc(Data.getResultDesc());
						studentData.setInceDesc(Data.getInceDesc());
						studentData.setSubOne(Data.getSubOne());
						studentData.setSubNmOne(Data.getSubNmOne());
						studentData.setCreditSubOne(Integer.valueOf(Data.getCreditSubOne()));
						studentData.setIntSubOne(Integer.valueOf(Data.getIntSubOne()));
						studentData.setExtSubOne(Integer.valueOf(Data.getExtSubOne()));
						studentData.setTotalSubOne(Integer.valueOf(Data.getTotalSubOne()));
						studentData.setGraceSubOne(Data.getGraceSubOne());
						studentData.setPrvFlgSubOne(Data.getPrvFlgSubOne());
						studentData.setMaxExtSubOne(Integer.valueOf(Data.getMaxExtSubOne()));
						studentData.setMaxIntSubOne(Integer.valueOf(Data.getMaxIntSubOne()));
						studentData.setMaxTotalSubOne(Integer.valueOf(Data.getMaxTotalSubOne()));
						studentData.setGradeSubOne(Data.getGradeSubOne());

						studentData.setSubTwo(Data.getSubTwo());
						studentData.setSubNmTwo(Data.getSubNmTwo());
						studentData.setCreditSubTwo(Integer.valueOf(Data.getCreditSubTwo()));
						studentData.setIntSubTwo(Integer.valueOf(Data.getIntSubTwo()));
						studentData.setExtSubTwo(Integer.valueOf(Data.getExtSubTwo()));
						studentData.setTotalSubTwo(Integer.valueOf(Data.getTotalSubTwo()));
						studentData.setGraceSubTwo(Data.getGraceSubTwo());
						studentData.setPrvFlgSubTwo(Data.getPrvFlgSubTwo());
						studentData.setMaxExtSubTwo(Integer.valueOf(Data.getMaxExtSubTwo()));
						studentData.setMaxIntSubTwo(Integer.valueOf(Data.getMaxIntSubTwo()));
						studentData.setMaxTotalSubTwo(Integer.valueOf(Data.getMaxTotalSubTwo()));
						studentData.setGradeSubTwo(Data.getGradeSubTwo());

						studentData.setSubThree(Data.getSubThree());
						studentData.setSubNmThree(Data.getSubNmThree());
						studentData.setCreditSubThree(Integer.valueOf(Data.getCreditSubThree()));
						studentData.setIntSubThree(Integer.valueOf(Data.getIntSubThree()));
						studentData.setExtSubThree(Integer.valueOf(Data.getExtSubThree()));
						studentData.setTotalSubThree(Integer.valueOf(Data.getTotalSubThree()));
						studentData.setGraceSubThree(Data.getGraceSubThree());
						studentData.setPrvFlgSubThree(Data.getPrvFlgSubThree());
						studentData.setMaxExtSubThree(Integer.valueOf(Data.getMaxExtSubThree()));
						studentData.setMaxIntSubThree(Integer.valueOf(Data.getMaxIntSubThree()));
						studentData.setMaxTotalSubThree(Integer.valueOf(Data.getMaxTotalSubThree()));
						studentData.setGradeSubThree(Data.getGradeSubThree());

						studentData.setSubFour(Data.getSubFour());
						studentData.setSubNmFour(Data.getSubNmFour());
						studentData.setCreditSubFour(Integer.valueOf(Data.getCreditSubFour()));
						studentData.setIntSubFour(Integer.valueOf(Data.getIntSubFour()));
						studentData.setExtSubFour(Integer.valueOf(Data.getExtSubFour()));
						studentData.setTotalSubFour(Integer.valueOf(Data.getTotalSubFour()));
						studentData.setGraceSubFour(Data.getGraceSubFour());
						studentData.setPrvFlgSubFour(Data.getPrvFlgSubFour());
						studentData.setMaxExtSubFour(Integer.valueOf(Data.getMaxExtSubFour()));
						studentData.setMaxIntSubFour(Integer.valueOf(Data.getMaxIntSubFour()));
						studentData.setMaxTotalSubFour(Integer.valueOf(Data.getMaxTotalSubFour()));
						studentData.setGradeSubFour(Data.getGradeSubFour());

						studentData.setSubFive(Data.getSubFive());
						studentData.setSubNmFive(Data.getSubNmFive());
						studentData.setCreditSubFive(Integer.valueOf(Data.getCreditSubFive()));
						studentData.setIntSubFive(Integer.valueOf(Data.getIntSubFive()));
						studentData.setExtSubFive(Integer.valueOf(Data.getExtSubFive()));
						studentData.setTotalSubFive(Integer.valueOf(Data.getTotalSubFive()));
						studentData.setGraceSubFive(Data.getGraceSubFive());
						studentData.setPrvFlgSubFive(Data.getPrvFlgSubFive());
						studentData.setMaxExtSubFive(Integer.valueOf(Data.getMaxExtSubFive()));
						studentData.setMaxIntSubFive(Integer.valueOf(Data.getMaxIntSubFive()));
						studentData.setMaxTotalSubFive(Integer.valueOf(Data.getMaxTotalSubFive()));
						studentData.setGradeSubFive(Data.getGradeSubFive());

						studentData.setSubSix(Data.getSubSix());
						studentData.setSubNmSix(Data.getSubNmSix());
						studentData.setCreditSubSix(Integer.valueOf(Data.getCreditSubSix()));
						studentData.setIntSubSix(Integer.valueOf(Data.getIntSubSix()));
						studentData.setExtSubSix(Integer.valueOf(Data.getExtSubSix()));
						studentData.setTotalSubSix(Integer.valueOf(Data.getTotalSubSix()));
						studentData.setGraceSubSix(Data.getGraceSubSix());
						studentData.setPrvFlgSubSix(Data.getPrvFlgSubSix());
						studentData.setMaxExtSubSix(Integer.valueOf(Data.getMaxExtSubSix()));
						studentData.setMaxIntSubSix(Integer.valueOf(Data.getMaxIntSubSix()));
						studentData.setMaxTotalSubSix(Integer.valueOf(Data.getMaxTotalSubSix()));
						studentData.setGradeSubSix(Data.getGradeSubSix());
						SavestudentData.setStream(Data.getStream());
						SavestudentData.setEnrollmentNo(Data.getEnrollmentNo());
						SavestudentData.setPassingYear(Data.getPassingYear());
						SavestudentData.setSemester(Data.getSemester());
						SavestudentData.setMonthOfPassing(monthnm);

						savedStudDataList.add(SavestudentData);
					} catch (Exception e) {
						UniversityStudDocResponse invaliddata = new UniversityStudDocResponse();

						invaliddata.setSemester(Data.getSemester());
						invaliddata.setStream(Data.getStream());
						invaliddata.setEnrollmentNo(Data.getEnrollmentNo());
						invaliddata.setPassingYear(Data.getPassingYear());
						invaliddata.setMonthOfPassing(Data.getMonthOfPassing());
						invaliddata.setPrnNo(Data.getPrnNo());
						invaliddata.setReason("Invalid Record Entry");
						rejectedData.add(invaliddata);
					}
				}

				else {
					UniversityStudDocResponse studentData = new UniversityStudDocResponse();
					if (passingyr == null || sem == null || stream == null || month == null
							|| collegeEntities == null) {
						reason = "Invalid";
						if (stream == null) {

							reason = reason + " Stream";
							if (passingyr == null || sem == null || month == null || collegeEntities == null) {

								reason = reason + ",";

							}
						}

						if (passingyr == null) {
							reason = reason + " Year of Passing";
							if (sem == null || month == null || collegeEntities == null) {
								reason = reason + ",";

							}

						}
						if (sem == null) {
							reason = reason + " Semester";
							if (month == null || collegeEntities == null) {
								reason = reason + ",";

							}
						}
						if (month == null) {

							reason = reason + " Month Of Passing";
							if (collegeEntities == null) {
								reason = reason + ",";

							}

						}
					}

					if (collegeEntities == null) {
						reason = reason + " College Name";

					}

					/*
					 * if( Data.getOriginalDOCuploadfilePath().equals("ImageNotAvailable")) {
					 * 
					 * if(reason!=null) { reason=reason+", ImageNotAvailable";
					 * 
					 * } else { reason=" ImageNotAvailable";
					 * 
					 * } }
					 */

					if (Data.getPrnNo() == null || Data.getPrnNo().equals("")) {
						if (reason != null) {
							reason = reason + " & Blank PRN NO";

						} else {
							reason = "Blank PRN NO";
						}
					}

					
					
					studentData.setSemester(Data.getSemester());
					studentData.setStream(Data.getStream());
					studentData.setEnrollmentNo(Data.getEnrollmentNo());
					studentData.setPassingYear(Data.getPassingYear());
					// studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
					studentData.setMonthOfPassing(Data.getMonthOfPassing());
					studentData.setPrnNo(Data.getPrnNo());
					studentData.setReason(reason);
					rejectedData.add(studentData);
				}
			} else {
				UniversityStudDocResponse studentData = new UniversityStudDocResponse();
				reason = "Duplicate record";
				// studentData.setFirstName(Data.getFirstName());
				// studentData.setLastName(Data.getLastName());
				// studentData.setCollegeName(Data.getCollegeName());
				studentData.setStream(Data.getStream());
				// studentData.setBranch_nm(Data.getBranch_nm());
				studentData.setSemester(Data.getSemester());
				studentData.setEnrollmentNo(Data.getEnrollmentNo());
				studentData.setPassingYear(Data.getPassingYear());
				studentData.setMonthOfPassing(Data.getMonthOfPassing());
				studentData.setPrnNo(Data.getPrnNo());
				// studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
				studentData.setReason(reason);

				rejectedData.add(studentData);
			}

		}
		logger.info("rejectedData : " + rejectedData);
		universityStudentDocRepository.saveAll(studentDataList);
		datalist.put("savedStudentData", savedStudDataList);
		datalist.put("RejectedData", rejectedData);
		return datalist;

	}

	@Value("${file.awsORtest}")
	private String awsORtest;

	@Override
	public List<UniversityStudDocResponse> ReviewStudentData(MultipartFile excelfile, MultipartFile datafile)
			throws IOException {

		logger.info("********AssociateManagerServiceImpl ReviewStudentData********");

		workbook = new XSSFWorkbook(excelfile.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(0);
		List<UniversityStudDocResponse> studentDataReviewList = new ArrayList<UniversityStudDocResponse>();
		int rowcnt = worksheet.getLastRowNum();

		int clomncnt = worksheet.getRow(0).getLastCellNum();

		int noOfColumns = worksheet.getRow(0).getPhysicalNumberOfCells();

		logger.info("noOfColumns=" + noOfColumns);

		logger.info("clomncnt=" + clomncnt);
		String fileSubPath = "file/";
		String filePath;

		String flag = "2";
		if (awsORtest.equalsIgnoreCase("TEST") || awsORtest.equalsIgnoreCase("LOCAL")) {

			filePath = fileStorageService.storeFile(datafile, fileSubPath, flag);
		} else {
			filePath = fileStorageService.storeFileOnAws(datafile, flag);
		}
		if (clomncnt == 5) {
			for (int i = 1; i <= rowcnt; i++) {

				XSSFRow row = worksheet.getRow(i);

				UniversityStudDocResponse studentData = new UniversityStudDocResponse();
				if (row != null) {
					int rowcell = row.getPhysicalNumberOfCells();
					logger.info("rowcell=" + rowcell);

					if (row.getCell(0) != null) {

						studentData.setStream(row.getCell(0).getStringCellValue());
					} else {
						studentData.setStream("");
					}

					if (row.getCell(1) != null) {

						studentData.setSemester(row.getCell(1).getStringCellValue());
					} else {
						studentData.setSemester("");
					}

					if (row.getCell(2) != null) {
						int cellType = row.getCell(2).getCellType();

						logger.info("celltype " + cellType);
						if (cellType == 1) {
							studentData.setEnrollmentNo(row.getCell(2).getStringCellValue());
						} else {
							Integer enrollNo = (int) row.getCell(2).getNumericCellValue();
							studentData.setEnrollmentNo(enrollNo.toString());
						}
					} else {
						studentData.setEnrollmentNo("");
					}

					if (row.getCell(3) != null) {

						studentData.setMonthOfPassing(row.getCell(3).getStringCellValue());
					} else {
						studentData.setMonthOfPassing("");
					}

					if (row.getCell(4) != null) {
						int yearcellType = row.getCell(4).getCellType();

						if (yearcellType == 1) {
							studentData.setPassingYear(row.getCell(4).getStringCellValue());
						} else {
							Integer yearofPassing = (int) row.getCell(4).getNumericCellValue();
							studentData.setPassingYear(yearofPassing.toString());
						}
					} else {
						studentData.setPassingYear("");
					}
					// studentData.setOriginalDOCuploadfilePath(filePath);
					studentDataReviewList.add(studentData);
				}
			}
		}

		return studentDataReviewList;
	}

	@Override
	public List<UniversityResponse> getStudentData(UniversityStudentRequest universityStudData) {

		logger.info("********AssociateManagerServiceImpl getStudentData********");

		List<UniversityStudentDocument> studentDataList = new ArrayList<UniversityStudentDocument>();

		/*
		 * List<UniversityStudentDocument> usdr = universityStudentDocRepository
		 * .searchBySeatNoLikeAndPassingYearIdLikeAndStreamIdLikeAndSemIdLikeAndMonthOfPassingLikeAndPrnNoLike(
		 * universityStudData.getEnrollmentNo(), universityStudData.getPassingYearId(),
		 * universityStudData.getStreamId(), universityStudData.getSemId(),
		 * universityStudData.getMonthOfPassing(), universityStudData.getPrnNo());
		 */
		List<UniversityResponse> studData = new ArrayList<>();
		
		String enrollementNo=null;
		 Long passingyrid=null;
		 Long strmid=null;
		 Long semid=null;
		 String monthofPass=null;
		 String prno=null;
		 
		 if(!universityStudData.getEnrollmentNo().equalsIgnoreCase("")) {
			 logger.info("HERE");
		 }
		 if(!universityStudData.getEnrollmentNo().equalsIgnoreCase("")) {
		   enrollementNo= universityStudData.getEnrollmentNo(); 
		 }
		 if(!universityStudData.getPassingYearId().equals("")) {

			 System.out.println("PssingyrData"+universityStudData.getPassingYearId());
			 passingyrid=Long.valueOf(universityStudData.getPassingYearId()); 
		 }
		 if(!universityStudData.getStreamId().equals("")) {

			 strmid=Long.valueOf(universityStudData.getStreamId()); 
		 }
		 if(!universityStudData.getSemId().equals("")) {

			 semid=Long.valueOf(universityStudData.getSemId());
		 }
		 if(!universityStudData.getMonthOfPassing().equals("")) {

			 monthofPass=universityStudData.getMonthOfPassing();
		 }
		 if(!universityStudData.getPrnNo().equals("")) {

			 prno=universityStudData.getPrnNo();
		 }
		 
		
		  List<UniversityStudentDocument> usdr = getUniversityStudData(enrollementNo,  passingyrid, strmid, semid, monthofPass,  prno);
		

		for (UniversityStudentDocument studentData : usdr) {

			UniversityStudDocResponse resp = new UniversityStudDocResponse();

			UniversityResponse uniResp = new UniversityResponse();

			Optional<StreamMaster> streaminfo = streamRespository.findById(studentData.getStreamId());
			StreamMaster stream = streaminfo.get();

			Optional<PassingYearMaster> passingyrinfo = yearOfPassingRespository
					.findById(studentData.getPassingYearId());
			PassingYearMaster passingyr = passingyrinfo.get();

			SemesterEntity sem = semesterService.getSemById(studentData.getSemId());

//			 BranchMasterEntity branch=branchMasterService.getbranchById(studentData.getBranchId());

			uniResp.setName(studentData.getStudentName());
			uniResp.setPrnNo(studentData.getPrnNo());
			uniResp.setResDesc(studentData.getResultDesc());
			uniResp.setSeatNo(studentData.getEnrollmentNo());
			uniResp.setSemClass(studentData.getSemclass());
			uniResp.setSemester(sem.getSemester());
			uniResp.setSemGpa(studentData.getSemGpa());
			uniResp.setSemGrade(studentData.getSemGrade());
			uniResp.setSemGrdTot(studentData.getSemGradeTotal());
//			 uniResp.setBranch(branch.getBranchName());
			uniResp.setSemTotMax(studentData.getSemTotalMax());
			uniResp.setStream(stream.getStreamName());
			uniResp.setYearOfPassing(passingyr.getYearOfPassing());
			uniResp.setMonthOfPassing(studentData.getMonthOfPassing());
			uniResp.setSemGrace(studentData.getSemGrace());

//			 resp.setId(studentData.getId());
//			// resp.setFirstName(studentData.getFirstName());
//			// resp.setLastName(studentData.getLastName());
//			// resp.setCollegeName(college.getCollegeName());
//			 resp.setEnrollmentNo(studentData.getEnrollmentNo());
//			 resp.setStream(stream.getStreamName());
//			 resp.setPassingYear(passingyr.getYearOfPassing());
//			// resp.setBranch_nm(branch.getBranchName());
//			 resp.setSemester(sem.getSemester());
//			 resp.setMonthOfPassing(studentData.getMonthOfPassing());
//			 //String Path=
//			// resp.setOriginalDOCuploadfilePath("/verifier/getimage/U/"+studentData.getId());
			studData.add(uniResp);

		}
//		em.close();
//	       emf.close();
		return studData;
	}

	@Override
	public String saveassociatefilepath(MultipartFile datafile) {
		String fileSubPath = "file/";
		String filePath;

		String flag = "2";
		if (awsORtest.equalsIgnoreCase("TEST") || awsORtest.equalsIgnoreCase("LOCAL")) {

			filePath = fileStorageService.storeFile(datafile, fileSubPath, flag);
		} else {
			filePath = fileStorageService.storeFileOnAws(datafile, flag);
		}
		return filePath;
	}

	@Override
	public HashMap<String, List<UniversityStudDocResponse>> AutosaveStudentInfo(List<UniversityStudDocResponse> list,
			Long userid) throws IOException {

		List<UniversityStudentDocument> studentDataList = new ArrayList<UniversityStudentDocument>();
		List<UniversityStudDocResponse> savedStudDataList = new ArrayList<UniversityStudDocResponse>();

		UniversityStudentDocument docEntity = null;
		List<UniversityStudDocResponse> rejectedData = new ArrayList<UniversityStudDocResponse>();
		HashMap<String, List<UniversityStudDocResponse>> datalist = new HashMap<String, List<UniversityStudDocResponse>>();

		for (UniversityStudDocResponse Data : list) {

			Long clgnm = null;
			Long passyr = null;
			Long strm = null;
			Long semId = null;
			Long branchId = null;
			String monthnm = null;
			Long clgId = null;
			CollegeMaster collegeEntities = collegeRespository.findByCollegeNameAndIsdeleted(Data.getCollegeName(),
					"N");
			MonthOfPassing month = monthOfPassingRepository.findByMonthOfPassing(Data.getMonthOfPassing());
			StreamMaster stream = streamRespository.findByStreamNameAndIsdeleted(Data.getStream(), "N");

			if (month != null) {
				monthnm = month.getMonthOfPassing();
			}
			if (stream != null) {
				strm = stream.getId();
				logger.info("strm" + strm);
			}
			PassingYearMaster passingyr = yearOfPassingRespository
					.findByYearOfPassingAndIsdeleted(Data.getPassingYear(), "N");

			SemesterEntity sem = semesterRepository.findBySemesterAndStreamIdAndIsdeleted(Data.getSemester(), strm,
					"N");
			// BranchMasterEntity
			// branch=branchMasterService.getbranchIdByname(Data.getBranch_nm(),strm);

			String enrollNo = Data.getEnrollmentNo();
			// String fnm=Data.getFirstName();
			// String lnm=Data.getLastName();

			if (collegeEntities != null) {
				clgnm = collegeEntities.getId();
				logger.info("clgnm" + clgnm);
			}
			if (passingyr != null) {

				passyr = passingyr.getId();
				logger.info("passyr" + passyr);
			}

			if (sem != null) {

				semId = sem.getId();
				logger.info("semId" + semId);
			}

			/*
			 * if(branch!=null) {
			 * 
			 * branchId=branch.getId(); logger.info("branchId"+branchId); }
			 */

			// docEntity=universityStudentDocRepository.findByEnrollmentNoAndStreamIdAndPassingYearIdAndSemIdAndMonthOfPassing(enrollNo,strm,passyr,semId,monthnm);
			docEntity = universityStudentDocRepository.findByPrnNoAndSemIdAndPassingYearId(Data.getPrnNo(), semId,
					passyr);
			String reason = null;
			if (docEntity == null) {

				if (passingyr != null && sem != null && !Data.getPrnNo().equals("") && month != null
						&& collegeEntities != null && Data.getPrnNo() != null) {
					// Data.getOriginalDOCuploadfilePath().equals("ImageNotAvailable")) {
					// if(Data.getOriginalDOCuploadfilePath().equals("FileNotAvailable")) {
					UniversityStudentDocument studentData = new UniversityStudentDocument();
					UniversityStudDocResponse SavestudentData = new UniversityStudDocResponse();

					// studentData.setFirstName(Data.getFirstName());
					// studentData.setLastName(Data.getLastName());
					try {
						studentData.setCollegeId(clgnm);
						studentData.setStreamId(stream.getId());
						studentData.setEnrollmentNo(Data.getEnrollmentNo());
						studentData.setPassingYearId(passingyr.getId());
						// studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
						studentData.setSemId(sem.getId());
						// studentData.setBranchId(branchId);
						studentData.setCreatedate(new Date());
						studentData.setIsdeleted("N");
						studentData.setCreateby(userid.toString());
						studentData.setMonthOfPassing(monthnm);

						studentData.setPrnNo(Data.getPrnNo());
						studentData.setCenter(Data.getCenter());
						studentData.setMedDesc(Data.getMedDesc());
						studentData.setStudentName(Data.getStudentName());
						studentData.setSemTotalMax(Integer.valueOf(Data.getSemTotalMax()));
						studentData.setSemGradeTotal(Integer.valueOf(Data.getSemGradeTotal()));
						studentData.setSemGrace(Data.getSemGrace());
						studentData.setSemclass(Data.getSemclass());
						studentData.setSemGpa(Double.valueOf(Data.getSemGpa()));
						studentData.setSemGrade(Data.getSemGrade());
						studentData.setGradeTotal(Integer.valueOf(Data.getGradeTotal()));
						studentData.setClassGrace(Data.getClassGrace());
						studentData.setMaxGrTotal(Integer.valueOf(Data.getMaxGrTotal()));
						studentData.setCredTotal(Integer.valueOf(Data.getCredTotal()));
						studentData.setGpa(Double.valueOf(Data.getGpa()));
						studentData.setFgrade(Data.getFgrade());
						studentData.setResultDesc(Data.getResultDesc());
						studentData.setInceDesc(Data.getInceDesc());
						studentData.setSubOne(Data.getSubOne());
						studentData.setSubNmOne(Data.getSubNmOne());
						studentData.setCreditSubOne(Integer.valueOf(Data.getCreditSubOne()));
						studentData.setIntSubOne(Integer.valueOf(Data.getIntSubOne()));
						studentData.setExtSubOne(Integer.valueOf(Data.getExtSubOne()));
						studentData.setTotalSubOne(Integer.valueOf(Data.getTotalSubOne()));
						studentData.setGraceSubOne(Data.getGraceSubOne());
						studentData.setPrvFlgSubOne(Data.getPrvFlgSubOne());
						studentData.setMaxExtSubOne(Integer.valueOf(Data.getMaxExtSubOne()));
						studentData.setMaxIntSubOne(Integer.valueOf(Data.getMaxIntSubOne()));
						studentData.setMaxTotalSubOne(Integer.valueOf(Data.getMaxTotalSubOne()));
						studentData.setGradeSubOne(Data.getGradeSubOne());

						studentData.setSubTwo(Data.getSubTwo());
						studentData.setSubNmTwo(Data.getSubNmTwo());
						studentData.setCreditSubTwo(Integer.valueOf(Data.getCreditSubTwo()));
						studentData.setIntSubTwo(Integer.valueOf(Data.getIntSubTwo()));
						studentData.setExtSubTwo(Integer.valueOf(Data.getExtSubTwo()));
						studentData.setTotalSubTwo(Integer.valueOf(Data.getTotalSubTwo()));
						studentData.setGraceSubTwo(Data.getGraceSubTwo());
						studentData.setPrvFlgSubTwo(Data.getPrvFlgSubTwo());
						studentData.setMaxExtSubTwo(Integer.valueOf(Data.getMaxExtSubTwo()));
						studentData.setMaxIntSubTwo(Integer.valueOf(Data.getMaxIntSubTwo()));
						studentData.setMaxTotalSubTwo(Integer.valueOf(Data.getMaxTotalSubTwo()));
						studentData.setGradeSubTwo(Data.getGradeSubTwo());

						studentData.setSubThree(Data.getSubThree());
						studentData.setSubNmThree(Data.getSubNmThree());
						studentData.setCreditSubThree(Integer.valueOf(Data.getCreditSubThree()));
						studentData.setIntSubThree(Integer.valueOf(Data.getIntSubThree()));
						studentData.setExtSubThree(Integer.valueOf(Data.getExtSubThree()));
						studentData.setTotalSubThree(Integer.valueOf(Data.getTotalSubThree()));
						studentData.setGraceSubThree(Data.getGraceSubThree());
						studentData.setPrvFlgSubThree(Data.getPrvFlgSubThree());
						studentData.setMaxExtSubThree(Integer.valueOf(Data.getMaxExtSubThree()));
						studentData.setMaxIntSubThree(Integer.valueOf(Data.getMaxIntSubThree()));
						studentData.setMaxTotalSubThree(Integer.valueOf(Data.getMaxTotalSubThree()));
						studentData.setGradeSubThree(Data.getGradeSubThree());

						studentData.setSubFour(Data.getSubFour());
						studentData.setSubNmFour(Data.getSubNmFour());
						studentData.setCreditSubFour(Integer.valueOf(Data.getCreditSubFour()));
						studentData.setIntSubFour(Integer.valueOf(Data.getIntSubFour()));
						studentData.setExtSubFour(Integer.valueOf(Data.getExtSubFour()));
						studentData.setTotalSubFour(Integer.valueOf(Data.getTotalSubFour()));
						studentData.setGraceSubFour(Data.getGraceSubFour());
						studentData.setPrvFlgSubFour(Data.getPrvFlgSubFour());
						studentData.setMaxExtSubFour(Integer.valueOf(Data.getMaxExtSubFour()));
						studentData.setMaxIntSubFour(Integer.valueOf(Data.getMaxIntSubFour()));
						studentData.setMaxTotalSubFour(Integer.valueOf(Data.getMaxTotalSubFour()));
						studentData.setGradeSubFour(Data.getGradeSubFour());

						studentData.setSubFive(Data.getSubFive());
						studentData.setSubNmFive(Data.getSubNmFive());
						studentData.setCreditSubFive(Integer.valueOf(Data.getCreditSubFive()));
						studentData.setIntSubFive(Integer.valueOf(Data.getIntSubFive()));
						studentData.setExtSubFive(Integer.valueOf(Data.getExtSubFive()));
						studentData.setTotalSubFive(Integer.valueOf(Data.getTotalSubFive()));
						studentData.setGraceSubFive(Data.getGraceSubFive());
						studentData.setPrvFlgSubFive(Data.getPrvFlgSubFive());
						studentData.setMaxExtSubFive(Integer.valueOf(Data.getMaxExtSubFive()));
						studentData.setMaxIntSubFive(Integer.valueOf(Data.getMaxIntSubFive()));
						studentData.setMaxTotalSubFive(Integer.valueOf(Data.getMaxTotalSubFive()));
						studentData.setGradeSubFive(Data.getGradeSubFive());

						studentData.setSubSix(Data.getSubSix());
						studentData.setSubNmSix(Data.getSubNmSix());
						studentData.setCreditSubSix(Integer.valueOf(Data.getCreditSubSix()));
						studentData.setIntSubSix(Integer.valueOf(Data.getIntSubSix()));
						studentData.setExtSubSix(Integer.valueOf(Data.getExtSubSix()));
						studentData.setTotalSubSix(Integer.valueOf(Data.getTotalSubSix()));
						studentData.setGraceSubSix(Data.getGraceSubSix());
						studentData.setPrvFlgSubSix(Data.getPrvFlgSubSix());
						studentData.setMaxExtSubSix(Integer.valueOf(Data.getMaxExtSubSix()));
						studentData.setMaxIntSubSix(Integer.valueOf(Data.getMaxIntSubSix()));
						studentData.setMaxTotalSubSix(Integer.valueOf(Data.getMaxTotalSubSix()));
						studentData.setGradeSubSix(Data.getGradeSubSix());
						studentDataList.add(studentData);

					} catch (Exception e) {
						UniversityStudDocResponse invaliddata = new UniversityStudDocResponse();

						invaliddata.setSemester(Data.getSemester());
						invaliddata.setStream(Data.getStream());
						invaliddata.setEnrollmentNo(Data.getEnrollmentNo());
						invaliddata.setPassingYear(Data.getPassingYear());
						invaliddata.setMonthOfPassing(Data.getMonthOfPassing());
						invaliddata.setPrnNo(Data.getPrnNo());
						invaliddata.setReason("Invalid Record Entry");
						rejectedData.add(invaliddata);
					}
				}

				else {
					UniversityStudDocResponse studentData = new UniversityStudDocResponse();
					if (passingyr == null || sem == null || stream == null || month == null
							|| collegeEntities == null) {
						reason = "Invalid";
						if (stream == null) {

							reason = reason + " Stream";
							if (passingyr == null || sem == null || month == null || collegeEntities == null) {

								reason = reason + ",";

							}
						}

						if (passingyr == null) {
							reason = reason + " Year of Passing";
							if (sem == null || month == null || collegeEntities == null) {
								reason = reason + ",";

							}

						}
						if (sem == null) {
							reason = reason + " Semester";
							if (month == null || collegeEntities == null) {
								reason = reason + ",";

							}
						}
						if (month == null) {

							reason = reason + " Month Of Passing";
							if (collegeEntities == null) {
								reason = reason + ",";

							}

						}
					}

					if (collegeEntities == null) {
						reason = reason + " College Name";

					}

					if (Data.getPrnNo() == null || Data.getPrnNo().equals("")) {
						if (reason != null) {
							reason = reason + " & Blank PRN NO";

						} else {
							reason = "Blank PRN NO";
						}
					}

					studentData.setSemester(Data.getSemester());
					studentData.setStream(Data.getStream());
					studentData.setEnrollmentNo(Data.getEnrollmentNo());
					studentData.setPassingYear(Data.getPassingYear());
					// studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
					studentData.setMonthOfPassing(Data.getMonthOfPassing());
					studentData.setPrnNo(Data.getPrnNo());
					studentData.setReason(reason);
					rejectedData.add(studentData);
				}
			} else {
				UniversityStudDocResponse studentData = new UniversityStudDocResponse();
				reason = "Duplicate record";
				// studentData.setFirstName(Data.getFirstName());
				// studentData.setLastName(Data.getLastName());
				// studentData.setCollegeName(Data.getCollegeName());
				studentData.setStream(Data.getStream());
				// studentData.setBranch_nm(Data.getBranch_nm());
				studentData.setSemester(Data.getSemester());
				studentData.setEnrollmentNo(Data.getEnrollmentNo());
				studentData.setPassingYear(Data.getPassingYear());
				studentData.setMonthOfPassing(Data.getMonthOfPassing());
				studentData.setPrnNo(Data.getPrnNo());
				// studentData.setOriginalDOCuploadfilePath(Data.getOriginalDOCuploadfilePath());
				studentData.setReason(reason);

				rejectedData.add(studentData);
			}

		}
		logger.info("rejectedData : " + rejectedData);
		universityStudentDocRepository.saveAll(studentDataList);
		datalist.put("savedStudentData", savedStudDataList);
		datalist.put("RejectedData", rejectedData);
		return datalist;

	}
	
public List<UniversityStudentDocument> getUniversityStudData(String enrollmentno, Long yearofpassid, Long streamid,Long semId,String monthOfPassing, String prnNo){
		
		//EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("PERSISTENCE");

		 EntityManager em = emf.createEntityManager();
       CriteriaBuilder cb = em.getCriteriaBuilder();
       
       List<UniversityStudentDocument> resp = new ArrayList<>();
       
       
       CriteriaQuery<UniversityStudentDocument> cq = cb.createQuery(UniversityStudentDocument.class);
       Root<UniversityStudentDocument> book = cq.from(UniversityStudentDocument.class);
       List<Predicate> predicates = new ArrayList<>();
       
     
       if(enrollmentno != null ) {
       	predicates.add(cb.equal(book.get("enrollmentNo"), enrollmentno));
       }
       if(prnNo != null ) {
            predicates.add(cb.equal(book.get("prnNo"), prnNo));
           }
       if(streamid != null) {
       	predicates.add( cb.equal(book.get("streamId"), streamid));
           }
       if(semId != null) {
       	predicates.add(cb.equal(book.get("semId"), semId));
           }
       if(yearofpassid != null) {
       	predicates.add( cb.equal(book.get("passingYearId"), yearofpassid));
           }
       if(monthOfPassing != null ) {
       	predicates.add( cb.equal(book.get("monthOfPassing"), monthOfPassing));
       	
           }
		
       cq.where(predicates.toArray(new Predicate[0]));
       resp=em.createQuery(cq).getResultList();
       
       em.close();
//       emf.close();
       return resp;
	
	}
}
