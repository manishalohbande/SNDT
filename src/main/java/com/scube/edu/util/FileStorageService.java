
package com.scube.edu.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.S3Object;
import com.scube.edu.awsconfig.BucketName;
import com.scube.edu.awsconfig.FileStore;
import com.scube.edu.controller.VerifierController;
import com.scube.edu.exception.FileStorageException;
import com.scube.edu.model.FileStorageProperties;
import com.scube.edu.model.PassingYearMaster;
import com.scube.edu.model.UniversityStudentDocument;
import com.scube.edu.model.VerificationRequest;
import com.scube.edu.repository.VerificationRequestRepository;
import com.scube.edu.service.UniversityStudentDocServiceImpl;


@Service
public class FileStorageService {
	
	private Path fileStorageLocation;
	
	private final String fileBaseLocation;
	
	private final String fileAssociateBaseLocation;
	
	private final String imagePathDir;
	
	private final String rejectedDataDir;

	private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
	 
	 @Autowired
	 VerificationRequestRepository verificationReqRepository;
	 
	 @Autowired
	 FileStore fileStore;
	 
	 
	 @Autowired
	 UniversityStudentDocServiceImpl universityStudentDocServiceImpl ;
	 
	 
	  public FileStorageService(FileStorageProperties fileStorageProperties) {
	  
	  this.fileBaseLocation = fileStorageProperties.getUploadDir();
	  this.fileAssociateBaseLocation = fileStorageProperties.getUploadassociateDir();
	  this.imagePathDir = fileStorageProperties.getImagepathDir();
	  this.rejectedDataDir=fileStorageProperties.getRejecteddataDir();
	  }
	 
	
	
	public String storeFile(MultipartFile file, String fileSubPath, String flag) {
		
		System.out.println("****fileStorageService storeFile*****"+file);
		
		Date date = new Date(System.currentTimeMillis());
		
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		
		String filename = fileName.split("\\.")[0];
		String extension = fileName.split("\\.")[1];
		
		String fileNewName = filename + "_" + date.getTime() + "." + extension;
		
		System.out.println(fileNewName);
		
		try {
			
			if(fileNewName.contains("..")) {
				throw new FileStorageException("Sorry! File Name contains invalid path sequence!");
			}
			String newPath;
			
			if(flag.equalsIgnoreCase("2")) {
				newPath = this.fileAssociateBaseLocation + "/" + fileSubPath;
			}else {
				newPath = this.fileBaseLocation +"/" + fileSubPath; 
			}
			
			this.fileStorageLocation = Paths.get(newPath).toAbsolutePath().normalize();
			
			Files.createDirectories(this.fileStorageLocation);
			
			Path targetLocation = this.fileStorageLocation.resolve(fileNewName);
			
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println("fileName" + fileNewName+ " ---filePath" + targetLocation);
			
			String returnPath = fileSubPath + fileNewName;
			
			
			return String.valueOf(returnPath);
		}catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
		
		
		
	}
	
	 @Value("${file.awsfileVRPath-dir}")
     private String vrFilePath;
	 
	 @Value("${file.awsfileUPath-dir}")
     private String uFilePath;
	
     public String storeFileOnAws(MultipartFile file, String flag) {
		
		System.out.println("****fileStorageService storeFile*****"+file);
		
		/*
		 * Date date = new Date(System.currentTimeMillis());
		 * 
		 * String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		 * 
		 * String filename = fileName.split("\\.")[0]; String extension =
		 * fileName.split("\\.")[1];
		 * 
		 * String fileNewName = filename + "_" + date.getTime() + "." + extension;
		 * 
		 * System.out.println(fileNewName);
		 */
		
		try {
			
			 if (file.isEmpty()) {
		            throw new IllegalStateException("Cannot upload empty file");
		        }
		        
			 Map<String, String> metadata = new HashMap<>();
		        metadata.put("Content-Type", file.getContentType());
		        metadata.put("Content-Length", String.valueOf(file.getSize()));
		        
		        
		        String newPath ;
				  
			
			  if(flag.equalsIgnoreCase("2")) { 
				  newPath = uFilePath+UUID.randomUUID(); 
			  }else { 
					  newPath = vrFilePath+UUID.randomUUID(); 
			  }
			 
		        
			 
			 String path = String.format("%s/%s",BucketName.TODO_IMAGE.getBucketName(),newPath);
		        
	        logger.info("---------TodoServiceImpl path----------------"+ path );
	        
	        String fileName = String.format("%s", file.getOriginalFilename());
	        
	        logger.info("---------TodoServiceImpl fileName----------------"+fileName );
	        
	        logger.info("---------TodoServiceImpl start fileStore upload----------------");
	        
            fileStore.upload(path, fileName, Optional.of(metadata), file.getInputStream());
			
			
			
			  
			 
//			  
			  String returnPath = newPath +"/"+ fileName;
//			  
//			  fileStore.upload(newPath, fileName, Optional.of(metadata),
//			  file.getInputStream());
			 
			  logger.info("---------returnPath----------------"+returnPath);
			
			return String.valueOf(returnPath);
		}catch (IOException ex) {
			throw new FileStorageException("Could not store file", ex);
		}
		
		
		
	}


public HashMap<String, Object> loadFileAsResourceFromAws(String userFor, Long id) throws Exception {
	
 	String fileName =" ";
 	try {
 		
 		String newPAth ;
 		String extension;
 		String randomId;
 		String nameOfFile;
    	
    	if(userFor.equalsIgnoreCase("VR")) {
    		
//    		newPAth = this.fileBaseLocation;
    		Optional<VerificationRequest> verifierData = verificationReqRepository.findById(id);
    		VerificationRequest data = verifierData.get();
    		
    		String filename = data.getUploadDocumentPath();
    		
    		logger.info("doc.vollegename--->"+data.getEnrollmentNumber());
    		
//    		 String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//    		  
//    		 String filename = fileName.split("\\.")[0]; String extension =
//    		 fileName.split("\\.")[1];
    		
    		fileName = data.getUploadDocumentPath();
    		
    		extension = fileName.split("\\.")[1];
    		
    		randomId = fileName.split("\\/")[2];
    		
//    		newPAth = "educred/file/verification_docs/"+randomId;
    		
    		newPAth = BucketName.TODO_IMAGE.getBucketName()+ "/" + vrFilePath + randomId ; 
    		
    		nameOfFile = fileName.split("\\/")[3];
    		
    		System.out.println("------------fileName--------------"+ nameOfFile);
    		logger.info("VR------fileName----->"+ fileName);
    		
    	}
    	else {
    		
    		UniversityStudentDocument doc = universityStudentDocServiceImpl.getUniversityDocDataById(id);
    		fileName = doc.getOriginalDOCuploadfilePath();
    		
    		extension = fileName.split("\\.")[1]; // req for mediaType
    		
    		randomId = fileName.split("\\/")[2]; // req for completing path
    		
//    		newPAth = "educred/file/associate_docs/"+randomId; // req for defining path
    		
    		newPAth = BucketName.TODO_IMAGE.getBucketName() + "/" + uFilePath + randomId ; 
    		
    		nameOfFile = fileName.split("\\/")[3];
    		
    		System.out.println("--------InsideElse----fileName--------------"+nameOfFile);
    		logger.info("U------fileName----->"+ fileName);
    	}
 		
 		
 		byte[] res = fileStore.download(newPAth, nameOfFile);
 		
 		HashMap<String,Object> map = new HashMap<String,Object>();
 		
 		map.put("byteArray", res);
 		map.put("extension", extension);
 		
         return map ;
    	

        //return fileStore.download("educred/file/verification_docs/0e233706-971c-4961-b817-4d73ad580dbd", "page.png");


    } catch (Exception ex) {
        throw new Exception("File not found " + fileName, ex);
    }
}
	
	
	
	
		public Resource loadFileAsResource(String userFor, Long id) throws Exception {
			
		 	String fileName ="";
		 	logger.info(this.imagePathDir);
		 	try {
	        	
	        	String newPAth = "";     
	        	if(userFor.equalsIgnoreCase("VR")) {	        		
	        		newPAth = this.fileBaseLocation;
	        		Optional<VerificationRequest> verifierData = verificationReqRepository.findById(id);
	        		VerificationRequest data = verifierData.get();
	        		logger.info("doc.vollegename--->"+data.getEnrollmentNumber());	        		
	        		fileName = data.getUploadDocumentPath();	        		
//	        		System.out.println("------------fileName--------------"+fileName);
	        		logger.info("VR------fileName----->"+ fileName);	      
	        		
		        	
	        		Path fileStorageLocation = Paths.get(newPAth).toAbsolutePath().normalize();
	  	          
	  	          System.out.println("----this.fileStorageLocation--------userFor---------"+fileStorageLocation+"-------------"+userFor);
	  	          
	  	            logger.info("newPath"+ newPAth);
	  	        	logger.info("fileStorageLocation"+ fileStorageLocation);
//	  	            System.out.println(this.fileStorageLocation);
	  	            
	  	            Path filePath = fileStorageLocation.resolve(fileName).normalize();
	  	            logger.info("filePath"+ filePath);
//	  	            System.out.println(filePath);
//	  	            System.out.println(filePath.toUri());
//	  	            logger.info(filePath.toUri());
	  	            
	  	            
	  	            Resource resource = new UrlResource(filePath.toUri());
	  	            if(resource.exists()) {
	  	            	logger.info("Inside IF(resource.exists)");
	  	                return resource;
	  	            } else {
	  	            	logger.info("Inside else()");
	  	                throw new Exception("File not found " + fileName);
	  	            }
	        		
	        		
	        	}
//	        	else {
//	        		newPAth = this.fileAssociateBaseLocation;
//	        		UniversityStudentDocument doc = universityStudentDocServiceImpl.getUniversityDocDataById(id);
//	        		fileName = doc.getOriginalDOCuploadfilePath();	        		
////	        		System.out.println("--------InsideElse----fileName--------------"+fileName);
//	        		logger.info("U------fileName----->"+ fileName);
//	        		
//	        		
//	        		Path fileStorageLocation = Paths.get(newPAth).toAbsolutePath().normalize();
//	  	          
//	  	          System.out.println("----this.fileStorageLocation--------userFor---------"+fileStorageLocation+"-------------"+userFor);
//	  	          
//	  	          //  logger.info("newPath"+ newPAth);
//	  	        //	logger.info("fileStorageLocation"+ this.fileStorageLocation);
////	  	            System.out.println(this.fileStorageLocation);
//	  	            
//	  	            Path filePath = fileStorageLocation.resolve(fileName).normalize();
//	  	            logger.info("filePath"+ filePath);
////	  	            System.out.println(filePath);
////	  	            System.out.println(filePath.toUri());
////	  	            logger.info(filePath.toUri());
//	  	            
//	  	            
//	  	            Resource resource = new UrlResource(filePath.toUri());
//	  	            if(resource.exists()) {
//	  	            	logger.info("Inside IF(resource.exists)");
//	  	                return resource;
//	  	            } else {
//	  	            	logger.info("Inside else()");
//	  	                throw new Exception("File not found " + fileName);
//	  	            }
//	        	}
	        
	        	return null;
	        	
	        } catch (Exception ex) {
	            throw new Exception("File not found " + fileName, ex);
	        }
	    }
	
	
		
		
		
		public String storeschedularFile(File file, String fileSubPath, String flag) {
			
			System.out.println("****fileStorageService storeFile*****"+file);
			
			Date date = new Date(System.currentTimeMillis());
			
			String fileName = StringUtils.cleanPath(file.getName());
			
			String filename = fileName.split("\\.")[0];
			String extension = fileName.split("\\.")[1];
			
			String fileNewName = filename + "_" + date.getTime() + "." + extension;
			
			System.out.println(fileNewName);
			
			try {
				
				if(fileNewName.contains("..")) {
					throw new FileStorageException("Sorry! File Name contains invalid path sequence!");
				}
				String newPath;
				
				if(flag.equalsIgnoreCase("2")) {
					newPath = this.fileAssociateBaseLocation + "/" + fileSubPath;
				}else {
					newPath = this.fileBaseLocation +"/" + fileSubPath; 
				}
				
				this.fileStorageLocation = Paths.get(newPath).toAbsolutePath().normalize();
				
				Files.createDirectories(this.fileStorageLocation);
				
				Path targetLocation = this.fileStorageLocation.resolve(fileNewName);
				
				Path sourceFilePath =file.toPath();
				Files.copy(sourceFilePath, targetLocation, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("fileName" + fileNewName+ " ---filePath" + targetLocation);
				
				String returnPath = fileSubPath + fileNewName;
				
				
				return String.valueOf(returnPath);
			}catch (IOException ex) {
				throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
			}
			
			
			
		}

	public String schedulerstoreFileOnAws(InputStream imagestream, String flag, String imgnm) {

		System.out.println("****fileStorageService schedulerstoreFileOnAws*****");

		/*
		 * Date date = new Date(System.currentTimeMillis());
		 * 
		 * String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		 * 
		 * String filename = fileName.split("\\.")[0]; String extension =
		 * fileName.split("\\.")[1];
		 * 
		 * String fileNewName = filename + "_" + date.getTime() + "." + extension;
		 * 
		 * System.out.println(fileNewName);
		 */

		try {

			/*
			 * if (((MultipartFile) imagestream).isEmpty()) { throw new
			 * IllegalStateException("Cannot upload empty file"); }
			 */

			Map<String, String> metadata = new HashMap<>();
			/*
			 * metadata.put("Content-Type", ((MultipartFile) imagestream).getContentType());
			 * metadata.put("Content-Length", String.valueOf(((MultipartFile)
			 * imagestream).getSize()));
			 */

			String newPath;

			if (flag.equalsIgnoreCase("2")) {
				newPath = uFilePath + UUID.randomUUID();
			} else {
				newPath = vrFilePath + UUID.randomUUID();
			}

			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
			String strdate = formatter.format(date);
			strdate = strdate.replace(" ", "_");
			strdate = strdate.replace(":", "-");
			String path = String.format("%s/%s", BucketName.TODO_IMAGE.getBucketName(), newPath);

			logger.info("---------TodoServiceImpl path----------------" + path);

			String fileName = String.format("%s", imgnm);

			logger.info("---------TodoServiceImpl fileName----------------" + fileName);

			logger.info("---------TodoServiceImpl start fileStore upload----------------");

			// fileStore.upload(path, fileName, );
			fileStore.upload(path, fileName, Optional.of(metadata), imagestream);

//					  
			String returnPath = newPath + "/" + fileName;
//					  
//					  fileStore.upload(newPath, fileName, Optional.of(metadata),
//					  file.getInputStream());

			logger.info("---------returnPath----------------" + returnPath);

			return String.valueOf(returnPath);
		} catch (Exception ex) {
			throw new FileStorageException("Could not store file", ex);
		}

	}

	@Value("${excel.img.moved.dir}")
    private String csvImgmovedDir;
	
	@Value("${rejected.excel.store.dir}")
    private String rejectedCsvStoreDir;
	public String MoveCsvAndImgToArchive(InputStream imagestream, String imgnm,String flag) {

		System.out.println("****fileStorageService MoveCsvAndImgToArchive*****");

		try {

		

			Map<String, String> metadata = new HashMap<>();
			

			String newPath = null;

			if(flag.equals("1")) {
			newPath = csvImgmovedDir;
			}
			else {
				newPath = rejectedCsvStoreDir;

			}

			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
			String strdate = formatter.format(date);
			strdate = strdate.replace(" ", "_");
			strdate = strdate.replace(":", "-");
			String path = String.format("%s/%s", BucketName.TODO_IMAGE.getBucketName(), newPath);

			logger.info("---------TodoServiceImpl path----------------" + path);

			String fileName = imgnm;

			logger.info("---------TodoServiceImpl fileName----------------" + fileName);

			logger.info("---------TodoServiceImpl start fileStore upload----------------");

			fileStore.upload(path, fileName, Optional.of(metadata), imagestream);

//					  
			String returnPath = newPath + "/" + fileName;
//					

			logger.info("---------returnPath----------------" + returnPath);

			return String.valueOf(returnPath);
		} catch (Exception ex) {
			throw new FileStorageException("Could not store file", ex);
		}

	}
   
	
	public String storeRejectedDataFile(String filenm) {
		
		System.out.println("****fileStorageService storeFile*****"+filenm);
		
		Date date = new Date(System.currentTimeMillis());
		
		try {
			
			if(filenm.contains("..")) {
				throw new FileStorageException("Sorry! File Name contains invalid path sequence!");
			}
			String newPath;
			
		     newPath= this.rejectedDataDir;
			
			this.fileStorageLocation = Paths.get(newPath).toAbsolutePath().normalize();
			
			Files.createDirectories(this.fileStorageLocation);
			
			Path targetLocation = this.fileStorageLocation.resolve(filenm);
			
			//Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println("--filePath" + targetLocation);
			
			String returnPath = targetLocation.toString();
			
			
			return String.valueOf(returnPath);
		}catch (IOException ex) {
			throw new FileStorageException("Could not store file " + filenm + ". Please try again!", ex);
		}
		
		
		
	}
	

}
