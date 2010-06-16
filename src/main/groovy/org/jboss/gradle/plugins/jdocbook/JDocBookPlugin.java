package org.jboss.gradle.plugins.jdocbook;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.initialization.dsl.ScriptHandler;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.specs.Spec;
import org.gradle.util.ObservableUrlClassLoader;
import org.jboss.jdocbook.Configuration;
import org.jboss.jdocbook.Environment;
import org.jboss.jdocbook.JDocBookComponentRegistry;
import org.jboss.jdocbook.JDocBookProcessException;
import org.jboss.jdocbook.MasterLanguageDescriptor;
import org.jboss.jdocbook.Profiling;
import org.jboss.jdocbook.ResourceDelegate;
import org.jboss.jdocbook.ValueInjection;
import org.jboss.jdocbook.render.FormatOptions;
import org.jboss.jdocbook.util.ResourceDelegateSupport;
import org.jboss.jdocbook.util.TranslationUtils;

/**
 * The Gradle plugin for jDocBook.
 *
 * @author Steve Ebersole
 */
public class JDocBookPlugin implements Plugin<Project> {
	private static final Logger log = Logging.getLogger( JDocBookPlugin.class );

	public static final String STYLES_CONFIG_NAME = "jdocbookStyles";
	public static final String DOCBOOK_CONFIG_NAME = "docbook";

	public static final String STAGE_TASK_GROUP = "stageStyles";
	public static final String TRANSLATE_TASK_GROUP = "translateDocBook";
	public static final String PROFILE_TASK_GROUP = "profileDocBook";
	public static final String RENDER_TASK_GROUP = "renderDocBook";

	private JDocBookConfiguration configuration = new JDocBookConfiguration();

	public JDocBookConfiguration getConfiguration() {
		return configuration;
	}

	private Project project;

	public Project getProject() {
		return project;
	}

	private DirectoryLayout directoryLayout;

	public DirectoryLayout getDirectoryLayout() {
		return directoryLayout;
	}

	private MasterSourceFileResolver masterSourceFileResolver;

	private JDocBookComponentRegistry jDocBookComponentRegistry;

	public JDocBookComponentRegistry getComponentRegistry() {
		return jDocBookComponentRegistry;
	}

	/**
	 * {@inheritDoc}
	 */
	public void apply(final Project project) {
		this.project = project;

		// set up the configurations
		project.getConfigurations().add( DOCBOOK_CONFIG_NAME )
				.setVisible( false )
				.setTransitive( false )
				.setDescription( "The DocBook artifact(s) to use." );

		project.getConfigurations().add( STYLES_CONFIG_NAME )
				.setVisible( false )
				.setTransitive( true )
				.setDescription( "Defines any jDocBook styles artifacts to apply" );

		// set up our convention and configuration objects
		project.getConvention().getPlugins().put( "jdocbook", new JDocBookConvention( this ) );
		directoryLayout = new DirectoryLayout( project, this );
		masterSourceFileResolver = new MasterSourceFileResolver( this );

		// set up the translation group task
		Task translateStage = project.getTasks().add( TRANSLATE_TASK_GROUP );
		translateStage.setDescription( "Perform all DocBook translations" );
		translateStage.dependsOn(
				new Callable<Object>() {
					public Object call() throws Exception {
						return project.getTasks().withType( TranslateTask.class ).getAll();
					}
				}
		);

		// Set up the staging task
		StyleStagingTask stagingTask = project.getTasks().add( STAGE_TASK_GROUP, StyleStagingTask.class );
		stagingTask.setDescription( "Stage all jdocbook styles to the staging directory" );
		stagingTask.configure( this );

		// set up the profiling group task
		Task profileStage = project.getTasks().add( PROFILE_TASK_GROUP );
		profileStage.setDescription( "Perform all DocBook profiling" );
		profileStage.dependsOn( translateStage );
		profileStage.dependsOn( stagingTask );
		profileStage.dependsOn(
				new Callable<Object>() {
					public Object call() throws Exception {
						return project.getTasks().withType( ProfileTask.class ).getAll();
					}
				}
		);

		// set up the rendering group task
		Task renderStage = project.getTasks().add( RENDER_TASK_GROUP );
		renderStage.setDescription( "Perform all DocBook formatting" );
		renderStage.dependsOn( profileStage );
		renderStage.dependsOn( stagingTask );
		renderStage.dependsOn(
				new Callable<Object>() {
					public Object call() throws Exception {
						return project.getTasks().withType( RenderTask.class ).getAll();
					}
				}
		);

		// Look for a "buildDocs" task and link in there.
		Task docsTask = project.getTasks().findByName( "buildDocs" );
		if ( docsTask == null ) {
			docsTask = project.getTasks().add( "buildDocs" );
			docsTask.setDescription( "Builds all documentation" );
		}
		docsTask.dependsOn( renderStage );

		// Add the POT update task
		final SynchronizePotTask potTask = project.getTasks().add( "updatePot", SynchronizePotTask.class );
		potTask.setDescription( "Update the POT files from the current state of the master language sources" );
		potTask.configure( this );

		// Add the PO update group task
		final Task poTask = project.getTasks().add( "updatePo" );
		poTask.setDescription( "Update the PO files for all translations from the current state of the POT files" );
		poTask.dependsOn(
				new Callable<Object>() {
					public Object call() throws Exception {
						return project.getTasks().withType( SynchronizePoTask.class ).getAll();
					}
				}
		);

		// Add the grouping task to manage both POT ans PO
		final Task updateTranslations = project.getTasks().add( "updateTranslations" );
		updateTranslations.setDescription( "Update POT and all PO files" );
		updateTranslations.dependsOn( potTask );
		updateTranslations.dependsOn( poTask );

		// set up the XSL-FO generation task
		GenerateXslFoTask xslFoTask = project.getTasks().add( "generateXslFo", GenerateXslFoTask.class );
		xslFoTask.setDescription( "Generate a XSL-FO file for FOP debugging (provided PDF format specified)" );
		xslFoTask.configure( this, masterSourceFileResolver );

		// finally prepare the JDocBookComponentFactory
		jDocBookComponentRegistry = new JDocBookComponentRegistry( new EnvironmentImpl(), new ConfigurationImpl() );
	}

	void applyConfiguration(boolean secondaryCall) {
		if ( secondaryCall ) {
			throw new IllegalStateException( "Configuring jDocBook in two sections not supported" );
		}
		createGroups();

		applyLanguage( configuration.getMasterLanguage(), true );

		for ( String translation : configuration.getTranslations() ) {
			applyLanguage( translation, false );
		}
	}

	private void createGroups() {
		final Task profileStage = project.getTasks().getByName( PROFILE_TASK_GROUP );

		for ( final FormatOptions format : configuration.getFormats() ) {
			Task formatGroup = project.getTasks().add( String.format( "formatDocBook_%s", format.getName() ) );
			formatGroup.setDescription(
					String.format(
							"Perform all DocBook formatting for the %s format", format.getName()
					)
			);
			formatGroup.dependsOn( profileStage );
			formatGroup.dependsOn(
					new Callable<Object>() {
						public Object call() throws Exception {
							return project.getTasks().withType( RenderTask.class ).matching(
									new Spec<RenderTask>() {
										public boolean isSatisfiedBy(RenderTask renderTask) {
											return format.getName().equals( renderTask.getFormat().getName() );
										}
									}
							);
						}
					}
			);
		}
	}

	private void applyLanguage(String language, boolean master) {
		StyleStagingTask stagingTask = (StyleStagingTask) project.getTasks().getByName( STAGE_TASK_GROUP );

		TranslateTask translateTask = null;
		if ( !master ) {
			translateTask = project.getTasks().add(
					String.format( TRANSLATE_TASK_GROUP + "_%s", language ),
					TranslateTask.class
			);
			translateTask.setDescription( String.format( "Perform DocBook translation for language %s", language ) );
			translateTask.configure( this, language );

			final SynchronizePoTask poTask = project.getTasks().add(
					String.format( "updatePo_%s", language ),
					SynchronizePoTask.class
			);
			translateTask.setDescription( String.format( "Update PO files from current POT for language %s", language ) );
			poTask.configure( this, language );
		}
		Task formatDependency = translateTask;

		if ( configuration.getProfiling().isEnabled() ) {
			ProfileTask profileTask = project.getTasks().add(
					String.format( PROFILE_TASK_GROUP + "_%s", language ),
					ProfileTask.class
			);
			profileTask.setDescription( String.format( "Perform DocBook profiling for language %s", language ) );
			profileTask.configure( JDocBookPlugin.this, language );
			if ( !master ) {
				profileTask.dependsOn( translateTask );
			}
			profileTask.dependsOn( stagingTask );
			formatDependency = profileTask;
		}

		Task formatLanguageGroup = project.getTasks().add( String.format( "formatDocBook_%s", language ) );
		formatLanguageGroup.setDescription(
				String.format( "Perform all DocBook formatting for language %s", language )
		);

		for ( FormatOptions format : configuration.getFormats() ) {
			RenderTask renderTask = project.getTasks().add(
					String.format( RENDER_TASK_GROUP + "_%s_%s", language, format.getName() ),
					RenderTask.class
			);
			renderTask.setDescription(
					String.format(
							"Perform DocBook %s formatting for language %s", format.getName(), language
					)
			);
			renderTask.configure( this, language, format );
			if ( formatDependency != null ) {
				renderTask.dependsOn( formatDependency );
			}
			renderTask.dependsOn( stagingTask );
			formatLanguageGroup.dependsOn( renderTask );
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

		public File getWorkDirectory() {
			return directoryLayout.getRootJDocBookWorkDirectory();
		}

		public File getStagingDirectory() {
			return directoryLayout.getStagingDirectory();
		}

		public List<File> getFontDirectories() {
			return Collections.singletonList( directoryLayout.getFontsDirectory() );
		}

		public DocBookXsltResolutionStrategy getDocBookXsltResolutionStrategy() {
			return DocBookXsltResolutionStrategy.INCLUSIVE;
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

		for( File file : project.getConfigurations().getByName( DOCBOOK_CONFIG_NAME ).getFiles() ) {
			try {
				urls.add( file.toURI().toURL() );
			}
			catch ( MalformedURLException e ) {
				log.warn( "Unable to retrieve file url [" + file.getAbsolutePath() + "]; ignoring" );
			}
		}

		for( File file : project.getBuildscript().getConfigurations().getByName( ScriptHandler.CLASSPATH_CONFIGURATION ).getFiles() ) {
			try {
				urls.add( file.toURI().toURL() );
			}
			catch ( MalformedURLException e ) {
				log.warn( "Unable to retrieve file url [" + file.getAbsolutePath() + "]; ignoring" );
			}
		}

		for( File file : project.getConfigurations().getByName( STYLES_CONFIG_NAME ).getFiles() ) {
			try {
				urls.add( file.toURI().toURL() );
			}
			catch ( MalformedURLException e ) {
				log.warn( "Unable to retrieve file url [" + file.getAbsolutePath() + "]; ignoring" );
			}
		}

		return new URLClassLoader(
				urls.toArray( new URL[ urls.size() ] ),
				Thread.currentThread().getContextClassLoader()
		);
	}

	private class MasterLanguageDescriptorImpl implements MasterLanguageDescriptor {
		public Locale getLanguage() {
			return TranslationUtils.parse(
					JDocBookPlugin.this.getConfiguration().getMasterLanguage(),
					JDocBookPlugin.this.getConfiguration().getLocaleSeparator()
			);
		}

		public File getPotDirectory() {
			return JDocBookPlugin.this.directoryLayout.getPotSourceDirectory();
		}

		public File getBaseSourceDirectory() {
			return JDocBookPlugin.this.directoryLayout.getMasterSourceDirectory();
		}

		public File getRootDocumentFile() {
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

		private LinkedHashSet<ValueInjection> valueInjections;

		public LinkedHashSet<ValueInjection> getValueInjections() {
			if ( valueInjections == null ) {
				valueInjections = new LinkedHashSet<ValueInjection>();
				valueInjections.addAll( gradleConfiguration().getValueInjections() );

				if ( gradleConfiguration().isApplyStandardInjectionValues() ) {
					valueInjections.add( new ValueInjection( "version", project.getVersion().toString() ) );
					SimpleDateFormat dateFormat = new SimpleDateFormat( gradleConfiguration().getInjectionDateFormat() );
					valueInjections.add( new ValueInjection( "today", dateFormat.format( new Date() ) ) );
				}
			}
			return valueInjections;
		}

		public LinkedHashSet<String> getCatalogs() {
			return gradleConfiguration().getCatalogs();
		}

		public Profiling getProfiling() {
			return gradleConfiguration().getProfiling();
		}

		public String getDocBookVersion() {
			return null;
		}
	}

	public Locale fromLanguageString(String languageStr) {
		return TranslationUtils.parse( languageStr, configuration.getLocaleSeparator() );
	}

	private boolean scriptClassLoaderExtended = false;

	/*package*/ void prepareForRendering() {
		if ( scriptClassLoaderExtended ) {
			return;
		}
		scriptClassLoaderExtended = true;
		log.lifecycle( "Extending script classloader with the {} dependencies", JDocBookPlugin.STYLES_CONFIG_NAME );
		ClassLoader classloader = getProject().getBuildscript().getClassLoader();
		if( classloader instanceof ObservableUrlClassLoader ){
			ObservableUrlClassLoader scriptClassloader = (ObservableUrlClassLoader)classloader;
			for( File file : getProject().getConfigurations().getByName( JDocBookPlugin.STYLES_CONFIG_NAME ).getFiles() ) {
				try {
					scriptClassloader.addURL( file.toURI().toURL() );
				}
				catch ( MalformedURLException e ) {
					log.warn( "Unable to retrieve file url [" + file.getAbsolutePath() + "]; ignoring" );
				}
			}
		}

	}
}
