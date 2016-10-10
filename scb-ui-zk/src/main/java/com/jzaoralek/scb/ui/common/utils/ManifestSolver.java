package com.jzaoralek.scb.ui.common.utils;

import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

import com.jzaoralek.scb.ui.common.WebConstants;

/**
 * Nacitani atributu z manifestu aplikace.
 */
public class ManifestSolver {

	private static final Logger LOG = LoggerFactory.getLogger(ManifestSolver.class);

	private static Attributes mainAttributes;

	private ManifestSolver() {}

	static {
		try {
			Manifest manifest = null;
			// Vytahneme bud z kontextu nebo podle ciloveho class souboru
			Execution exec = Executions.getCurrent();
			if (exec != null) {
				ServletContext context = exec.getDesktop().getWebApp().getServletContext();
				if (context != null) {
					try {
						InputStream is = context.getResourceAsStream(WebConstants.APP_MANIFEST);
						manifest = new Manifest(is);
					} catch (Exception e) {
						LOG.error("Unexpected exception caught.", e);
					}
				}
			}
			if (manifest == null) {
				try (InputStream is = ManifestSolver.class.getClassLoader().getResourceAsStream(
																	WebConstants.APP_MANIFEST)) {
					manifest = new Manifest(is);
				}
			}
			mainAttributes = manifest.getMainAttributes();
			LOG.info("Manifest loaded: " + WebConstants.APP_MANIFEST);
		} catch (Throwable e) {
			LOG.error("Nelze nacist manifest soubor ulozeny v: " + WebConstants.APP_MANIFEST, e);
		}

	}

	/**
	 * Nacteni atributu z manifestu.
	 * @param name Nazev atributu
	 * @return Hodnota parametru, pokud neexistuje nebo je prazdny, tak null
	 */
	public static String getMainAttributeValue(String name) {
		if (mainAttributes == null) {
			return null;
		} else {
			return mainAttributes.getValue(name);
		}
	}
}