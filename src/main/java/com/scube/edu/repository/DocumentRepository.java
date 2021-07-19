package com.scube.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scube.edu.model.DocumentMaster;
import com.scube.edu.model.StreamMaster;
import com.scube.edu.model.UserMasterEntity;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentMaster, Long>{
	
	DocumentMaster findById(long id);
	DocumentMaster findByDocumentName(String docName);
	
	//DocumentMaster findById(long id);
	DocumentMaster findByDocumentNameAndIsdeleted(String docName,String isDeleted);
	
	
	DocumentMaster deleteById(long id);
	List<DocumentMaster> findAllByIsdeleted(String isdeleted);

}
