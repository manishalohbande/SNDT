package com.scube.edu.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.FinancialStatResponse;
import com.scube.edu.response.TopFiverYearRevenueResponse;
import com.scube.edu.response.TopTenEmployResponse;
import com.scube.edu.response.VerifierPerformanceResponse;
import com.scube.edu.service.AdminDashboardService;
import com.scube.edu.util.StringsUtils;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/AdminDashboard")
public class AdminDashboardController {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

	BaseResponse response = null;

	@Autowired
	AdminDashboardService adminDashboardService;
	@GetMapping(value = "/getRequestStatitics/{year}" )
	public ResponseEntity<BaseResponse> getRequestStatiticsByYearWeekMonth (@PathVariable int year) {
		
		System.out.println("*******AdminDashboardController getRequestStatiticsByYearWeekMonth********");
		BaseResponse response1 = null;
		response1 = new BaseResponse();
		
	    try {
	    	
	    	HashMap<String,HashMap<String,String>> List = adminDashboardService.getRequestStatByStatus(year);
				
				response1.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response1.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response1.setRespData(List);
				
				return ResponseEntity.ok(response1);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response1.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response1.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response1.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response1);
				
			}
	}
	
	@GetMapping(value = "/getTopTenEmployer/{year}" )
	public ResponseEntity<BaseResponse> getTopTenemployersOfYear (@PathVariable int year) {
		
		System.out.println("*******AdminDashboardController getTopTenemployersOfYear********");
		BaseResponse response2 = null;
		response2 = new BaseResponse();
		
	    try {
	    	
	    	List<TopTenEmployResponse>  List = adminDashboardService.gettopTenEmployer(year);
				
				response2.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response2.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response2.setRespData(List);
				
				return ResponseEntity.ok(response2);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response2.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response2.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response2.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response2);
				
			}
	}
	
	@GetMapping(value = "/getRequestratio/{year}" )
	public ResponseEntity<BaseResponse> requestRatio (@PathVariable int year) {
		
		System.out.println("*******AdminDashboardController requestRatio********");
		BaseResponse response3 = null;
		response3 = new BaseResponse();
		
	    try {
	    	
	    	Map<String,Integer>  List = adminDashboardService.getPostiveNegReqRation(year);
				
				response3.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response3.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response3.setRespData(List);
				
				return ResponseEntity.ok(response3);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response3.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response3.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response3.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response3);
				
			}
	}
	
	@GetMapping(value = "/getYearRevenue" )
	public ResponseEntity<BaseResponse> getYearRevenue () {
		
		System.out.println("*******AdminDashboardController getYearRevenue********");
		BaseResponse response4 = null;
		response4 = new BaseResponse();
		
	    try {
	    	
	    	List<TopFiverYearRevenueResponse>  List = adminDashboardService.getTopFiveYearRevenue();
				
				response4.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response4.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response4.setRespData(List);
				
				return ResponseEntity.ok(response4);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response4.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response4.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response4.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response4);
				
			}
	}
	
	@GetMapping(value = "/getDisputeRatio/{year}" )
	public ResponseEntity<BaseResponse> getDisputeRation (@PathVariable int year) {
		
		System.out.println("*******AdminDashboardController requestRatio********");
		BaseResponse response5 = null;
		response5 = new BaseResponse();
		
	    try {
	    	
	    	Map<String, Integer>  List = adminDashboardService.getDisputeRatio(year);
				
				response5.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response5.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response5.setRespData(List);
				
				return ResponseEntity.ok(response5);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response5.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response5.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response5.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response5);
				
			}
	}
	

	@GetMapping(value = "/getFinancialStat/{fistofMont}/{currenDateOfmonth}" )
	public ResponseEntity<BaseResponse> getFinancialStat (@PathVariable String fistofMont,@PathVariable String currenDateOfmonth) {
		
		System.out.println("*******AdminDashboardController getFinancialStat********");
		BaseResponse response6 = null;
		response6 = new BaseResponse();
		
	    try {
	    	
	    	List<FinancialStatResponse> List = adminDashboardService.getFinancialStat(fistofMont, currenDateOfmonth);
				
				response6.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response6.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response6.setRespData(List);
				
				return ResponseEntity.ok(response6);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response6.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response6.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response6.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response6);
				
			}
	}
	

	@GetMapping(value = "/getPerformanceGraphMonthly/{month}/{year}" )
	public ResponseEntity<BaseResponse> getMonthlyPerformanceGraph (@PathVariable int month,@PathVariable int year) {
		
		System.out.println("*******AdminDashboardController getPerformanceGraph********");
		BaseResponse response7 = null;
		response7 = new BaseResponse();
		
	    try {
	    	
	    	List<VerifierPerformanceResponse> List = adminDashboardService.getMonthlyPerformanceOfVerfier(month,year);
				
				response7.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response7.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response7.setRespData(List);
				
				return ResponseEntity.ok(response7);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response7.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response7.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response7.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response7);
				
			}
	}
	
	@GetMapping(value = "/getPerformanceGraphDaily/{date}" )
	public ResponseEntity<BaseResponse> getDailyPerformanceGraph (@PathVariable String date) {
		
		System.out.println("*******AdminDashboardController getDailyPerformanceGraph********");
		BaseResponse response8 = null;
		response8 = new BaseResponse();
		
	    try {
	    	
	    	List<VerifierPerformanceResponse> List = adminDashboardService.getDailyPerformanceOfVerfier(date);
				response8.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response8.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response8.setRespData(List);
				
				return ResponseEntity.ok(response8);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response8.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response8.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response8.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response8);
				
			}
	}
	
	@GetMapping(value = "/getPerformanceGraphCustomRange/{fromdate}/{todate}" )
	public ResponseEntity<BaseResponse> getPerformanceGraphCustomRange (@PathVariable String fromdate,@PathVariable String todate) {
		
		System.out.println("*******AdminDashboardController getPerformanceGraphCustomRange********");
		BaseResponse response9 = null;
		response9 = new BaseResponse();
		
	    try {
	    	
	    	List<VerifierPerformanceResponse> List = adminDashboardService.getPerformanceOfVerfier(fromdate, todate);
				response9.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
				response9.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
				response9.setRespData(List);
				
				return ResponseEntity.ok(response9);
					
			}catch (Exception e) {
				
				logger.error(e.getMessage()); //BAD creds message comes from here
				
				response9.setRespCode(StringsUtils.Response.FAILURE_RESP_CODE);
				response9.setRespMessage(StringsUtils.Response.FAILURE_RESP_MSG);
				response9.setRespData(e.getMessage());
				
				return ResponseEntity.badRequest().body(response9);
				
			}
	}
}


