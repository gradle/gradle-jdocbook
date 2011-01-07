package org.jboss.gradle.plugins.jdocbook.book

import java.util.Map.Entry
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil
import org.jboss.gradle.plugins.jdocbook.JDocBookConvention
import org.jboss.jdocbook.JDocBookComponentRegistry

/**
 * Book is the main concept in this plugin, for each $Book, it contains all the info that used in the document generation
 * And there is a scriptsdefault book for each jdocbook project, if only one book in this project, then it is the one of scriptsdefault.
 * if there are mutil books, then all books inherit from the scriptsdefault one.
 *
 * @author Strong Liu
 */
class Book {
	public static final String DEFAULT_BOOK_NAME = "COMMON_BOOK"
	String name
	FormatOptionsContainer formats
	def masterSourceDocumentName = "book.xml"

	def masterLanguage = "en-US"
	def translations = []
	def concrete = true
	BookStructure sourceSet
	JDocBookComponentRegistry componentRegistry
	JDocBookConvention convention
	Project project
	@Delegate
	BookConfiguration configuration

	public Book(String name, JDocBookConvention convention, Book parent = null) {
		this.name = name
		this.convention = convention
		this.project = convention.project
		this.formats = convention.generator.newInstance(FormatOptionsContainer.class, convention.generator)
		mergeParent(parent)
		sourceSet = new BookStructure(this)
		configuration = new BookConfiguration(convention.project);
		componentRegistry = new JDocBookComponentRegistry(new EnvironmentImpl(this,convention.project), configuration)
	}
	//FIXME find a better(groovy) way to do this clone

	private void mergeParent(Book parent) {
		if ( parent == null ) return;
		parent.concrete = false
		initFormatOptionsContainerWithDefault(parent.formats)
		if ( parent.translations != null ) {
			translations.addAll(parent.translations)
		}
		//todo merge configuration
	}

	private void initFormatOptionsContainerWithDefault(FormatOptionsContainer defaultFormats) {
		if ( defaultFormats == null ) return;
		Map<String, FormatOption> map = defaultFormats.asMap
		for ( Entry<String, FormatOption> entry: map ) {
			FormatOption fo = new FormatOption(entry.key);
			fo.finalName = entry.value.finalName
			fo.stylesheet = entry.value.stylesheet
			formats.addObject entry.key, fo
		}
	}

	def format(Closure closure) {
		FormatOption fo = new FormatOption()
		ConfigureUtil.configure closure, fo
		format fo.name, closure
	}

	def format(String name, Closure closure) {
		formats.add name, closure
	}

	public String getDisplayName() {
		return (name == DEFAULT_BOOK_NAME) ? "" : name;
	}
}

