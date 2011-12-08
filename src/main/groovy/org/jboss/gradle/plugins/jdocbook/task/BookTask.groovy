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
package org.jboss.gradle.plugins.jdocbook.task

import org.gradle.api.DefaultTask
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.jboss.gradle.plugins.jdocbook.book.Book

/**
 * Abstract book tasks, which set task group and also apply Spec
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
        if (lang == null) this.lang = book.masterLanguage
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
