package org.jboss.gradle.plugins.jdocbook.book

import org.jboss.jdocbook.MasterLanguageDescriptor
import org.jboss.jdocbook.util.TranslationUtils
import org.jboss.jdocbook.util.XIncludeHelper

/**
 *
 * @author: Strong Liu
 */
class MasterLanguageDescriptorImpl implements MasterLanguageDescriptor {
	final Book book
	MasterLanguageDescriptorImpl(Book book){
		this.book = book
	}

	@Override
	Locale getLanguage() {
		TranslationUtils.parse(book.masterLanguage,book.localeSeparator)
	}

	@Override
	File getPotDirectory() {
		book.convention.project.file(book.sourceSet.pot())
	}

	@Override
	File getBaseSourceDirectory() {
		book.convention.project.file(book.sourceSet.base())
	}

	@Override
	File getRootDocumentFile() {
		book.convention.project.file(book.sourceSet.master())
	}

	@Override
	Set<File> getDocumentFiles() {
		def files = [] as Set
		files << getRootDocumentFile()
		XIncludeHelper.findAllInclusionFiles( getRootDocumentFile(), files );
		return files

	}
}
