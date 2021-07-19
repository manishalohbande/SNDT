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
import com.scube.edu.model.PassingYearMaster;
import com.scube.edu.repository.DocumentRepository;
import com.scube.edu.repository.YearOfPassingRepository;
import com.scube.edu.request.YearOfPassingRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.YearOfPassingResponse;

@Service
public class YearOfPassingServiceImpl  implements YearOfPassingService{
  
	 private static final Logger logger = LoggerFactory.getLogger(YearOfPassingServiceImpl.class);
		
		BaseResponse	baseResponse	= null;
		
		@Autowired
		YearOfPassingRepository yearOfPassingRespository;

		@Override
		public List<YearOfPassingResponse> getYearOfPassingList(HttpServletRequest request) {
			
			  List<YearOfPassingResponse> List = new ArrayList<>();
				
				List<PassingYearMaster> yearEntities    = yearOfPassingRespository.findAllByIsdeleted("N");
				
				for(PassingYearMaster entity : yearEntities) {
					
					YearOfPassingResponse response = new YearOfPassingResponse();

					response.setId(entity.getId());
					response.setYearOfPassing(entity.getYearOfPassing());
					
					
					List.add(response);
				}
				
				return List;
		}
		
		@Override
		public PassingYearMaster getYearById(String id) {
				Long ID = Long.parseLong(id);
				System.out.println(ID);
				Optional<PassingYearMaster> yearEntity    = yearOfPassingRespository.findById(ID);
				PassingYearMaster yearEnt = yearEntity.get();
				System.out.println("yearEnt---"+ yearEnt);
				
						return yearEnt;
		}

		@Override
		public boolean addYear(YearOfPassingRequest yearOfPassReq) throws Exception {
			
			logger.info("*******YearOfPassingServiceImpl addYear*******");
			
			PassingYearMaster yr = yearOfPassingRespository.findByYearOfPassingAndIsdeleted(yearOfPassReq.getYearOfPass(),"N");
			
			if(yr != null) {
				throw new Exception("Year already exists.");
			}
			else {
				
			PassingYearMaster year = new PassingYearMaster();
			PassingYearMaster isdeletedchk = yearOfPassingRespository.findByYearOfPassing(yearOfPassReq.getYearOfPass());
			if (isdeletedchk != null) {

			
				isdeletedchk.setIsdeleted("N");
				yearOfPassingRespository.save(isdeletedchk);

			}
			else {
			year.setYearOfPassing(yearOfPassReq.getYearOfPass());
			year.setIsdeleted("N");
			yearOfPassingRespository.save(year);

			}
			}
			
			return true;
		}

		@Override
		public boolean deleteYear(long id) throws Exception {
			
			logger.info("*******YearOfPassingServiceImpl deleteYear*******");
			
			baseResponse	= new BaseResponse();	
			
			PassingYearMaster year = yearOfPassingRespository.findById(id);
			
			if(year == null) {
				throw new Exception("Invalid ID");
			}else {
			
				year.setIsdeleted("Y");
				yearOfPassingRespository.save(year);
//				yearOfPassingRespository.delete(year);
			}
			
			return true;
		}

		@Override
		public String updateYearById(YearOfPassingRequest yearOfPassReq, HttpServletRequest request) throws Exception {
			
			logger.info("*******YearOfPassingServiceImpl updateYearById*******");
			
			String resp = null;
			boolean flg;
			PassingYearMaster response = yearOfPassingRespository.findByYearOfPassing(yearOfPassReq.getYearOfPass());
			
			if(response!=null){
				logger.info("ids"+response.getId()+" "+yearOfPassReq.getId());
				if(response.getId()!=yearOfPassReq.getId())
				{
				resp="Year Already exist!";
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
				
				Optional<PassingYearMaster> yearEntitiess = yearOfPassingRespository.findById(yearOfPassReq.getId());
				PassingYearMaster yearEntities = yearEntitiess.get();
				
				if(yearEntities == null) {
					resp="Invalid Id";
				}
				else {
					PassingYearMaster year = new PassingYearMaster();
					
					year.setId(yearOfPassReq.getId());
					year.setYearOfPassing(yearOfPassReq.getYearOfPass());
					year.setIsdeleted("N");
					
					yearOfPassingRespository.save(year);
					resp = "Success";
				}
				
			}
			return resp;
			
		}

}
