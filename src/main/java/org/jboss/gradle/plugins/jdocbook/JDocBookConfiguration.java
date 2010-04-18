package org.jboss.gradle.plugins.jdocbook;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import groovy.lang.Closure;
import org.gradle.util.ConfigureUtil;
import org.jboss.jdocbook.Profiling;
import org.jboss.jdocbook.ValueInjection;
import org.jboss.jdocbook.render.FormatOptions;

/**
 * Represents user jDocBook configuration.
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "UnusedDeclaration" })
public class JDocBookConfiguration {
	public static final String DEFAULT_STANDARD_DATE_INJECTION_FORMAT = "yyyy-MM-dd";


	// MASTER DOCUMENT ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private String masterSourceDocumentName = "book.xml";

	public String getMasterSourceDocumentName() {
		return masterSourceDocumentName;
	}


	// MASTER LANGUAGE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private String masterLanguage = "en-US";

	public String getMasterLanguage() {
		return masterLanguage;
	}


	// TRANSLATIONS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private Set<String> translations = new HashSet<String>();

	public Set<String> getTranslations() {
		return translations;
	}

	/**
	 * Allow adding them one by one
	 *
	 * @param translation The translation to add
	 */
	public void translation(String translation) {
		translations.add( translation );
	}

	/**
	 * Also, allow adding them all at once
	 *
	 * @param translations An array of translation languages.
	 */
	public void translations(String[] translations) {
		this.translations.addAll( Arrays.asList( translations ) );
	}


	// PROFILING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private Profiling profiling = new Profiling();

	public Profiling getProfiling() {
		return profiling;
	}

	/**
	 * Allow config by closure
	 *
	 * @param closure The config closure
	 */
	public void profiling(Closure closure) {
		ConfigureUtil.configure( closure, profiling );
	}

	/**
	 * Allow config by map
	 *
	 * @param settings The user settings.
	 */
	public void profiling(Map<String,?> settings) {
		ConfigureUtil.configureByMap( settings, profiling );
	}


	// FORMATS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private Set<FormatOptions> formats = new HashSet<FormatOptions>();

	public Set<FormatOptions> getFormats() {
		return formats;
	}

	/**
	 * Allow config by Closure
	 *
	 * @param closure The config closure
	 */
	public void format(Closure closure) {
		final FormatOptionsImpl formatOptions = new FormatOptionsImpl();
		ConfigureUtil.configure( closure, formatOptions );
		formats.add( formatOptions );
	}

	/**
	 * Allow config by map
	 *
	 * @param settings The user settings
	 */
	public void format(Map<String,?> settings) {
		final FormatOptionsImpl formatOptions = new FormatOptionsImpl();
		ConfigureUtil.configureByMap( settings, formatOptions );
		formats.add( formatOptions );
	}

	public static class FormatOptionsImpl implements FormatOptions {
		private String name;
		private String finalName;
		private String stylesheet;

		public String getName() {
			return name;
		}

		public String getTargetFinalName() {
			return finalName;
		}

		public String getStylesheetResource() {
			return stylesheet;
		}
	}


	// CATALOGS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private LinkedHashSet<String> catalogs = new LinkedHashSet<String>();

	public LinkedHashSet<String> getCatalogs() {
		return catalogs;
	}

	public void catalog(String catalog) {
		catalogs.add( catalog );
	}


	// LOCALE SEPARATOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private char localeSeparator = '-';

	public char getLocaleSeparator() {
		return localeSeparator;
	}


	// IMAGE URI HANDLING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private boolean useRelativeImageUris = true;

	public boolean isUseRelativeImageUris() {
		return useRelativeImageUris;
	}


	// AUTO-DETECT FONTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private boolean autoDetectFonts = false;

	public boolean isAutoDetectFonts() {
		return autoDetectFonts;
	}


	// FONT CACHE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private boolean useFopFontCache = true;

	public boolean isUseFopFontCache() {
		return useFopFontCache;
	}

	// VALUE INJECTIONS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private LinkedHashSet<ValueInjection> valueInjections = new LinkedHashSet<ValueInjection>();

	public LinkedHashSet<ValueInjection> getValueInjections() {
		return valueInjections;
	}

	/**
	 * Allow configuration by closure
	 *
	 * @param closure The config closure
	 */
	public void valueInjection(Closure closure) {
		ValueInjection injection = new ValueInjection();
		ConfigureUtil.configure( closure, injection );
		valueInjections.add( injection );
	}

	/**
	 * Allow configuration by map
	 *
	 * @param settings The user settings
	 */
	public void valueInjection(Map<String,?> settings) {
		ValueInjection injection = new ValueInjection();
		ConfigureUtil.configureByMap( settings, injection );
		valueInjections.add( injection );
	}

	private boolean applyStandardInjectionValues = true;

	public boolean isApplyStandardInjectionValues() {
		return applyStandardInjectionValues;
	}

	private String injectionDateFormat = DEFAULT_STANDARD_DATE_INJECTION_FORMAT;

	public String getInjectionDateFormat() {
		return injectionDateFormat;
	}


	// TRANSFORMER PARAMETERS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private Properties transformerParameters;

	public Properties getTransformerParameters() {
		return transformerParameters;
	}

}
