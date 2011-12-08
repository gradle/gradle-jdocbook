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
package org.jboss.gradle.plugins.jdocbook.task;

import org.gradle.api.artifacts.Configuration.State
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin

/**
 * Applies staging of style artifacts
 *
 * @author Steve Ebersole
 */
@SuppressWarnings(["UnusedDeclaration"])
public class StyleStagingTask extends BookTask {
    final static Logger log = Logging.getLogger(StyleStagingTask);
    @Input
    @Optional
    def jdocbookStyle = project.configurations."$JDocBookPlugin.STYLES_CONFIG_NAME"

    //FIXME skip this action if no jdocbook style defined

    @TaskAction
    public void stageJDocbookStyles() {
        log.lifecycle("Staging styles to {}", getStagingDirectory());
        if (jdocbookStyle.state != State.RESOLVED) {
            jdocbookStyle.resolve();
        }
        jdocbookStyle.each {File file ->
            project.copy {
                into(getStagingDirectory())
                from(project.zipTree(file).matching { exclude "META-INF/**" })
            }
        }
    }

    @TaskAction
    public void stageBookImages() {
        def image = getBookImages()
        if (image != null && image.exists() && image.list()) {
            log.lifecycle("Staging project images to {}", getStagingDirectory());
            project.copy {
                into project.file(book.environment.stageDirName + '/images' + '/' + getBookImages().name)
                from getBookImages()
            }
        }
    }

    @TaskAction
    public void stageBookCSS() {
        def css = getBookCss()
        if (css != null && css.exists() && css.list()) {
            log.lifecycle("Staging project css to {}", getStagingDirectory());
            project.copy {
                into project.file(book.environment.stageDirName + '/css' + '/' + getBookCss().name)
                from getBookCss()
            }
        }
    }

    @InputDirectory
    @Optional
    public File getBookImages() {
        existsOrNull(book.environment.imagesDirectory)
    }

    @InputDirectory
    @Optional
    public File getBookCss() {
        existsOrNull(book.environment.cssDirectory)
    }

    @OutputDirectory
    public File getStagingDirectory() {
        if (!book.environment.stagingDirectory.exists()) {
            book.environment.stagingDirectory.mkdir()
        }
        return book.environment.stagingDirectory
    }

}
