package org.jboss.gradle.plugins.jdocbook.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.jboss.jdocbook.Profiling
import org.jboss.jdocbook.profile.ProfilingSource
import org.jboss.jdocbook.util.TranslationUtils

/**
 * Task for performing DocBook profiling
 *
 * @author Steve Ebersole
 */
//@SuppressWarnings({ "UnusedDeclaration" })
public class ProfileTask extends DefaultTask {
	private static final Logger log = Logging.getLogger(ProfileTask.class);

	private JDocBookPlugin plugin;
	private String language;
	private ProfilingSourceImpl profilingSource;
	private Book book

	public void configure(Book book, String language) {
		this.book = book
		this.language = language;
		this.profilingSource = new ProfilingSourceImpl();
		this.plugin = project.plugins.getPlugin(JDocBookPlugin)
	}

	@Input
	public String getLanguage() {
		return language;
	}

//	@Input

	public Profiling getProfiling() {
		return book.configuration.profiling
	}
// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// it is temporary that these flattened attributes are marked as @Input rather than the getProfiling() attribute
// see http://jira.codehaus.org/browse/GRADLE-970 for details

	@Input
	public boolean isProfilingEnabled() {
		return getProfiling().isEnabled();
	}

	@Input
	@Optional
	public String isProfilingAttributeName() {
		return getProfiling().getAttributeName();
	}

	@Input
	@Optional
	public String isProfilingAttributeValue() {
		return getProfiling().getAttributeValue();
	}
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@InputDirectory
	public File getProfileInputDirectory() {
		// todo : this is slightly different between the case of master and translation.
		// 		for master the more correct thing is the list of files
		//		for translation, the translation output dir is probably enough.
//		return getLanguage().equals( plugin.getConfiguration().getMasterLanguage() )
//				? plugin.getDirectoryLayout().getMasterSourceDirectory()
//				: plugin.getDirectoryLayout().getTranslationDirectory( getLanguage() );
		if ( getLanguage() == book.masterLanguage ) {
			return book.sourceSet.base()
		}
		else {
			return book.sourceSet.work(getLanguage())
		}
	}

	@OutputDirectory
	public File getProfileOutputDirectory() {
		return project.file(book.sourceSet.profile(getLanguage()))
	}

	@TaskAction
//	@SuppressWarnings({ "UnusedDeclaration" })
	public void profile() {
		log.lifecycle("profiling {} into {}", getLanguage(), getProfileOutputDirectory());
		book.componentRegistry.profiler.profile(profilingSource);
	}

	private class ProfilingSourceImpl implements ProfilingSource {
		public Locale getLanguage() {
			return TranslationUtils.parse(language, book.localeSeparator)
		}

		public File resolveDocumentFile() {
			return new File(getProfileInputDirectory(),book.masterSourceDocumentName);
		}

		public File resolveProfiledDocumentFile() {
			return new File(getProfileOutputDirectory(), book.masterSourceDocumentName);
		}
	}
}
