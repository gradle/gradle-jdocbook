package org.jboss.gradle.plugins.jdocbook.task

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jboss.gradle.plugins.jdocbook.book.Book

/**
 * Task for performing POT synchronization
 *
 * @author Steve Ebersole
 * @author Strong Liu
 */
@SuppressWarnings([ "UnusedDeclaration" ])
public class SynchronizePotTask extends BookTask {
	final static Logger log = Logging.getLogger( SynchronizePotTask );

	@OutputDirectory
	public File getPotDirectory() {
		book.environment.potDirectory
	}

	@TaskAction
	public void synchronizePot() {
		log.lifecycle( "Starting POT synchronization" );
		book.componentRegistry.potSynchronizer.synchronizePot()
	}
}

