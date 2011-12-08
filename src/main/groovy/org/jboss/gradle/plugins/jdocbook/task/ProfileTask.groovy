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
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jboss.jdocbook.Profiling
import org.jboss.jdocbook.profile.ProfilingSource

/**
 * Task for performing DocBook profiling
 *
 * @author Steve Ebersole
 */
//@SuppressWarnings({ "UnusedDeclaration" })
public class ProfileTask extends BookTask implements ProfilingSource {
    static final Logger log = Logging.getLogger(ProfileTask);

    public Profiling getProfiling() {
        return book.profiling
    }
// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// it is temporary that these flattened attributes are marked as @Input rather than the getProfiling() attribute
// see http://jira.codehaus.org/browse/GRADLE-970 for details

    @Input
    public boolean isProfilingEnabled() {
        return getProfiling().isEnabled();
    }

    @Input
    @Optional
    public String isProfilingAttributeName() {
        return getProfiling().getAttributeName();
    }

    @Input
    @Optional
    public String isProfilingAttributeValue() {
        return getProfiling().getAttributeValue();
    }
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @InputFile
    public File getDocumentFile() {
        if (lang == book.masterLanguage) {
            return book.environment.rootDocumentFile
        } else {
            new File(book.environment.getWorkDirPerLang(lang), book.masterSourceDocumentName)
        }
    }

    public File resolveDocumentFile() {getDocumentFile()}

    @OutputFile
    public File getProfiledDocumentFile() {
        return new File(getProfileOutputDirectory(), book.masterSourceDocumentName);
    }

    public File resolveProfiledDocumentFile() {getProfiledDocumentFile()}

    @OutputDirectory
    public File getProfileOutputDirectory() {
        book.environment.getProfileDirPerLang(lang)
    }

    @TaskAction
    public void profile() {
        log.lifecycle("profiling {} into {}", lang, getProfileOutputDirectory());
        book.componentRegistry.profiler.profile(this);
    }

}
