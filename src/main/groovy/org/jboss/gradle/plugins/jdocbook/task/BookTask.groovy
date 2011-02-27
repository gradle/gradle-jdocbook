package org.jboss.gradle.plugins.jdocbook.task

import org.gradle.api.DefaultTask
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.gradle.api.tasks.OutputDirectory

/**
 *
 * @author: Strong Liu
 */
abstract class BookTask extends DefaultTask {
	Book book
	@Input
	@Optional
	String lang

	def configure(Book book, String lang = null) {
		this.book = book
		if ( lang == null ) this.lang = book.masterLanguage
		else this.lang = lang
	}

	public BookTask() {
		setGroup(CreateTasksPerBookAction.JDOCBOOK_TASK_GROUP)
		onlyIf spec
	}

	@Input
	public String getBookName() {
		return book.name
	}

	protected File existsOrNull(File file) {
		return file.exists() ? file : null
	}

	public Locale getLanguage() {
		return book.environment.getLanguage(lang)
	}

	//task should on runs on a concrete book
	Spec<BookTask> spec = new Spec<BookTask>() {
		@Override
		boolean isSatisfiedBy(BookTask task) {
			return task.book != null && task.book.concrete
		}
	}
}
