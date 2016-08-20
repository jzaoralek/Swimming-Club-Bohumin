package com.jzaoralek.scb.ui.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.zkoss.util.resource.Labels;

import com.jzaoralek.scb.dataservice.controller.CourseApplicationController;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
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


    public static byte[] getReport(CourseApplication courseApplication, String title) {
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
            paramsMap.put("organizationNameTitle", Labels.getLabel("txt.ui.organization.name"));
            paramsMap.put("contactPhone", Labels.getLabel("txt.ui.organization.phone"));
            paramsMap.put("contactEmail", Labels.getLabel("txt.ui.organization.email"));
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
