package com.scube.edu.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scube.edu.model.CollegeMaster;
import com.scube.edu.model.DocumentMaster;
import com.scube.edu.repository.CollegeRepository;
import com.scube.edu.request.CollegeAddRequest;
import com.scube.edu.request.DocumentAddRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.CollegeResponse;
import com.scube.edu.util.StringsUtils;

@Service
public class CollegeSeviceImpl implements CollegeSevice {

private static final Logger logger = LoggerFactory.getLogger(CollegeSeviceImpl.class);
	
	BaseResponse	baseResponse	= null;
	
	@Autowired
	CollegeRepository collegeRespository;
	
	@Override
	public List<CollegeResponse> getCollegeList(HttpServletRequest request) {
		
		 List<CollegeResponse> collegeList = new ArrayList<>();
			
			List<CollegeMaster> collegeEntities    = collegeRespository.findByIsdeleted("N");
			
			for(CollegeMaster entity : collegeEntities) {
				
				CollegeResponse response = new CollegeResponse();

				response.setId(entity.getId());
				response.setCollegeName(entity.getCollegeName());
				response.setUniversityId(entity.getUniversityId());
				
				collegeList.add(response);
			}
			return collegeList;
	}

	
	
	
	//Abhishek Added
	
	@Override
	public String addCollege(CollegeAddRequest collegeRequest) throws Exception {
		
		String resp;
		CollegeMaster collegeEntities  = collegeRespository.findByCollegeNameAndIsdeleted(collegeRequest.getCollegeName(),"N");
		
		
		if(collegeEntities != null) {
			resp = "College Name Already Exist!"; 
			throw new Exception("College Name Already Exist");
	
			
		}
		else {
	
			CollegeMaster collegeMaster = collegeRespository.findByCollegeName(collegeRequest.getCollegeName());
			if (collegeMaster != null) {
				
				collegeMaster.setIsdeleted("N");
				collegeMaster.setUpdatedate(new Date());

				collegeRespository.save(collegeMaster);
			} else {
			
			CollegeMaster collegeMasterEntity = new CollegeMaster();

			collegeMasterEntity.setUniversityId((long)1);// 1
			collegeMasterEntity.setCollegeName(collegeRequest.getCollegeName());// Name Document
			collegeMasterEntity.setCreateby(collegeRequest.getCreated_by()); // Logged User Id
			collegeMasterEntity.setIsdeleted("N"); // By Default N

			collegeRespository.save(collegeMasterEntity);
				}
	     resp = "Success";
	
		}
			
		return resp;
		
	}
	
	
	
	
	@Override
	public String UpdateCollege(CollegeMaster collegeMaster) throws Exception {
		
		
		String resp = null;
		boolean flg;
		CollegeMaster response=collegeRespository.findByCollegeName(collegeMaster.getCollegeName());

		if(response!=null){
			logger.info("ids"+response.getId()+""+collegeMaster.getId());
			if(response.getId()!=collegeMaster.getId())
			{
			resp="College Name Already exist!";
			flg=true;
			}
			else {
				flg=false;
			}
		}
		else {
			flg=false;
		}
		
		
		if(flg==false) {
		Optional<CollegeMaster> collegeEntities  = collegeRespository.findById(collegeMaster.getId());
		
		   if(collegeEntities == null) {
			   
				resp="Invalid Id";
			}
		
		   else
		   {
		CollegeMaster collegeEntit = new CollegeMaster();
		
		collegeEntit.setId(collegeMaster.getId());
		collegeEntit.setCollegeName(collegeMaster.getCollegeName());
		collegeEntit.setUniversityId(collegeMaster.getUniversityId());
		collegeEntit.setIsdeleted("N");
		collegeEntit.setUpdatedate(new Date());
		
		
		collegeRespository.save(collegeEntit);
		resp="Success";
		   }
		}
		return resp;
	}




	@Override
	public CollegeResponse getNameById(Long collegeId) {
		
		Optional<CollegeMaster> cm = collegeRespository.findById(collegeId);
		CollegeMaster cmm = cm.get();
		
		CollegeResponse cresp = new CollegeResponse();
		
		cresp.setCollegeName(cmm.getCollegeName());
		cresp.setId(cmm.getId());
		cresp.setUniversityId(cmm.getUniversityId());
		
		
		return cresp;
	}
	
	
	@Override
	public BaseResponse deleteClgRequest(long id, HttpServletRequest request) throws Exception{
		
		baseResponse	= new BaseResponse();	
		
		
		CollegeMaster collegeEntities  = collegeRespository.findById(id);
		
		   if(collegeEntities == null) {
			   
				throw new Exception(" Invalid ID");
			}else {
							

//		 collegeEntities  = collegeRespository.deleteById(id);
				collegeEntities.setIsdeleted("Y");
				collegeRespository.save(collegeEntities);
		
		 
		baseResponse.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
		baseResponse.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
		baseResponse.setRespData("success");
			
			
	}
		 
		return baseResponse;
	
	}
	
	
	
	//Abhishek Added
}
