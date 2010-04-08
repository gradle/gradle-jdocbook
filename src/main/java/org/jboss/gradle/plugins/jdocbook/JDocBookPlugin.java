package org.jboss.gradle.plugins.jdocbook;

import org.gradle.api.*;
import org.jboss.gradle.plugins.jdocbook.i18n.ApplyTranslationTask;

import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class JDocBookPlugin implements Plugin<Project> {
	private JDocBookConfiguration configuration;

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
	public void apply(final Project project) {
		this.project = project;

		// set up the configuration used to define jDocBook-based artifacts (docbook, styles, etc).
        project.getConfigurations().add( "jDocBook" )
				.setVisible( false )
				.setTransitive( false )
				.setDescription( "The DocBook and jDocBook artifacts to use." );

		// set up our convention and configuration objects
		project.getConvention().getPlugins().put( "jdocbook", new JDocBookConvention( this ) );

        configuration = new JDocBookConfiguration(translationAddedAction());
		directoryLayout = new DirectoryLayout( project, this );
		masterSourceFileResolver = new MasterSourceFileResolver( this );

        Task translateTask = project.getTasks().add( "translateDocBook" );
        translateTask.setDescription( "Perform all DocBook translation tasks" );
        translateTask.dependsOn ( new Callable<Object>() {
            public Object call() throws Exception {
                return project.getTasks().withType(ApplyTranslationTask.class).getAll();
            }
        });

        Task docsTask = project.getTasks().add( "buildDocs" );
        docsTask.setDescription( "Builds all documentation" );
        docsTask.dependsOn( translateTask );
	}

    private Action<String> translationAddedAction() {
        return new Action<String>() {
            public void execute(String language) {
                String taskName = String.format( "translate_%s", language );
                ApplyTranslationTask task = project.getTasks().add( taskName, ApplyTranslationTask.class );
                task.setDescription(String.format("Translates DocBook source for language %s", language));
                task.setTranslationLanguage(language);
                task.setTranslationOutputDirectory(directoryLayout.getTranslationDirectory(language));
            }
        };
    }
}
