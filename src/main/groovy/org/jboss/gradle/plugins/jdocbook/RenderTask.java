package org.jboss.gradle.plugins.jdocbook;

import java.io.File;
import java.util.Locale;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jboss.jdocbook.render.FormatOptions;
import org.jboss.jdocbook.render.RenderingSource;

/**
 * Task for performing DocBook rendering.
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "UnusedDeclaration" })
public class RenderTask extends DefaultTask {
	private static final Logger log = Logging.getLogger( RenderTask.class );

	private JDocBookPlugin plugin;
	private String language;
	private FormatOptions format;
	private RenderingSourceImpl renderingSource;

	public void configure(JDocBookPlugin plugin, String language, FormatOptions format) {
		this.plugin = plugin;
		this.language = language;
		this.format = format;
		renderingSource = new RenderingSourceImpl();
	}

	@Input
	public String getLanguage() {
		return language;
	}

//	@Input
	public FormatOptions getFormat() {
		return format;
	}
// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
// it is temporary that these flattened attributes are marked as @Input rather than the getFormat() attribute
// see http://jira.codehaus.org/browse/GRADLE-970 for details
	@Input
	public String getFormatName() {
		return getFormat().getName();
	}
	@Input
	@Optional
	public String getFormatFinalName() {
		return getFormat().getTargetFinalName();
	}
	@Input
	@Optional
	public String getFormatStylesheetResource() {
		return getFormat().getStylesheetResource();
	}
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@InputDirectory
	public File getDocumentDirectory() {
		if ( plugin.getConfiguration().getProfiling().isEnabled() ) {
			return plugin.getDirectoryLayout().getProfilingDirectory( getLanguage() );
		}
		else if ( getLanguage().equals( plugin.getConfiguration().getMasterLanguage() ) ) {
			return plugin.getDirectoryLayout().getMasterSourceDirectory();
		}
		else {
			return plugin.getDirectoryLayout().getTranslationDirectory( getLanguage() );
		}
	}

	@OutputDirectory
	public File getPublishDirectory() {
		return plugin.getDirectoryLayout().getPublishDirectory( getLanguage(), getFormat().getName() );
	}

	@TaskAction
	public void render() {
		log.lifecycle( "rendering {} / {}", getLanguage(), getFormat().getName() );
		plugin.getComponentRegistry().getRenderer().render( renderingSource, format );
	}

	private class RenderingSourceImpl implements RenderingSource {
		public Locale getLanguage() {
			return plugin.fromLanguageString( language );
		}

		public File resolveSourceDocument() {
			return new File( getDocumentDirectory(), plugin.getConfiguration().getMasterSourceDocumentName() );
		}

		public File resolvePublishingBaseDirectory() {
			return plugin.getDirectoryLayout().getPublishBaseDirectory( RenderTask.this.getLanguage() );
		}

		public File getXslFoDirectory() {
			return plugin.getDirectoryLayout().getXslFoDirectory( RenderTask.this.getLanguage() );
		}
	}
}
