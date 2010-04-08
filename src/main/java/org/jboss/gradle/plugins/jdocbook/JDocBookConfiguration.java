package org.jboss.gradle.plugins.jdocbook;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.gradle.api.Action;

/**
 * Represents jDocBook configuration.
 *
 * @author Steve Ebersole
 */
public class JDocBookConfiguration {
	public static final String DEFAULT_STANDARD_DATE_INJECTION_FORMAT = "yyyy-MM-dd";

	private String masterSourceDocumentName;
	private String masterTranslationLanguage = "en-US";
	private Set<String> translations = new HashSet<String>();
	private Profiling profiling = new Profiling();
	private Set<Format> formats = new HashSet<Format>();
	private Set<String> catalogs = new HashSet<String>();
	private Properties transformerParameters;
	private char localeSeparator = '-';
	private boolean useRelativeImageUris = true;
	private boolean autoDetectFonts = false;
	private boolean useFopFontCache = true;
	private boolean applyStandardInjectionValues = true;
	private String injectionDateFormat = DEFAULT_STANDARD_DATE_INJECTION_FORMAT;

	public String getMasterSourceDocumentName() {
		return masterSourceDocumentName;
	}

	public void setMasterSourceDocumentName(String masterSourceDocumentName) {
		this.masterSourceDocumentName = masterSourceDocumentName;
	}

	public String getMasterTranslationLanguage() {
		return masterTranslationLanguage;
	}

	public void setMasterTranslationLanguage(String masterTranslationLanguage) {
		this.masterTranslationLanguage = masterTranslationLanguage;
	}

	public Set<String> getTranslations() {
		return translations;
	}

	public Profiling getProfiling() {
		return profiling;
	}

	public Set<Format> getFormats() {
		return formats;
	}

    public JDocBookConfiguration format(Format format) {
		formats.add( format );
        return this;
    }

	public Set<String> getCatalogs() {
		return catalogs;
	}

    public JDocBookConfiguration catalog(String catalog) {
        catalogs.add( catalog );
		return this;
    }

	public Properties getTransformerParameters() {
		return transformerParameters;
	}

	public void setTransformerParameters(Properties transformerParameters) {
		this.transformerParameters = transformerParameters;
	}

	public char getLocaleSeparator() {
		return localeSeparator;
	}

	public void setLocaleSeparator(char localeSeparator) {
		this.localeSeparator = localeSeparator;
	}

	public boolean isUseRelativeImageUris() {
		return useRelativeImageUris;
	}

	public void setUseRelativeImageUris(boolean useRelativeImageUris) {
		this.useRelativeImageUris = useRelativeImageUris;
	}

	public boolean isAutoDetectFonts() {
		return autoDetectFonts;
	}

	public void setAutoDetectFonts(boolean autoDetectFonts) {
		this.autoDetectFonts = autoDetectFonts;
	}

	public boolean isUseFopFontCache() {
		return useFopFontCache;
	}

	public void setUseFopFontCache(boolean useFopFontCache) {
		this.useFopFontCache = useFopFontCache;
	}

	public boolean isApplyStandardInjectionValues() {
		return applyStandardInjectionValues;
	}

	public void setApplyStandardInjectionValues(boolean applyStandardInjectionValues) {
		this.applyStandardInjectionValues = applyStandardInjectionValues;
	}

	public String getInjectionDateFormat() {
		return injectionDateFormat;
	}

	public void setInjectionDateFormat(String injectionDateFormat) {
		this.injectionDateFormat = injectionDateFormat;
	}
}
