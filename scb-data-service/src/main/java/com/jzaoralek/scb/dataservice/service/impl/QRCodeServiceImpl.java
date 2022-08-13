package com.jzaoralek.scb.dataservice.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jzaoralek.scb.dataservice.domain.PaymentInstruction;
import com.jzaoralek.scb.dataservice.service.QRCodeService;

import webtools.rest.RestExecutor;
import webtools.rest.exception.RestException;

@Service("qrCodeService")
public class QRCodeServiceImpl implements QRCodeService {

	private static final Logger LOG = LoggerFactory.getLogger(CourseServiceImpl.class);

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final String QR_CODE_GEN_DOMAIN = "https://api.paylibo.com";
	private static final String ACCOUNT_PREFIX_DELIM = "-";
	
	private RestExecutor restExecutor;
	
	@PostConstruct
    public void init() {
        restExecutor = new RestExecutor(QR_CODE_GEN_DOMAIN, null);
    }
	
	@Override
	public byte[] getPaymentQRCode(PaymentInstruction paymentInstruction, Date dueDate) {
		LOG.debug("Generating QRCode for payment: {}, dueDate: {}", paymentInstruction, dueDate);
		try {
			StringBuilder params = new StringBuilder();
			
			String[] accountInfo = paymentInstruction.getBankAccountNumber().split("/");
			String accountNo = accountInfo[0];
			String bankCode = accountInfo[1];
			
			String accountPrefix = null;
			if (accountNo.contains(ACCOUNT_PREFIX_DELIM)) {
				// account has prefix
				String[] accountWithPrefix = accountNo.split(ACCOUNT_PREFIX_DELIM);
				accountPrefix = accountWithPrefix[0];
				accountNo = accountWithPrefix[1];
			}
			
			params.append("?accountNumber=" + accountNo);
			if (StringUtils.hasText(accountPrefix)) {
				params.append("&accountPrefix=" + accountPrefix);
			}
			params.append("&bankCode=" + bankCode);
			params.append("&amount=" + paymentInstruction.getPriceSemester());
			params.append("&currency=" + "CZK");
			params.append("&vs=" + paymentInstruction.getVarsymbol());
			params.append("&message=" + paymentInstruction.getCourseParticName());
			
			if (dueDate != null) {
				// termin splatnosti, format yyyy-MM-dd
				params.append("&date=" + DATE_FORMAT.format(dueDate));
			}
			
			LOG.debug("Generating QRCode for params: {}", params);
			
			// call REST to generate QR Code
			return restExecutor.execute("/paylibo/generator/czech/image" + params.toString(),
					HttpMethod.GET, 
					null, 
					byte[].class);
			
		} catch (RestException e) {
			LOG.error("RestException caught during processing paymentInstruction: {}, dueDate: {}", paymentInstruction, dueDate, e);
			throw new RuntimeException(e);
		}
	}
}
