package com.scube.edu.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.scube.edu.model.UserMasterEntity;
import com.scube.edu.model.VerificationRequest;

import com.scube.edu.response.StudentVerificationDocsResponse;

@Repository
public interface VerificationRequestRepository extends JpaRepository<VerificationRequest, Long>{

	@Query(value = "SELECT pym.year_of_passing as yearr, vr.* FROM verification_request vr left join passing_year_master pym on vr.year_of_passing_id = pym.id where user_id = ?1 order by created_date desc", nativeQuery = true)
	List<VerificationRequest> findByUserId(long userId);

	@Query(value = "SELECT * FROM verification_request where doc_status = 'Requested' and (assigned_to = 0 OR assigned_to = ?1) order by created_date asc limit 5", nativeQuery = true)
	List<VerificationRequest> getVerifierRecords(Long userId);

	@Query(value = "SELECT * FROM verification_request where doc_status != 'Requested' and user_id = ?1" , nativeQuery = true)
	List<VerificationRequest> findByStatusAndUserId(long userId);


	List<VerificationRequest> findByApplicationId(long applicationId);

	@Query(value = "SELECT MAX(application_id) from verification_request " , nativeQuery = true)
	Long getMaxApplicationId();

	@Query(value = "SELECT * FROM verification_request where id = (?1)" , nativeQuery = true)
	List<VerificationRequest> getDataByIdToVerify(long id);
	
	VerificationRequest findById(long id);

	@Query(value = "SELECT * FROM verification_request where user_id = (?1) and convert(created_date , Date) >= (?2) and convert(created_date , Date) <= (?3)" , nativeQuery = true)
	List<VerificationRequest> findByUserIdAndDates(long userId, String fromDate, String toDate);

	@Query(value = "SELECT * FROM verification_request where doc_status in ('Rejected', 'Unable To Verify', 'Offline') and convert(created_date , Date) >= (?1) and convert(created_date , Date) <= (?2)" , nativeQuery = true)
	List<VerificationRequest> findByStatus(String fromDate, String toDate);

	@Query(value = "SELECT * FROM verification_request where doc_status in ('Approved','SV_Approved','SV_Rejected')", nativeQuery = true)
	List<VerificationRequest> findByStatusForUniversityMail();
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "UPDATE verification_request set assigned_to=0 where assigned_to=(?1) and doc_status='Requested'", nativeQuery = true)
	Integer updateList(long id);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "UPDATE verification_request set Payment_flg='Y' where id=(?1) and doc_status='Requested'", nativeQuery = true)
	Integer updatePaymentFlag(long id);

	@Query(value="SELECT * FROM verification_request where convert(created_date , Date) <= (?1)", nativeQuery = true)
	List<VerificationRequest> findRecordsByPreviousYearCreatedDate(String prevDate);


}
