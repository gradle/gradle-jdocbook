package org.jboss.gradle.plugins.jdocbook.test

/**
 *
 * @author: Strong Liu
 */
class MutilBooksSpock extends AbstractJDocbookSpock {
	def DEFAULT_SCRIPT = "/scripts/mutilbooks.gradle"

	def "tasks per book"() {
		applyScript DEFAULT_SCRIPT
		applyRepositories()
		applyJDocbookStyle()
		project.copy {
			from resources.getResource("/hibernatedocbook/devguide")
			into project.file("src/main/docbook/devguide/en-US")
		}
		project.copy {
			from resources.getResource("/hibernatedocbook/quickstart")
			into project.file("src/main/docbook/quickstart/en-US")
		}
		expect:
		def tasks = project.tasks
		tasks.getByName('buildDocs')
	}


	def "find all concrete books"() {
		applyScript DEFAULT_SCRIPT
		expect:
		convention.books.all.size() == 4
		convention.books.findAll({it.concrete}).size() == 3
	}


	def "mutil books configure support"() {
		applyScript DEFAULT_SCRIPT
		expect:
		convention != null
		convention.books != null
		convention.books.commonBook != null
		def common = convention.books.COMMON_BOOK
		common != null
		common.masterSourceDocumentName == "book.xml"
		common.formats.each {format ->
			checkFormat(format)
		}
		common.translations == ["zh-CN"]
		//assert manual book
		def manual = convention.books.manual
		manual != null
		manual.masterSourceDocumentName == "HIBERNATE_-_Relational_Persistence_for_Idiomatic_Java.xml"
		manual.formats.each {format ->
			checkFormat(format)
		}
		manual.translations == ["zh-CN"]
		//assert devguide book
		def devguide = convention.books.devguide
		devguide != null
		devguide.masterSourceDocumentName == "Hibernate_Developer_Guide.xml"
		devguide.translations == []
		devguide.formats.html_single.finalName == "hibernate.html"
		//FIXME formats should not contains PDF
		//devguide.formats
		devguide.formats.html.stylesheet == "classpath:/xslt/org/hibernate/jdocbook/xslt/xhtml.xsl"

		//assert quickstart book
		def quickstart = convention.books.quickstart
		quickstart.formats.html.enable == false
		quickstart.translations == ['zh-CN', 'de-DE']

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
