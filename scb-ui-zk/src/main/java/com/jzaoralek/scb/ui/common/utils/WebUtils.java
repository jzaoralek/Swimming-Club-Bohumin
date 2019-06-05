package com.jzaoralek.scb.ui.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.javatuples.Pair;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;

public final class WebUtils {

	private static final Logger logger = Logger.getLogger(WebUtils.class);
	
	private WebUtils() {}

	public static void showNotificationInfo(String msg) {
		Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, null, null, 6000, true);
	};

	public static void showNotificationError(String msg) {
		Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_ERROR, null, null, 6000, true);
	};

	public static void showNotificationWarning(String msg) {
		Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_WARNING, null, null, 6000, true);
	};
	
	public static void showNotificationInfoAfterRedirect(String msg) {
		WebUtils.setSessAtribute(WebConstants.NOTIFICATION_MESSAGE, msg);
	};

	/**
	 * Vraci url za domenou
	 * @return
	 */
	public static String getCurrentUrl() {
		return Executions.getCurrent().getContextPath()
				+ Executions.getCurrent().getDesktop().getRequestPath();
	}

    public static String getRequestPath() {
        return Executions.getCurrent().getDesktop().getRequestPath();
    }

	public static HttpServletRequest getRequest() {
		return (HttpServletRequest)Executions.getCurrent().getNativeRequest();
	}
	
	public static void setSessAtribute(String atr, Object obj) {
		Execution exec = Executions.getCurrent();
		if (exec == null || exec.getSession() == null) {
			return;
		}
		exec.getSession().setAttribute(atr, obj);
	}
	
	public static Object getSessAtribute(String atr) {
		Execution exec = Executions.getCurrent();
		if (exec == null || exec.getSession() == null) {
			return null;
		}
		return exec.getSession().getAttribute(atr);
	}

	public static void removeSessAtribute(String atr) {
		Execution exec = Executions.getCurrent();
		if (exec == null || exec.getSession() == null) {
			return;
		}
		exec.getSession().removeAttribute(atr);
	}

	public static void downloadAttachment(Attachment attachment) {
		if (attachment == null) {
			return;
		}
		Filedownload.save(attachment.getByteArray(), attachment.getContentType(), attachment.getName());
	}

	public static void openModal(String uri) {
		if (!StringUtils.hasText(uri)) {
			return;
		}
		Window window = (Window)Executions.createComponents(uri, null, null);
		window.doModal();
	}

	// ==========================================================================
	// Popups
	// ==========================================================================
	/**
	 * Otevre popup s predanim parametru do VM popupu.
	 *
	 * @param page
	 *            Nazev stranky (ZUL soubor)
	 * @param windowName
	 *            Nadpis okna
	 * @param args
	 *            Seznam predavanych parametru. V modalnim okne lze argumenty nacist metodou WebHelper.getArg(argName)
	 */
	public static void openModal(String page, String windowName, Map<String, Object> args) {
		if (!StringUtils.hasText(page)) {
			throw new IllegalArgumentException("URL stranky je null");
		}
		Window window = (Window) Executions.createComponents(page, null, args);
		window.doModal();
		if (StringUtils.hasText(windowName)) {
			window.setTitle(windowName);
		}
	}

	/**
	 *
	 * @param argName
	 * @return
	 */
	public static Object getArg(String argName) {
		return Executions.getCurrent().getArg().get(argName);
	}
	
	/**
	 * Metoda pro ziskani listu objektu SelectItem z enum objektu.
	 *
	 * Vysledny List obsahuje pro kazdou konstantu definovanou pro dany Enum jednu polozku.
	 * Value polozky je konstanta enumu a Label polozky je nacten z bundle LABELS pod klicem {@literal "nbs.enum." + ${enum.name} + ${enum.item.name}}
	 *
	 * @param enumSet        objekt EnumSet obsahujici polozky enumu, pro ktere maji byt nacteny messages z message bundle
	 * @return               List<SelectItem>
	 */
	public static List<Listitem> getMessageItemsFromEnum(EnumSet<? extends Enum<?>> enumSet){

		List<Listitem> messagesList = new ArrayList<Listitem>(enumSet.size());

		// fill messageList with selected items loaded from enum object
		for(Enum<?> object:enumSet){
			Listitem item= new Listitem();
			item.setLabel(getMessageItemFromEnum(object));
			item.setValue(object);
			messagesList.add(item);
		}
		return messagesList;
	}

	public static List<Listitem> getMessageItemsFromEnumWithEmptyItem(EnumSet<? extends Enum<?>> enumSet){

		List<Listitem> messagesList = getMessageItemsFromEnum(enumSet);
		Listitem item= new Listitem();
		item.setLabel("");
		item.setValue(null);
		messagesList.add(0, item);

		return messagesList;
	}

	/**
	 * Metoda pro ziskani lokalizovane reprezentace polozky enumu z resource bundle
	 * @param e
	 * @return
	 */
	public static String getMessageItemFromEnum(Enum<?> e){
		return Labels.getLabel("enum." + e.getDeclaringClass().getSimpleName() + "." + e.name());
	}
	
	/**
	 * Vraci byteArray odpoviodajici souboru na vstupu.
	 * @param path
	 * @return
	 */
	public static byte[] getFileAsByteArray(String path) {
		byte[] ret = null;
		FileInputStream fileInputStream = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			fileInputStream = new FileInputStream(new File(WebApps.getCurrent().getRealPath(path)));
			bufferedInputStream = new BufferedInputStream(fileInputStream);			

			ret = IOUtils.toByteArray(bufferedInputStream);       		
			
			bufferedInputStream.close();				
			fileInputStream.close();
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException caught.", e);
		} catch (IOException e) {
			logger.error("IOException caught.", e);
		} 
		
		return ret;
	}
	
	/**
	 * Validace retezce emailovych adres na vstupu.
	 * @param value Retezec emailovych adres, oddelovac ";"
	 * @return Pair obsahujici list validnich a nevalidnich adres.
	 */
	public static Pair<List<String>, List<String>> validateEmailList(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		
		List<String> emailList = emailAddressStrToList(value);
	    
	    List<String> validEmailList = new ArrayList<>();
	    List<String> invalidEmailList = new ArrayList<>();
	    
	    Pattern emailPattern = Pattern.compile(WebConstants.EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
	    String emailItem;
	    for (String item : emailList) {
	    	emailItem = item.trim();
	    	if (emailPattern.matcher(emailItem).matches()) {
	    		validEmailList.add(emailItem.trim());
	    	} else {
	    		invalidEmailList.add(emailItem.trim());
	    	}
	    }
	    
		return new Pair<>(validEmailList, invalidEmailList);
	}
	
	/**
	 * Prevede retezec emailovych adres na list.
	 * @param value
	 * @return
	 */
	public static List<String> emailAddressStrToList(String value) {
		if (!StringUtils.hasText(value)) {
			return Collections.emptyList();
		}
		
		String[] emailArr = value.split(WebConstants.EMAIL_LIST_SEPARATOR);
		return Arrays.stream(emailArr).collect(Collectors.toList());
	}
	
	/**
	 * Prevede retezec emailovych adres na list.
	 * @param value
	 * @return
	 */
	public static String emailAddressListToStr(List<String> value) {
		if (CollectionUtils.isEmpty(value)) {
			return null;
		}
		
		return value.stream().collect(Collectors.joining(WebConstants.EMAIL_LIST_SEPARATOR));
	}
	
	
	// ***********************************
	// *** SCB util methods
	// ***********************************
	
	/**
	 * Redirect to new course page.
	 */
	public static void redirectToNewCourse() {
		Executions.sendRedirect("/pages/secured/ADMIN/kurz.zul?" + WebConstants.FROM_PAGE_PARAM + "=" + WebPages.COURSE_LIST);
	}
	
	/**
	 * Filter course by loaction.
	 * @param location
	 * @param courseListBase
	 * @return
	 */
	public static List<Course> filterByLocation(CourseLocation location, List<Course> courseListBase) {
		if (CollectionUtils.isEmpty(courseListBase)) {
			return Collections.emptyList();
		}
		
		if (location == null) {
			return courseListBase;
		}
		
		return courseListBase.stream()
                .filter(line -> location.getUuid().toString().equals(line.getCourseLocation().getUuid().toString()))
                .collect(Collectors.toList());
	}
}