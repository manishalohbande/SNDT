package com.scube.edu.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scube.edu.controller.MasterController;
import com.scube.edu.model.DailyVrBackupEntity;
import com.scube.edu.model.RaiseDespute;
import com.scube.edu.model.UserMasterEntity;
import com.scube.edu.model.VerificationRequest;
import com.scube.edu.model.YearlyVerReqBackup;
import com.scube.edu.repository.AdminDashboardRepository;
import com.scube.edu.repository.DailyVrBackupRepository;
import com.scube.edu.repository.RaiseDisputeRepository;
import com.scube.edu.repository.UserRepository;
import com.scube.edu.repository.YearlyVerReqBackupRepository;
import com.scube.edu.response.FinancialStatResponse;
import com.scube.edu.response.TopFiverYearRevenueResponse;
import com.scube.edu.response.TopTenEmployResponse;
import com.scube.edu.response.VerifierPerformanceResponse;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminDashboardServiceImpl.class);

	@Autowired
	AdminDashboardRepository adminDashboardRepository;
	
	@Autowired
	RaiseDisputeRepository raiseDisputeRepository;
	
	@Autowired
	YearlyVerReqBackupRepository yearlyVerReqBackupRepository;
	
	@Autowired
	DailyVrBackupRepository dailyVrBackupRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public HashMap<String,HashMap<String,String>> getRequestStatByStatus(int useryear) throws Exception {
		
		logger.info("****AdminDashboardServiceImpl   getRequestStatByStatus******");
		
		Date d=new Date ();
		int day =d.getDay();
		int month=d.getMonth();
		month=month+1;
		int year=d.getYear(); 
		year=1900+year;
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(new Date());
		int today = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DAY_OF_WEEK, -today+Calendar.MONDAY);
		
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1); // to get previous year add -1
		
		Date lastYear = cal.getTime();
		int prevyear=lastYear.getYear();
		prevyear=1900+prevyear;
		logger.info("-----lastYear=" +prevyear );
		
		Date datofmonday=c.getTime();
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		
		System.out.println("MondayDate:- "+datofmonday);
		
        String dayWeekText = new SimpleDateFormat("EEEE").format(d);


		logger.info( "Date:-"  +d+ "year:-"  +year+ " month:-"+month+ "Day:-"+day+ " Day Name:-"+dayWeekText);
		
		HashMap<String,HashMap<String,String>> AllstatCountMap=new HashMap<>();

		HashMap<String,String> YearstatCountMap=new HashMap<>();
		HashMap<String,String> monthstatCountMap=new HashMap<>();
		HashMap<String,String> weekstatCountMap=new HashMap<>();
		
		String newreuestCount = null;
		String closreuestCount = null;
		String disputereqCount = null;
		
		if(useryear!=year && useryear!=prevyear) {
			
			newreuestCount=yearlyVerReqBackupRepository.getstatNewreqByYear(useryear);
			 closreuestCount=yearlyVerReqBackupRepository.getstatclosreqByYear(useryear);
			 disputereqCount=yearlyVerReqBackupRepository.getstatdisputByYear(useryear);
		}
		else {
		 newreuestCount=adminDashboardRepository.getstatNewreqByYear(useryear);
		 closreuestCount=adminDashboardRepository.getstatclosreqByYear(useryear);
		 disputereqCount=raiseDisputeRepository.getstatdisputByYear(useryear);
		}
	if(newreuestCount!=null) {
		YearstatCountMap.put("New",newreuestCount);
	}
	if(closreuestCount!=null) {

		YearstatCountMap.put("Closed",closreuestCount);		
	}
	if(disputereqCount!=null) {

		YearstatCountMap.put("Dispute",disputereqCount);				
	}		
		if(useryear==year) {
		
		String newreuestMonthCount=adminDashboardRepository.getstatnewReqByMonth(month);
		String closreuestMonthCount=adminDashboardRepository.getstatclosreqByMonth(month);
		String disputereqMonthCount=raiseDisputeRepository.getstatdisputByMonth(month);

				
		monthstatCountMap.put("New",newreuestMonthCount);
		monthstatCountMap.put("Closed",closreuestMonthCount);		
		monthstatCountMap.put("Dispute",disputereqMonthCount);				
		
		String monday = simpleDateFormat.format(datofmonday);
		System.out.println(monday);
		
		String currentdate = simpleDateFormat.format(d);
		System.out.println(currentdate);
		
			
		
		String newreuestWeekcount=adminDashboardRepository.countByCreatedateGreaterThanEqualAndCreatedateLessThanEqual(monday,currentdate);	
		String clsdreuestWeekcount=adminDashboardRepository.countByClosedDateGreaterThanEqualAndClosedDateLessThanEqual(monday,currentdate);
		String disputereqWeekCount=raiseDisputeRepository.countByCreatedateGreaterThanEqualAndCreatedateLessThanEqual(monday,currentdate);

		
		weekstatCountMap.put("New",newreuestWeekcount);
		weekstatCountMap.put("Closed",clsdreuestWeekcount);		
		weekstatCountMap.put("Dispute",disputereqWeekCount);	
		
		logger.info("Weekly ocunt = "+newreuestWeekcount);
		}
		
		if(YearstatCountMap!=null) {
		AllstatCountMap.put("Year", YearstatCountMap);
		}
		if(monthstatCountMap!=null) {
		AllstatCountMap.put("CurrentMonth", monthstatCountMap);
		}
		if(weekstatCountMap!=null) {
		AllstatCountMap.put("CurrentWeek", weekstatCountMap);
		}
		
		return AllstatCountMap;
		
	}

	@Override
	public List<TopTenEmployResponse>  gettopTenEmployer(int year) {
		logger.info("****AdminDashboardServiceImpl   gettopTenEmployer******");
		
		/*
		 * Date d=new Date (); int curryear=d.getYear(); curryear=1900+curryear;
		 * 
		 * Calendar cal = Calendar.getInstance(); cal.add(Calendar.YEAR, -1); // to get
		 * previous year add -1 Date lastYear = cal.getTime(); int
		 * prevyear=lastYear.getYear(); prevyear=1900+prevyear;
		 */
		List<TopTenEmployResponse> response=new ArrayList<>();
		
		List<Object[]> toptemEmp=null;
		
			 toptemEmp=dailyVrBackupRepository.findToptenEmp(year);
		if(toptemEmp!=null) {
		for(Object[] list:toptemEmp )
			{				
			TopTenEmployResponse resp=new TopTenEmployResponse();
				int  amt=0;
				if(list[0]!=null) {
					amt=((BigDecimal) list[0]).intValue();
				}
				String compnyNm= (String) list[1];
				resp.setCompanyNm(compnyNm);
				resp.setTotalamt(amt);
				logger.info("amt=" +amt+ " compnayNm="+compnyNm);			
				response.add(resp);
			}
		}
		return response;
	}

	@Override
	public Map<String, Integer> getPostiveNegReqRation(int year) {
		logger.info("****AdminDashboardServiceImpl   getPostiveNegReqRation******");
		Map<String,Integer> rationcount=new HashMap<>();
		
		Date d=new Date ();
		int curryear=d.getYear(); 
		curryear=1900+curryear;
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1); // to get previous year add -1		
		Date lastYear = cal.getTime();
		int prevyear=lastYear.getYear();
		prevyear=1900+prevyear;

		String positiveReqCount=null;
		String negativeReqCount=null;
		if(year!=curryear&&year!=prevyear) {
			 positiveReqCount=yearlyVerReqBackupRepository.getcountOfpositiveReq(year);
			 negativeReqCount=yearlyVerReqBackupRepository.getcountOfNegReq(year);
		}
		else {
		 positiveReqCount=adminDashboardRepository.getcountOfpositiveReq(year);
		 negativeReqCount=adminDashboardRepository.getcountOfNegReq(year);
		}
		if(positiveReqCount!=null) {
		rationcount.put("Positive", Integer.parseInt(positiveReqCount));
		}
		if(negativeReqCount!=null) {
		rationcount.put("Negative", Integer.parseInt(negativeReqCount));
		}

		return rationcount;
	}

	@Override
	public List<TopFiverYearRevenueResponse> getTopFiveYearRevenue() {
		logger.info("****AdminDashboardServiceImpl   getTopFiveYearRevenue******");
		
		
		List<Object[]> topFiyear=null;
		
		topFiyear=dailyVrBackupRepository.findVerificaTopFiveYearRevenu();
		
	//	topFiyear=adminDashboardRepository.findVerificaTopFiveYearRevenu();
		List<TopFiverYearRevenueResponse> response=new ArrayList<>();

		if(topFiyear!=null) {
		
		for(Object[] list:topFiyear )
		{	
			TopFiverYearRevenueResponse resp=new TopFiverYearRevenueResponse();
			
			int  Totalamt= 0;
			if(list[0]!=null) {
				Totalamt=((BigDecimal) list[0]).intValue();
			}
			Integer year= (Integer) list[1];
			resp.setYear(Integer.toString(year));
			resp.setTotalAmt( Totalamt);
			logger.info("Totalamt=" +Totalamt+ " year="+year);			
			response.add(resp);
		
		}
		}
		return response;
	}

	@Override
	public Map<String, Integer> getDisputeRatio(int year) {
		logger.info("****AdminDashboardServiceImpl   getDisputeRatio******");
		Map<String,Integer> rationcount=new HashMap<>();
		
		Date d=new Date ();
		int curryear=d.getYear(); 
		curryear=1900+curryear;
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1); // to get previous year add -1		
		Date lastYear = cal.getTime();
		int prevyear=lastYear.getYear();
		prevyear=1900+prevyear;

		String raisedDisputCount=null;
		String clearDisputCount=null;
		
		if(year!=curryear&&year!=prevyear) {
			raisedDisputCount=yearlyVerReqBackupRepository.getstatdisputByYear(year);
			 clearDisputCount=yearlyVerReqBackupRepository.getclosedstatdisputByYear(year);
		}
		else {
		 raisedDisputCount=raiseDisputeRepository.getstatdisputByYear(year);
		 clearDisputCount=raiseDisputeRepository.getclosedstatdisputByYear(year);
		}
		if(raisedDisputCount!=null)
		{
		rationcount.put("Open",  Integer. parseInt(raisedDisputCount));
		}
		if(clearDisputCount!=null)
		{
		rationcount.put("Clear",  Integer. parseInt(clearDisputCount));
		}

		return rationcount;		
	}

	@Override
	public List<FinancialStatResponse> getFinancialStat(String fistofMont,String currenDateOfmonth) throws ParseException {
		logger.info("****AdminDashboardServiceImpl   getFinancialStat******");

		List<Object[]> financialStat=dailyVrBackupRepository.getFinanvialStat(fistofMont, currenDateOfmonth);
		
		//List<Object[]> financialStat=adminDashboardRepository.getFinanvialStat( fistofMont, currenDateOfmonth);
		List<FinancialStatResponse> response=new ArrayList<>();

		int stuUniamt=0;
		int studSecamt=0;
		if(financialStat!=null) {
		for(Object[] list:financialStat )
		{	
			FinancialStatResponse resp=new FinancialStatResponse();
			int TotalUniamt=0;
			if(list[0]!=null) {
			TotalUniamt=((BigDecimal) list[0]).intValue();
			}
			int  TotalSecamt= 0;
			if(list[1]!=null) {
			TotalSecamt=((BigDecimal) list[1]).intValue();
			}
			String companyNm= (String) list[2];
			//Long userId= ((BigInteger) list[3]).longValue();
			//Long roleId=((BigInteger) list[4]).longValue();
			
			/*if(companyNm==null && roleId==1)
			{						
				stuUniamt=stuUniamt+TotalUniamt;
				studSecamt=studSecamt+TotalSecamt;
					
			}*/
			//else {
			  resp.setCompnayNm(companyNm); 
			  resp.setUniversityamt(TotalUniamt);
			  resp.setSecureAmt(TotalSecamt);
			 // resp.setUserId(userId);
			//  resp.setRoleId(roleId);

			  logger.info("companyNm= " +companyNm+ " uniAmt=" +TotalUniamt+ " secAmt="+TotalSecamt);
			 			
			response.add(resp);
			}
		}
		
		//}
	/*
	 * FinancialStatResponse resp=new FinancialStatResponse();
	 * 
	 * resp.setCompnayNm("STUDENT"); resp.setUniversityamt(stuUniamt);
	 * resp.setSecureAmt(studSecamt); // resp.setUserId((long) 1);
	 * resp.setRoleId((long) 1);
	 */
		//  response.add(resp);


		return response;	
		
	}

	@Override
	public List<VerifierPerformanceResponse> getMonthlyPerformanceOfVerfier(int month,int year) {
		
		logger.info("****AdminDashboardServiceImpl   getMonthlyPerformanceOfVerfier******");
		
		List<Object[]> financialStat=adminDashboardRepository.getVerPerformanceMonthly(month,year);
		List<VerifierPerformanceResponse> response=new ArrayList<>();

		if(financialStat!=null) {
		for(Object[] list:financialStat ) {
			
			VerifierPerformanceResponse resp=new VerifierPerformanceResponse();
			resp.setCount(((BigInteger) list[0]).intValue());
			resp.setVerId(((BigInteger)  list[1]).longValue());
			String fullnm=list[2].toString()+" "+list[3].toString();
			resp.setFullNm(fullnm);
			response.add(resp);
		}

		}

		return response;
	}

	@Override
	public List<VerifierPerformanceResponse> getDailyPerformanceOfVerfier(String str){
		logger.info("****AdminDashboardServiceImpl   getDailyPerformanceOfVerfier******");
		
		
		List<Object[]> financialStat=adminDashboardRepository.getVerPerformanceDaily(str);
		List<VerifierPerformanceResponse> response=new ArrayList<>();

		if(financialStat!=null) {
		for(Object[] list:financialStat ) {
			
			VerifierPerformanceResponse resp=new VerifierPerformanceResponse();
			resp.setCount(((BigInteger) list[0]).intValue());
			resp.setVerId(((BigInteger)  list[1]).longValue());
			String fullnm=list[2].toString()+" "+list[3].toString();
			resp.setFullNm(fullnm);
			response.add(resp);
		}		
		}
		return response;
	}

	@Override
	public List<VerifierPerformanceResponse> getPerformanceOfVerfier(String frmdate, String todate) {
logger.info("****AdminDashboardServiceImpl   getDailyPerformanceOfVerfier******");
		
		
		List<Object[]> financialStat=adminDashboardRepository.getVerPerformance(frmdate, todate);
		List<VerifierPerformanceResponse> response=new ArrayList<>();

		if(financialStat!=null) {

		for(Object[] list:financialStat ) {
			
			VerifierPerformanceResponse resp=new VerifierPerformanceResponse();
			resp.setCount(((BigInteger) list[0]).intValue());
			resp.setVerId(((BigInteger)  list[1]).longValue());
			String fullnm=list[2].toString()+" "+list[3].toString();
			resp.setFullNm(fullnm);
			response.add(resp);
		}	
		}
		
		return response;	}


}
