package org.jboss.gradle.plugins.jdocbook.tasks

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
	private static final Logger log = Logging.getLogger(SynchronizePoTask.class);

	@InputDirectory
	public File getTranslationPoDirectory() {
		project.file(book.sourceSet.lang(getTranslationLanguage()))
	}

	@TaskAction
	public void synchronize() {
		log.lifecycle("Starting PO synchronization [{}]", getTranslationLanguage());
		book.componentRegistry.poSynchronizer.synchronizePo(this);
	}
}