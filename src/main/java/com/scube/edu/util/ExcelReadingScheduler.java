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
		if(fi!=null) {
		InputStream csvstream = fi.getObjectContent();

		if(csvstream!=null) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(csvstream));

		System.out.println("--File content:");
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
			if (check == 0) {
				check++;
				continue;
			}

			String[] datalist = line.split(",");
			logger.info("" + datalist);
			UniversityStudDocResponse studentData = new UniversityStudDocResponse();

			for (k = 0; k < datalist.length; k++) {

				if (k == 0) {
					studentData.setStream(datalist[0]);
				} else if (k == 1) {
					studentData.setSemester(datalist[1]);

				} else if (k == 2) {
					studentData.setEnrollmentNo(datalist[2]);

				} else if (k == 3) {
					studentData.setMonthOfPassing(datalist[3]);
				}
				else if (k == 4) {
					studentData.setPassingYear(datalist[4]);

				} else if (k == 5) {
					imagefilenm = datalist[5];
					imagefile=imgLocation;
					logger.info("imageFileNAme" + imagefile);

					String flag = "2";

					Date date1 = new Date();
					SimpleDateFormat formatter1 = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
					String strdate1 = formatter1.format(date1);
					strdate1 = strdate1.replace(" ", "_");
					strdate1 = strdate1.replace(":", "-");
					if (!previmagefile.equals(imagefilenm)) {
						S3Object imgdata = fileStore.getimage(imagefile,imagefilenm);
						if (imgdata != null) {

							InputStream imagestream = imgdata.getObjectContent();

							filePath = fileStorageService.schedulerstoreFileOnAws(imagestream, flag, imagefilenm);

							S3Object mvimgdata = fileStore.getimage(imagefile,imagefilenm);
							InputStream movedimagestream = mvimgdata.getObjectContent();

							String filename = imagefilenm.split("\\.")[0];
							String extension = imagefilenm.split("\\.")[1];

							String newimagefilenm = filename+"_" + strdate1 +  "." + extension;
							fileStorageService.MoveCsvAndImgToArchive(movedimagestream, newimagefilenm, "1");
							fileStore.deleteFile(mvimgdata.getKey());

						} else {
							filePath = "ImageNotAvailable";
						}
					}
					previmagefile = imagefilenm;
					//studentData.setOriginalDOCuploadfilePath(filePath);

				}

			}
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
		fileStorageService.MoveCsvAndImgToArchive(mvcsvstream, csvnm, "1");
		

		fileStore.deleteFile(fi.getKey());
		}
		}

		// reader.close();
		long id = 0000;
		
		HashMap<String, List<UniversityStudDocResponse>> resp = associateManagerService
				.AutosaveStudentInfo(studentDataReviewList, id);

		if (resp.get("RejectedData") != null && !resp.get("RejectedData").isEmpty()) {
			List<UniversityStudDocResponse> response = resp.get("RejectedData");

			XSSFWorkbook workbook = new XSSFWorkbook();

			// spreadsheet object
			XSSFSheet sheet = workbook.createSheet(" Student Data ");

			int rownum = 0;
			// XSSFRow row = null;
			Row row = sheet.createRow(0);

			Cell cell = row.createCell(0);
			cell.setCellValue("Degree");

			cell = row.createCell(1);
			cell.setCellValue("Semester");

			cell = row.createCell(2);
			cell.setCellValue("Seat No.");
			
			cell = row.createCell(3);
			cell.setCellValue("MonthOfPassing");

			cell = row.createCell(4);
			cell.setCellValue("YearOfPassing");

			cell = row.createCell(5);
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

			fileStorageService.MoveCsvAndImgToArchive(targetStream, emailexcelstorePath.getName(), "2");

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
		cell.setCellValue(user.getStream());

		cell = row.createCell(1);
		cell.setCellValue(user.getSemester());

		cell = row.createCell(2);
		cell.setCellValue(user.getEnrollmentNo());

		cell = row.createCell(3);
		cell.setCellValue(user.getMonthOfPassing());
		
		cell = row.createCell(4);
		cell.setCellValue(user.getPassingYear());

		cell = row.createCell(5);
		cell.setCellValue(user.getReason());

	}

}
