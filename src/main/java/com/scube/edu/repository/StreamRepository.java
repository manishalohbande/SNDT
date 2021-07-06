package com.scube.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scube.edu.model.CollegeMaster;
import com.scube.edu.model.StreamMaster;
@Repository
public interface StreamRepository  extends JpaRepository<StreamMaster, Long>{

	
	StreamMaster findById(long pid);

	StreamMaster deleteById(long id);
	StreamMaster findByStreamName( String Streamname);
	StreamMaster findByStreamNameAndIsdeleted( String Streamname,String isdeleted);

	List<StreamMaster> findAllByIsdeleted(String isdeleted);
	
}
