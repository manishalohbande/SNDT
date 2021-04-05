package com.scube.edu.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scube.edu.model.DocumentMaster;
import com.scube.edu.model.RequestTypeMaster;
import com.scube.edu.repository.RequestTypeRepository;
import com.scube.edu.response.DocumentResponse;
import com.scube.edu.response.RequestTypeResponse;

@Service
public class RequestTypeServiceImpl implements RequestTypeService {
	
	private static final Logger logger = LoggerFactory.getLogger(RequestTypeServiceImpl.class);
	
	@Autowired
	RequestTypeRepository reqTypeRepo;

	@Override
	public List<RequestTypeResponse> getRequestTypeList(HttpServletRequest request) {
		
		logger.info("*******RequestServiceImpl getRequestTypeList*******");
		
		List<RequestTypeResponse> reqTypeList = new ArrayList<>();
		
		List<RequestTypeMaster> entt = reqTypeRepo.findAll();
		
		for(RequestTypeMaster entity : entt) {
			
			RequestTypeResponse reqTypeResponse = new RequestTypeResponse();

			reqTypeResponse.setId(entity.getId());
			reqTypeResponse.setRequestType(entity.getRequestType());
			reqTypeResponse.setUniversityId(entity.getUniversityId());
			
			reqTypeList.add(reqTypeResponse);
		}
		
		return reqTypeList;
	}
	
	

}
