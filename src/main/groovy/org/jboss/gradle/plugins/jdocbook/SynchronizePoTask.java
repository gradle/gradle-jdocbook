package org.jboss.gradle.plugins.jdocbook;

import java.io.File;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * Task for performing POT synchronization
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "UnusedDeclaration" })
public class SynchronizePoTask extends AbstractTranslationTask {
	private static final Logger log = Logging.getLogger( SynchronizePoTask.class );

	@InputDirectory
	public File getTranslationPoDirectory() {
		return getPlugin().getDirectoryLayout().getTranslationSourceDirectory( getTranslationLanguage() );
	}

	@TaskAction
	public void synchronize() {
		log.lifecycle( "Starting PO synchronization [{}]", getTranslationLanguage() );
		getPlugin().getComponentRegistry().getPoSynchronizer().synchronizePo( getTranslationSource() );
	}
}
