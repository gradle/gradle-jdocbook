package org.jboss.gradle.plugins.jdocbook.profile;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskInputs;
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin;
import org.jboss.gradle.plugins.jdocbook.Profiling;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class JDocBookProfile extends DefaultTask {
	private static final Logger log = Logging.getLogger( JDocBookProfile.class );

	private JDocBookPlugin plugin;
	private String language;

	public void configure(JDocBookPlugin plugin, String language) {
		this.plugin = plugin;
		this.language = language;
	}

    @Input
    public String getLanguage() {
        return language;
    }

	@Input
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
	public void profile() {
        log.lifecycle( "profiling {} into {}", getLanguage(), getProfileOutputDirectory() );

		// TODO : hook into jDocBook profile processor
		// 		after we have split that out to work on individual languages (it currently handles them all at once
		//		since that is the model used in the maven plugin)
	}
}
