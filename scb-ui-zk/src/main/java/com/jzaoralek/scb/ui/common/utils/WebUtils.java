package com.jzaoralek.scb.ui.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.zkoss.bind.BindUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.jzaoralek.scb.dataservice.domain.Attachment;
import com.jzaoralek.scb.dataservice.domain.Contact;
import com.jzaoralek.scb.dataservice.domain.Course;
import com.jzaoralek.scb.dataservice.domain.CourseApplication;
import com.jzaoralek.scb.dataservice.domain.CourseLocation;
import com.jzaoralek.scb.dataservice.domain.CourseParticipant;
import com.jzaoralek.scb.dataservice.domain.Lesson;
import com.jzaoralek.scb.dataservice.domain.ScbUser;
import com.jzaoralek.scb.dataservice.service.ConfigurationService;
import com.jzaoralek.scb.dataservice.service.CourseService;
import com.jzaoralek.scb.dataservice.service.ScbUserService;
import com.jzaoralek.scb.ui.common.WebConstants;
import com.jzaoralek.scb.ui.common.WebPages;
import com.jzaoralek.scb.ui.common.converter.Converters;
import com.jzaoralek.scb.ui.pages.courseapplication.vm.CourseApplicationVM;

public final class WebUtils {

	private static final Logger logger = LoggerFactory.getLogger(WebUtils.class);
	
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
	
	public static HttpServletResponse getResponse() {
		return (HttpServletResponse)Executions.getCurrent().getNativeResponse();
	}
	
	public static String readCookieValue(String key, HttpServletRequest request) {
		if (request == null) {
			return null;
		}
		Optional<String> cookie = Arrays.stream(request.getCookies())
	      .filter(c -> key.equals(c.getName()) 
	    		  			&& "/".equals(c.getPath()) 
	    		  			&& c.getMaxAge() != 0)
	      .map(Cookie::getValue)
	      .findAny();
		
		if (cookie.isPresent()) {
			return cookie.get();
		}
		
		return null;
	}
	
	public static void setCookie(String key, 
								String value, 
								HttpServletRequest request, 
								HttpServletResponse response) {
		Optional<Cookie> cookieOpt = Arrays.stream(request.getCookies())
			      .filter(c -> key.equals(c.getName()))
			      .findAny();
		
		// delete origin cookie
		if (cookieOpt.isPresent()) {
			cookieOpt.get().setMaxAge(0);
			cookieOpt.get().setValue(null);
			response.addCookie(cookieOpt.get());
		}
		
		Cookie cookie = new Cookie(key, value);
		cookie.setPath("/");
		// set cookie expiration to one year
		cookie.setMaxAge(365 * 24 * 60 * 60);
		response.addCookie(cookie);
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
	
	public static void setSessAtribute(String atr, Object obj, HttpServletRequest request) {
		request.getSession().setAttribute(atr, obj);
	}
	
	public static Object getSessAtribute(String atr, HttpServletRequest request) {
		return request.getSession().getAttribute(atr);
	}

	public static void removeSessAtribute(String atr) {
		Execution exec = Executions.getCurrent();
		if (exec == null || exec.getSession() == null) {
			return;
		}
		exec.getSession().removeAttribute(atr);
	}
	
	public static void setDesktopAtribute(String atr, Object obj) {
		Execution exec = Executions.getCurrent();
		if (exec == null || exec.getSession() == null) {
			return;
		}
		exec.getDesktop().setAttribute(atr, obj);
	}
	
	public static Object getDesktopAtribute(String atr) {
		Execution exec = Executions.getCurrent();
		if (exec == null || exec.getSession() == null) {
			return null;
		}
		return exec.getDesktop().getAttribute(atr);
	}

	public static void removeDesktopAtribute(String atr) {
		Execution exec = Executions.getCurrent();
		if (exec == null || exec.getSession() == null) {
			return;
		}
		exec.getDesktop().removeAttribute(atr);
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
		
		Set<String> emailList = emailAddressStrToList(value);
	    
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
	 * Prevede retezec emailovych adres na Set, tzn. unikatnost.
	 * @param value
	 * @return
	 */
	public static Set<String> emailAddressStrToList(String value) {
		if (!StringUtils.hasText(value)) {
			return Collections.emptySet();
		}
		
		Set<String> ret = new HashSet<>();
		String[] emailArr = value.split(WebConstants.EMAIL_LIST_SEPARATOR);
		for (int i = 0; i < emailArr.length; i++) {
			String item = emailArr[i];
			if (StringUtils.hasText(item)) {
				ret.add(item.trim());
			}
			
		}
		return ret;
	}
	
	/**
	 * Prevede retezec emailovych adres na list.
	 * @param value
	 * @return
	 */
	public static String contactListToEmailStr(Collection<Contact> contactList) {
		if (CollectionUtils.isEmpty(contactList)) {
			return null;
		}
		
		Collection<String> emailList = new ArrayList<>(); 
		contactList.forEach(i -> emailList.add(i.getEmail1()));
		
		return emailList.stream().collect(Collectors.joining(WebConstants.EMAIL_LIST_SEPARATOR + " "));
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
		
		if (location == null || location.getUuid() == null) {
			return courseListBase;
		}
		
		return courseListBase.stream()
                .filter(line -> line.getCourseLocation() != null 
                			&& location.getUuid().toString().equals(line.getCourseLocation().getUuid().toString()))
                .collect(Collectors.toList());
	}
	
	/**
	 * For course returns email contacts of course participant representatives.
	 */
	public static List<Contact> getParticEmailAddressList(Course course
			, CourseService courseService
			, ScbUserService scbUserService) {
		if (CollectionUtils.isEmpty(course.getParticipantList())) {
			course.setParticipantList(courseService.getByCourseParticListByCourseUuid(course.getUuid(), false));
		}
		final List<Contact> ret = new ArrayList<>();
		ScbUser representative = null;
		for (CourseParticipant courseParticipant : course.getParticipantList()) {
			if (courseParticipant.getRepresentativeUuid() != null) {
				representative = scbUserService.getByUuid(courseParticipant.getRepresentativeUuid());
				if (representative != null) {
					ret.add(representative.getContact());
				}
				
			}
		}
		
		return ret;
	}
	
	/**
	 * Validation personal number CHECKSUM.
	 * Number sum without last % 11 == last number.
	 * Spočti zbytek po dělení prvních devíti číslic a čísla 11.
	 * Je-li zbytek 10, musí být poslední číslice 0.
	 * Jinak poslední číslice musí být rovna zbytku.
	 * @param birthNo
	 * @param configurationService
	 * @return
	 */
	public static boolean validateBirthNoCheckSum(String birthNo, 
			boolean checkSumBirthNumAllowed) {
		if (!StringUtils.hasText(birthNo)) {
			return true;
		}
		
		if (!checkSumBirthNumAllowed) {
			return true;
		}
		
		logger.info("validateBirthNoCheckSum():: birthNo: {}", birthNo);
		
		if (birthNo.length() != 10) {
			return false;
		}
		
		int rcWithoutLastNo = Integer.parseInt(birthNo.substring(0, 9));
		int modulo = rcWithoutLastNo%11;
		int lastNo = Integer.parseInt(String.valueOf(birthNo.charAt(birthNo.length()-1)));
		if (modulo == 10) {
			return lastNo == 0;
		} else {
			return lastNo == modulo;			
		}
	}
	
	/**
	 * Parse birth number (RČ) part to Date.
	 * Na vstupu šestimístné číslo obsahující datum narození.
	 * Pravidla:
	 * měsíc - muž  - standard, např. 01 pro leden
	 *              - standard + 20, např. 21 pro leden
	 *       - žena - standard + 50, např. 51 pro leden
	 *              - standard + 50 + 20, např. 71 pro leden
	 * @param rcDatePart
	 * @return
	 */
	public static Pair<Boolean, Date> parseRcDatePart(String rcDatePart) {
		// validace delka
		if (rcDatePart.length() != 6) {
			throw new IllegalArgumentException("Nespravna delka: " + rcDatePart.length());
		}

		// validace cislo
		try {
			Integer.parseInt(rcDatePart);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Nespravny format cisla: " + rcDatePart);
		}

		// YEAR
		Integer year = Integer.parseInt(rcDatePart.substring(0, 2));
		if (year > 40) {
			// 20 century
			year = 1900 + year;
		} else {
			// 21 century
			year = 2000 + year;
		}

		// MONTH
		Boolean male = null;
		Integer monthInt = Integer.parseInt(rcDatePart.substring(2, 4)) - 1;
		Integer monthFinal = null;
		if (monthInt >= 0 && monthInt <= 11) {
			// muz - normalni
			monthFinal = monthInt;
			male = true;
		} else if (monthInt >= 20 && monthInt <= 31) {
			// muz - pripocteni 20
			monthFinal = monthInt - 20;
			male = true;
		} else if (monthInt >= 50 && monthInt <= 61) {
			// zena - normalni (pripocteni 50)
			monthFinal = monthInt - 50;
			male = false;
		} else if (monthInt >= 70 && monthInt <= 81) {
			// zena - pripocteni 50 + 20
			monthFinal = monthInt - (50 + 20);
			male = false;
		}

		if (monthFinal == null || monthFinal < 0 || monthFinal > 11) {
			throw new IllegalArgumentException("Nespravny mesic: " + monthInt);
		}

		// DAY
		Integer day = Integer.parseInt(rcDatePart.substring(4));
		if (day < 1 || day > 31) {
			throw new IllegalArgumentException("Nespravny den: " + day);
		}

		// compose calendar and return date
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, monthFinal);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return new Pair<>(male, cal.getTime());
	}
	
	public static boolean setBirthdateByBirthNumer(String birtNumber, 
			CourseParticipant courseParticipant,
			ConfigurationService configurationService) {
		if (!StringUtils.hasText(birtNumber) || courseParticipant == null) {
			return false;
		}
		if (!courseParticipant.getContact().isCzechCitizenship()) {
			return false;
		}
		
		try {
			if (!WebUtils.validateBirthNoCheckSum(WebUtils.getBirthDateWithoutDelims(birtNumber), configurationService.isCheckSumBirthNumAllowed()))  {
				return false;
			}
			Pair<Boolean, Date> sexMaleBirthDate = WebUtils.parseRcDatePart(birtNumber.substring(0, birtNumber.indexOf("/")));
			courseParticipant.setBirthdate(sexMaleBirthDate.getValue1());
			courseParticipant.getContact().setSexMale(sexMaleBirthDate.getValue0());
			return true;
		} catch (Exception e) {
			logger.error("Exception caught for personalNumber: " + birtNumber, e);
			return false;
		}
	}
	
	/**
	 * Return birth number without delimiters.
	 * @param value
	 * @return
	 */
	public static String getBirthDateWithoutDelims(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		
		return value.replace("/", "").replace("_", "");
	}
	
	/**
	 * Validation of unique username.
	 * @param email
	 * @param fx
	 * @param scbUserService
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean validateUniqueUsernameCore(String email, final CourseApplicationVM fx, ScbUserService scbUserService) {
		if (!StringUtils.hasText(email)) {
			return true;
		}
		String emailTrimmed = email.trim();
		final ScbUser scbUser = scbUserService.getByUsername(emailTrimmed);
		if (scbUser != null) {
			String question = Labels.getLabel("msg.ui.quest.participantRepresentativeExists",new Object[] {emailTrimmed, scbUser.getContact().getCompleteName()});			
			Messagebox.show(question, Labels.getLabel("txt.ui.common.warning"), Messagebox.OK, Messagebox.EXCLAMATION, new org.zkoss.zk.ui.event.EventListener() {
			    public void onEvent(Event evt) throws InterruptedException {
			        // vymazat email
			        fx.getApplication().getCourseParticRepresentative().getContact().setEmail1("");
			        BindUtils.postNotifyChange(null, null, fx, "*");
			    }
			});
			return false;
		}
		
		return true;
	}
	
	/**
	 * Email validation.
	 * @param value
	 * @return
	 */
	public static boolean emailValid(String value) {
		if (!StringUtils.hasText(value)) {
			return false;
		}
		Pattern emailPattern = Pattern.compile(WebConstants.EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
		return emailPattern.matcher(value).matches();
	}
	
	/**
	 * Build course and lesson info used in confirmation mail and pdf.
	 * @param courseApplication
	 * @return
	 */
	public static String buildCourseApplMailCourseInfo(CourseApplication courseApplication
			, String lineSeparator) {
		if (CollectionUtils.isEmpty(courseApplication.getCourseParticipant().getCourseList())) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		Course course = courseApplication.getCourseParticipant().getCourseList().get(0);
		sb.append(lineSeparator);
		sb.append(lineSeparator);
		sb.append(Labels.getLabel("msg.ui.mail.courseApplication.text4"));
		sb.append(" ");
		// nazev a popis
		sb.append(course.getName() + (StringUtils.hasText(course.getDescription()) ? (", " + course.getDescription()) : ""));
		sb.append(" | ");
		if (course.getCourseLocation() != null) {
			// nazev a popis mista kurzu
			sb.append(Labels.getLabel("txt.ui.common.courseLocation2") + ": ");
			sb.append(course.getCourseLocation().getName() + (StringUtils.hasText(course.getCourseLocation().getDescription()) ? (", " + course.getCourseLocation().getDescription()) : ""));
		}
		if (course.getLessonList() != null && !course.getLessonList().isEmpty()) {
			// lekce
			sb.append(" | ");
			sb.append(Labels.getLabel("txt.ui.common.lessons") + ": ");
			sb.append(course.getLessonList().
				stream().map(WebUtils::getLessonToUi).
				collect(Collectors.joining(", ")));
		}
		
		return sb.toString();
	}
	
	/**
	 * Prevede lekci na format zobrazitelny v tabulce se spravnym formatem dnu a casu.
	 * @param lesson
	 * @return
	 */
	public static String getLessonToUi(Lesson lesson) {
		StringBuilder sb = new StringBuilder();
		sb.append(Converters.getEnumlabelconverter().coerceToUi(lesson.getDayOfWeek(), null, null));
		sb.append(" ");
		sb.append(Converters.getTimeconverter().coerceToUi(lesson.getTimeFrom(), null, null));
		sb.append(" - ");
		sb.append(Converters.getTimeconverter().coerceToUi(lesson.getTimeTo(), null, null));
		return sb.toString();
	}
	
	/**
	 * Transform national letters to html entites.
	 * @param value
	 * @return
	 */
	public static String escapeHtml(String value) {
		if (!StringUtils.hasText(value)) {
			return null;
		}
		
		return StringEscapeUtils.escapeHtml(value);
	}
}