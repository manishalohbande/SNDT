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

import com.scube.edu.model.BranchMasterEntity;
import com.scube.edu.model.PassingYearMaster;
import com.scube.edu.model.SemesterEntity;
import com.scube.edu.model.StreamMaster;
import com.scube.edu.repository.BranchMasterRepository;
import com.scube.edu.repository.SemesterRepository;
import com.scube.edu.request.SemesterRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.BranchResponse;
import com.scube.edu.response.SemesterResponse;

@Service
public class SemesterServiceImpl implements SemesterService{
	private static final Logger logger = LoggerFactory.getLogger(CollegeSeviceImpl.class);

	BaseResponse	baseResponse	= null;

    @Autowired
    SemesterRepository semesterRepository;
    
    @Autowired
    StreamService streamService;
	
	@Override
	public List<SemesterResponse> getSemList(Long id, HttpServletRequest request) {
		logger.info("ID"+id);
		 List<SemesterResponse> branchList = new ArrayList<>();
			
			List<SemesterEntity> branchEntities    = semesterRepository.findByStreamIdAndIsdeleted(id,"N");
			for(SemesterEntity entity : branchEntities) {
				
				SemesterResponse response = new SemesterResponse();

				response.setId(entity.getId());
				response.setSemester(entity.getSemester());
				response.setStreamId(entity.getStreamId());
				response.setUniversityId(entity.getUniversityId());
				
				branchList.add(response);
			}
			return branchList;
	}

	@Override
	public SemesterEntity getSemById(Long id) {
		System.out.println(id);
		Optional<SemesterEntity> semEntity    = semesterRepository.findById(id);
		SemesterEntity branchEnt = semEntity.get();
		System.out.println("yearEnt---"+ branchEnt);
		
				return branchEnt;
}
	@Override
	public SemesterEntity getSemIdByNm(String sem,Long StreamId ) {
		System.out.println(sem);
		SemesterEntity semEntity = semesterRepository.findBySemesterAndStreamId(sem,StreamId);
		System.out.println("yearEnt---"+ semEntity);
		
				return semEntity;
}

	@Override
	public boolean saveSem(SemesterRequest semReq, HttpServletRequest request) throws Exception {
		
		logger.info("*******SemesterServiceImpl addSem*******");
		
		SemesterEntity sEnt = semesterRepository.findBySemesterAndStreamIdAndIsdeleted(semReq.getSemestername(), semReq.getStreamid(),"N");
		
		if(sEnt != null) {
			throw new Exception("This sem name and Stream Id combo already exist.");
		} else {
			SemesterEntity semEnt = new SemesterEntity();
			SemesterEntity isdeletedchk = semesterRepository.findBySemesterAndStreamId(semReq.getSemestername(),
					semReq.getStreamid());
			if (isdeletedchk != null) {
				isdeletedchk.setIsdeleted("N");
				isdeletedchk.setUpdatedate(new Date());
				semesterRepository.save(isdeletedchk);

			}
			else {

			semEnt.setIsdeleted("N");
			semEnt.setSemester(semReq.getSemestername());
			semEnt.setStreamId(semReq.getStreamid());
			semEnt.setUniversityId(semReq.getUniversityid());
			
			semesterRepository.save(semEnt);
			}
			return true;
		}
		
		
	}

	@Override
	public boolean deleteSemester(Long id, HttpServletRequest request) throws Exception {
		
		logger.info("*******SemesterServiceImpl deleteSemester*******"+id);
		
		Optional<SemesterEntity> sme = semesterRepository.findById(id);
		SemesterEntity sEnt = sme.get();
		
		if(sEnt == null) {
			throw new Exception("Invalid Id");
		}else {
		
			sEnt.setIsdeleted("Y");
			semesterRepository.save(sEnt);
//		semesterRepository.delete(sEnt);
		}
		return true;
	}

	@Override
	public boolean updateSem(SemesterRequest semReq, HttpServletRequest request) throws Exception {
		
		logger.info("*******SemesterServiceImpl deleteSemester*******");
		
		boolean flag;
		
		SemesterEntity se = semesterRepository.findBySemesterAndStreamId(semReq.getSemestername(), semReq.getStreamid());
		
		if(se != null) {
			logger.info("ids"+se.getId()+""+semReq.getId());
			if(se.getId() != semReq.getId()) {
				flag = true;
				throw new Exception("This Semester name and streamID combo already exists.");
			}else {
				flag = false;
			}
//			throw new Exception("This Semester name and streamId combo already exist");
		}else {
			flag = false;
		}
		
		if(flag == false) {
		Optional<SemesterEntity> semEnt = semesterRepository.findById(semReq.getId());
		SemesterEntity sem = semEnt.get();
		
		sem.setId(semReq.getId());
		sem.setSemester(semReq.getSemestername());
		sem.setStreamId(semReq.getStreamid());
		sem.setUniversityId(semReq.getUniversityid());
		
		semesterRepository.save(sem);
		}
		return true;
	}

	@Override
	public List<SemesterResponse> getAllSemList(HttpServletRequest request) {
		
		logger.info("*******SemesterServiceImpl getAllSemList*******");
		
		List<SemesterEntity> list = semesterRepository.findAllByIsdeleted("N");
		
		List<SemesterResponse> respList = new ArrayList<>();
		
		for(SemesterEntity semEnt: list) {
			
			SemesterResponse sem = new SemesterResponse();
			
			StreamMaster stream = streamService.getNameById(semEnt.getStreamId());
			
			sem.setId(semEnt.getId());
			sem.setSemester(semEnt.getSemester());
			sem.setStreamId(semEnt.getStreamId());
			sem.setUniversityId(semEnt.getUniversityId());
			sem.setStreamName(stream.getStreamName());
			
			respList.add(sem);
			
		}
		
		return respList;
	}
}
