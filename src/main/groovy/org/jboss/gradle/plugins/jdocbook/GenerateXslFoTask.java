/*
 * jDocBook, processing of DocBook sources
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.jboss.gradle.plugins.jdocbook;

import java.io.File;
import java.util.Locale;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.jboss.jdocbook.JDocBookProcessException;
import org.jboss.jdocbook.render.FormatOptions;
import org.jboss.jdocbook.render.RenderingSource;
import org.jboss.jdocbook.util.StandardDocBookFormatMetadata;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "UnusedDeclaration" })
public class GenerateXslFoTask extends DefaultTask {
	private static final Logger log = Logging.getLogger( GenerateXslFoTask.class );

	private JDocBookPlugin plugin;
	private MasterSourceFileResolver masterSourceFileResolver;

	public void configure(JDocBookPlugin plugin, MasterSourceFileResolver masterSourceFileResolver) {
		this.plugin = plugin;
		this.masterSourceFileResolver = masterSourceFileResolver;
	}

	@TaskAction
	public void generate() {
		log.lifecycle( "Generating XSL-FO file" );
		final FormatOptions pdfFormatOptions = findPdfFormatOptions();
		log.trace( "found pdf format options" );
		plugin.getComponentRegistry().getXslFoGenerator().generateXslFo(
				new RenderingSourceImpl(),
				pdfFormatOptions
		);
	}

	private class RenderingSourceImpl implements RenderingSource {
		// todo : potentially make this available from plugin

		private String masterLanguage() {
			return plugin.getConfiguration().getMasterLanguage();
		}

		public Locale getLanguage() {
			return plugin.fromLanguageString( masterLanguage() );
		}

		public File resolveSourceDocument() {
			return masterSourceFileResolver.getMainMasterFile();
		}

		public File getXslFoDirectory() {
			return plugin.getDirectoryLayout().getXslFoDirectory( masterLanguage() );
		}

		public File resolvePublishingBaseDirectory() {
			// n/a
			return null;
		}
	}

	private FormatOptions findPdfFormatOptions() {
		for ( FormatOptions formatOptions : plugin.getConfiguration().getFormats() ) {
			if ( StandardDocBookFormatMetadata.PDF.getName().equals( formatOptions.getName() ) ) {
				return formatOptions;
			}
		}
		throw new JDocBookProcessException( "Could not locate PDF format options" );
	}
}
