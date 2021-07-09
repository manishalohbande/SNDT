package com.scube.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scube.edu.model.VerificationRequest;

@Repository
public interface UniversityVerifierRepository extends JpaRepository<VerificationRequest, Long>{
	@Query(value = "SELECT * FROM verification_request where doc_status in ('Approved_Pass', 'Approved_Fail','SV_Approved_Pass','SV_Approved_Fail','SV_Rejected') order by  created_date desc" , nativeQuery = true)
	List<VerificationRequest> findByStatus();

	@Query(value = "SELECT * FROM verification_request where request_type = 2 and result_collection_type in (2,3) and doc_status in ('UN_Approved_Pass','UN_Rejected') and convert(created_date, DATE) <= (?2) and convert(created_date, DATE) >= (?1)", nativeQuery = true)
	List<VerificationRequest> findByRequestTypeAndDocStatusAndResultCollectionType(String fromDate, String toDate);

}
