package com.scube.edu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scube.edu.model.RequestTypeMaster;

@Repository
public interface RequestTypeRepository extends JpaRepository<RequestTypeMaster, Long>{

	Optional<RequestTypeMaster> findById(Long id);
	RequestTypeMaster findByRequestTypeAndIsdeleted(String rqstTyp,String isdeleted);
	List<RequestTypeMaster> findAllByIsdeleted(String isdeleted);
	
	List<RequestTypeMaster> findByIsdeleted(String isdeleted);
}
