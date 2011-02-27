package org.jboss.gradle.plugins.jdocbook.task;


import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.jboss.jdocbook.translate.TranslationSource
import org.gradle.api.tasks.SkipWhenEmpty

/**
 * Helper for translation-based tasks
 *
 * @author Steve Ebersole
 */
public class AbstractTranslationTask extends BookTask implements TranslationSource {

	@OutputDirectory
	public File resolveTranslatedXmlDirectory() {
		book.environment.getWorkDirPerLang(lang)
	}

	@InputDirectory
	@SkipWhenEmpty
	public File resolvePoDirectory() {
		book.environment.getSourceDirPerLang(lang)
	}

}
