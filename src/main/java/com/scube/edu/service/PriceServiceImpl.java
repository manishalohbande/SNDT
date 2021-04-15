package com.scube.edu.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scube.edu.model.DocumentMaster;
import com.scube.edu.model.PriceMaster;
import com.scube.edu.repository.PriceMasterRepository;
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
	BaseResponse  baseResponse = null;
    Base64.Decoder decoder = Base64.getDecoder();  
	
	//Abhishek Added
    
    
	@Override
	public List<PriceMasterResponse> getPriceList(HttpServletRequest request) {
		
		logger.info("-----------getPriceList---------------------");
		
		List<PriceMasterResponse> priceList = new ArrayList<>();
		
		List<PriceMaster> priceEntities    = pricemasterRespository.findAll();
		
		for(PriceMaster entity : priceEntities) {
			
			PriceMasterResponse priceResponse = new PriceMasterResponse();

			priceResponse.setId(entity.getId());
			priceResponse.setServiceName(entity.getServiceName());
			priceResponse.setAmount(entity.getAmount());
			priceResponse.setYearrangeStart(entity.getYearrangeStart());
			priceResponse.setYearrangeEnd(entity.getYearrangeEnd());
			priceResponse.setCreated_by(entity.getCreateby());
			
			
			priceList.add(priceResponse);
		}
		return priceList;
	}
	

	
		@Override
		public Boolean addPrice(PriceAddRequest priceRequest) throws Exception {
			
			
			
			PriceMaster priceMasterEntity  = new  PriceMaster();
			
			priceMasterEntity.setServiceName(priceRequest.getServiceName());//1
			priceMasterEntity.setAmount(priceRequest.getAmount());//1
			priceMasterEntity.setYearrangeStart(priceRequest.getYearrangeStart());//
			priceMasterEntity.setYearrangeEnd(priceRequest.getYearrangeEnd());//
			priceMasterEntity.setCreateby(priceRequest.getCreated_by()); // Logged User Id 
			//docMasterEntity.setIsdeleted(documentRequest.getIs_deleted()); // By Default N	
		
			pricemasterRespository.save(priceMasterEntity);
		
			
				
			return true;
			
		}

		
		
		
		@Override
		public BaseResponse UpdatePrice(PriceMaster priceMaster) throws Exception {
			
			baseResponse	= new BaseResponse();	

			Optional<PriceMaster> priceEntities  = pricemasterRespository.findById(priceMaster.getId());
			
			   if(priceEntities == null) {
				   
					throw new Exception(" Invalid ID");
				}
			
			
			   PriceMaster priceEntit = priceEntities.get();
			
			   priceEntit.setServiceName(priceMaster.getServiceName());
			   priceEntit.setAmount(priceMaster.getAmount());//1
			   priceEntit.setYearrangeStart(priceMaster.getYearrangeStart());//
			   priceEntit.setYearrangeEnd(priceMaster.getYearrangeEnd());//
			
			
			   pricemasterRespository.save(priceEntit);
			
			   
		
			baseResponse.setRespCode(StringsUtils.Response.SUCCESS_RESP_CODE);
			baseResponse.setRespMessage(StringsUtils.Response.SUCCESS_RESP_MSG);
			baseResponse.setRespData("success");
			 
			return baseResponse;
		}
		
		
		//Abhishek Added
}