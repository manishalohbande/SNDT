package com.scube.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scube.edu.model.DailyVrBackupEntity;
import com.scube.edu.model.YearlyVerReqBackup;

@Repository
public interface DailyVrBackupRepository extends JpaRepository<DailyVrBackupEntity, Long>{


	@Query(value="select id,date(vr.created_date) as req_date,sum(vr.doc_amt_with_gst) as total_Amt,sum(vr.doc_uni_amt) as university_amt , sum(vr.doc_secur_charge) as secure_amt, um.company_name as company_name from verification_request vr left join user_master um on um.id=vr.user_id  where date(vr.created_date)<(date(sysdate())) and date(vr.created_date)>(date(sysdate())-2) group by date(vr.created_date),um.company_name"  ,nativeQuery = true)
	List<DailyVrBackupEntity> findDailyData();
	
	@Query(value="select sum(university_amt) as university_amt,sum(secure_amt) as secure_amt, company_name from daily_ver_req_backup where req_date>=?1 and req_date<=?2 group by company_name",nativeQuery = true)
	List<Object[]> getFinanvialStat(String fistofMont,String currenDateOfmonth);
	
	@Query(value="select sum(total_amt) as docamtwithgst ,year(req_date) as topyear from daily_ver_req_backup vr group by topyear order by docamtwithgst desc limit 5",nativeQuery = true)
	List<Object[]> findVerificaTopFiveYearRevenu();
	
	@Query(value ="SELECT sum(total_amt) as total_amt,company_name from daily_ver_req_backup where year(req_date) = ?1 and company_name!= 'STUDENT' group by company_name order by total_amt desc limit 10", nativeQuery = true)
	List<Object[]> findToptenEmp(int year);
	
	@Query(value="select max(vr.id) as id,date(vr.created_date) as req_date,sum(vr.doc_amt_with_gst) as total_Amt,sum(vr.doc_uni_amt) as university_amt , sum(vr.doc_secur_charge) as secure_amt, um.company_name as company_name from verification_request vr left join user_master um on um.id=vr.user_id  where date(vr.created_date)<(date(sysdate())) and date(vr.created_date)>?1 group by req_date,um.company_name"  ,nativeQuery = true)
	List<DailyVrBackupEntity> getDailyDataSchedulerSkipped(String Date);
	
	@Query(value="select max(vr.id) as id,date(vr.created_date) as req_date,sum(vr.doc_amt_with_gst) as total_Amt,sum(vr.doc_uni_amt) as university_amt , sum(vr.doc_secur_charge) as secure_amt, um.company_name as company_name from verification_request vr left join user_master um on um.id=vr.user_id  where date(vr.created_date)<(date(sysdate())) group by req_date,um.company_name"  ,nativeQuery = true)
	List<DailyVrBackupEntity> getDailyData();
	
	@Query(value="select max(req_date) from daily_ver_req_backup",nativeQuery=true)
	String getHighestdate();
}
