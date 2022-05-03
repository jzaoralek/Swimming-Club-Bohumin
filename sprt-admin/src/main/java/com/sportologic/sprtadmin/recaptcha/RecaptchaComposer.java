package com.sportologic.sprtadmin.recaptcha;

import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.service.ReCaptchaService;
import com.sportologic.sprtadmin.vm.CustomerConfigVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class RecaptchaComposer extends SelectorComposer<Component>  {

    private static final Logger logger = LoggerFactory.getLogger(RecaptchaComposer.class);
	private static final long serialVersionUID = 1L;

    @WireVariable
    private ConfigService configService;

    @WireVariable
    private ReCaptchaService reCaptchaService;
	
	@Wire
    Button submitBtn;
    
    @Listen("onUserRespond = #recaptcha")
    public void verify(Event event) throws Exception {
        JSONObject result = reCaptchaService.verifyResponse(((JSONObject)event.getData()).get("response").toString());
        if (Boolean.parseBoolean(result.get("success").toString())){
        	submitBtn.setDisabled(false);
        }else{
            String errorCodes = result.get("error-codes").toString();
            logger.error("ReCaptcha validation failed error-codes: {}", errorCodes);
            Clients.alert(result.get("error-codes").toString());
            submitBtn.setDisabled(true);
        }
    }
}
