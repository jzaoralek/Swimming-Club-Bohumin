package webtools.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import webtools.rest.exception.RestException;
import webtools.rest.logging.LoggingHandler;
import webtools.rest.logging.LoggingInterceptor;

import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Project: jira-client
 *
 * Created: 15. 10. 2016
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public class RestExecutor {

    private static final String IO_ERROR = "Server returned HTTP response code";

    private String domain;
    private String jsessionid;
    private RestTemplate restTemplate;
    private RestResponseErrorHandler restResponseErrorHandler;

    /**
     * 
     * @param domain
     * @param loggingHandler
     */
    public RestExecutor(String domain, LoggingHandler loggingHandler) {
        this.domain = domain;
        configureRestTemplate(loggingHandler);
    }

    /**
     * 
     * @param loggingHandler
     */
    private void configureRestTemplate(LoggingHandler loggingHandler) {
        restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));

        restResponseErrorHandler = new RestResponseErrorHandler();
        restTemplate.setErrorHandler(restResponseErrorHandler);

        if (loggingHandler != null) {
            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
            interceptors.add(new LoggingInterceptor(loggingHandler));
            restTemplate.setInterceptors(interceptors);
        }

        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        restTemplate.getMessageConverters().add(jsonHttpMessageConverter);
    }

    /**
     * 
     * @param path
     * @param method
     * @param request
     * @param response
     * @param uriVariables
     * @return
     * @throws RestException 
     * @throws Exception
     */
    public <REQ, RESP> RESP execute(String path, HttpMethod method, REQ request, Class<RESP> response, Object... uriVariables) throws RestException {
        return executeWithHeaders(path, method, request, response, null, uriVariables);
    }

    /**
     * 
     * @param path
     * @param method
     * @param request
     * @param response
     * @param headers
     * @param uriVariables
     * @return
     * @throws RestException
     */
    public <REQ, RESP> RESP executeWithHeaders(String path, HttpMethod method, REQ request, Class<RESP> response, HttpHeaders headers, Object... uriVariables)
            throws RestException {
        if (headers == null) {
            headers = new HttpHeaders();
        }
        initHeaders(headers);
        ResponseEntity<RESP> result = null;
        try {
            result = restTemplate.exchange(domain + path, method, new HttpEntity<REQ>(request, headers), response, uriVariables);
        } catch (Exception e) {
            processException(e);
        }
        if (result != null) {
            RestException.throwIfBadStatus(result.getStatusCode());
        }
        return result.getBody();
    }

    private void processException(Exception e) throws RestException {
        // ugly hack: protože , když se používá logging interceptor, tak se to někdy divně chová
        if (e.getCause() != null && e.getCause() instanceof IOException && e.getCause().getMessage().contains(IO_ERROR)) {
            String msg = e.getCause().getMessage();
            String statusCode = msg.split(" ")[5];
            RestException.throwIfBadStatus(HttpStatus.valueOf(new Integer(statusCode).intValue()));
        }
        throw new RestException(e);
    }

    /**
     * 
     * @param headers
     */
    private void initHeaders(HttpHeaders headers) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (jsessionid != null) {
            headers.set("Cookie", "JSESSIONID=" + jsessionid);
        }
    }

    public String getJsessionid() {
        return jsessionid;
    }

    public void setJsessionid(String jsessionid) {
        this.jsessionid = jsessionid;
    }

}
