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

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.util.ConfigureUtil
import org.jboss.jdocbook.JDocBookComponentRegistry
import org.jboss.jdocbook.render.FormatOptions

/**
 * The notion of a book exposed to gradle script for configuration.
 *
 * @author Strong Liu
 */
class Book {
    final static Logger log = Logging.getLogger(Book);

    @Delegate
    BookConfiguration configuration

    String name
    def masterSourceDocumentName = "book.xml"
    /**
     * default language of the book is en-US, if the book is not language aware, then this should be
     * override to ''
     */
    def masterLanguage = "en-US"
    NamedDomainObjectContainer<FormatOption> formats
    def translations = []
    def baseDirName
    def potDirName
    def imagesDirName
    def cssDirName
    def fontsDirName
    def version

    // internal use only ~~~~~~~~~~~~~~~~~~~~~~~~~~~
    def concrete = true
    JDocBookComponentRegistry componentRegistry
    BookEnvironment environment
    Project project
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public Book(String name, Project project) {
        this.name = name
        this.project = project
        this.baseDirName = "src/main/docbook/$name"
        this.potDirName = baseDirName + "/pot"
        this.formats = project.container(FormatOption) { formatName -> new FormatOption(formatName)}
        this.version = project.version
        this.configuration = new BookConfiguration(this);
        this.environment = new BookEnvironment(this, project)
    }

    public JDocBookComponentRegistry getComponentRegistry() {
        if ( concrete ) {
            if ( componentRegistry == null ) {
                this.componentRegistry = new JDocBookComponentRegistry(environment, configuration)
            }
            return componentRegistry
        }
        throw new UnsupportedOperationException("abstract book doesn't have component registry")
    }
    def format(String name){
        format(new FormatOption(name))
    }
    def format(Closure closure) {
        format(null, closure)
    }

    def format(String name, Closure closure) {
		// if name is null then sometimes! the wrong ctor gets called. 
		// calls FormatOption(FormatOption parent)
		// appears to be platform dependent
		def fo = name ? new FormatOption(name) : new FormatOption()
		def config = ConfigureUtil.configure(closure, fo)
        format(config)
    }

    def format(FormatOption f) {
        FormatOption sd = formats.create(f.name)
        sd.finalName = f.finalName
        sd.stylesheet = f.stylesheet
        sd.enable = f.enable
//        formats.addObject(f.name, new FormatOption(f))
    }

    def translation(lang) {
        translations << lang
    }

    def transformerParameters(Map<String,?> parameters) {
        transformerParameters << parameters;
    }

    def transformerParameter(String key, Object value) {
        transformerParameters.put( key, value );
    }

    static class FormatOption implements FormatOptions {
        String name
        String finalName
        String stylesheet
        boolean enable = true
        String getTargetFinalName() {
            return finalName
        }

        String getStylesheetResource() {
            return stylesheet
        }

        FormatOption() {
        }

        FormatOption(String name) {
            this.name = name
        }

        FormatOption(FormatOption parent) {
            this.name = parent.name
            this.finalName = parent.finalName
            this.stylesheet = parent.stylesheet
        }

        public String toString() {
            return "$name";
        }
    }

}

