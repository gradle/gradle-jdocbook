package org.jboss.gradle.plugins.jdocbook;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.jboss.gradle.plugins.jdocbook.profile.JDocBookProfile;
import org.jboss.gradle.plugins.jdocbook.render.JDocBookRender;
import org.jboss.gradle.plugins.jdocbook.translate.JDocBookTranslate;
import org.jboss.jdocbook.Configuration;
import org.jboss.jdocbook.Environment;
import org.jboss.jdocbook.JDocBookComponentFactory;
import org.jboss.jdocbook.JDocBookProcessException;
import org.jboss.jdocbook.MasterLanguageDescriptor;
import org.jboss.jdocbook.Profiling;
import org.jboss.jdocbook.ResourceDelegate;
import org.jboss.jdocbook.ValueInjection;
import org.jboss.jdocbook.util.ResourceDelegateSupport;
import org.jboss.jdocbook.util.TranslationUtils;

/**
 * The Gradle plugin for jDocBook.
 *
 * @author Steve Ebersole
 */
public class JDocBookPlugin implements Plugin<Project> {
	public static final String JDOCBOOK_CONFIG_NAME = "jDocBook";

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
	private Task renderStage;
	private Map<String, Task> formatGroups = new HashMap<String, Task>();

	private JDocBookComponentFactory jDocBookComponentFactory;

	public JDocBookComponentFactory getComponentFactory() {
		return jDocBookComponentFactory;
	}


	/**
	 * {@inheritDoc}
	 */
	public void apply(final Project project) {
		this.project = project;

		// set up the 'jDocBook' specialized configuration
		project.getConfigurations().add( JDOCBOOK_CONFIG_NAME )
				.setVisible( false )
				.setTransitive( false )
				.setDescription( "The DocBook and jDocBook artifacts to use." );

		// set up our convention and configuration objects
		project.getConvention().getPlugins().put( "jdocbook", new JDocBookConvention( this ) );
		directoryLayout = new DirectoryLayout( project, this );
		masterSourceFileResolver = new MasterSourceFileResolver( this );

		// set up the translation group task
		translateStage = project.getTasks().add( "translateDocBook" );
		translateStage.setDescription( "Perform all DocBook translations" );
		translateStage.dependsOn(
				new Callable<Object>() {
					public Object call() throws Exception {
						return project.getTasks().withType( JDocBookTranslate.class ).getAll();
					}
				}
		);

		// set up the profiling group task
		profileStage = project.getTasks().add( "profileDocBook" );
		profileStage.setDescription( "Perform all DocBook profiling" );
		profileStage.dependsOn(
				new Callable<Object>() {
					public Object call() throws Exception {
						return project.getTasks().withType( JDocBookProfile.class ).getAll();
					}
				}
		);
		profileStage.dependsOn( translateStage ); // in case profiling is not enabled

		// set up the rendering group task
		renderStage = project.getTasks().add( "formatDocBook" );
		renderStage.setDescription( "Perform all DocBook formatting" );
		renderStage.dependsOn( profileStage ); // in case profiling is not enabled

		// Look for a "buildDocs" task and link in there.
		Task docsTask = project.getTasks().getByName( "buildDocs" );
		if ( docsTask == null ) {
			docsTask = project.getTasks().add( "buildDocs" );
			docsTask.setDescription( "Builds all documentation" );
		}
		docsTask.dependsOn( renderStage );

		// finally prepare the JDocBookComponentFactory
		jDocBookComponentFactory = new JDocBookComponentFactory( new EnvironmentImpl(), new ConfigurationImpl() );
	}

	private Action<String> translationAddedAction() {
		return new Action<String>() {
			public void execute(String language) {
				// create translation task
				JDocBookTranslate translateTask = project.getTasks().add(
						String.format( "translateDocBook_%s", language ),
						JDocBookTranslate.class
				);
				translateTask.setDescription(
						String.format(
								"Perform DocBook translations for language %s", language
						)
				);
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

	void applyConfiguration(boolean secondaryCall) {
		if ( secondaryCall ) {
			throw new IllegalStateException( "Configuring jDocBook in two sections not yet supported" );
		}
		createGroups();

		applyLanguage( configuration.getMasterTranslationLanguage(), true );

		for ( String translation : configuration.getTranslations() ) {
			applyLanguage( translation, false );
		}
	}

	private void createGroups() {
		for ( Format format : configuration.getFormats() ) {
			Task formatGroup = project.getTasks().add( String.format( "formatDocBook_%s", format.getFormatName() ) );
			formatGroup.setDescription(
					String.format(
							"Perform all DocBook formatting for the %s format", format.getFormatName()
					)
			);
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
		formatLanguageGroup.setDescription(
				String.format(
						"Perform all DocBook formatting for language %s", language
				)
		);

		for ( Format format : configuration.getFormats() ) {
			JDocBookRender renderTask = project.getTasks().add(
					String.format( "profileDocBook_%s_%s", language, format.getFormatName() ),
					JDocBookRender.class
			);
			renderTask.setDescription(
					String.format(
							"Perform DocBook %s formatting for language %s", format.getFormatName(), language
					)
			);
			renderTask.configure( this, language, format );
			if ( formatDependency != null ) {
				renderTask.dependsOn( formatDependency );
			}
			formatLanguageGroup.dependsOn( renderTask );
			formatGroups.get( format.getFormatName() ).dependsOn( renderTask );
		}
	}

	private class EnvironmentImpl implements Environment {
		private final ResourceDelegateImpl resourceDelegate = new ResourceDelegateImpl();
		private final MasterLanguageDescriptorImpl masterDescriptor = new MasterLanguageDescriptorImpl();

		public ResourceDelegate getResourceDelegate() {
			return resourceDelegate;
		}

		public MasterLanguageDescriptor getMasterLanguageDescriptor() {
			return masterDescriptor;
		}
	}

	private class ResourceDelegateImpl extends ResourceDelegateSupport {
		private ClassLoader loader;

		@Override
		protected ClassLoader getResourceClassLoader() {
			if ( loader == null ) {
				loader = buildResourceDelegateClassLoader();
			}
			return loader;
		}
	}

	private ClassLoader buildResourceDelegateClassLoader() {
		List<URL> urls = new ArrayList<URL>();

		if ( directoryLayout.getStagingDirectory().exists() ) {
			try {
				urls.add( directoryLayout.getStagingDirectory().toURI().toURL() );
			}
			catch ( MalformedURLException e ) {
				throw new JDocBookProcessException( "Unable to resolve staging directory to URL", e );
			}
		}

		// TODO : how to get all jars/artifacts/dependencies named in the 'jDocBook' configuration???
		//		these need to get added the the urls list

		return new URLClassLoader(
				urls.toArray( new URL[ urls.size() ] ),
				Thread.currentThread().getContextClassLoader()
		);
	}

	private class MasterLanguageDescriptorImpl implements MasterLanguageDescriptor {
		public Locale getLanguage() {
			return TranslationUtils.parse(
					JDocBookPlugin.this.getConfiguration().getMasterTranslationLanguage(),
					JDocBookPlugin.this.getConfiguration().getLocaleSeparator()
			);
		}

		public File getPotDirectory() {
			return JDocBookPlugin.this.directoryLayout.getPotSourceDirectory();
		}

		public File getBaseSourceDirectory() {
			return JDocBookPlugin.this.directoryLayout.getMasterSourceDirectory();
		}

		public File getRootDocument() {
			return JDocBookPlugin.this.masterSourceFileResolver.getMainMasterFile();
		}

		public Set<File> getDocumentFiles() {
			return JDocBookPlugin.this.masterSourceFileResolver.getFiles();
		}
	}

	private class ConfigurationImpl implements Configuration {
		private JDocBookConfiguration gradleConfiguration() {
			return JDocBookPlugin.this.getConfiguration();
		}

		public Properties getTransformerParameters() {
			return gradleConfiguration().getTransformerParameters();
		}

		public boolean isUseRelativeImageUris() {
			return gradleConfiguration().isUseRelativeImageUris();
		}

		public char getLocaleSeparator() {
			return gradleConfiguration().getLocaleSeparator();
		}

		public boolean isAutoDetectFontsEnabled() {
			return gradleConfiguration().isAutoDetectFonts();
		}

		public boolean isUseFopFontCacheEnabled() {
			return gradleConfiguration().isUseFopFontCache();
		}

		public boolean isApplyStandardInjectionValuesEnabled() {
			return gradleConfiguration().isApplyStandardInjectionValues();
		}

		public String getInjectionDateFormat() {
			return gradleConfiguration().getInjectionDateFormat();
		}

		public LinkedHashSet<ValueInjection> getValueInjections() {
			return gradleConfiguration().getValueInjections();
		}

		public LinkedHashSet<String> getCatalogs() {
			return gradleConfiguration().getCatalogs();
		}

		public Profiling getProfiling() {
			return gradleConfiguration().getProfiling();
		}
	}
}
