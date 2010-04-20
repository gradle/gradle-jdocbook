package org.jboss.gradle.plugins.jdocbook;

import java.io.File;
import java.util.Set;

import org.apache.tools.ant.taskdefs.Untar;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import static org.jboss.gradle.plugins.jdocbook.JDocBookPlugin.STYLES_CONFIG_NAME;

/**
 * Applies staging of style artifacts
 *
 * @author Steve Ebersole
 */
@SuppressWarnings({ "UnusedDeclaration" })
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

		Untar untar = new Untar();
		untar.setProject( plugin.getProject().getAnt().getAntProject() );
		untar.setDest( getStagingDirectory() );
		untar.addPatternset( STANDARD_EXCLUSIONS );
		for ( File file : getStyleArtifacts() ) {
			untar.addFileset( new SingleFileFileSet( file ) );
		}
		untar.execute();
	}

	private static final PatternSet STANDARD_EXCLUSIONS;
	static {
		STANDARD_EXCLUSIONS = new PatternSet();
		STANDARD_EXCLUSIONS.createExclude().setName( "META-INF/**/*" );
		STANDARD_EXCLUSIONS.createExclude().setName( "META-INF/*" );
	}

	private static class SingleFileFileSet extends FileSet {
		public SingleFileFileSet(File file) {
			super();
			setFile( file );
		}
	}
}
