package org.jboss.gradle.plugins.jdocbook.book

/**
 *
 * @author: Strong Liu
 */
class BookStructure {
	Book book

	BookStructure(Book book) {
		this.book = book
	}
	//~~~~~~~~~~~~~~~~~~   source

	def root() {
		return "src/main/docbook"
	}
	/*
	by scriptsdefault, the base document folder is "src/main/docbook/{book name}/{master lang}
	if there is only one book in the project(the scriptsdefault), then {book name} can be ignored
	and also, scriptsdefault {master lang} is 'en-US', but it also can be ignored in the project structure
	 */

	def base() {
		String.format("%s/%s", root(), book.displayName)
	}

	def lang(def l) {
		String.format("%s/%s", base(), l)
	}
	/*
	Master Document Source File
	 */

	def master() {
		String.format("%s/%s", lang(book.masterLanguage), book.masterSourceDocumentName)
	}

	def pot() {
		lang("pot")
	}

	def images() {
		String.format("%s/%s", lang(book.masterLanguage), "images")
	}

	def css() {
		String.format("%s/%s", lang(book.masterLanguage), "css")
	}
	//~~~~~~~~~~~~~~~~~~   output
	//private String baseOutput = project.buildDirName+"/docbook"

	def work() {
		output("work")
	}

	def work(def lang) {
		String.format("%s/%s", work(), lang)
	}

	def profile(def lang) {
		String.format("%s/%s/%s", work(), "profile", lang)
	}

	def stage() {
		output("stage")
	}

	def publish(def lang) {
		String.format("%s/%s", output("publish"), lang)
	}

	private String output(String name) {
		return String.format("%s/%s/%s/%s", book.project.buildDirName, "docbook", name, book.displayName)
	}
}
