package com.jzaoralek.scb.ui.common.recaptcha;

import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;

import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.ui.common.utils.WebUtils;

public class RecaptchaComposer extends SelectorComposer<Component>  {

	private static final long serialVersionUID = 1L;

	@WireVariable
	private ConfigurationService configurationService;
	
	@Wire
    Button submitBtn;
    
    @Listen("onUserRespond = #recaptcha")
    public void verify(Event event) throws Exception{
        JSONObject result = RecaptchaVerifier.verifyResponse(configurationService.getRecaptchaSecredkey(), ((JSONObject)event.getData()).get("response").toString());
        if (Boolean.parseBoolean(result.get("success").toString())){
        	submitBtn.setDisabled(false);
        }else{
            //log or show error
            WebUtils.showNotificationWarning(result.get("error-codes").toString());
            submitBtn.setDisabled(true);
        }
    }
}
