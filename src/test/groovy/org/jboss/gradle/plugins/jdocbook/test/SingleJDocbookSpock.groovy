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



package org.jboss.gradle.plugins.jdocbook.test

/**
 *
 * @author: Strong Liu
 */
class SingleJDocbookSpock extends AbstractJDocbookSpock {
    def DEFAULT_SCRIPT = "/scripts/default.gradle"

    def "check if tasks are well configured"() {
        applyScript DEFAULT_SCRIPT
        expect:
        project.tasks.all {
            println it.name
        }
        project.tasks.buildDocs.execute()
    }

    def "make sure compatibility does not broken"() {
        applyScript DEFAULT_SCRIPT
        expect:
        convention != null
        convention.books != null
        convention.commonBook != null
        def book = convention.commonBook
        book != null
        book.formats.each {format ->
            checkFormat(format)
        }
        book.masterSourceDocumentName == "HIBERNATE_-_Relational_Persistence_for_Idiomatic_Java.xml"
        book.translations == ["zh-CN"]
    }

    def "does transformer parameters get set correctly"(){
                applyScript DEFAULT_SCRIPT
        expect:
        convention != null
        convention.books != null
        convention.commonBook != null
        def book = convention.commonBook
        !book.transformerParameters.isEmpty()
        book.transformerParameters.get("a") == "b"
    }

    def checkFormat(def format) {
        assert FORMATS[format.name].finalName == format.finalName
        assert FORMATS[format.name].stylesheet == format.stylesheet
    }

    def FORMATS = [
            pdf:
            [name: "pdf", finalName: "hibernate_reference.pdf", stylesheet: "classpath:/xslt/org/hibernate/jdocbook/xslt/pdf.xsl"],
            html:
            [name: "html", finalName: "index.html", stylesheet: "classpath:/xslt/org/hibernate/jdocbook/xslt/xhtml.xsl"],
            html_single:
            [name: "html_single", finalName: "index.html", stylesheet: "classpath:/xslt/org/hibernate/jdocbook/xslt/xhtml-single.xsl"]
    ]
}
