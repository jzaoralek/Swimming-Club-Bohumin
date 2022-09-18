package com.jzaoralek.scb.ui.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.zkoss.util.resource.Labels;

import com.jzaoralek.scb.dataservice.controller.CourseApplicationController;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.ui.common.converter.Converters;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;

public class JasperUtil {
	private static final Logger LOG = LoggerFactory.getLogger(CourseApplicationController.class);
    public static String REPORT_MIME = "application/pdf";
    public static String XLSX_MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";


    public static byte[] getReport(CourseApplication courseApplication, String title, ConfigurationService configurationService) {
        InputStream reportStream = null;
        ByteArrayOutputStream baos = null;
        if (courseApplication == null) {
        	throw new IllegalArgumentException("courseApplication is null");
        }
        try {
            String inputFile = "prihlaska_report.jrxml";
            reportStream = JasperUtil.class.getClassLoader().getResourceAsStream(inputFile);
            JasperReport rp = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> paramsMap = new HashMap<String, Object>();
            paramsMap.put("reportTitle", title);
            paramsMap.put("organizationNameTitle", ConfigUtil.getOrgName(configurationService));
            paramsMap.put("contactPhone", ConfigUtil.getOrgPhone(configurationService));
            paramsMap.put("contactEmail", ConfigUtil.getOrgEmail(configurationService));
            paramsMap.put("courseParticFirstname", getNotNullValue(courseApplication.getCourseParticipant().getContact().getFirstname()));
            paramsMap.put("courseParticSurname", getNotNullValue(courseApplication.getCourseParticipant().getContact().getSurname()));
            paramsMap.put("courseParticBirthdate", Converters.getDateconverter().coerceToUi(courseApplication.getCourseParticipant().getBirthdate(), null, null));
            paramsMap.put("courseParticPersonalNo", getNotNullValue(courseApplication.getCourseParticipant().getPersonalNo()));
            paramsMap.put("courseParticHealthInsurance", getNotNullValue(courseApplication.getCourseParticipant().getHealthInsurance()));
            paramsMap.put("representativeTitle", Labels.getLabel("txt.ui.common.representative"));
            paramsMap.put("representativeFirstname", getNotNullValue(courseApplication.getCourseParticRepresentative().getContact().getFirstname()));
            paramsMap.put("representativeSurname", getNotNullValue(courseApplication.getCourseParticRepresentative().getContact().getSurname()));
            paramsMap.put("representativePhone1", getNotNullValue(courseApplication.getCourseParticRepresentative().getContact().getPhone1()));
            paramsMap.put("representativePhone2", getNotNullValue(courseApplication.getCourseParticRepresentative().getContact().getPhone2()));
            paramsMap.put("representativeEmail1", getNotNullValue(courseApplication.getCourseParticRepresentative().getContact().getEmail1()));
            paramsMap.put("representativeEmail2", getNotNullValue(courseApplication.getCourseParticRepresentative().getContact().getEmail2()));
            paramsMap.put("healthInfo", getNotNullValue(courseApplication.getCourseParticipant().getHealthInfo()));
            paramsMap.put("healthAgreement", configurationService.getHealthAgreement());
            paramsMap.put("persDataProcessAgreement", configurationService.getPersDataProcessAgreement());
            paramsMap.put("courseInfo", WebUtils.buildCourseApplMailCourseInfo(courseApplication, ""));

            String language = "cs";
            paramsMap.put(JRParameter.REPORT_LOCALE, new Locale(language));

            JasperPrint jasperPrint = JasperFillManager.fillReport(rp, paramsMap, new JREmptyDataSource());

            baos = new ByteArrayOutputStream();
            JRAbstractExporter jrExporter = new JRPdfExporter();
            jrExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            jrExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
            jrExporter.exportReport();

            return baos.toByteArray();
        }
        catch (Exception e) {
            LOG.error("Exception during report generating. ", e);
            throw new RuntimeException(e);
        }
        finally {
            try {
                closeStream(reportStream);
                closeStream(baos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public static byte[] getPaymentConfirmation(Course course, 
    									CourseParticipant courseParticipan,
    									String representCompleteName,
    									long coursePaymentSum,
    									Date paymentDate,
    									String title, 
    									ConfigurationService configurationService) {
        InputStream reportStream = null;
        ByteArrayOutputStream baos = null;
        if (course == null) {
        	throw new IllegalArgumentException("course is null");
        }
        try {
            String inputFile = "payment_confirm_report.jrxml";
            reportStream = JasperUtil.class.getClassLoader().getResourceAsStream(inputFile);
            JasperReport rp = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("reportTitle", title);
            paramsMap.put("organizationNameTitle", ConfigUtil.getOrgName(configurationService));
            paramsMap.put("contactPhone", ConfigUtil.getOrgPhone(configurationService));
            paramsMap.put("contactEmail", ConfigUtil.getOrgEmail(configurationService));
            paramsMap.put("representativeTitle", Labels.getLabel("txt.ui.common.representative"));
            paramsMap.put("healthAgreement", configurationService.getHealthAgreement());
            
            paramsMap.put("courseParticCompleteName", getNotNullValue(courseParticipan.getContact().getCompleteName()));
            paramsMap.put("courseParticAddress", getNotNullValue(courseParticipan.getContact().getCompleteAddress()));
            paramsMap.put("representativeCompleteName", getNotNullValue(representCompleteName));
            paramsMap.put("courseParticBirthdate", Labels.getLabel("txt.ui.paymentConfirmReport.Birthdate", new Object[] {DateUtil.dateAsString(courseParticipan.getBirthdate())}));
            paramsMap.put("orgRepresentative", ConfigUtil.getOrgContactPerson(configurationService));
            paramsMap.put("orgName", ConfigUtil.getOrgName(configurationService));            
            paramsMap.put("invoiceDate", DateUtil.dateAsString(Calendar.getInstance().getTime()));
            paramsMap.put("paymentDate", DateUtil.dateAsString(paymentDate));
            paramsMap.put("courseInfo", Labels.getLabel("txt.ui.paymentConfirmReport.CoursePaymentInfo", new Object[] {course.getName(), course.getYear()}));
            paramsMap.put("paymentSum", coursePaymentSum + " " + Labels.getLabel("txt.ui.common.CZK"));
            
            // TODO: (JZ), nahradit hodnotami z konfigurace
            paramsMap.put("organizationAddress", getNotNullValue("Na Koutě 400, Bohumín, 735 81"));
            paramsMap.put("organizationIdentNo", Labels.getLabel("txt.ui.paymentConfirmReport.IdentificationNo", new Object[] {"26993660"}));
            
            // podpis
            String orgReprSignature = "pkbohumin_repr_sign.png";
            /*
            URI uri = new URI(JasperUtil.class.getResource("payment_confirm_report.jrxml").getPath());
            new File(uri.getPath());
             */
            paramsMap.put("orgReprSignature", orgReprSignature);

            paramsMap.put(JRParameter.REPORT_LOCALE, new Locale("cs_CZ"));

            JasperPrint jasperPrint = JasperFillManager.fillReport(rp, paramsMap, new JREmptyDataSource());

            baos = new ByteArrayOutputStream();
            JRAbstractExporter jrExporter = new JRPdfExporter();
            jrExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            jrExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
            jrExporter.exportReport();

            return baos.toByteArray();
        }
        catch (Exception e) {
            LOG.error("Exception during report generating. ", e);
            throw new RuntimeException(e);
        }
        finally {
            try {
                closeStream(reportStream);
                closeStream(baos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getNotNullValue(String value) {
    	return StringUtils.hasText(value) ? value : "";
    }

    private static void closeStream(Closeable is) throws IOException {
        if (is != null) {
            is.close();
            is = null;
        }
    }
}
