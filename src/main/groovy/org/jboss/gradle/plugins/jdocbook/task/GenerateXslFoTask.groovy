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
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jboss.jdocbook.JDocBookProcessException
import org.jboss.jdocbook.render.FormatOptions
import org.jboss.jdocbook.render.RenderingSource
import org.jboss.jdocbook.util.StandardDocBookFormatMetadata

/**
 * Generates an XSL-FO file.  If you happen to run into problems with the FOP library in terms of PDF generation, the
 * FOP team will always want to see your FO file.  Our normal generation process skips the generation of this physical
 * file.  This task allows creation of that file for such cases.
 *
 * @author Steve Ebersole
 * @author Strong Liu
 */
@SuppressWarnings(["UnusedDeclaration"])
public class GenerateXslFoTask extends BookTask implements RenderingSource {
    static final Logger log = Logging.getLogger(GenerateXslFoTask);

    @TaskAction
    public void generate() {
        log.lifecycle("Generating XSL-FO file");
        final FormatOptions pdfFormatOptions = findPdfFormatOptions();
        log.trace("found pdf format options");

        ScriptClassLoaderExtender.Result extensionResult = ScriptClassLoaderExtender.extendScriptClassLoader( project );
        try {
            book.componentRegistry.xslFoGenerator.generateXslFo(
                    this,
                    pdfFormatOptions
            );
        }
        finally {
            ScriptClassLoaderExtender.unextendScriptClassLoader( extensionResult );
        }
    }

    @OutputDirectory
    public File getXslFoDirectory() {
        book.environment.getWorkDirPerLang("xsl-fo")
    }

    private FormatOptions findPdfFormatOptions() {
        def format = book.formats.findByName(StandardDocBookFormatMetadata.PDF.name)
        if (format == null) {
            throw new JDocBookProcessException("Could not locate PDF format options")
        }
        return format
    }

    File resolveSourceDocument() {
        getSourceDocument()
    }

    @InputFile
    File getSourceDocument() {
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

    File resolvePublishingBaseDirectory() {
        return null
    }
}