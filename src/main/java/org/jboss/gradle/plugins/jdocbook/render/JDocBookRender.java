package org.jboss.gradle.plugins.jdocbook.render;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.jboss.gradle.plugins.jdocbook.Format;
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class JDocBookRender extends DefaultTask {
	private static final Logger log = Logging.getLogger( JDocBookRender.class );

	private JDocBookPlugin plugin;
	private String language;
	private Format format;

	public void configure(JDocBookPlugin plugin, String language, Format format) {
		this.plugin = plugin;
		this.language = language;
		this.format = format;
	}
}
