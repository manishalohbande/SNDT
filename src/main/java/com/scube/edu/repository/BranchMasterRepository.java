package com.scube.edu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scube.edu.model.BranchMasterEntity;

@Repository
public interface BranchMasterRepository extends JpaRepository<BranchMasterEntity, Long> {

	@Query(value ="SELECT * from branch_master where stream_id =?1", nativeQuery = true)
	List<BranchMasterEntity> getbrachbyStreamId(Long id);
	Optional<BranchMasterEntity> findById(Long id);
	BranchMasterEntity findByBranchNameAndStreamId (String branchnm,Long StreamId);
	List<BranchMasterEntity> findByIsdeleted(String isdeleted);
	List<BranchMasterEntity> findByStreamIdAndIsdeleted(Long id, String isdeleted);
	BranchMasterEntity findByBranchNameAndStreamIdAndIsdeleted (String branchnm,Long StreamId,String isDeleted);

}

