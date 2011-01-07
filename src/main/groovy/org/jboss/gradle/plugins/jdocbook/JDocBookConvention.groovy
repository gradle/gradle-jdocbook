package org.jboss.gradle.plugins.jdocbook

import org.gradle.api.Project
import org.gradle.api.internal.ClassGenerator
import org.gradle.listener.ActionBroadcast
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.jboss.gradle.plugins.jdocbook.book.BooksContainer

/**
 * @author Strong Liu
 */
class JDocBookConvention {
	Project project;
	BooksContainer books;
	ClassGenerator generator
	ActionBroadcast<Book> configBookActions = new ActionBroadcast<Book>();
	ActionBroadcast<JDocBookConvention> conventionActions = new ActionBroadcast<JDocBookConvention>()
	JDocBookConvention(Project project) {
		this.project = project
		this.generator = project.services.get(ClassGenerator)
		this.books = generator.newInstance(BooksContainer, this)
	}

	def jdocbook(Closure closure) {
		books.configure closure
		books.all.each { book ->
			configBookActions.execute(book)
		}
		conventionActions.execute this
	}


}
