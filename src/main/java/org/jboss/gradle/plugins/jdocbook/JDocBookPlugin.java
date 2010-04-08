package org.jboss.gradle.plugins.jdocbook;

import org.gradle.api.*;
import org.jboss.gradle.plugins.jdocbook.render.JDocBookRender;
import org.jboss.gradle.plugins.jdocbook.profile.JDocBookProfile;
import org.jboss.gradle.plugins.jdocbook.translate.JDocBookTranslate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class JDocBookPlugin implements Plugin<Project> {
	private JDocBookConfiguration configuration = new JDocBookConfiguration();

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

	// references to "grouping tasks" for easy later reference
	private Task translateStage;
	private Task profileStage;
	private Task formatStage;
	private Map<String, Task> formatGroups = new HashMap<String, Task>();

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

		directoryLayout = new DirectoryLayout( project, this );
		masterSourceFileResolver = new MasterSourceFileResolver( this );

        translateStage = project.getTasks().add( "translateDocBook" );
        translateStage.setDescription( "Perform all DocBook translations" );
        translateStage.dependsOn (
				new Callable<Object>() {
					public Object call() throws Exception {
						return project.getTasks().withType( JDocBookTranslate.class).getAll();
					}
				}
		);

		profileStage = project.getTasks().add( "profileDocBook" );
		profileStage.setDescription( "Perform all DocBook profiling" );
        profileStage.dependsOn (
				new Callable<Object>() {
					public Object call() throws Exception {
						return project.getTasks().withType( JDocBookProfile.class).getAll();
					}
				}
		);
		profileStage.dependsOn( translateStage ); // in case profiling is not enabled

        formatStage = project.getTasks().add( "formatDocBook" );
        formatStage.setDescription( "Perform all DocBook formatting" );
        formatStage.dependsOn( profileStage ); // in case profiling is not enabled

		// Look for a "buildDocs" task and link in there.
        Task docsTask = project.getTasks().getByName( "buildDocs" );
		if ( docsTask == null ) {
			docsTask = project.getTasks().add( "buildDocs" );
			docsTask.setDescription( "Builds all documentation" );
		}
        docsTask.dependsOn( formatStage );
	}

    private Action<String> translationAddedAction() {
        return new Action<String>() {
            public void execute(String language) {
				// create translation task
                JDocBookTranslate translateTask = project.getTasks().add(
						String.format( "translateDocBook_%s", language ),
						JDocBookTranslate.class
				);
                translateTask.setDescription( String.format( "Perform DocBook translations for language %s", language ) );
				translateTask.configure( JDocBookPlugin.this, language );

				// create profile task
				JDocBookProfile profileTask = project.getTasks().add(
						String.format( "profileDocBook_%s", language ),
						JDocBookProfile.class
				);
				profileTask.setDescription( String.format( "Perform DocBook profiling for language %s", language ) );
				profileTask.configure( JDocBookPlugin.this, language );
				profileTask.dependsOn( translateTask );
            }
        };
    }

	void applyConfiguration() {
		createGroups();

		applyLanguage( configuration.getMasterTranslationLanguage(), true );

		for ( String translation : configuration.getTranslations() ) {
			applyLanguage( translation, false );
		}
	}

	private void createGroups() {
		for ( Format format : configuration.getFormats() ) {
			Task formatGroup = project.getTasks().add( String.format( "formatDocBook_%s", format.getFormatName() ) );
        	formatGroup.setDescription( String.format( "Perform all DocBook formatting for the %s format", format.getFormatName() ) );
        	formatGroup.dependsOn( profileStage );
			formatGroups.put( format.getFormatName(), formatGroup );
		}
	}

	private void applyLanguage(String language, boolean master) {
		JDocBookTranslate translateTask = null;
		if ( !master ) {
			translateTask = project.getTasks().add(
					String.format( "translateDocBook_%s", language ),
					JDocBookTranslate.class
			);
		}
		Task formatDependency = translateTask;

		JDocBookProfile profileTask = null;
		if ( configuration.getProfiling().isEnabled() ) {
			profileTask = project.getTasks().add(
					String.format( "profileDocBook_%s", language ),
					JDocBookProfile.class
			);
			profileTask.setDescription( String.format( "Perform DocBook profiling for language %s", language ) );
			profileTask.configure( JDocBookPlugin.this, language );
			if ( !master ) {
				profileTask.dependsOn( translateTask );
			}
			formatDependency = profileTask;
		}

		Task formatLanguageGroup = project.getTasks().add( String.format( "formatDocBook_%s", language ) );
		formatLanguageGroup.setDescription( String.format( "Perform all DocBook formatting for language %s", language ) );

		for ( Format format : configuration.getFormats() ) {
			JDocBookRender renderTask = project.getTasks().add(
					String.format( "profileDocBook_%s_%s", language, format.getFormatName() ),
					JDocBookRender.class
			);
			renderTask.setDescription( String.format( "Perform DocBook %s formatting for language %s", format.getFormatName(), language ) );
			renderTask.configure( this, language, format );
			if ( formatDependency != null ) {
				renderTask.dependsOn( formatDependency );
			}
			formatLanguageGroup.dependsOn( renderTask );
			formatGroups.get( format.getFormatName() ).dependsOn( renderTask );
		}
	}
}
