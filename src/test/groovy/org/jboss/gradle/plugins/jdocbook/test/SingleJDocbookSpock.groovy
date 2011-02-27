package org.jboss.gradle.plugins.jdocbook.test

/**
 *
 * @author: Strong Liu
 */
class SingleJDocbookSpock extends AbstractJDocbookSpock {
	def DEFAULT_SCRIPT = "/scripts/default.gradle"
	def "check if tasks are well configured"(){
		applyScript DEFAULT_SCRIPT
		expect:
		project.tasks.all{
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
