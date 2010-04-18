package org.jboss.gradle.plugins.jdocbook;

import java.io.File;
import java.util.Locale;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.jboss.jdocbook.translate.TranslationSource;

/**
 * Helper for translation-based tasks
 *
 * @author Steve Ebersole
 */
public class AbstractTranslationTask extends DefaultTask {
	private JDocBookPlugin plugin;
	private String translationLanguage;
	private TranslationSourceImpl translationSource;

	public void configure(JDocBookPlugin plugin, String translationLanguage) {
		this.plugin = plugin;
		this.translationLanguage = translationLanguage;
		this.translationSource = new TranslationSourceImpl();
	}

	protected JDocBookPlugin getPlugin() {
		return plugin;
	}

	protected TranslationSourceImpl getTranslationSource() {
		return translationSource;
	}

	@Input
	public String getTranslationLanguage() {
		return translationLanguage;
	}

	@InputDirectory
	public File getTranslationSourceDirectory() {
		return plugin.getDirectoryLayout().getTranslationSourceDirectory( getTranslationLanguage() );
	}

	@OutputDirectory
	public File getTranslationOutputDirectory() {
		return plugin.getDirectoryLayout().getTranslationDirectory( getTranslationLanguage() );
	}

	private class TranslationSourceImpl implements TranslationSource {
		public Locale getLanguage() {
			return plugin.fromLanguageString( translationLanguage );
		}

		public File resolvePoDirectory() {
			return getTranslationSourceDirectory();
		}

		public File resolveTranslatedXmlDirectory() {
			return getTranslationOutputDirectory();
		}
	}
}
