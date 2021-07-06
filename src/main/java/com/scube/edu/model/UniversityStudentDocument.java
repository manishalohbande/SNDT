package com.scube.edu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table (name = "universityStudentdocument")
@Getter @Setter
public class UniversityStudentDocument extends CreateUpdate {
		

		@Id
		@Column(name = "id")
		@GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
		
		
		@Size(max = 100)
		@Column(name = "seat_no")
		private String seatNo;
		
		@Size(max = 100)
		@Column(name = "prn_no")
		private String prnNo;
		
		//@Size(max = 100)
		@Column(name = "center")
		private String center;
		
		//@Size(max = 100)
		@Column(name = "med_desc")
		private Long medDesc;
		
		//@Size(max = 100)
		@Column(name = "CollegeId")
		private Long collegeId;
		
		//@NotBlank
		@Size(max = 100)
		@Column(name = "student_name")
		private String studentName;
		
		//@NotBlank
	//	@Size(max = 100)
		@Column(name = "passingYearId")
		private Long passingYearId;
		
		@Column(name = "month_of_passing")
		private String monthOfPassing;
		
		
		
		@Column(name = "semester_Id")
		private Long semId;
		
		@Column(name = "sem_total_max")
		private String semTotalMax;

		
		@Column(name = "sem_grd_total")
		private String semGradeTotal;
		
		@Column(name = "sem_grace")
		private String semGrace;
		
		@Column(name = "sem_class")
		private String semclass;
		
		
		@Column(name = "sem_gpa")
		private String semGpa;
		
		@Column(name = "sem_grade")
		private String semGrade;
		
		@Column(name = "grade_total")
		private String gradeTotal;
		
		@Column(name = "class_grc")
		private String classGrace;
		
		@Column(name = "max_gr_total")
		private String maxGrTotal;
		
		@Column(name = "cred_total")
		private String credTotal;
		
		@Column(name = "gpa")
		private String gpa;
		
		@Column(name = "fgrade")
		private String fgrade;
		
		@Column(name = "result_desc")
		private String resultDesc;
		
		@Column(name = "ince_desc")
		private String inceDesc;
		
		@Column(name = "subject_1")
		private String subOne;
		
		@Column(name = "subject_name_1")
		private String subNmOne;
		
		@Column(name = "credit_1")
		private String creditOne;
		
		@Column(name = "int_1")
		private String intSubOne;
		
		@Column(name = "ext_1")
		private String extSubOne;
		
		@Column(name = "total_1")
		private String totalSubOne;
		
		@Column(name = "grace_1")
		private String graceSubOne;
		
		@Column(name = "prv_flag_1")
		private String prvFlgSubOne;
		
		@Column(name = "max_int_1")
		private String maxIntSubOne;
		
		@Column(name = "max_ext_1")
		private String maxExtSubOne;
		
		@Column(name = "max_total_1")
		private String maxTotalSubOne;
		
		
		@Column(name = "subject_2")
		private String subTwo;
		
		@Column(name = "subject_name_2")
		private String subNmTwo;
		
		@Column(name = "credit_2")
		private String creditSubTwo;
		
		@Column(name = "int_2")
		private String intSubTwo;
		
		@Column(name = "ext_2")
		private String extSubTwo;
		
		@Column(name = "total_2")
		private String totalSubTwo;
		
		@Column(name = "grace_2")
		private String graceSubTwo;
		
		@Column(name = "prv_flag_2")
		private String prvFlgSubTwo;
		
		@Column(name = "max_int_2")
		private String maxIntSubTwo;
		
		@Column(name = "max_ext_2")
		private String maxExtSubTwo;
		
		@Column(name = "max_total_2")
		private String maxTotalSubTwo;
		
		
		@Column(name = "subject_3")
		private String subThree;
		
		@Column(name = "subject_name_3")
		private String subNmThree;
		
		@Column(name = "credit_3")
		private String creditSubThree;
		
		@Column(name = "int_3")
		private String intSubThree;
		
		@Column(name = "ext_3")
		private String extSubThree;
		
		@Column(name = "total_3")
		private String totalSubThree;
		
		@Column(name = "grace_3")
		private String graceSubThree;
		
		@Column(name = "prv_flag_3")
		private String prvFlgSubThree;
		
		@Column(name = "max_int_3")
		private String maxIntSubThree;
		
		@Column(name = "max_ext_3")
		private String maxExtSubThree;
		
		@Column(name = "max_total_3")
		private String maxTotalSubThree;
		
		
		@Column(name = "subject_4")
		private String subFour;
		
		@Column(name = "subject_name_4")
		private String subNmFour;
		
		@Column(name = "credit_4")
		private String creditSubFour;
		
		@Column(name = "int_4")
		private String intSubFour;
		
		@Column(name = "ext_4")
		private String extSubFour;
		
		@Column(name = "total_4")
		private String totalSubFour;
		
		@Column(name = "grace_4")
		private String graceSubFour;
		
		@Column(name = "prv_flag_4")
		private String prvFlgSubFour;
		
		@Column(name = "max_int_4")
		private String maxIntSubFour;
		
		@Column(name = "max_ext_4")
		private String maxExtSubFour;
		
		@Column(name = "max_total_4")
		private String maxTotalSubFour;
		
		
		
		@Column(name = "subject_5")
		private String subFive;
		
		@Column(name = "subject_name_5")
		private String subNmFive;
		
		@Column(name = "credit_5")
		private String creditSubFive;
		
		@Column(name = "int_5")
		private String intSubFive;
		
		@Column(name = "ext_5")
		private String extSubFive;
		
		@Column(name = "total_5")
		private String totalSubFive;
		
		@Column(name = "grace_5")
		private String graceSubFive;
		
		@Column(name = "prv_flag_5")
		private String prvFlgSubFive;
		
		@Column(name = "max_int_5")
		private String maxIntSubFive;
		
		@Column(name = "max_ext_5")
		private String maxExtSubFive;
		
		@Column(name = "max_total_5")
		private String maxTotalSubFive;
		
		
		
		@Column(name = "subject_6")
		private String subSix;
		
		@Column(name = "subject_name_6")
		private String subNmSix;
		
		@Column(name = "credit_6")
		private String creditSubSix;
		
		@Column(name = "int_6")
		private String intSubSix;
		
		@Column(name = "ext_6")
		private String extSubSix;
		
		@Column(name = "total_6")
		private String totalSubSix;
		
		@Column(name = "grace_6")
		private String graceSubSix;
		
		@Column(name = "prv_flag_6")
		private String prvFlgSubSix;
		
		@Column(name = "max_int_6")
		private String maxIntSubSix;
		
		@Column(name = "max_ext_6")
		private String maxExtSubSix;
		
		@Column(name = "max_total_6")
		private String maxTotalSubSix;
		
		
		
		
		@Column(name = "subject_7")
		private String subSeven;
		
		@Column(name = "subject_name_7")
		private String subNmSeven;
		
		@Column(name = "credit_7")
		private String creditSubSeven;
		
		@Column(name = "int_7")
		private String intSubSeven;
		
		@Column(name = "ext_7")
		private String extSubSeven;
		
		@Column(name = "total_7")
		private String totalSubSeven;
		
		@Column(name = "grace_7")
		private String graceSubSeven;
		
		@Column(name = "prv_flag_7")
		private String prvFlgSubSeven;
		
		@Column(name = "max_int_7")
		private String maxIntSubSeven;
		
		@Column(name = "max_ext_7")
		private String maxExtSubSeven;
		
		@Column(name = "max_total_7")
		private String maxTotalSubSeven;
		
		
		
		
		@Column(name = "subject_8")
		private String subEight;
		
		@Column(name = "subject_name_8")
		private String subNmEight;
		
		@Column(name = "credit_8")
		private String creditSubEight;
		
		@Column(name = "int_8")
		private String intSubEight;
		
		@Column(name = "ext_8")
		private String extSubEight;
		
		@Column(name = "total_8")
		private String totalSubEight;
		
		@Column(name = "grace_8")
		private String graceSubEight;
		
		@Column(name = "prv_flag_8")
		private String prvFlgSubEight;
		
		@Column(name = "max_int_8")
		private String maxIntEight;
		
		@Column(name = "max_ext_8")
		private String maxExtSubEight;
		
		@Column(name = "max_total_8")
		private String maxTotalSubEight;
		
		
		
		@Column(name = "subject_9")
		private String subNine;
		
		@Column(name = "subject_name_9")
		private String subNmNine;
		
		@Column(name = "credit_9")
		private String creditSubNine;
		
		@Column(name = "int_9")
		private String intSubNine;
		
		@Column(name = "ext_9")
		private String extSubNine;
		
		@Column(name = "total_9")
		private String totalSubNine;
		
		@Column(name = "grace_9")
		private String graceSubNine;
		
		@Column(name = "prv_flag_9")
		private String prvFlgSubNine;
		
		@Column(name = "max_int_9")
		private String maxIntSubNine;
		
		@Column(name = "max_ext_9")
		private String maxExtSubNine;
		
		@Column(name = "max_total_9")
		private String maxTotalSubNine;
		
		
		
		
		
		@Column(name = "subject_10")
		private String subTen;
		
		@Column(name = "subject_name_10")
		private String subNmTen;
		
		@Column(name = "credit_10")
		private String creditTen;
		
		@Column(name = "int_10")
		private String intTen;
		
		@Column(name = "ext_10")
		private String extTen;
		
		@Column(name = "total_10")
		private String totalTen;
		
		@Column(name = "grace_10")
		private String graceTen;
		
		@Column(name = "prv_flag_10")
		private String prvFlgTen;
		
		@Column(name = "max_int_10")
		private String maxIntTen;
		
		@Column(name = "max_ext_10")
		private String maxExtTen;
		
		@Column(name = "max_total_10")
		private String maxTotalTen;
				
		@Column(name = "branch_Id")
		private Long branchId;
}
