/*
 * jDocBook, processing of DocBook sources
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.jboss.gradle.plugins.jdocbook

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.listener.ActionBroadcast
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.jboss.gradle.plugins.jdocbook.book.BookConfiguration

/**
 * The top-level convention object exposed to the Gradle build for jDocBook configuration
 * @author Strong Liu
 */
class JDocBookConvention {
    final static Logger log = Logging.getLogger(JDocBookConvention);
    final NamedDomainObjectContainer<Book> books;
    @Delegate
    Book commonBook
    @Delegate
    BookConfiguration bookConfiguration;
    Project project
    ActionBroadcast<Book> configBookActions = new ActionBroadcast<Book>();

    JDocBookConvention(Project project, NamedDomainObjectContainer<Book> books) {
        this.project = project
        this.books = books
        this.commonBook = new Book("", project)
        this.bookConfiguration = new BookConfiguration(commonBook)
        commonBook.setConfiguration(bookConfiguration)
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
