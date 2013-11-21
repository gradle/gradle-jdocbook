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

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.jboss.jdocbook.render.RenderingSource

/**
 * Task for performing DocBook rendering.
 *
 * @author Strong Liu
 */
class RenderTask extends BookTask implements RenderingSource {
    static final Logger log = Logging.getLogger(RenderTask);
    def format;

    public void configure(Book book, def language, def format) {
        configure(book, language)
        this.format = format
    }

    @Input public String getFormatName() { format.name }

    @Input @Optional public String getFormatFinalName() { format.finalName }

    @Input @Optional public String getStylesheet() { return format.stylesheet }


    @TaskAction
    public void render() {
        log.lifecycle( "rendering Book({}) {}/{}", book.name, lang, format.name );
        ScriptClassLoaderExtender.Result extensionResult = ScriptClassLoaderExtender.extendScriptClassLoader( project );
        try {
            book.componentRegistry.renderer.render( this, format )
        }
        finally {
            ScriptClassLoaderExtender.unextendScriptClassLoader( extensionResult );
        }
    }

    @Override
    @Optional
    File getXslFoDirectory() {
        existsOrNull(book.environment.getWorkDirPerLang("xsl-fo"))
    }

    @InputFile
    public File getSourceDocument() {
        if (book.profiling.enabled) {
            return new File(book.environment.getProfileDirPerLang(lang), book.masterSourceDocumentName)
        }
        else if (lang == book.masterLanguage) {
            return book.environment.rootDocumentFile
        }
        else {
            return new File(book.environment.getWorkDirPerLang(lang), book.masterSourceDocumentName)
        }
    }

    public File resolveSourceDocument() { getSourceDocument() }

    @OutputDirectory
    File getPublishingBaseDirectory() { resolvePublishingBaseDirectory() }

    File resolvePublishingBaseDirectory() {
        book.environment.getPublishDirPerLang(lang)
    }
}
