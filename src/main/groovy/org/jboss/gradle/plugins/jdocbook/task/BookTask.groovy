package org.jboss.gradle.plugins.jdocbook.tasks

import org.gradle.api.DefaultTask
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.gradle.api.specs.Spec
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin

/**
 *
 * @author: Strong Liu
 */
class BookTask extends DefaultTask {
	Book book

	public BookTask() {
		setGroup(JDocBookPlugin.JDOCBOOK_TASK_GROUP)
		onlyIf spec
	}
	//task should on runs on a concrete book
	Spec<BookTask> spec = new Spec<BookTask>() {
		@Override
		boolean isSatisfiedBy(BookTask task) {
			return task.book!=null && task.book.concrete
		}
	}
}
