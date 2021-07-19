package com.scube.edu.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scube.edu.model.DocumentMaster;
import com.scube.edu.model.PassingYearMaster;
import com.scube.edu.model.StreamMaster;
import com.scube.edu.model.UserMasterEntity;
import com.scube.edu.repository.CollegeRepository;
import com.scube.edu.repository.DocumentRepository;
import com.scube.edu.request.DocumentAddRequest;
import com.scube.edu.request.UserAddRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.DocumentResponse;
import com.scube.edu.util.StringsUtils;


@Service
public class DocumentServiceImpl implements DocumentService{
	
    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
	
	@Autowired
	DocumentRepository documentRespository;
	BaseResponse  baseResponse = null;
    Base64.Decoder decoder = Base64.getDecoder();  

	@Override
	public List<DocumentResponse> getDocumentList(HttpServletRequest request) {
		
		logger.info("-----------getDocumentList---------------------");
		
		List<DocumentResponse> docList = new ArrayList<>();
		
		List<DocumentMaster> docEntities    = documentRespository.findAllByIsdeleted("N");
		
		for(DocumentMaster entity : docEntities) {
			
			DocumentResponse documentResponse = new DocumentResponse();

			documentResponse.setId(entity.getId());
			documentResponse.setDocumentName(entity.getDocumentName());
			documentResponse.setUniversityId(entity.getUniversityId());
			
			docList.add(documentResponse);
		}
		return docList;
	}

	@Override
	public DocumentMaster getNameById(String documentName) {
		
		Long ID = Long.parseLong(documentName);
		System.out.println(ID);
		
		Optional<DocumentMaster> docMas = documentRespository.findById(ID);
		DocumentMaster resp = docMas.get();
		
		return resp;
	}
	
	
	//Abhishek Added
	
	@Override
	public String addDocument(DocumentAddRequest documentRequest) throws Exception {
		
		String resp;
		DocumentMaster docMasterResponse=documentRespository.findByDocumentNameAndIsdeleted( documentRequest.getDocumentName(),"N");
		if(docMasterResponse!=null)
		{
			resp="Document Name Already exist!";
		}
		else
		{
		DocumentMaster docMasterEntity  = new  DocumentMaster();
		DocumentMaster isdeleteChk=documentRespository.findByDocumentName( documentRequest.getDocumentName());
		if(isdeleteChk!=null) {
			 DocumentMaster docEntit = new DocumentMaster();
			    
			 isdeleteChk.setIsdeleted("N");
			  //  docEntit.setUpdateby(String.valueOf(documentRequest.getId()));
			 isdeleteChk.setUpdatedate(new Date());	
			 documentRespository.save(isdeleteChk);

		}
		else {
		docMasterEntity.setUniversityId(documentRequest.getUniversityId());//1
		docMasterEntity.setDocumentName(documentRequest.getDocumentName());//Name Document
		docMasterEntity.setCreateby(documentRequest.getCreated_by()); // Logged User Id 
		docMasterEntity.setIsdeleted("N"); // By Default N
	    documentRespository.save(docMasterEntity);

		}

	    resp="Success";
		}
		return resp;
		
	}
	
	
	
	
	@Override
	public String UpdateDocument(DocumentMaster documentMaster) throws Exception {
		
		String resp = null;	
		boolean flg;
		DocumentMaster docResponse=documentRespository.findByDocumentName( documentMaster.getDocumentName());
		if(docResponse!=null)
		{
			if(docResponse.getId()!=documentMaster.getId()) {
				resp="Stream Already existed!";
				flg=true;
			   }
			   else
			   {
				   flg=false;
			   }
			}
		else
		{
			flg=false;
		}
		   if (flg==false) {
		    DocumentMaster docEntit = new DocumentMaster();
		    
		    docEntit.setId(documentMaster.getId());
		    docEntit.setDocumentName(documentMaster.getDocumentName());
		    docEntit.setUniversityId(documentMaster.getUniversityId());
		    docEntit.setIsdeleted("N");
		    docEntit.setUpdatedate(new Date());		
		 documentRespository.save(docEntit);
		 resp="Success";
		    }
		
		
		return resp;
	}
	
	
	
	@Override
	public BaseResponse deleteDocument(long id, HttpServletRequest request) throws Exception{
		
		baseResponse	= new BaseResponse();	
		
		
		DocumentMaster docEntities  = documentRespository.findById(id);
		
		   if(docEntities == null) {
			   
				throw new Exception(" Invalid ID");
			}else {
							

//		docEntities  = documentRespository.deleteById(id);
		docEntities.setIsdeleted("Y");
		documentRespository.save(docEntities);
		
		 
		baseResponse.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
		baseResponse.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
		baseResponse.setRespData("success");
			
			
	}
		 
		return baseResponse;
	
	}
	
	//Abhishek Added
}
