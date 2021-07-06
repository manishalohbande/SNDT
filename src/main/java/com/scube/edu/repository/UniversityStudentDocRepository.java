package com.scube.edu.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.Convert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scube.edu.model.UniversityStudentDocument;
import com.scube.edu.model.UserMasterEntity;
import com.scube.edu.response.UniversityStudentDocumentResponse;

@Repository
public interface UniversityStudentDocRepository extends JpaRepository<UniversityStudentDocument, Long> {

	@Query(value = "SELECT * FROM university_studentdocument where seat_no = ?1 "
			+ "and passing_year_id = ?2 and semester_id = ?3 and sream_id = ?4 and month_of_passing = ?5" , nativeQuery = true)
//	@Query(value = "SELECT * FROM university_studentdocument where enrollment_no = ?1 ", nativeQuery = true)
	UniversityStudentDocument getDocDataByFourFields(String enrollmentNo , String yearOfPassing, Long semId, Long streamId, String monthOfPassing);

	UniversityStudentDocument findByEnrollmentNoAndStreamIdAndPassingYearIdAndSemIdAndMonthOfPassing(String enrollmentNo,Long stream,Long passyr,Long semId,String Month);

	UniversityStudentDocument findByPrnNoAndSemId(String prnNo,Long semId);
	
	@Query(value = "SELECT * FROM university_studentdocument where seat_no LIKE %?1% and passing_year_id LIKE %?2% and sream_id LIKE %?3% and semester_id LIKE %?4% and month_of_passing LIKE %?5% and prn_no LIKE %?6%" , nativeQuery = true)
	List<UniversityStudentDocument> searchBySeatNoLikeAndPassingYearIdLikeAndStreamIdLikeAndSemIdLikeAndMonthOfPassingLikeAndPrnNoLike(
			String enrollmentno, String yearofpassid, String streamid,String semId,String monthOfPassing, String prnNo);

	@Query(value = "SELECT * FROM university_studentdocument where prn_no = ?1 and semester_id = ?2", nativeQuery = true)
	List<UniversityStudentDocument> findByPrnNoAndSemesterId(String prnNo, long semesterId);



}
