package org.jboss.gradle.plugins.jdocbook.task

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task for performing POT synchronization
 *
 * @author Steve Ebersole
 * @author Strong Liu
 */
@SuppressWarnings(["UnusedDeclaration"])
public class SynchronizePoTask extends AbstractTranslationTask {
	final static Logger log = Logging.getLogger(SynchronizePoTask);

	@InputDirectory
	public File getTranslationPoDirectory() {
		resolvePoDirectory()
	}

	@TaskAction
	public void synchronize() {
		log.lifecycle("Starting PO synchronization [{}]", lang);
		book.componentRegistry.poSynchronizer.synchronizePo(this);
	}
}