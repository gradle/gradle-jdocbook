package org.jboss.gradle.plugins.jdocbook.task

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.jboss.jdocbook.JDocBookProcessException
import org.jboss.jdocbook.render.FormatOptions
import org.jboss.jdocbook.render.RenderingSource
import org.jboss.jdocbook.util.StandardDocBookFormatMetadata
import org.jboss.jdocbook.util.TranslationUtils

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 * @author Strong Liu
 */
@SuppressWarnings(["UnusedDeclaration"])
public class GenerateXslFoTask extends DefaultTask implements RenderingSource {
	private static final Logger log = Logging.getLogger(GenerateXslFoTask.class);

	private Book book;

	public void configure(Book book) {
		this.book = book;
	}

	@TaskAction
	public void generate() {
		log.lifecycle("Generating XSL-FO file");
		final FormatOptions pdfFormatOptions = findPdfFormatOptions();
		log.trace("found pdf format options");
		book.componentRegistry.xslFoGenerator.generateXslFo(
				this,
				pdfFormatOptions
		);
	}

	public Locale getLanguage() {
		TranslationUtils.parse(book.masterLanguage, book.localeSeparator)
	}

	public File resolveSourceDocument() {
		return project.file(book.sourceSet.master())
	}

	public File getXslFoDirectory() {
		//TODO
		return null;//plugin.getDirectoryLayout().getXslFoDirectory(masterLanguage());
	}

	public File resolvePublishingBaseDirectory() {
		// todo
		return null;
	}

	private FormatOptions findPdfFormatOptions() {
		def format = book.formats.findByName(StandardDocBookFormatMetadata.PDF.name)
		if ( format == null )
		throw new JDocBookProcessException("Could not locate PDF format options");
	}
}