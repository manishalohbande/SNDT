package com.scube.edu.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scube.edu.model.DocumentMaster;
import com.scube.edu.model.PriceMaster;
import com.scube.edu.model.RequestTypeMaster;
import com.scube.edu.repository.DocumentRepository;
import com.scube.edu.repository.PriceMasterRepository;
import com.scube.edu.repository.RequestTypeRepository;
import com.scube.edu.request.PriceAddRequest;
import com.scube.edu.response.BaseResponse;
import com.scube.edu.response.DocumentResponse;
import com.scube.edu.response.PriceMasterResponse;
import com.scube.edu.util.StringsUtils;

@Service
public class PriceServiceImpl  implements PriceService {
	
	
    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
	
	@Autowired
	PriceMasterRepository pricemasterRespository;
	@Autowired
	RequestTypeRepository requestTypeRepository;
	
	@Autowired
	DocumentRepository documentRepository;
	BaseResponse  baseResponse = null;
    Base64.Decoder decoder = Base64.getDecoder();  
	
	//Abhishek Added
    
    
	@Override
	public List<PriceMasterResponse> getPriceList(HttpServletRequest request) {
		
		logger.info("-----------getPriceList---------------------");
		
		List<PriceMasterResponse> priceList = new ArrayList<>();
		
		List<PriceMaster> priceEntities    = pricemasterRespository.findByIsdeleted("N");
		
		for(PriceMaster entity : priceEntities) {
			
			PriceMasterResponse priceResponse = new PriceMasterResponse();

			Optional<RequestTypeMaster> requestTypeMaster=requestTypeRepository.findById(entity.getRequestTypeId());
			RequestTypeMaster requestTypeMasterdata=requestTypeMaster.get();
			
			Optional<DocumentMaster> documentMaster=documentRepository.findById(entity.getDoctypeId());
			DocumentMaster documentMasterdata=documentMaster.get();

			priceResponse.setId(entity.getId());
			//priceResponse.setServiceName(entity.getServiceName());			
			priceResponse.setAmount(entity.getAmount());
			priceResponse.setYearrangeStart(entity.getYearrangeStart());
			priceResponse.setYearrangeEnd(entity.getYearrangeEnd());
			priceResponse.setCreated_by(entity.getCreateby());
			priceResponse.setGst(entity.getGst());
			priceResponse.setSecure_charge(entity.getSecurCharge());
			priceResponse.setRequestType(requestTypeMasterdata.getRequestType());
			priceResponse.setDocType(documentMasterdata.getDocumentName());
			priceResponse.setRequestTypeid(entity.getRequestTypeId());
			priceResponse.setDocTypeid(entity.getDoctypeId());

			
			priceList.add(priceResponse);
		}
		return priceList;
	}
	

	
		@Override
		public Boolean addPrice(PriceAddRequest priceRequest) throws Exception {
			
			PriceMaster PriceMasterentt=pricemasterRespository.findByYearrangeStartAndYearrangeEndAndRequestTypeIdAndDoctypeIdAndIsdeleted(priceRequest.getYearrangeStart(), priceRequest.getYearrangeEnd(), priceRequest.getRequestTypeId(), priceRequest.getDoctypeId(),"N");
			
			if(PriceMasterentt!=null) {
				throw new Exception("This price entry already exists");

			}
			else {
			PriceMaster priceMasterEntity  = new  PriceMaster();
			PriceMaster ischkpriceMaster=pricemasterRespository.findByYearrangeStartAndYearrangeEndAndRequestTypeIdAndDoctypeId(priceRequest.getYearrangeStart(), priceRequest.getYearrangeEnd(), priceRequest.getRequestTypeId(), priceRequest.getDoctypeId());

			if(ischkpriceMaster!=null) {
				ischkpriceMaster.setIsdeleted("N"); // By Default N
			//	priceMasterEntity.setUpdateby(StringpriceRequest.getId());
				ischkpriceMaster.setUpdatedate(new Date());
				pricemasterRespository.save(ischkpriceMaster);

				
			}else {
			//priceMasterEntity.setServiceName(priceRequest.getServiceName());//1
			priceMasterEntity.setAmount(priceRequest.getAmount());//1
			priceMasterEntity.setYearrangeStart(priceRequest.getYearrangeStart());//
			priceMasterEntity.setYearrangeEnd(priceRequest.getYearrangeEnd());//
			priceMasterEntity.setCreateby(priceRequest.getCreated_by()); // Logged User Id 
			priceMasterEntity.setIsdeleted("N"); // By Default N	
			priceMasterEntity.setGst(priceRequest.getGst());
			priceMasterEntity.setSecurCharge(priceRequest.getSecuritycharge());
			priceMasterEntity.setTotalAmt(priceRequest.getAmount()+priceRequest.getSecuritycharge());

			priceMasterEntity.setRequestTypeId(priceRequest.getRequestTypeId());
			priceMasterEntity.setDoctypeId(priceRequest.getDoctypeId());
			pricemasterRespository.save(priceMasterEntity);

			}
		
			}
				
			return true;
			
		}
		
		

		
		
		
		@Override
		public BaseResponse UpdatePrice(PriceAddRequest priceMaster) throws Exception {
			
			baseResponse	= new BaseResponse();	

			PriceMaster priceEntities  = pricemasterRespository.findById(priceMaster.getId());
			
			   if(priceEntities == null) {
				   
					throw new Exception(" Invalid ID");
				}
			
			
//			   PriceMaster priceEntit = priceEntities.get();
			
			//   priceEntities.setServiceName(priceMaster.getServiceName());
			   priceEntities.setAmount(priceMaster.getAmount());//1
			   priceEntities.setYearrangeStart(priceMaster.getYearrangeStart());//
			   priceEntities.setYearrangeEnd(priceMaster.getYearrangeEnd());//
			   priceEntities.setGst(priceMaster.getGst());
			   priceEntities.setSecurCharge(priceMaster.getSecuritycharge());
			   priceEntities.setRequestTypeId(priceMaster.getRequestTypeId());
			   priceEntities.setTotalAmt(priceMaster.getAmount()+priceMaster.getSecuritycharge());
			   priceEntities.setDoctypeId(priceMaster.getDoctypeId());
			   
			   pricemasterRespository.save(priceEntities);
			
			   
		
			baseResponse.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
			baseResponse.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
			baseResponse.setRespData("success");
			 
			return baseResponse;
		}



		@Override
		public Boolean deletePrice(Long id) {
			
			logger.info("*******PriceServiceImpl deletePrice*******");
			
			Optional<PriceMaster> pmm = pricemasterRespository.findById(id);
			PriceMaster pm = pmm.get();
			
			pm.setIsdeleted("Y");
			
			pricemasterRespository.save(pm);
			return true;
		}
		
		
		//Abhishek Added
}
