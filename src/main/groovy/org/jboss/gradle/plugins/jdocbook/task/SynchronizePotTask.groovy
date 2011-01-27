package org.jboss.gradle.plugins.jdocbook.task

import org.gradle.api.DefaultTask
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
	private static final Logger log = Logging.getLogger( TranslateTask.class );


	public void configure(Book book) {
		this.book = book;
	}

	@InputDirectory
	public File getSourceDirectory() {
		return project.file(book.sourceSet.base())
	}

	@OutputDirectory
	public File getPotDirectory() {
		return project.file(book.sourceSet.pot())
	}

	@TaskAction
	public void synchronizePot() {
		log.lifecycle( "Starting POT synchronization" );
		book.componentRegistry.potSynchronizer.synchronizePot()
	}
}

