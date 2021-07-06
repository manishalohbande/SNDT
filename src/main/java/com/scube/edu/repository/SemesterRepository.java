package com.scube.edu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scube.edu.model.BranchMasterEntity;
import com.scube.edu.model.SemesterEntity;

@Repository
public interface SemesterRepository extends JpaRepository<SemesterEntity, Long> {
	
	@Query(value ="SELECT * from semester_master where stream_id =?1 and is_deleted = '?2'", nativeQuery = true)
	List<SemesterEntity> getSembyStreamIdAndIsdeleted(Long id, String isdeleted);
	Optional<SemesterEntity> findById(Long id);
	
	SemesterEntity findBySemesterAndStreamId(String sem,Long StrmId);
	SemesterEntity findBySemesterAndStreamIdAndIsdeleted(String sem,Long StrmId,String isdeleted);

	List<SemesterEntity> findAllByIsdeleted(String isdeleted);
	List<SemesterEntity> findByStreamIdAndIsdeleted(Long id, String isdeleted);
}
