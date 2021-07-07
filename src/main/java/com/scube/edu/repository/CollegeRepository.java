package com.scube.edu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scube.edu.model.CollegeMaster;

@Repository
public interface CollegeRepository extends JpaRepository<CollegeMaster, Long> {

	
	CollegeMaster findById(long id);
		CollegeMaster findByCollegeName(String collegeName);
		CollegeMaster findByCollegeNameAndIsdeleted(String collegeName,String isdeleted);	

	CollegeMaster deleteById(long id);
	List<CollegeMaster> findByIsdeleted(String isdeleted);
	
}
