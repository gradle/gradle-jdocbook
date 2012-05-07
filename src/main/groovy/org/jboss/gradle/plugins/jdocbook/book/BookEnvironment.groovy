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
package org.jboss.gradle.plugins.jdocbook.book

import org.gradle.api.Project
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin
import org.jboss.jdocbook.Environment
import org.jboss.jdocbook.JDocBookProcessException
import org.jboss.jdocbook.MasterLanguageDescriptor
import org.jboss.jdocbook.util.ResourceDelegateSupport
import org.jboss.jdocbook.util.TranslationUtils
import org.jboss.jdocbook.util.XIncludeHelper
import org.jboss.jdocbook.DocBookSchemaResolutionStrategy
import org.gradle.api.artifacts.Configuration

/**
 * An implementation of the jDocBook {@link Environment} contract specific to each configured book
 *
 * @author: Strong Liu
 */
class BookEnvironment implements Environment, MasterLanguageDescriptor {
    private static final Logger log = Logging.getLogger(Environment);

    Book book;
    Project project
    MasterLanguageDescriptor masterLanguageDescriptor;
    DocBookSchemaResolutionStrategy docBookSchemaResolutionStrategy = DocBookSchemaResolutionStrategy.RNG;
    private Locale language
    ResourceDelegate resourceDelegate
    def outputDirName
    def workDirName
    def stageDirName
    def publishDirName
    def profileDirName

    private Set<File> documentFiles;

    BookEnvironment(Book book, Project project) {
        this.book = book
        this.project = project
        this.masterLanguageDescriptor = this
        this.resourceDelegate = new ResourceDelegate()
        this.outputDirName = "${project.buildDir}/docbook"
        this.workDirName = outputDirName + "/work"
        this.stageDirName = outputDirName + "/stage"
        this.publishDirName = outputDirName + "/publish"
        this.profileDirName = outputDirName + "/profile"
        this.workDirName = "$outputDirName/work/${book.name}"
        this.stageDirName = "$outputDirName/stage/${book.name}"
        this.publishDirName = "$outputDirName/publish/${book.name}"
        this.profileDirName = "$outputDirName/profile/${book.name}"
    }

    Locale getLanguage() {
        if (language == null)
            language = getLanguage(book.masterLanguage)
        return language
    }

    Locale getLanguage(lang) {
        TranslationUtils.parse(lang, book.localeSeparator)
    }

    public ResourceDelegate getResourceDelegate() {
        return resourceDelegate
    }

    Set<File> getDocumentFiles() {
        if (documentFiles == null) {
            documentFiles = [] as Set
            documentFiles << getRootDocumentFile()
            XIncludeHelper.findAllInclusionFiles(getRootDocumentFile(), documentFiles, getDocBookSchemaResolutionStrategy());
        }
        return documentFiles
    }

    Environment.DocBookXsltResolutionStrategy getDocBookXsltResolutionStrategy() {
        return Environment.DocBookXsltResolutionStrategy.INCLUSIVE
    }

    private ClassLoader buildResourceDelegateClassLoader() {
        List<URL> urls = new ArrayList<URL>();

        if (stagingDirectory.exists()) {
            try {
                urls.add(stagingDirectory.toURI().toURL());
            }
            catch (MalformedURLException e) {
                throw new JDocBookProcessException("Unable to resolve staging directory to URL", e);
            }
        }
        def feedingClasspathWithDependencies = { Configuration configuration ->
            configuration.files.each { File file ->
                try {
                    urls.add(file.toURI().toURL());
                }
                catch (MalformedURLException e) {
                    log.warn("Unable to retrieve file url [" + file.getAbsolutePath() + "]; ignoring");
                }
            }
        }

        feedingClasspathWithDependencies(project.configurations."${JDocBookPlugin.DOCBOOK_CONFIG_NAME}")
        feedingClasspathWithDependencies(project.buildscript.configurations."${ScriptHandler.CLASSPATH_CONFIGURATION}")
        feedingClasspathWithDependencies(project.configurations."${JDocBookPlugin.STYLES_CONFIG_NAME}")
        feedingClasspathWithDependencies(project.configurations."${JDocBookPlugin.XSL_CONFIG_NAME}")
        return new URLClassLoader( urls.toArray(new URL[urls.size()]), Thread.currentThread().contextClassLoader);
    }

    File getWorkDirectory() {
        project.file(workDirName)
    }

    File getWorkDirPerLang(lang) {
        new File(workDirectory, lang)
    }

    File getProfileDirPerLang(lang) {
        new File(profileDirName, lang)
    }

    File getPublishDirPerLang(lang) {
        project.file("$publishDirName/$lang")
    }

    File getStagingDirectory() {
        project.file(stageDirName)
    }

    private List<File> fontDirectories

    List<File> getFontDirectories() {
        if (fontDirectories == null) {
            fontDirectories = []
            if (book.fontsDirName == null) {
                book.fontsDirName = "${book.baseDirName}/${book.masterLanguage}/fonts"
            }
            def font = project.file(book.fontsDirName)
            if (font.exists() && font.isDirectory()) {
                fontDirectories << project.file(book.fontsDirName)
            }
            fontDirectories << new File(stagingDirectory, "fonts")
        }
        return fontDirectories
    }

    File getPotDirectory() {
        project.file(book.potDirName)
    }

    File getImagesDirectory() {
        if (book.imagesDirName == null) {
            book.imagesDirName = "${book.baseDirName}/${book.masterLanguage}/images"
        }
        project.file(book.imagesDirName)
    }

    File getCssDirectory() {
        if (book.cssDirName == null) {
            book.cssDirName = "${book.baseDirName}/${book.masterLanguage}/css"
        }
        project.file(book.cssDirName)
    }

    File getSourceDirPerLang(lang) {
        project.file("${book.baseDirName}/$lang")
    }

    File getBaseSourceDirectory() {
        getSourceDirPerLang(book.masterLanguage)
    }

    private File rootDocumentFile

    File getRootDocumentFile() {
        if (rootDocumentFile == null) {
            rootDocumentFile = new File(getBaseSourceDirectory(), book.masterSourceDocumentName)
        }
        return rootDocumentFile
    }

    class ResourceDelegate extends ResourceDelegateSupport {
        private ClassLoader loader;

        protected ClassLoader getResourceClassLoader() {
            if (loader == null) {
                loader = buildResourceDelegateClassLoader();
            }
            return loader;
        }
    }
}
