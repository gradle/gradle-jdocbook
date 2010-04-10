package org.jboss.gradle.plugins.jdocbook.translate;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin;
import org.jboss.jdocbook.TranslationSource;
import org.jboss.jdocbook.util.TranslationUtils;

import java.io.File;
import java.util.Locale;
import java.util.Set;

/**
 * Task to apply GNU <a href="http://www.gnu.org/software/gettext/">gettext</a>-based translations to generate
 * translated DocBook sources from the master language source and the translation PO files (see
 * <a href="http://en.wikipedia.org/wiki/GNU_gettext">http://en.wikipedia.org/wiki/GNU_gettext</a> for discussion).
 *
 * @author Steve Ebersole
 */
public class JDocBookTranslate extends DefaultTask {
	private static final Logger log = Logging.getLogger( JDocBookTranslate.class );

	private JDocBookPlugin plugin;
	private String translationLanguage;
	private TranslationSourceImpl translationSource;

	public void configure(JDocBookPlugin plugin, String translationLanguage) {
		this.plugin = plugin;
		this.translationLanguage = translationLanguage;
		this.translationSource = new TranslationSourceImpl();
	}

	@Input
	public String getTranslationLanguage() {
		return translationLanguage;
	}

	@InputFiles
	@SuppressWarnings({ "UnusedDeclaration" })
	public Set<File> getMasterSourceFiles() {
		return plugin.getMasterSourceFileResolver().getFiles();
	}

	@InputDirectory
	public File getTranslationSourceDirectory() {
		return plugin.getDirectoryLayout().getTranslationSourceDirectory( getTranslationLanguage() );
	}

	@OutputDirectory
	public File getTranslationOutputDirectory() {
		return plugin.getDirectoryLayout().getTranslationDirectory( getTranslationLanguage() );
	}

	@TaskAction
	@SuppressWarnings({ "UnusedDeclaration" })
	public void translate() {
		log.lifecycle( "translating {} into {}", translationLanguage, getTranslationOutputDirectory() );
		plugin.getComponentFactory().getTranslator().translate( translationSource );
	}

	private class TranslationSourceImpl implements TranslationSource {
		public Locale getLanguage() {
			return TranslationUtils.parse( getTranslationLanguage(), plugin.getConfiguration().getLocaleSeparator() );
		}

		public File resolvePoDirectory() {
			return getTranslationSourceDirectory();
		}

		public File resolveTranslatedXmlDirectory() {
			return getTranslationOutputDirectory();
		}
	}
}
