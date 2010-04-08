package org.jboss.gradle.plugins.jdocbook;

import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class JDocBookPlugin implements Plugin<Project> {
	private final JDocBookConfiguration configuration = new JDocBookConfiguration();

	public JDocBookConfiguration getConfiguration() {
		return configuration;
	}

	private Project project;

	private DirectoryLayout directoryLayout;

	public DirectoryLayout getDirectoryLayout() {
		return directoryLayout;
	}

	private MasterSourceFileResolver masterSourceFileResolver;

	public MasterSourceFileResolver getMasterSourceFileResolver() {
		return masterSourceFileResolver;
	}


	/**
	 * {@inheritDoc}
	 */
	public void apply(Project project) {
		this.project = project;

		// set up the configuration used to define jDocBook-based artifacts (docbook, styles, etc).
        project.getConfigurations().add( "jDocBook" )
				.setVisible( false )
				.setTransitive( false )
				.setDescription( "The DocBook and jDocBook artifacts to use." );

		// set up our convention and configuration objects
		project.getConvention().getPlugins().put( "jdocbook", new JDocBookConvention( this ) );

		directoryLayout = new DirectoryLayout( project, this );
		masterSourceFileResolver = new MasterSourceFileResolver( this );
	}

	public void applyConfigurationChanges() {
		// set up the translation tasks
		Task translateTask = project.getTasks().add( "translateDocBook" );
		translateTask.setDescription( "Perform all DocBook translation tasks" );
		for ( String translationLanguage : configuration.getTranslations() ) {
			
		}
	}

}
