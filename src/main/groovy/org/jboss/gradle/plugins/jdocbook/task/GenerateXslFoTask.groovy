package org.jboss.gradle.plugins.jdocbook.task


import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction
import org.jboss.jdocbook.JDocBookProcessException
import org.jboss.jdocbook.render.FormatOptions
import org.jboss.jdocbook.render.RenderingSource
import org.jboss.jdocbook.util.StandardDocBookFormatMetadata
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.InputFile

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 * @author Strong Liu
 */
@SuppressWarnings(["UnusedDeclaration"])
public class GenerateXslFoTask extends BookTask implements RenderingSource {
	static final Logger log = Logging.getLogger(GenerateXslFoTask);

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

	@OutputDirectory
	public File getXslFoDirectory() {
		book.environment.getWorkDirPerLang("xsl-fo")
	}

	private FormatOptions findPdfFormatOptions() {
		def format = book.formats.findByName(StandardDocBookFormatMetadata.PDF.name)
		if ( format == null )
		throw new JDocBookProcessException("Could not locate PDF format options");
		return format
	}

	File resolveSourceDocument(){ getSourceDocument() }

	@InputFile
	File getSourceDocument() {
		if ( book.profiling.enabled ) {
			return new File(book.environment.getProfileDirPerLang(lang), book.masterSourceDocumentName)
		}
		else if ( lang == book.masterLanguage ) {
			return book.environment.rootDocumentFile
		}
		else {
			return new File(book.environment.getWorkDirPerLang(lang), book.masterSourceDocumentName)
		}
	}

	File resolvePublishingBaseDirectory() {
		//n/a
		return null
	}
}