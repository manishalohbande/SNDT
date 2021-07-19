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
import com.scube.edu.model.StreamMaster;
import com.scube.edu.repository.DocumentRepository;
import com.scube.edu.repository.StreamRepository;
import com.scube.edu.request.DocumentAddRequest;
import com.scube.edu.request.StreamRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.StreamResponse;
import com.scube.edu.util.StringsUtils;

@Service
public class StreamServiceImpl  implements StreamService{
	
	private static final Logger logger = LoggerFactory.getLogger(StreamServiceImpl.class);
		
		BaseResponse	baseResponse	= null;
		
		@Autowired
		StreamRepository  streamRespository;

		@Override
		public List<StreamResponse> getStreamList(HttpServletRequest request) {

			List<StreamResponse> List = new ArrayList<>();
			
			List<StreamMaster> streamEntities    = streamRespository.findAllByIsdeleted("N");
			
			for(StreamMaster entity : streamEntities) {
				
				StreamResponse streamResponse = new StreamResponse();

				streamResponse.setId(entity.getId());
				streamResponse.setUniversityId(entity.getUniversityId());
				streamResponse.setStreamName(entity.getStreamName());
				
				List.add(streamResponse);
			}
			return List;
		}
		
		
		//Abhishek Added
		
		@Override
		public String addStream(StreamRequest streamRequest) throws Exception {
			
			StreamMaster streamMasterEntity  = new  StreamMaster();
			StreamMaster Response =streamRespository.findByStreamNameAndIsdeleted(streamRequest.getStreamName(),"N");
			String resp = null;
			if(Response!=null)
			{
				resp="Stream already exist!";
			}
			else
			{
				StreamMaster strmMasterEntity  = new  StreamMaster();
				StreamMaster streamMaster=streamRespository.findByStreamName(streamRequest.getStreamName());
				if(streamMaster!=null) {
					streamMaster.setIsdeleted("N");
					streamMaster.setUpdatedate(new Date());
				       streamRespository.save(streamMaster);

				}
				else {
			streamMasterEntity.setUniversityId(streamRequest.getUniversityId());//1
			streamMasterEntity.setStreamName(streamRequest.getStreamName());//Name Document
			streamMasterEntity.setCreateby(streamRequest.getCreated_by()); // Logged User Id 
			streamMasterEntity.setIsdeleted("N"); // By Default N	
		
			streamRespository.save(streamMasterEntity);
				}
			resp="success";
			
			}
			return resp;
			
		}
		
		

		@Override
		public String updateStream(StreamMaster streamMaster) throws Exception {
			
			String resp = null;	
			boolean flg;
			StreamMaster streamEntities  = streamRespository.findByStreamName(streamMaster.getStreamName());
			
			   if(streamEntities != null) {
					logger.info("ids"+streamEntities.getId()+""+streamMaster.getId());

				   if(streamEntities.getId()!=streamMaster.getId()) {
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
				   Optional<StreamMaster> streamEntit = streamRespository.findById(streamMaster.getId());
			      if(streamEntit!=null) {
				   StreamMaster entity=new StreamMaster(); 
				   entity.setId(streamMaster.getId());
				   entity.setUniversityId(streamMaster.getUniversityId());
				   entity.setStreamName(streamMaster.getStreamName());
				   entity.setIsdeleted("N");
				   entity.setUpdatedate(new Date());
			       streamRespository.save(entity);
			       resp = "Success";
			      }
			   }
			
			 
			return resp;
		}
		
		
		//Abhishek Added
		
		

		@Override
		public StreamMaster getNameById(Long streamId) {
			
			System.out.println("*******streamServiceImpl getNameById*******");
			
			Optional<StreamMaster> stream = streamRespository.findById(streamId);
			StreamMaster stre = stream.get();
			
			return stre;
		}
		
		
		
		@Override
		public BaseResponse deleteStreamRequest(long id, HttpServletRequest request) throws Exception{
			
			baseResponse	= new BaseResponse();	
			
			
			StreamMaster streamEntities  = streamRespository.findById(id);
			
			   if(streamEntities == null) {
				   
					throw new Exception(" Invalid ID");
					
				}else {
								

//					streamEntities  = streamRespository.deleteById(id);
					streamEntities.setIsdeleted("Y");
					streamRespository.save(streamEntities);
			
			 
			baseResponse.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
			baseResponse.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
			baseResponse.setRespData("success");
				
				
		}
			 
			return baseResponse;
		
		}
		
		

}
