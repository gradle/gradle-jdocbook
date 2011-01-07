package org.jboss.gradle.plugins.jdocbook.book

import org.gradle.api.Project
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin
import org.jboss.jdocbook.Environment
import org.jboss.jdocbook.Environment.DocBookXsltResolutionStrategy
import org.jboss.jdocbook.JDocBookProcessException
import org.jboss.jdocbook.MasterLanguageDescriptor
import org.jboss.jdocbook.util.ResourceDelegateSupport

/**
 *
 * @author: Strong Liu
 */
class EnvironmentImpl implements Environment {
	Logger log = Logging.getLogger(Environment.class);
	Book book;
	MasterLanguageDescriptor masterLanguageDescriptor;
	Project project
	ResourceDelegate resourceDelegate = new ResourceDelegate()
	EnvironmentImpl(Book book,Project project) {
		this.book = book
		this.project = project
		this.masterLanguageDescriptor = new MasterLanguageDescriptorImpl(book)
	}
	@Override
	public ResourceDelegate getResourceDelegate(){
		resourceDelegate
	}
	@Override
	File getWorkDirectory() {
		project.file(getSourceSet().work())
	}

	@Override
	File getStagingDirectory() {
		project.file(getSourceSet().stage())
	}
	//FIXME

	@Override
	List<File> getFontDirectories() {
		return null
	}

	@Override
	DocBookXsltResolutionStrategy getDocBookXsltResolutionStrategy() {
		return DocBookXsltResolutionStrategy.INCLUSIVE
	}
	public BookStructure getSourceSet(){
		book.sourceSet
	}
	private ClassLoader buildResourceDelegateClassLoader() {
		List<URL> urls = new ArrayList<URL>();

		if ( getStagingDirectory().exists() ) {
			try {
				urls.add(getStagingDirectory().toURI().toURL());
			}
			catch (MalformedURLException e) {
				throw new JDocBookProcessException("Unable to resolve staging directory to URL", e);
			}
		}

		for ( File file: project.getConfigurations().getByName(JDocBookPlugin.DOCBOOK_CONFIG_NAME).getFiles() ) {
			try {
				urls.add(file.toURI().toURL());
			}
			catch (MalformedURLException e) {
				log.warn("Unable to retrieve file url [" + file.getAbsolutePath() + "]; ignoring");
			}
		}

		for ( File file: project.getBuildscript().getConfigurations().getByName(ScriptHandler.CLASSPATH_CONFIGURATION).getFiles() ) {
			try {
				urls.add(file.toURI().toURL());
			}
			catch (MalformedURLException e) {
				log.warn("Unable to retrieve file url [" + file.getAbsolutePath() + "]; ignoring");
			}
		}

		for ( File file: project.getConfigurations().getByName(JDocBookPlugin.STYLES_CONFIG_NAME).getFiles() ) {
			try {
				urls.add(file.toURI().toURL());
			}
			catch (MalformedURLException e) {
				log.warn("Unable to retrieve file url [" + file.getAbsolutePath() + "]; ignoring");
			}
		}
		return new URLClassLoader(
				urls.toArray(new URL[urls.size()]),
				Thread.currentThread().getContextClassLoader()
		);
	}

	class ResourceDelegate extends ResourceDelegateSupport {
		private ClassLoader loader;

		@Override
		protected ClassLoader getResourceClassLoader() {
			if ( loader == null ) {
				loader = buildResourceDelegateClassLoader();
			}
			return loader;
		}
	}
}
