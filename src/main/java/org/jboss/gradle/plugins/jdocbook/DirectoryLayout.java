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

	private final File rootJDocBookSourceDirectory;
	private final File rootJDocBookTranslationWorkDirectory;
	private final File rootJDocBookProfileWorkDirectory;

	public DirectoryLayout(Project project, JDocBookPlugin plugin) {
		this.project = project;
		this.plugin = plugin;

		this.rootJDocBookSourceDirectory = new File( new File( new File( project.getProjectDir(), "src" ), "main" ), "docbook" );
		File rootJDocBookWorkDirectory = new File( new File( project.getBuildDir(), "docbook" ), "work" );
		this.rootJDocBookTranslationWorkDirectory = new File( rootJDocBookWorkDirectory, "translate" );
		this.rootJDocBookProfileWorkDirectory = new File( rootJDocBookWorkDirectory, "profile" );
	}


	// source dir layout ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public File getMasterSourceDirectory() {
		return getTranslationSourceDirectory( plugin.getConfiguration().getMasterTranslationLanguage() );
	}

	public File getPotSourceDirectory() {
		return getTranslationSourceDirectory( "pot" );
	}

	public File getTranslationSourceDirectory(String language) {
		return new File( rootJDocBookSourceDirectory, language );
	}


	// translation work dir layout ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	public File getTranslationDirectory(String language) {
		return new File( rootJDocBookTranslationWorkDirectory, language );
	}


	// profile work dir layout ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	public File getProfilingDirectory(String language) {
		return new File( rootJDocBookProfileWorkDirectory, language );
	}

}
