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

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.jboss.gradle.plugins.jdocbook.book.Book.FormatOption
import org.jboss.jdocbook.util.StandardDocBookFormatMetadata

/**
 * Config tasks per book action
 *
 * @author: Strong Liu
 */
class CreateTasksPerBookAction implements Action<Book> {
    final static Logger log = Logging.getLogger(CreateTasksPerBookAction);
    public static final String STAGE_TASK_GROUP = "stageStyles";
    public static final String TRANSLATE_TASK_GROUP = "translateDocBook";
    public static final String UPDATE_TRANSLATION_TASK_GROUP = "updateTranslations";
    public static final String PROFILE_TASK_GROUP = "profileDocBook";
    public static final String RENDER_TASK_GROUP = "renderDocBook";
    public static final String JDOCBOOK_TASK_GROUP = "DocBook";
    public static final String UPDATE_PO_TASK_GROUP = "updatePo";
    public static final String UPDATE_POT_TASK_GROUP = "updatePot";
    public static final String BUILDDOC_TASK_GROUP = "buildDocs";
    public static final String XSL_FO_TASK_GROUP = "xslFo";
    private Project project;

    public CreateTasksPerBookAction(Project project) {
        this.project = project
    }

    def taskPerBook(Book book) {
        if (!book.concrete) return;
        //stage style task per book
        String taskName = getTaskName(STAGE_TASK_GROUP, book.name)
        StyleStagingTask styleStageTask = addTask(taskName, StyleStagingTask)
        styleStageTask.description = String.format("Stage %s styles to the staging directory", getDescriptionName(book.name))
        styleStageTask.configure(book)

        //update pot task per book
        taskName = getTaskName(UPDATE_POT_TASK_GROUP, book.name)
        SynchronizePotTask updatePotTask = addTask(taskName, SynchronizePotTask)
        updatePotTask.description = String.format("Update the %s POT files from the current state of the master language sources", getDescriptionName(book.name))
        updatePotTask.configure(book)
        //it is same if there is only one common book
        if (taskName != UPDATE_POT_TASK_GROUP) {
            getOrCreateTask(UPDATE_POT_TASK_GROUP, 'Update the POT files from the current state of the master language sources').dependsOn updatePotTask
        }

        //render task per book
        Task renderBookTask = getOrCreateTask(getTaskName(RENDER_TASK_GROUP, book.name), String.format("Perform %s formatting", getDescriptionName(book.name)))
        getTask(BUILDDOC_TASK_GROUP).dependsOn renderBookTask
        //render/translation/updatepo task for master lang
        taskPerBookPerLang(book.masterLanguage, book, styleStageTask)
        //translation task per book
        if (book.translations.empty) return
        taskName = getTaskName(TRANSLATE_TASK_GROUP, book.name)
        Task translationTask = getOrCreateTask(taskName, String.format("Perform %s translation", getDescriptionName(book.name)))
        if (taskName != TRANSLATE_TASK_GROUP) {
            getOrCreateTask(TRANSLATE_TASK_GROUP, "Perform all DocBook translations").dependsOn translationTask
        }
        //update po task per book
        taskName = getTaskName(UPDATE_PO_TASK_GROUP, book.name)
        Task updatePoTask = getOrCreateTask(taskName, String.format("Update %s PO files", getDescriptionName(book.name)))
        if (taskName != UPDATE_PO_TASK_GROUP) {
            getOrCreateTask(UPDATE_PO_TASK_GROUP, 'Update the PO files for all translations from the current state of the POT files').dependsOn updatePoTask
        }
        //render/translation/updatepo task per translation lang
        book.translations.each { lang ->
            taskPerBookPerLang(lang, book, styleStageTask)
        }
    }


    void execute(Book book) {
        log.debug("configing tasks for Book(${book.name}")
        getOrCreateTask(BUILDDOC_TASK_GROUP, "Builds all documention")
        taskPerBook(book)

//		Task updateTransTask = getOrCreateTask(UPDATE_TRANSLATION_TASK_GROUP, "Update POT and all PO files")
//		updateTransTask.dependsOn getTask(UPDATE_POT_TASK_GROUP)
//		if ( getTask(UPDATE_PO_TASK_GROUP) ) {
//			updateTransTask.dependsOn(getTask(UPDATE_PO_TASK_GROUP))
//		}
    }

    def taskPerBookPerLang(String lang, Book book, Task styleStageTask) {
        TranslateTask translateTask;

        if (lang != book.masterLanguage) {
            //translation task per lang per book
            translateTask = addTask(
                    String.format( "%s_%s", getTaskName( TRANSLATE_TASK_GROUP, book.name ), lang ),
                    TranslateTask
            )
            translateTask.description = String.format(
                    "Perform %s translation for language %s",
                    getDescriptionName( book.name ),
                    lang
            )
            translateTask.configure( book, lang )
            getTask( getTaskName( TRANSLATE_TASK_GROUP, book.name ) ).dependsOn translateTask
            //synchronize po task per lang per book
            SynchronizePoTask poTask = addTask(
                    String.format( "%s_%s", getTaskName( UPDATE_PO_TASK_GROUP, book.name ), lang ),
                    SynchronizePoTask
            )
            poTask.description = String.format(
                    "Update %s PO files from current POT for language %s",
                    getDescriptionName( book.name ),
                    lang
            )
            poTask.configure( book, lang );
            getTask( getTaskName( UPDATE_PO_TASK_GROUP, book.name ) ).dependsOn poTask
        }

        book.formats.all { FormatOption format ->
            if (!format.enable) {
                return
            }

            RenderTask render = addTask(
                    String.format("%s_%s_%s", getTaskName( RENDER_TASK_GROUP, book.name ), lang, format.name ),
                    RenderTask
            )

            render.description = String.format(
                    "Perform %s %s formatting for language %s",
                    getDescriptionName( book.name ),
                    format.name,
                    lang
            )
            render.configure( book, lang, format )
            render.dependsOn styleStageTask
            if (lang != book.masterLanguage && translateTask != null) {
                render.dependsOn translateTask
            }
            if (format.name == StandardDocBookFormatMetadata.PDF.name) {
                String xslFoTaskName = getTaskName( XSL_FO_TASK_GROUP, book.name )
                Task xslFoTask = getTask(xslFoTaskName)
                if (xslFoTask == null) {
                    xslFoTask = addTask( xslFoTaskName, GenerateXslFoTask )
                    xslFoTask.description = String.format( "Generating %s XSL FO files", getDescriptionName( book.name ) )
                    xslFoTask.configure( book, lang )
                }
            }
            getTask( getTaskName( RENDER_TASK_GROUP, book.name ) ).dependsOn render
        }

    }

    private Task getTask(String name) {
        project.tasks.findByName( name )
    }

    private <T extends Task> T addTask(String name, Class<T> type) {
        project.tasks.create( name, type )
    }

    private Task getOrCreateTask(String name, String description) {
        if (getTask(name) == null) {
            Task task = project.tasks.create( name )
            task.description = description
            task.group = JDOCBOOK_TASK_GROUP
            return task
        }
        else {
            return getTask(name)
        }
    }

    private static String getDescriptionName(String displayName) {
        String.format( "Docbook %s", (displayName) ? "($displayName)" : "" )
    }

    private static String getTaskName(String perfix, String displayName) {
        (displayName) ? String.format("%s_%s", perfix, displayName) : perfix
    }
}
