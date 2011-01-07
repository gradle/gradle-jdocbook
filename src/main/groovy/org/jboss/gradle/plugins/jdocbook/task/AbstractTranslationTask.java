package org.jboss.gradle.plugins.jdocbook.tasks;

import java.io.File;
import java.util.Locale;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.jboss.gradle.plugins.jdocbook.book.Book;
import org.jboss.jdocbook.translate.TranslationSource;
import org.jboss.jdocbook.util.TranslationUtils;

/**
 * Helper for translation-based tasks
 *
 * @author Steve Ebersole
 */
public class AbstractTranslationTask extends BookTask implements TranslationSource {
	private String translationLanguage;


	public void configure(Book book, String translationLanguage) {
		setBook( book );
		this.translationLanguage = translationLanguage;

	}

	@Input
	public String getTranslationLanguage() {
		return translationLanguage;
	}

	@InputDirectory
	public File resolvePoDirectory() {
		return getProject().file( getBook().getSourceSet().lang( getTranslationLanguage() ) );
	}

	@OutputDirectory
	public File resolveTranslatedXmlDirectory() {
		return getProject().file( getBook().getSourceSet().work( getTranslationLanguage() ) );
	}

	public Locale getLanguage() {
		return TranslationUtils.parse( translationLanguage, getBook().getConfiguration().getLocaleSeparator() );
	}

}
