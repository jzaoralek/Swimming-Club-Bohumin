package webtools.logging;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project: webtools-logging
 *
 * Created: 9. 12. 2015
 *
 * @author Ales Wojnar | ales@wojnar.cz | http://ales.wojnar.cz
 */
public abstract class LoggingAspectSupport {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    /**
     * 
     * @param pjp
     */
    protected void logMethod(JoinPoint pjp) {
        final Class<?> targetInvocationClass = pjp.getTarget().getClass();
        final String methodName = pjp.getSignature().getName();
        final Logger logger = LoggerFactory.getLogger(targetInvocationClass);
        final StringBuffer sb = new StringBuffer();
        sb.append(methodName);
        logArguments(pjp, sb);
        logger.info(sb.toString());
    }

    /**
     * 
     * @param pjp
     * @param sb
     * @return
     */
    protected String logArguments(final JoinPoint pjp, StringBuffer sb) {
        sb.append(" Arguments: {");

        if (pjp.getArgs() != null) {
            for (final Object obj : pjp.getArgs()) {
                if (obj != null) {
                    if (obj instanceof Calendar) {
                        sb.append(MessageFormat.format("[{0}]", ((Calendar) obj).getTime().toString()));
                    } else if (obj instanceof Date) {
                        sb.append(MessageFormat.format("[{0}]", ((Date) obj).toString()));
                    } else if (obj instanceof GregorianCalendar) {
                        sb.append(MessageFormat.format("[{0}]", ((GregorianCalendar) obj).getTime().toString()));
                    } else {
                        sb.append(MessageFormat.format("[{0}]", obj.toString()));
                    }
                } else {
                    sb.append("NULL");
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
