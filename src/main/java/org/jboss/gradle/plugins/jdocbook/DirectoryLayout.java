package org.jboss.gradle.plugins.jdocbook;

import java.io.File;

import org.gradle.api.Project;

/**
 * Represents the layout of the directories jDocBook will need.
 * <p/>
 * Currently these are hardcoded to help facilitate initial development; once stabilized it is expected we
 * will add a source-set and allow configuration of some of these entries.  We centralize here to help
 * facilitate that later move.
 *
 * @author Steve Ebersole
 */
public class DirectoryLayout {
	private final Project project;
	private final JDocBookPlugin plugin;


	public DirectoryLayout(Project project, JDocBookPlugin plugin) {
		this.project = project;
		this.plugin = plugin;
	}


	// source dir layout ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private File rootJDocBookSourceDirectory;

	private File getRootJDocBookSourceDirectory() {
		if ( rootJDocBookSourceDirectory == null ) {
			final File src = new File( project.getProjectDir(), "src" );
			final File srcMain = new File( src, "main" );
			this.rootJDocBookSourceDirectory = new File( srcMain, "docbook"  );
		}
		return rootJDocBookSourceDirectory;
	}

	public File getMasterSourceDirectory() {
		return getTranslationSourceDirectory( plugin.getConfiguration().getMasterLanguage() );
	}

	public File getPotSourceDirectory() {
		return getTranslationSourceDirectory( "pot" );
	}

	public File getTranslationSourceDirectory(String language) {
		return new File( getRootJDocBookSourceDirectory(), language );
	}


	// staging directory ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private File stagingDirectory;

	public File getStagingDirectory() {
		if ( stagingDirectory == null ) {
			stagingDirectory = new File( new File( project.getBuildDir(), "docbook" ), "staging" );
		}
		return stagingDirectory;
	}


 	// work directory ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private File rootJDocBookWorkDirectory;

	public File getRootJDocBookWorkDirectory() {
		if ( rootJDocBookWorkDirectory == null ) {
			rootJDocBookWorkDirectory = new File( new File( project.getBuildDir(), "docbook" ), "work" );
		}
		return rootJDocBookWorkDirectory;
	}


	// translation work dir layout ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private File rootJDocBookTranslationWorkDirectory;

	private File getRootJDocBookTranslationWorkDirectory() {
		if ( rootJDocBookTranslationWorkDirectory == null ) {
			rootJDocBookTranslationWorkDirectory = new File( getRootJDocBookWorkDirectory(), "translate" );
		}
		return rootJDocBookTranslationWorkDirectory;
	}

	public File getTranslationDirectory(String language) {
		return new File( getRootJDocBookTranslationWorkDirectory(), language );
	}


	// profile work dir layout ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	private File rootJDocBookProfileWorkDirectory;

	private File getRootJDocBookProfileWorkDirectory() {
		if ( rootJDocBookProfileWorkDirectory == null ) {
			rootJDocBookProfileWorkDirectory = new File( getRootJDocBookWorkDirectory(), "profile" );
		}
		return rootJDocBookProfileWorkDirectory;
	}

	public File getProfilingDirectory(String language) {
		return new File( getRootJDocBookProfileWorkDirectory(), language );
	}

}
