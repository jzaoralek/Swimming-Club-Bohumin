package com.jzaoralek.scb.ui.common.recaptcha;

import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;

import com.jzaoralek.scb.ui.common.utils.WebUtils;

public class RecaptchaComposer extends SelectorComposer<Component>  {

	private static final long serialVersionUID = 1L;

	// "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe";
	private static final String SECRET_KEY = "6LftpX8UAAAAANwPOmxWUhOY-xSE6Bp601yj5meE";
	
	@Wire
    Button submit;
    
    @Listen("onUserRespond = #recaptcha")
    public void verify(Event event) throws Exception{
        JSONObject result = RecaptchaVerifier.verifyResponse(SECRET_KEY, ((JSONObject)event.getData()).get("response").toString());
        if (Boolean.parseBoolean(result.get("success").toString())){
            submit.setDisabled(false);
        }else{
            //log or show error
            WebUtils.showNotificationWarning(result.get("error-codes").toString());
            submit.setDisabled(true);
        }
    }
}
