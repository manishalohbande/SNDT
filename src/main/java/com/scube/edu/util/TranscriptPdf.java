package com.scube.edu.util;

import java.awt.Color;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.scube.edu.controller.StudentController;
import com.scube.edu.model.BranchMasterEntity;
import com.scube.edu.model.DocumentMaster;
import com.scube.edu.model.PassingYearMaster;
import com.scube.edu.model.SemesterEntity;
import com.scube.edu.model.StreamMaster;
import com.scube.edu.model.VerificationRequest;
import com.scube.edu.repository.VerificationRequestRepository;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.UserResponse;
import com.scube.edu.service.BranchMasterService;
import com.scube.edu.service.DocumentService;
import com.scube.edu.service.SemesterService;
import com.scube.edu.service.StreamService;
import com.scube.edu.service.UserService;
import com.scube.edu.service.YearOfPassingService;

@Service
public class TranscriptPdf {
	@Autowired
	VerificationRequestRepository verificationReqRepository;

	@Autowired
	UserService userService;

	@Autowired
	BranchMasterService branchService;

	@Autowired
	SemesterService semService;

	@Autowired
	StreamService streamService;

	@Autowired
	YearOfPassingService yearOfPassService;

	@Autowired
	DocumentService documentService;
	
	
	
	@Value("${file.Email-Files}")
    private String emailFileLocation;
	
	@Value("${file.imagepath-dir}")
    private String logoimageLocation;
	
//	@Value("${file.Email-Files-test}")
//    private String emailFileLocationTest;
	
//	@Value("${file.imagepathtest-dir}")
//    private String logoimageLocationTest;
	
	@Value("${file.awsORtest}")
    private String awsORtest;

	@Value("${from.mail.id}")
    private String fromMailID;
	
	@Value("${to.mail.id}")
    private String toMailId;
	
	@Value("${CC.Mail.id}")
    private String CCMailid;

	
	private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

	BaseResponse response = null;
	
	public void writeTranscriptPdf(HttpServletResponse response , Long id) throws Exception {

		System.out.println("******EmailServiceImpl writeApprovalPdf*******");

		System.out.println("--------------" + java.time.LocalDate.now());

		try {

			logger.info("imageLocation----->" );

			Optional<VerificationRequest> vrr = verificationReqRepository.findById(id);
			VerificationRequest vr = vrr.get();

			UserResponse ume = userService.getUserInfoById(vr.getUserId());

			Document document = new Document(PageSize.A4, 40, 40, 50, 7);
			// Set all required fonts here with appropriate names
			Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
			headFont.setSize(15);
			headFont.setColor(Color.BLACK);

			Font headAddrFont12 = FontFactory.getFont(FontFactory.HELVETICA);
			headAddrFont12.setSize(12);
			headAddrFont12.setColor(Color.BLACK);

			Font headAddrFont11 = FontFactory.getFont(FontFactory.HELVETICA);
			headAddrFont11.setSize(11);
			headAddrFont11.setColor(Color.BLACK);

			Font footerFont9 = FontFactory.getFont(FontFactory.HELVETICA);
			footerFont9.setSize(9);
			footerFont9.setColor(Color.BLACK);

			PdfWriter.getInstance(document, response.getOutputStream());

			HeaderFooter footer = new HeaderFooter(
					new Phrase("System generated document does not require signature.", footerFont9), false);
			footer.setAlignment(Element.ALIGN_CENTER);
//        footer.setBorder(Rectangle.NO_BORDER);
			document.setFooter(footer);

			// left, right, top, bottom
			logger.info("headerFooter set here--->just before document.open()");

			Image img;
	        if(awsORtest.equalsIgnoreCase("AWS")) {
	        	img = Image.getInstance(logoimageLocation+"logo.png"); // live
	        }if(awsORtest.equalsIgnoreCase("TEST")){
	        	img = Image.getInstance(logoimageLocation+"logo.png"); // test
//	        	img = Image.getInstance("logo.png");
	        }else {
	        	img = Image.getInstance(logoimageLocation+"logo.png");
	        }
			
//			 Image img = Image.getInstance(imageLocation+"/logo.png");
			//Image img = Image.getInstance("logo.png");

			img.setAlignment(Element.ALIGN_CENTER);
			img.scaleToFit(120, 100); // width, height

			document.open();
//	    EduCred_Logo.jpg

			document.add(img);

			Paragraph head = new Paragraph();
			head.setAlignment(Paragraph.ALIGN_RIGHT);
			head.setFont(headFont);
			head.add("Marks And Certification Unit");
			document.add(head);

			Paragraph headAddr = new Paragraph();
			headAddr.setFont(headAddrFont12);
			headAddr.setAlignment(Paragraph.ALIGN_RIGHT);
			headAddr.add(Chunk.NEWLINE);
			headAddr.add("Examinations Section, \r" + "M.J. Phule Bhavan, \r" + "Vidyanagari, Santacruz (East), \r"
					+ "Mumbai- 400 098. \r" + "Date: " + java.time.LocalDate.now());
			document.add(headAddr);

			Paragraph Addr = new Paragraph();
			Addr.setFont(headAddrFont12);
			Addr.setAlignment(Paragraph.ALIGN_LEFT);
			Addr.add(Chunk.NEWLINE);
			Addr.add("To, \r" + ume.getFirst_name() + " " + ume.getLast_name() + "\r" + "Email Id: " + ume.getEmail()
					+ ", \r" + "Phone No: " + ume.getPhone_no() + ", \r");
			Addr.add(Chunk.NEWLINE);
			Addr.add(Chunk.NEWLINE);
			Addr.add(Chunk.NEWLINE);
			document.add(Addr);

//	    Paragraph para = new Paragraph();
//	    para.setFont(headAddrFont10);
//	    para.setAlignment(Paragraph.ALIGN_CENTER);
//	    para.add("Sir/Madam, \r"
//	    		+ "       With reference to your application of Verification Document, this \r"
//	    		+ "is to inform you that the contents of the photocopy of the statement of marks of the \r"
//	    		+ "below mentioned candidate received along with your letter have been duly verified \r"
//	    		+ "and found correct.");
//	    document.add(para);
			logger.info("greeting set below here--->");

			Paragraph greeting = new Paragraph();
			greeting.setFont(headAddrFont11);
			greeting.setAlignment(Paragraph.ALIGN_LEFT);
			greeting.add("Sir/Madam, \r");

			Paragraph para = new Paragraph();
			para.setFont(headAddrFont11);
			para.setAlignment(Paragraph.ALIGN_LEFT);
			if(vr.getDocStatus().equalsIgnoreCase("UN_Approved_Pass")||vr.getDocStatus().equalsIgnoreCase("SVD_Approved_Pass")) {
			
			para.add(
					"          With reference to your application for Verification of the educational document , this is to inform you that the contents of the photocopy of the Statement Of Marks of the below mentioned candidate received along with your letter have been verified and found correct(Pass).");
			para.add(Chunk.NEWLINE);
			para.add(Chunk.NEWLINE);
			}
			if(vr.getDocStatus().equalsIgnoreCase("UN_Approved_Fail")||vr.getDocStatus().equalsIgnoreCase("SVD_Approved_Fail")) {
				
				para.add("          With reference to your application for Verification of the educational document , this is to inform you that the contents of the photocopy of the Statement Of Marks of the below mentioned candidate received along with your letter have been verified and found Fail.");
				para.add(Chunk.NEWLINE);
				para.add(Chunk.NEWLINE);
				}
			document.add(greeting);
			document.add(para);

			 PdfPTable detailsTable = new PdfPTable(5);
			    detailsTable.setWidthPercentage(100);
			   detailsTable.setWidths(new int[] {20,20,20,20,20});
			    
//	    PdfPCell cell1 = new PdfPCell(new Paragraph("Serial No"));
	    PdfPCell cell1 = new PdfPCell(new Paragraph("Date"));
	    PdfPCell cell2 = new PdfPCell(new Paragraph("Name Of Candidate"));
//	    PdfPCell cell3 = new PdfPCell(new Paragraph("Document Name"));
	    PdfPCell cell4 = new PdfPCell(new Paragraph("Month and Year Of Exam"));
	    PdfPCell cell5 = new PdfPCell(new Paragraph("Seat Number"));
//	    PdfPCell cell6 = new PdfPCell(new Paragraph("Branch"));
	    PdfPCell cell7 = new PdfPCell(new Paragraph("Stream"));
//	    PdfPCell cell8 = new PdfPCell(new Paragraph("Semester"));
	    
	    
	    
	    detailsTable.addCell(cell1);
	    detailsTable.addCell(cell2);
//	    detailsTable.addCell(cell3);
	    detailsTable.addCell(cell7);
	    detailsTable.addCell(cell4);
	    detailsTable.addCell(cell5);
//	    detailsTable.addCell(cell6);
	    
//	    detailsTable.addCell(cell8);

	    
	    
//	    for(VerificationRequest ent: vr) {
			logger.info("record values set here--->");

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String strDate = formatter.format(vr.getCreatedate());

			PassingYearMaster year = yearOfPassService.getYearById(vr.getYearOfPassingId());

			DocumentMaster doc = documentService.getNameById(vr.getDocumentId());

			BranchMasterEntity branch = branchService.getbranchById(vr.getBranchId());

			SemesterEntity sem = semService.getSemById(vr.getSemId());

			StreamMaster stream = streamService.getNameById(vr.getStreamId());

			PdfPCell dateCell = new PdfPCell(new Paragraph(strDate));
	    	PdfPCell nameCell = new PdfPCell(new Paragraph(vr.getFirstName() + " " + vr.getLastName()));
//	    	PdfPCell docCell = new PdfPCell(new Paragraph(doc.getDocumentName()));
	    	PdfPCell yearCell = new PdfPCell(new Paragraph(vr.getMonthOfPassing()+" "+year.getYearOfPassing()));
	    	PdfPCell enrollCell = new PdfPCell(new Paragraph(vr.getEnrollmentNumber()));
//	    	PdfPCell branchCell = new PdfPCell(new Paragraph(branch.getBranchName()));
	    	PdfPCell streamCell = new PdfPCell(new Paragraph(stream.getStreamName()));
//	    	PdfPCell semCell = new PdfPCell(new Paragraph(sem.getSemester()));
	    	
	    	detailsTable.addCell(dateCell);
	    	detailsTable.addCell(nameCell);
//	    	detailsTable.addCell(docCell);
	    	detailsTable.addCell(streamCell);
	    	detailsTable.addCell(yearCell);
	    	detailsTable.addCell(enrollCell);
//	    	detailsTable.addCell(branchCell);
	    	
//	    	detailsTable.addCell(semCell);
	    	
	    	

	    document.add(detailsTable);
	    
	    logger.info("detail table added--->");
	    //Add footer of PDF here
	    
	    Paragraph footer1 = new Paragraph();
	    footer1.setAlignment(Paragraph.ALIGN_RIGHT);
	    footer1.setFont(headAddrFont12);
	    footer1.add("Yours faithfully,");
	    document.add(footer1);
	    
	 //   Image signImg = Image.getInstance("signn.jpg");
//    Image signImg = Image.getInstance(imageLocation+ "/signn.jpg");
	    
	    Image signImg;
        if(awsORtest.equalsIgnoreCase("AWS")) {
        	signImg = Image.getInstance(logoimageLocation+"signn.jpg");
        }if(awsORtest.equalsIgnoreCase("TEST")){
        	signImg = Image.getInstance(logoimageLocation+"signn.jpg");
//        	signImg = Image.getInstance("signn.jpg");
        }else {
        	signImg = Image.getInstance(logoimageLocation+"signn.jpg");
        }
	    
	    signImg.setAlignment(Element.ALIGN_RIGHT);
	    signImg.scaleToFit(150, 120);
	    
	    document.add(signImg);
	    
			Paragraph foot = new Paragraph();
			foot.setAlignment(Paragraph.ALIGN_RIGHT);
			foot.setFont(headAddrFont12);
			foot.add("(Narendra G. Khalane \r" + "Assistant Registrar");
			document.add(foot);

//        HeaderFooter footer = new HeaderFooter( new Phrase("System generated document does not require signature.", footerFont9), true);
//        footer.setAlignment(Element.ALIGN_CENTER);
////        footer.setBorder(Rectangle.NO_BORDER);
//        document.setFooter(footer);
			logger.info("before document.close() here--->");

			document.close();
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

	}

}
