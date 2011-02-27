package org.jboss.gradle.plugins.jdocbook

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.listener.ActionBroadcast
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.gradle.util.ConfigureUtil

/**
 * @author Strong Liu
 */
class JDocBookConvention {
	Logger log = Logging.getLogger(JDocBookConvention);
	final NamedDomainObjectContainer<Book> books;
	@Delegate
	Book commonBook
	Project project
	ActionBroadcast<Book> configBookActions = new ActionBroadcast<Book>();

	JDocBookConvention(Project project, NamedDomainObjectContainer<Book> books) {
		this.project = project
		this.books = books
		this.commonBook = new Book("", project)
		/**
		 * if it is a mutil-books project, then we have to merge the info defined in the common book
		 * area into each book, and also mark the internal common book as abstract
		 */
		books.all { Book book ->
			book.translations += commonBook.translations
			book.masterLanguage = commonBook.masterLanguage
			book.useRelativeImageUris = commonBook.useRelativeImageUris
			book.autoDetectFonts = commonBook.autoDetectFonts
			book.useFopFontCache = commonBook.useFopFontCache
			book.localeSeparator = commonBook.localeSeparator
			book.transformerParameters.putAll(commonBook.transformerParameters)
			book.applyStandardInjectionValues = commonBook.applyStandardInjectionValues
			book.injectionDateFormat = commonBook.injectionDateFormat
			commonBook.formats.each {format ->
				book.format(format)
			}
			commonBook.concrete = false
		}

	}

	/**
	 * don't know if there is a "more gradle" way to do this, here i'm using listener to configure tasks
	 * per book, the reason is tasks depend on how book is configured, for example, if a book does not have
	 * a translation, then it should not has update-po, and render-html-zh-cn
	 */
	def jdocbook(Closure closure) {
		books.configure(closure)
		books.all { book -> configBookActions.execute book }
		configBookActions.execute commonBook
	}
}
