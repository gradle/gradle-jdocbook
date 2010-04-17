package org.jboss.gradle.plugins.jdocbook;

import java.io.File;
import java.util.Locale;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.jboss.jdocbook.render.FormatOptions;
import org.jboss.jdocbook.render.RenderingSource;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
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

	@Input
	public FormatOptions getFormat() {
		return format;
	}

	@TaskAction
	@SuppressWarnings({ "UnusedDeclaration" })
	public void render() {
		log.lifecycle( "rendering {} / {}", getLanguage(), getFormat().getName() );
		plugin.getComponentRegistry().getRenderer().render( renderingSource, format );
	}

	private class RenderingSourceImpl implements RenderingSource {
		public Locale getLanguage() {
			return plugin.fromLanguageString( language );
		}

		public File resolveSourceDocument() {
			return null;
		}

		public File resolvePublishingBaseDirectory() {
			return null;
		}

		public File getXslFoDirectory() {
			return null;
		}
	}
}
