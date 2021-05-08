package com.scube.edu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scube.edu.model.BranchMasterEntity;
import com.scube.edu.model.CollegeMaster;
import com.scube.edu.repository.BranchMasterRepository;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.BranchResponse;
import com.scube.edu.response.CollegeResponse;

@Service
public class BranchMasterServiceImpl implements BranchMasterService {
	
private static final Logger logger = LoggerFactory.getLogger(CollegeSeviceImpl.class);
	
	BaseResponse	baseResponse	= null;

    @Autowired
	BranchMasterRepository branchMasterRepository;
	
	@Override
	public List<BranchResponse> getBranchList(Long id,HttpServletRequest request) {
		logger.info("ID"+id);
		 List<BranchResponse> branchList = new ArrayList<>();
			
			List<BranchMasterEntity> branchEntities    = branchMasterRepository.getbrachbyStreamId(id);
			for(BranchMasterEntity entity : branchEntities) {
				
				BranchResponse response = new BranchResponse();

				response.setId(entity.getId());
				response.setBranchName(entity.getBranchName());
				response.setStreamId(entity.getStreamId());
				response.setUniversityId(entity.getUniversityId());
				
				branchList.add(response);
			}
			return branchList;
	}
}