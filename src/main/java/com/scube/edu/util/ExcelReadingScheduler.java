package com.scube.edu.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import javax.mail.Multipart;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.scube.edu.awsconfig.FileStore;
import com.scube.edu.model.checkautoReadingActiveEntity;
import com.scube.edu.repository.ChkAutoReadingStatus;
import com.scube.edu.response.UniversityStudDocResponse;
import com.scube.edu.service.AssociateManagerService;
import com.scube.edu.service.AssociateManagerServiceImpl;
import com.scube.edu.service.EmailService;
import com.scube.edu.response.UniversityStudDocResponse;

@Service
public class ExcelReadingScheduler {

	private static final Logger logger = LoggerFactory.getLogger(AssociateManagerServiceImpl.class);

	@Autowired
	private AssociateManagerService associateManagerService;

	@Autowired
	FileStore fileStore;
	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private EmailService emailService;
	

	@Autowired
	ChkAutoReadingStatus chkAutoReadingStatus;

	@Value("${file.awsORtest}")
	private String awsORtest;

	 @Value("${csv.file.folder}")
	   private String csvFileLocation;
	 
	/*
	 * @Value("${file.RejectedData-Files}") private String rejcsvfile;
	 */
	 
	/*
	 * @Value("${csv.filename}") private String csvfilenm;
	 */
	 
	 @Value("${img.folder}")
	    private String imgLocation;
	
	//@Scheduled(cron = "${cron.time.excelRead}")
	public String readExcelFiles() throws Exception, IOException {		
		String result = "";
		if(awsORtest.equalsIgnoreCase("AWS")) {

			Optional<checkautoReadingActiveEntity> chk= chkAutoReadingStatus.findById((long) 1);
			checkautoReadingActiveEntity ischk=chk.get();
			String chkflag = null;
			if(ischk!=null)
			{
				if(ischk.getFlag()!=null){
					chkflag=ischk.getFlag();
				}
			}
			if(chkflag!=null) {
			if(chkflag.equalsIgnoreCase("InActive")) {
				String statusflag="Active";
				chkAutoReadingStatus.updateFlg(statusflag);

		logger.info("********Enterning ExcelReadingScheduler readExcelFiles********");
		List<UniversityStudDocResponse> studentDataReviewList = new ArrayList<UniversityStudDocResponse>();
		String imagefile = null;
		String imagefilenm=null;
		String previmagefile = "";
		
		File emailexcelstorePath = null;
		int k;
		int check = 0;
		String filePath = null;
		String fileSubPath = "file/";
		String line;
		String chekrow;

		String csvnm;
         
		String folder=csvFileLocation;
		//String flnm=csvfilenm;
		S3Object fi = fileStore.readExcel(csvFileLocation);
		//fileStore.deleteFile(fi.getKey());

		if(fi!=null) {
		InputStream csvstream = fi.getObjectContent();

		if(csvstream!=null) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(csvstream));

		System.out.println("--File content:");
		String monthyear;
		String month = null;
		String year = null;
		String clgnsem;
		String stream = null;
		String sem = null;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			if (check == 0) {
				check++;
				continue;
			}
			
			String[] datalist = line.split(",");
			logger.info("" + datalist);
			UniversityStudDocResponse studentData = new UniversityStudDocResponse();
			
           if(check ==1) {
        	   for (k = 0; k < 5; k++) {

   				 if (k == 2) {
   					monthyear=datalist[2];
   					String[] arr=monthyear.split(" ");
   					month=arr[0];
   					year=arr[1];
   					
   				} else if (k == 3) {
   					clgnsem=datalist[3];
   					String[] arr1=clgnsem.split(" - ");
   					stream=arr1[0];
   					sem=arr1[1];

   					
   				}
   				}
        	   check++;

        	   continue;
			}
			for (k = 0; k < datalist.length; k++) {
				
				if (k == 0) {
					studentData.setPrnNo(datalist[0]);
				} else if (k == 1) {
					studentData.setEnrollmentNo(datalist[1]);


				} else if (k == 2) {
					studentData.setCenter(datalist[2]);

				} else if (k == 3) {
					studentData.setCollegeName(datalist[3]);
				}
				else if (k == 4) {
					studentData.setMedDesc(datalist[4]);

				} else if (k == 5) {
					studentData.setStudentName(datalist[5]);

				}
			 else if (k == 6) {
					studentData.setSemTotalMax(Integer.valueOf(datalist[6]));
			}
			 else if (k == 7) {
					studentData.setSemGradeTotal(Integer.valueOf(datalist[7]));

					
				} else if (k == 8) {
					studentData.setSemGrace(datalist[8]);

					
				} else if (k == 9) {
					studentData.setSemclass(datalist[9]);

					
				} else if (k == 10) {
					studentData.setSemGpa(Double.valueOf(datalist[10]));

					
				} else if (k == 11) {
					studentData.setSemGrade(datalist[11]);

				} else if (k == 12) {
					studentData.setGradeTotal(Integer.valueOf(datalist[12]));

				} else if (k == 13) {
					studentData.setClassGrace(datalist[13]);

				} else if (k == 14) {
					studentData.setMaxGrTotal(Integer.valueOf(datalist[14]));

				} else if (k == 15) {
					studentData.setCredTotal(Integer.valueOf(datalist[15]));

				} else if (k == 16) {
					studentData.setGpa(Double.valueOf(datalist[16]));

				} else if (k == 17) {
					studentData.setFgrade(datalist[17]);

				} else if (k == 18) {
					studentData.setResultDesc(datalist[18]);

				} else if (k == 19) {
					studentData.setInceDesc(datalist[19]);

				} else if (k == 20) {
					studentData.setSubOne(datalist[20]);

					
				} else if (k == 21) {
					studentData.setSubNmOne(datalist[21]);

				} else if (k == 22) {
					studentData.setCreditSubOne(Integer.valueOf(datalist[22]));

				} else if (k == 23) {
					studentData.setIntSubOne(Integer.valueOf(datalist[23]));

				} else if (k == 24) {
					studentData.setExtSubOne(Integer.valueOf(datalist[24]));

				} else if (k == 25) {
					studentData.setTotalSubOne(Integer.valueOf(datalist[25]));

				} else if (k == 26) {
					studentData.setGraceSubOne(datalist[26]);
				} else if (k == 27) {
					studentData.setPrvFlgSubOne(datalist[27]);

				} else if (k == 28) {
					studentData.setMaxExtSubOne(Integer.valueOf(datalist[28]));

				} else if (k == 29) {
					studentData.setMaxIntSubOne(Integer.valueOf(datalist[29]));

				} else if (k == 30) {
					studentData.setMaxTotalSubOne(Integer.valueOf(datalist[30]));

				} else if (k == 31) {
					studentData.setGradeSubOne(datalist[31]);

				} else if (k == 32) {
				
				studentData.setSubTwo(datalist[32]);
				} else if (k == 33) {
				studentData.setSubNmTwo(datalist[33]);
				} else if (k == 34) {
				studentData.setCreditSubTwo(Integer.valueOf(datalist[34]));
				} else if (k == 35) {
				studentData.setIntSubTwo(Integer.valueOf(datalist[35]));
				} else if (k == 36) {
				studentData.setExtSubTwo(Integer.valueOf(datalist[36]));
				} else if (k == 37) {
				studentData.setTotalSubTwo(Integer.valueOf(datalist[37]));
				} else if (k == 38) {
				studentData.setGraceSubTwo(datalist[38]);
				} else if (k == 39) {
				studentData.setPrvFlgSubTwo(datalist[39]);
				} else if (k == 40) {
				studentData.setMaxExtSubTwo(Integer.valueOf(datalist[40]));
				} else if (k == 41) {
				studentData.setMaxIntSubTwo(Integer.valueOf(datalist[41]));
				} else if (k == 42) {
				studentData.setMaxTotalSubTwo(Integer.valueOf(datalist[42]));
				} else if (k == 43) {
				studentData.setGradeSubTwo(datalist[43]);
				} 
				else if (k == 44) {

				studentData.setSubThree(datalist[44]);
				} else if (k == 45) {
				studentData.setSubNmThree(datalist[45]);
				} else if (k == 46) {
				studentData.setCreditSubThree(Integer.valueOf(datalist[46]));
				} else if (k == 47) {
				studentData.setIntSubThree(Integer.valueOf(datalist[47]));
				} else if (k == 48) {
				studentData.setExtSubThree(Integer.valueOf(datalist[48]));
				} else if (k == 49) {
				studentData.setTotalSubThree(Integer.valueOf(datalist[49]));
				} else if (k == 50) {
				studentData.setGraceSubThree(datalist[50]);
				} else if (k == 51) {
				studentData.setPrvFlgSubThree(datalist[51]);
				} else if (k == 52) {
				studentData.setMaxExtSubThree(Integer.valueOf(datalist[52]));
				} else if (k == 53) {
				studentData.setMaxIntSubThree(Integer.valueOf(datalist[53]));
				} else if (k == 54) {
				studentData.setMaxTotalSubThree(Integer.valueOf(datalist[54]));
				} else if (k == 55) {
				studentData.setGradeSubThree(datalist[55]);
				} else if (k == 56) {

				studentData.setSubFour(datalist[56]);
				} else if (k == 57) {
				studentData.setSubNmFour(datalist[57]);
				} else if (k == 58) {
				studentData.setCreditSubFour(Integer.valueOf(datalist[58]));
				} else if (k == 59) {
				studentData.setIntSubFour(Integer.valueOf(datalist[59]));
				} else if (k == 60) {
				studentData.setExtSubFour(Integer.valueOf(datalist[60]));
				} else if (k == 61) {
				studentData.setTotalSubFour(Integer.valueOf(datalist[61]));
				} else if (k == 62) {
				studentData.setGraceSubFour(datalist[62]);
				} else if (k == 63) {
				studentData.setPrvFlgSubFour(datalist[63]);
				} else if (k == 64) {
				studentData.setMaxExtSubFour(Integer.valueOf(datalist[64]));
				} else if (k == 65) {
				studentData.setMaxIntSubFour(Integer.valueOf(datalist[65]));
				} else if (k == 66) {
				studentData.setMaxTotalSubFour(Integer.valueOf(datalist[66]));
				} else if (k == 67) {
				studentData.setGradeSubFour(datalist[67]);
				} else if (k == 68) {

				studentData.setSubFive(datalist[68]);
				} else if (k == 69) {
				studentData.setSubNmFive(datalist[69]);
				} else if (k == 70) {
				studentData.setCreditSubFive(Integer.valueOf(datalist[70]));
				} else if (k == 71) {
				studentData.setIntSubFive(Integer.valueOf(datalist[71]));
				} else if (k == 72) {
				studentData.setExtSubFive(Integer.valueOf(datalist[72]));
				} else if (k == 73) {
				studentData.setTotalSubFive(Integer.valueOf(datalist[73]));
				} else if (k == 74) {
				studentData.setGraceSubFive(datalist[74]);
				} else if (k == 75) {
				studentData.setPrvFlgSubFive(datalist[75]);
				} else if (k == 76) {
				studentData.setMaxExtSubFive(Integer.valueOf(datalist[76]));
				} else if (k == 77) {
				studentData.setMaxIntSubFive(Integer.valueOf(datalist[77]));
				} else if (k == 78) {
				studentData.setMaxTotalSubFive(Integer.valueOf(datalist[78]));
				} else if (k == 79) {
				studentData.setGradeSubFive(datalist[79]);
				} else if (k == 80) {

				studentData.setSubSix(datalist[80]);
				} else if (k == 81) {
				studentData.setSubNmSix(datalist[81]);
				} else if (k == 82) {
				studentData.setCreditSubSix(Integer.valueOf(datalist[82]));
				} else if (k == 83) {
				studentData.setIntSubSix(Integer.valueOf(datalist[83]));
				} else if (k == 84) {
				studentData.setExtSubSix(Integer.valueOf(datalist[84]));
				} else if (k == 85) {
				studentData.setTotalSubSix(Integer.valueOf(datalist[85]));
				} else if (k == 86) {
				studentData.setGraceSubSix(datalist[86]);
				} else if (k == 87) {
				studentData.setPrvFlgSubSix(datalist[87]);
				} else if (k == 88) {
				studentData.setMaxExtSubSix(Integer.valueOf(datalist[88]));
				} else if (k == 89) {
				studentData.setMaxIntSubSix(Integer.valueOf(datalist[89]));
				} else if (k == 90) {
				studentData.setMaxTotalSubSix(Integer.valueOf(datalist[90]));
				} else if (k == 91) {
				studentData.setGradeSubSix(datalist[91]);
				} 
				
			}
			studentData.setPassingYear(year);
			studentData.setMonthOfPassing(month);
			studentData.setStream(stream);
			studentData.setSemester(sem);
			studentDataReviewList.add(studentData);

		}
		
		}
		else
		{
			result="File is Empty/Blank";
		}
		
		Date date1 = new Date();
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
		String strdate1 = formatter1.format(date1);
		strdate1 = strdate1.replace(" ", "_");
		strdate1 = strdate1.replace(":", "-");

		csvnm = strdate1 + ".csv";
		S3Object moveddat = fileStore.readExcel(csvFileLocation);
		if(moveddat!=null) {
		InputStream mvcsvstream = moveddat.getObjectContent();
		if(mvcsvstream!=null){
		//fileStorageService.MoveCsvAndImgToArchive(mvcsvstream, csvnm, "1");
		

		fileStore.deleteFile(fi.getKey());
		}
		}

		// reader.close();
		long id = 0000;
		
		HashMap<String, List<UniversityStudDocResponse>> resp = associateManagerService
				.saveStudentInfo(studentDataReviewList, id);

		if (resp.get("RejectedData") != null && !resp.get("RejectedData").isEmpty()) {
			List<UniversityStudDocResponse> response = resp.get("RejectedData");

			XSSFWorkbook workbook = new XSSFWorkbook();

			// spreadsheet object
			XSSFSheet sheet = workbook.createSheet(" Student Data ");

			int rownum = 0;
			// XSSFRow row = null;
			Row row = sheet.createRow(0);

			Cell cell = row.createCell(0);
			cell.setCellValue("PRN NO.");
			cell = row.createCell(1);
			cell.setCellValue("Semester");
			cell = row.createCell(2);
			cell.setCellValue("Reasons");
			rownum++;
			for (UniversityStudDocResponse user : response) {
				row = sheet.createRow(rownum++);
				createList(user, row);
			}

			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
			String strdate = formatter.format(date);
			strdate = strdate.replace(" ", "_");
			strdate = strdate.replace(":", "-");

			if(awsORtest.equalsIgnoreCase("AWS")) {
				String returnpath=fileStorageService.storeRejectedDataFile("Rejected_Data_" + strdate + ".csv");
			    emailexcelstorePath = new File(returnpath);
			}
			else {
				
				emailexcelstorePath = new File("./Rejected_Csv/Rejected_Data_" + strdate + ".csv");

			}
			FileOutputStream out = new FileOutputStream(emailexcelstorePath);
			workbook.write(out);
			out.close();
			InputStream targetStream = new FileInputStream(emailexcelstorePath);

			//fileStorageService.MoveCsvAndImgToArchive(targetStream, emailexcelstorePath.getName(), "2");

			emailService.sendRejectedDatamail(emailexcelstorePath);
		
			if(emailexcelstorePath.delete())
	        {
	            System.out.println("File deleted successfully From EDU");
	        }
	        else
	        {
	            System.out.println("Failed to delete the file From EDU");
	        }
			result="Added data successfully, Rejected Data sent via mail";

		}
		else {
			result="Added data succefully,No data is Rejected from this file";
		}

		}
		else
		{
			result="File not found to read data";
			logger.info("********result********"+result);

		}
		logger.info("********result********"+result);

	
		String statusflag1="InActive";
		chkAutoReadingStatus.updateFlg(statusflag1);
			}
			else
			{
				result="Process already active!";
				logger.info("********result********"+result);

			}
			
		}
		}
		logger.info("******** Exiting ExcelReadingScheduler readExcelFiles********");


		return result;

	}

	private static void createList(UniversityStudDocResponse user, Row row) // creating cells for each row
	{
		Cell cell = row.createCell(0);
		cell.setCellValue(user.getPrnNo());

		/*
		 * cell = row.createCell(1); cell.setCellValue(user.getSemester());
		 * 
		 * cell = row.createCell(2); cell.setCellValue(user.getEnrollmentNo());
		 * 
		 * cell = row.createCell(3); cell.setCellValue(user.getMonthOfPassing());
		 * 
		 * cell = row.createCell(4); cell.setCellValue(user.getPassingYear());
		 */
		cell = row.createCell(1);
		cell.setCellValue(user.getSemester());
		cell = row.createCell(2);
		cell.setCellValue(user.getReason());

	}

}
