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


import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import static org.jboss.gradle.plugins.jdocbook.JDocBookPlugin.STYLES_CONFIG_NAME

/**
 * Applies staging of style artifacts
 *
 * @author Steve Ebersole
 */
@SuppressWarnings([ "UnusedDeclaration" ])
public class StyleStagingTask extends DefaultTask {
	private static final Logger log = Logging.getLogger( TranslateTask.class );

	// TODO : still need to account for "project local" style resources

	private JDocBookPlugin plugin;

	public void configure(JDocBookPlugin plugin) {
		this.plugin = plugin;
	}

	private Set<File> styleArtifacts;

	@InputFiles
	public Set<File> getStyleArtifacts() {
		if ( styleArtifacts == null ) {
			styleArtifacts = resolveFileArtifacts();
		}
		return styleArtifacts;
	}

	private Set<File> resolveFileArtifacts() {
		return plugin.getProject().getConfigurations().getByName( STYLES_CONFIG_NAME ).getFiles();
	}

	@OutputDirectory
	public File getStagingDirectory() {
		return plugin.getDirectoryLayout().getStagingDirectory();
	}

	@TaskAction
	public void stage() {
		log.lifecycle( "Staging styles to {}", getStagingDirectory() );

        for ( File file : getStyleArtifacts() ) {
            project.copy {
                into( getStagingDirectory() )
                from( project.zipTree( file.getAbsolutePath() ).matching { exclude "META-INF/**" } )
            }
        }
	}
}
