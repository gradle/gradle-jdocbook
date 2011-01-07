package org.jboss.gradle.plugins.jdocbook.book

import java.text.SimpleDateFormat
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil
import org.jboss.jdocbook.Configuration
import org.jboss.jdocbook.Profiling
import org.jboss.jdocbook.ValueInjection

/**
 *
 * @author: Strong Liu
 */
class BookConfiguration implements Configuration {
	Project project
	BookConfiguration(Project project){
		this.project = project
	}
	// IMAGE URI HANDLING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	boolean useRelativeImageUris = true;

	@Override
	boolean isUseRelativeImageUris() { useRelativeImageUris }
	// AUTO-DETECT FONTS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	boolean autoDetectFonts = false;

	@Override
	boolean isAutoDetectFontsEnabled() { return autoDetectFonts }
	// FONT CACHE ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	boolean useFopFontCache = true;

	@Override
	boolean isUseFopFontCacheEnabled() { return useFopFontCache }
	// LOCALE SEPARATOR ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	char localeSeparator = '-';
	Map<String, String> transformerParameters = [:]
	// VALUE INJECTIONS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	def applyStandardInjectionValues = true;
	def injectionDateFormat = "yyyy-MM-dd"
	LinkedHashSet<ValueInjection> valueInjections = new LinkedHashSet<ValueInjection>();
	@Override
	LinkedHashSet<ValueInjection> getValueInjections() {
		if ( applyStandardInjectionValues ) {
			valueInjections.add(new ValueInjection("version", project.getVersion().toString()));
			SimpleDateFormat dateFormat = new SimpleDateFormat(injectionDateFormat);
			valueInjections.add(new ValueInjection("today", dateFormat.format(new Date())));
		}
		return valueInjections;
	}
	/**
	 * Allow configuration by closure
	 *
	 * @param closure The config closure
	 */
	public void valueInjection(Closure closure) {
		valueInjections << ConfigureUtil.configure(closure, new ValueInjection())
	}

	/**
	 * Allow configuration by map
	 *
	 * @param settings The user settings
	 */
	public void valueInjection(Map<String, ?> settings) {
		valueInjections << ConfigureUtil.configureByMap(settings, new ValueInjection())
	}

	LinkedHashSet<String> catalogs = new LinkedHashSet<String>();

	public void catalog(String catalog) {
		catalogs.add(catalog);
	}

	@Override
	LinkedHashSet<String> getCatalogs() {
		return catalogs
	}

	def profiling = new Profiling()

	@Override
	Profiling getProfiling() {
		return profiling
	}
	/**
	 * Allow config by closure
	 *
	 * @param closure The config closure
	 */
	def profiling(Closure closure) {
		ConfigureUtil.configure(closure, profiling);
	}

	/**
	 * Allow config by map
	 *
	 * @param settings The user settings.
	 */
	def profiling(Map<String, ?> settings) {
		ConfigureUtil.configureByMap(settings, profiling);
	}

	@Override
	String getDocBookVersion() {
		return null
	}

}
