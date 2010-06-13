package org.jboss.gradle.plugins.jdocbook;

import java.io.File;
import java.util.Locale;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jboss.jdocbook.Profiling;
import org.jboss.jdocbook.profile.ProfilingSource;

/**
 * Task for performing DocBook profiling
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "UnusedDeclaration" })
public class ProfileTask extends DefaultTask {
	private static final Logger log = Logging.getLogger( ProfileTask.class );

	private JDocBookPlugin plugin;
	private String language;
	private ProfilingSourceImpl profilingSource;

	public void configure(JDocBookPlugin plugin, String language) {
		this.plugin = plugin;
		this.language = language;
		this.profilingSource = new ProfilingSourceImpl();
	}

	@Input
	public String getLanguage() {
		return language;
	}

//	@Input
	public Profiling getProfiling() {
		return plugin.getConfiguration().getProfiling();
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
		return getLanguage().equals( plugin.getConfiguration().getMasterLanguage() )
				? plugin.getDirectoryLayout().getMasterSourceDirectory()
				: plugin.getDirectoryLayout().getTranslationDirectory( getLanguage() );
	}

	@OutputDirectory
	public File getProfileOutputDirectory() {
		return plugin.getDirectoryLayout().getProfilingDirectory( getLanguage() );
	}

	@TaskAction
	@SuppressWarnings({ "UnusedDeclaration" })
	public void profile() {
		log.lifecycle( "profiling {} into {}", getLanguage(), getProfileOutputDirectory() );
		plugin.getComponentRegistry().getProfiler().profile( profilingSource );
	}

	private class ProfilingSourceImpl implements ProfilingSource {
		public Locale getLanguage() {
			return plugin.fromLanguageString( language );
		}

		public File resolveDocumentFile() {
			return new File( getProfileInputDirectory(), plugin.getConfiguration().getMasterSourceDocumentName() );
		}

		public File resolveProfiledDocumentFile() {
			return new File( getProfileOutputDirectory(), plugin.getConfiguration().getMasterSourceDocumentName() );
		}
	}
}
