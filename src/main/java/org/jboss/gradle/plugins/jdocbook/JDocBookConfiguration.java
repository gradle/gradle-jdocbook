package org.jboss.gradle.plugins.jdocbook;

import java.util.*;

import groovy.util.ObservableList;
import org.gradle.api.Action;
import org.gradle.api.Project;

/**
 * Represents jDocBook configuration.
 *
 * @author Steve Ebersole
 */
public class JDocBookConfiguration {
	public static final String DEFAULT_STANDARD_DATE_INJECTION_FORMAT = "yyyy-MM-dd";

	private String masterTranslationLanguage = "en-US";
	private final Set<String> translations = new HashSet<String>();
	private String masterSourceDocumentName;
	private String[] catalogs;
	private Properties transformerParameters;
	private char localeSeparator = '-';
	private boolean useRelativeImageUris = true;
	private boolean autoDetectFonts = false;
	private boolean useFopFontCache = true;
	private boolean applyStandardInjectionValues = true;
	private String injectionDateFormat = DEFAULT_STANDARD_DATE_INJECTION_FORMAT;
    private final Action<String> translationListener;

    public JDocBookConfiguration(Action<String> translationListener) {
        this.translationListener = translationListener;
    }

    public String[] getCatalogs() {
		return catalogs;
	}

	public void setCatalogs(String[] catalogs) {
		this.catalogs = catalogs;
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

	public String getMasterTranslationLanguage() {
		return masterTranslationLanguage;
	}

	public void setMasterTranslationLanguage(String masterTranslationLanguage) {
		this.masterTranslationLanguage = masterTranslationLanguage;
	}

	public Collection<String> getTranslations() {
		return translations;
	}

    public JDocBookConfiguration translation(String translation) {
        if (translations.add(translation)) {
            translationListener.execute(translation);
        }
        return this;
    }

	public String getMasterSourceDocumentName() {
		return masterSourceDocumentName;
	}

	public void setMasterSourceDocumentName(String masterSourceDocumentName) {
		this.masterSourceDocumentName = masterSourceDocumentName;
	}
}
