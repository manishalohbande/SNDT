package com.scube.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scube.edu.model.YearlyVerReqBackup;

@Repository
public interface YearlyVerReqBackupRepository extends JpaRepository<YearlyVerReqBackup, Long>{
	

	@Query(value="select  max(vr.id) as id,year(vr.created_date) as financial_year,sum(vr.doc_amt_with_gst) as total_Amt,sum(vr.doc_uni_amt) as university_amt , sum(vr.doc_secur_charge) as secure_amt, count(*) as new_request, (select count(*) from verification_request where year(closed_date)=financial_year) as closed_request,(select count(*) from verification_request where doc_status in ('UN_Approved_Pass','Uni_Auto_Approved_Pass','SVD_Approved_Pass','UN_Approved_Fail','Uni_Auto_Approved_Fail','SVD_Approved_Fail') and year(closed_date)=financial_year) as  positive_request,(select count(*)from verification_request where  doc_status in ('UN_Rejected','Uni_Auto_Rejected','SVD_Rejected') and year(closed_date)=financial_year) as  negative_request,(SELECT count(*) from raise_despute where year(created_date) =financial_year) as dispute_raised, (SELECT count(*) from raise_despute where year(created_date) = financial_year and status in ('NCL','CL')) as dispute_clear from verification_request vr where year(vr.created_date)<=(year(sysdate())-2) group by financial_year"  ,nativeQuery = true)
	List<YearlyVerReqBackup> findprevYearData();
	
	@Query(value="select max(vr.id) as id, year(vr.created_date) as financial_year,sum(vr.doc_amt_with_gst) as total_Amt,sum(vr.doc_uni_amt) as university_amt , sum(vr.doc_secur_charge) as secure_amt, count(*) as new_request, (select count(*) from verification_request where year(closed_date)=financial_year) as closed_request,(select count(*) from verification_request where doc_status in ('UN_Approved_Pass','Uni_Auto_Approved_Pass','SVD_Approved_Pass','UN_Approved_Fail','Uni_Auto_Approved_Fail','SVD_Approved_Fail') and year(closed_date)=financial_year) as  positive_request,(select count(*)from verification_request where  doc_status in ('UN_Rejected','Uni_Auto_Rejected','SVD_Rejected') and year(closed_date)=financial_year) as  negative_request,(SELECT count(*) from raise_despute where year(created_date) =financial_year) as dispute_raised, (SELECT count(*) from raise_despute where year(created_date) = financial_year and status in ('NCL','CL')) as dispute_clear from verification_request vr where year(vr.created_date)>?1 and year(vr.created_date)<=?2 group by financial_year"  ,nativeQuery = true)
	List<YearlyVerReqBackup> findprevYearDatabydate(String year, String prevYear);
	
	@Query(value ="SELECT new_request from yearly_ver_req_backup where financial_year = ?1", nativeQuery = true)
	String getstatNewreqByYear(int year);
	
	int findNewReqByFinancialYear(int year);
	
	@Query(value ="SELECT closed_request from yearly_ver_req_backup where financial_year = ?1", nativeQuery = true)
	String getstatclosreqByYear(int year);
	
	@Query(value ="SELECT dispute_raised from yearly_ver_req_backup where financial_year = ?1", nativeQuery = true)
	String getstatdisputByYear(int year);
	
	@Query(value ="SELECT  dispute_raised-dispute_clear  as dispute_raised FROM edu_db.yearly_ver_req_backup; where year(created_date) = ?1 and status=1", nativeQuery = true)
	String getstatdpendigisputByYear(int year);
	
	@Query(value ="SELECT positive_request from yearly_ver_req_backup where financial_year = ?1", nativeQuery = true)
	String getcountOfpositiveReq(int year);

	@Query(value ="SELECT negative_request from yearly_ver_req_backup where financial_year = ?1 ", nativeQuery = true)
	String getcountOfNegReq(int year);
	
	@Query(value="select total_amt as docamtwithgst ,financial_year as topyear from yearly_ver_req_backup  group by topyear order by docamtwithgst desc limit 5",nativeQuery = true)
	List<Object[]> findVerificaTopFiveYearRevenu();
	
	@Query(value ="SELECT dispute_clear from yearly_ver_req_backup where financial_year = ?1", nativeQuery = true)
	String getclosedstatdisputByYear(int year);
	
	@Query(value="select max(financial_year) from yearly_ver_req_backup",nativeQuery=true)
	String getHighestdate();
}
