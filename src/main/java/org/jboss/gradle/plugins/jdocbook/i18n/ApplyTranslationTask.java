package org.jboss.gradle.plugins.jdocbook.i18n;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jboss.gradle.plugins.jdocbook.MasterSourceFileResolver;

import java.io.File;

/**
 * Task to apply GNU <a href="http://www.gnu.org/software/gettext/">gettext</a>-based translations to generate
 * translated DocBook sources from the master language source and the translation PO files (see
 * <a href="http://en.wikipedia.org/wiki/GNU_gettext">http://en.wikipedia.org/wiki/GNU_gettext</a> for discussion).
 *
 * @author Steve Ebersole
 */
public class ApplyTranslationTask extends DefaultTask {
	private static final Logger log = Logging.getLogger( ApplyTranslationTask.class );

	private String translationLanguage;
	private MasterSourceFileResolver masterSourceFileResolver;
    private File translationOutputDirectory;

    @Input
    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    @OutputDirectory
	public File getTranslationOutputDirectory() {
		return translationOutputDirectory;
	}

	public void setTranslationOutputDirectory(File translationOutputDirectory) {
		this.translationOutputDirectory = translationOutputDirectory;
	}

	private File masterSourceDirectory;

	@TaskAction
	public void apply() {
//		log.trace( "Starting translation task [{}]", translationLanguage );

		// See comments in JDocBookPlugin#apply...

        log.lifecycle("translating {}", translationLanguage);
        log.lifecycle("into {}", translationOutputDirectory);
	}

}
