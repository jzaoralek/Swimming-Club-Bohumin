package com.jzaoralek.scb.dataservice.aspect;

import java.text.MessageFormat;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class LoggingAspect {

	@Pointcut("execution(* com.jzaoralek.scb.dataservice.controller.*.*(..)) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void controllerClassPointcut() {}

	@Around("controllerClassPointcut()")
	public Object logControllerMethod(final ProceedingJoinPoint pjp) throws Throwable {
		return this.logMethod(pjp);
	}

	private Object logMethod(final ProceedingJoinPoint pjp) throws Throwable {
        final Class<?> targetInvocationClass = pjp.getTarget().getClass();
        final String methodName = pjp.getSignature().getName();
        final Logger logger = LoggerFactory.getLogger(targetInvocationClass);

        try {

            if (logger.isTraceEnabled()) {
                final StringBuffer sb = new StringBuffer();
                sb.append(methodName + "():: ");
                sb.append("ENTER: ");
                sb.append("[" + this.logArguments(pjp) + "]");
                logger.trace(sb.toString());
            }

            final Object retVal = pjp.proceed();

            if (logger.isTraceEnabled()) {
                final StringBuffer sb = new StringBuffer();
                sb.append(methodName + "():: ");
                sb.append("EXIT: ");
                sb.append("[" + retVal + "]");
                logger.trace(sb.toString());
            }
            return retVal;
        } catch (final Exception e) {
            final StringBuffer sb = new StringBuffer();
            sb.append(methodName + "():: ");
            sb.append("ENTER: ");
            sb.append("[" + this.logArguments(pjp) + "]");
            sb.append(" Unexpected exception caught: ");
            logger.error(sb.toString(), e);
            throw e;
        }
        
    }

    /**
     * Method for transforming array of the incoming arguments into message
     * 
     * @param invocation
     *            - the method invocation
     * @return message which contains all incoming arguments
     */
    private String logArguments(final ProceedingJoinPoint pjp) {

        final StringBuffer arguments = new StringBuffer();

        if (pjp.getArgs() != null) {

            for (final Object obj : pjp.getArgs()) {
                if (obj != null) {
                	if (arguments.length() > 0) {
                		arguments.append(", ");                		
                	}
                    arguments.append(MessageFormat.format("{0}", obj.toString()));
                } else {
                    arguments.append("Arg is NULL.");
                }
            }
        }

        return arguments.toString();
    }
}