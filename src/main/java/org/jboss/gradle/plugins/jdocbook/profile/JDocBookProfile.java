package org.jboss.gradle.plugins.jdocbook.profile;

import java.io.File;
import java.util.Locale;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin;
import org.jboss.jdocbook.Profiling;
import org.jboss.jdocbook.ProfilingSource;
import org.jboss.jdocbook.util.TranslationUtils;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class JDocBookProfile extends DefaultTask {
	private static final Logger log = Logging.getLogger( JDocBookProfile.class );

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

	@Input
	@SuppressWarnings({ "UnusedDeclaration" })
	public Profiling getProfiling() {
		return plugin.getConfiguration().getProfiling();
	}

	@InputDirectory
	public File getProfileInputDirectory() {
		// todo : this is slightly different between the case of master and translation.
		// 		for master the more correct thing is the list of files
		//		for translation, the translation output dir is probably enough.
		return getLanguage().equals( plugin.getConfiguration().getMasterTranslationLanguage() )
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
		plugin.getComponentFactory().getProfiler().profile( profilingSource );
	}

	private class ProfilingSourceImpl implements ProfilingSource {
		public Locale getLanguage() {
			return TranslationUtils.parse(
					JDocBookProfile.this.getLanguage(),
					plugin.getConfiguration().getLocaleSeparator()
			);
		}

		public File resolveDocumentFile() {
			return new File( getProfileInputDirectory(), plugin.getConfiguration().getMasterSourceDocumentName() );
		}

		public File resolveProfiledDocumentFile() {
			return new File( getProfileOutputDirectory(), plugin.getConfiguration().getMasterSourceDocumentName() );
		}
	}
}
