package com.scube.edu.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scube.edu.model.VerificationRequest;
import com.scube.edu.response.TopTenEmployResponse;

@Repository
public interface AdminDashboardRepository extends JpaRepository<VerificationRequest, Long>{

	@Query(value ="SELECT count(*) from verification_request where year(created_date) = ?1", nativeQuery = true)
	String getstatNewreqByYear(int year);
	
	@Query(value ="SELECT count(*) from verification_request where year(closed_date) = ?1", nativeQuery = true)
	String getstatclosreqByYear(int year);
	
	
	@Query(value ="SELECT count(*) from verification_request where month(created_date) = ?1 and year(created_date)=year(sysdate())", nativeQuery = true)
	String getstatnewReqByMonth(int month);
	
	@Query(value ="SELECT count(*) from verification_request where month(closed_date) = ?1 and year(created_date)=year(sysdate())", nativeQuery = true)
	String getstatclosreqByMonth(int month);
	
	@Query(value ="SELECT count(*) from verification_request where date(created_date) >= ?1 and date(created_date) <=?2 ", nativeQuery = true)
	String countByCreatedateGreaterThanEqualAndCreatedateLessThanEqual(String value1, String value2);
	
	@Query(value ="SELECT count(*) from verification_request where date(closed_date) >= ?1 and date(closed_date) <=?2 ", nativeQuery = true)
	String countByClosedDateGreaterThanEqualAndClosedDateLessThanEqual(String value1, String value2);

	@Query(value="select sum(vr.doc_amt_with_gst) as totalAmt ,um.company_name,vr.user_id from verification_request vr left join user_master um on vr.user_id=um.id where um.company_name is not null and um.role_id =2 and year(vr.created_date)= ?1 group by user_id order by totalAmt desc limit 10",nativeQuery = true)
	List<Object[]> findVerificationRequestToptenEmp(int year);
	
	@Query(value ="SELECT count(*) from verification_request where year(closed_date) = ?1 and doc_status in ('UN_Approved_Pass','Uni_Auto_Approved_Pass','SVD_Approved_Pass','UN_Approved_Fail','Uni_Auto_Approved_Fail','SVD_Approved_Fail')", nativeQuery = true)
	String getcountOfpositiveReq(int year);

	@Query(value ="SELECT count(*) from verification_request where year(closed_date) = ?1 and doc_status in ('UN_Rejected','Uni_Auto_Rejected','SVD_Rejected')", nativeQuery = true)
	String getcountOfNegReq(int year);
	
	@Query(value="select sum(vr.doc_amt_with_gst) as docamtwithgst ,year(created_date) as topyear from verification_request vr group by topyear order by docamtwithgst desc limit 5",nativeQuery = true)
	List<Object[]> findVerificaTopFiveYearRevenu();
	
	@Query(value="SELECT sum(vr.doc_uni_amt) ,sum(vr.doc_secur_charge) ,um.company_name as companyname,vr.user_id as userid,um.role_id from verification_request vr left join user_master um on vr.user_id=um.id where date(vr.created_date )>= ?1 and date(vr.created_date) <= ?2 group by vr.user_id ",nativeQuery = true)
	List<Object[]> getFinanvialStat(String fistofMont,String currenDateOfmonth);
	
	@Query(value="SELECT count(*),ver_id,um.first_name,um.last_name FROM verification_request vr left join user_master um on um.id=vr.ver_id  where month(ver_action_date) = ?1 and  year(ver_action_date) = ?2 and ver_id is not null   group by ver_id",nativeQuery = true)
	List<Object[]> getVerPerformanceMonthly(int month,int year);

	@Query(value="SELECT count(*),ver_id,um.first_name,um.last_name FROM verification_request vr left join user_master um on um.id=vr.ver_id  where date(ver_action_date)= ?1  and ver_id is not null   group by ver_id",nativeQuery = true)
	List<Object[]> getVerPerformanceDaily(String date);
	
	@Query(value="SELECT count(vr.ver_id),vr.ver_id,um.first_name,um.last_name FROM verification_request vr left join user_master um on um.id=vr.ver_id where date(ver_action_date)>=?1 and date(ver_action_date)<=?2 and vr.ver_id is not null   group by vr.ver_id",nativeQuery = true)
	List<Object[]> getVerPerformance(String fromdate,String todate);
}
