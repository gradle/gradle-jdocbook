package org.jboss.gradle.plugins.jdocbook.book

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.util.ConfigureUtil
import org.jboss.gradle.plugins.jdocbook.JDocBookConvention
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin
import org.jboss.jdocbook.JDocBookComponentRegistry
import org.jboss.jdocbook.render.FormatOptions

/**
 * Book is the main concept in this plugin, for each $Book, it contains all the info that used in the document generation
 * And there is a scriptsdefault book for each jdocbook project, if only one book in this project, then it is the one of scriptsdefault.
 * if there are mutil books, then all books inherit from the scriptsdefault one.
 *
 * @author Strong Liu
 */
class Book {
	final static Logger log = Logging.getLogger(Book);
	def masterSourceDocumentName = "book.xml"
	/**
	 * default language of the book is en-US, if the book is not language aware, then this should be
	 * override to ''
	 */
	def masterLanguage = "en-US"
	String name
	NamedDomainObjectContainer<FormatOption> formats
	def translations = []
	@Delegate
	BookConfiguration configuration
	def baseDirName
	def potDirName
	def imagesDirName
	def cssDirName
	def fontsDirName
	def version
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~ internal use only
	def concrete = true
	JDocBookComponentRegistry componentRegistry
	BookEnvironment environment
	Project project
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public Book(String name, Project project) {
		this.name = name
		this.project = project
		this.baseDirName = "src/main/docbook/$name"
		this.potDirName = baseDirName + "/pot"
		this.formats = project.container(FormatOption) { formatName -> new FormatOption(formatName)}
		this.version = project.version
		this.configuration = new BookConfiguration(this);
		this.environment = new BookEnvironment(this, project)
		this.componentRegistry = new JDocBookComponentRegistry(environment, configuration)

	}

	def format(Closure closure) {
		format(null,closure)
	}

	def format(String name, Closure closure) {
		format(ConfigureUtil.configure( closure ,new FormatOption(name)))
	}

	def format(FormatOption f) {
		formats.addObject(f.name, new FormatOption(f))
	}
	def translation(lang){
		translations << lang
	}

	static class FormatOption implements FormatOptions {
		String name
		String finalName
		String stylesheet
		boolean enable = true

		@Override
		String getTargetFinalName() {
			return finalName
		}

		@Override
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

		@Override
		public String toString() {
			return "$name";
		}
	}

}

