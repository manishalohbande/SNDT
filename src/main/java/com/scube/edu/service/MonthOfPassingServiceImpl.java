package com.scube.edu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scube.edu.model.MonthOfPassing;
import com.scube.edu.repository.MonthOfPassingRepository;
import com.scube.edu.response.MonthOfPassingResponse;

@Service
public class MonthOfPassingServiceImpl implements MonthOfPassingService {

	@Autowired
	MonthOfPassingRepository monthOfPassingRepository;
	
	@Override
	public List<MonthOfPassingResponse> getAllMonth() {
    
		List<MonthOfPassing>list=monthOfPassingRepository.findAll();
		
		List<MonthOfPassingResponse>response=new ArrayList<>();
		for(MonthOfPassing monthlist:list) {
			MonthOfPassingResponse resp=new MonthOfPassingResponse();
			resp.setId(monthlist.getId());
			resp.setMonthOfPAssing(monthlist.getMonthOfPassing());
			response.add(resp);
		}
		return response;
	}
	
	@Override
	public MonthOfPassingResponse getMonthById(String id) {
    
		Optional<MonthOfPassing>lists=monthOfPassingRepository.findById(Long.valueOf(id));
		MonthOfPassing list = lists.get();
		
			MonthOfPassingResponse resp=new MonthOfPassingResponse();
			resp.setId(list.getId());
			resp.setMonthOfPAssing(list.getMonthOfPassing());

		return resp;
	}

}
