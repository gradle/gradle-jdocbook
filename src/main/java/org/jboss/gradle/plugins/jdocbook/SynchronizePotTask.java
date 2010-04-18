package org.jboss.gradle.plugins.jdocbook;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * Task for performing POT synchronization
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "UnusedDeclaration" })
public class SynchronizePotTask extends DefaultTask {
	private static final Logger log = Logging.getLogger( TranslateTask.class );

	private JDocBookPlugin plugin;

	public void configure(JDocBookPlugin plugin) {
		this.plugin = plugin;
	}

	@InputDirectory
	public File getSourceDirectory() {
		return plugin.getDirectoryLayout().getMasterSourceDirectory();
	}

	@OutputDirectory
	public File getPotDirectory() {
		return plugin.getDirectoryLayout().getPotSourceDirectory();
	}

	@TaskAction
	public void synchronizePot() {
		log.lifecycle( "Starting POT synchronization" );
		plugin.getComponentRegistry().getPotSynchronizer().synchronizePot();
	}
}
