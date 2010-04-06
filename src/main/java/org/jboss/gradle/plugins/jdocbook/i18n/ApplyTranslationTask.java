package org.jboss.gradle.plugins.jdocbook.i18n;

import java.io.File;
import java.util.Locale;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jboss.gradle.plugins.jdocbook.MasterSourceFileResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task to apply GNU <a href="http://www.gnu.org/software/gettext/">gettext</a>-based translations to generate
 * translated DocBook sources from the master language source and the translation PO files (see
 * <a href="http://en.wikipedia.org/wiki/GNU_gettext">http://en.wikipedia.org/wiki/GNU_gettext</a> for discussion).
 *
 * @author Steve Ebersole
 */
public class ApplyTranslationTask extends DefaultTask {
	private static final Logger log = LoggerFactory.getLogger( ApplyTranslationTask.class );

	// See comments in JDocBookPlugin#apply...

//	private final Locale translationLanguage;
//	private final TranslationEnvironment environment;
//	private final MasterSourceFileResolver masterSourceFileResolver;
//
//	@OutputDirectory
//	public File getTranslationOutputDirectory() {
//		return translationOutputDirectory;
//	}
//
//	public void setTranslationOutputDirectory(File translationOutputDirectory) {
//		this.translationOutputDirectory = translationOutputDirectory;
//	}
//
//	private File masterSourceDirectory;

	@TaskAction
	public void apply() {
//		log.trace( "Starting translation task [{}]", translationLanguage );

		// See comments in JDocBookPlugin#apply...
	}

}
