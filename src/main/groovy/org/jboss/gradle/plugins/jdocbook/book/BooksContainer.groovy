package org.jboss.gradle.plugins.jdocbook.book

import org.gradle.api.internal.AutoCreateDomainObjectContainer
import org.gradle.util.ConfigureUtil
import org.jboss.gradle.plugins.jdocbook.JDocBookConvention

/**
 * @author Strong Liu
 */
class BooksContainer extends AbstractBooksContainer {
	JDocBookConvention convention;
	/*
	common book is an abstract book in a mutil-books jdocbook project.
	(even the mutil-books project only has one book, it only matters that if
	"book" is defined in the script)
	but it is a concrete book in a old-style script.
	 */
	@Delegate
	Book commonBook;


	BooksContainer(JDocBookConvention convention) {
		super(Book, convention.generator)
		this.convention = convention
		this.commonBook = add(Book.DEFAULT_BOOK_NAME)
	}

	Book create(String name) {
		convention.generator.newInstance(Book.class, name, convention, commonBook)
	}

	public AutoCreateDomainObjectContainer<Book> configure(Closure configureClosure) {
		ConfigureUtil.configure(configureClosure, new BookAutoCreateDomainObjectContainerDelegate(
				configureClosure.getOwner(), this));
		return this;
	}
}
