package com.jzaoralek.scb.dataservice.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;


/** Pomocne funkce pro vyjimky.
 */
public final class ExcUtil {

	private ExcUtil() { }

	/** Ziskani textove reprezentace stacktrace. */
	public static CharSequence traceMessage(Throwable th) {
		Objects.requireNonNull(th);
		StringWriter sw = new StringWriter(1024);
		th.printStackTrace(new PrintWriter(sw));
		return sw.getBuffer();
	}
}
