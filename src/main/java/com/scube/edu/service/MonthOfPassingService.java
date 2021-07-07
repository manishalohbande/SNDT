package com.scube.edu.service;

import java.util.List;

import com.scube.edu.response.MonthOfPassingResponse;

public interface MonthOfPassingService {
	
	public List<MonthOfPassingResponse> getAllMonth();

	public MonthOfPassingResponse getMonthById(String id);

	

}
