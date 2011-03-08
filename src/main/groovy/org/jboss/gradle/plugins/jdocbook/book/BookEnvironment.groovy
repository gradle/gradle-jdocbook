package org.jboss.gradle.plugins.jdocbook.book

import org.gradle.api.Project
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin
import org.jboss.jdocbook.Environment
import org.jboss.jdocbook.JDocBookProcessException
import org.jboss.jdocbook.MasterLanguageDescriptor
import org.jboss.jdocbook.util.ResourceDelegateSupport
import org.jboss.jdocbook.util.TranslationUtils
import org.jboss.jdocbook.util.XIncludeHelper

/**
 *
 * @author: Strong Liu
 */
class BookEnvironment implements Environment, MasterLanguageDescriptor {
	Logger log = Logging.getLogger(Environment);
	Book book;
	MasterLanguageDescriptor masterLanguageDescriptor;
	Project project
	ResourceDelegate resourceDelegate
	def outputDirName
	def workDirName
	def stageDirName
	def publishDirName
	def profileDirName

	BookEnvironment(Book book, Project project) {
		this.book = book
		this.project = project
		this.masterLanguageDescriptor = this
		this.resourceDelegate = new ResourceDelegate()
		this.outputDirName = "${project.buildDirName}/docbook"
		this.workDirName = outputDirName + "/work"
		this.stageDirName = outputDirName + "/stage"
		this.publishDirName = outputDirName + "/publish"
		this.profileDirName = outputDirName + "/profile"
		this.workDirName = "$outputDirName/work/${book.name}"
		this.stageDirName = "$outputDirName/stage/${book.name}"
		this.publishDirName = "$outputDirName/publish/${book.name}"
		this.profileDirName = "$outputDirName/profile/${book.name}"
	}

	@Override
	Locale getLanguage() {
		TranslationUtils.parse(book.masterLanguage, book.localeSeparator)
	}

	Locale getLanguage(lang) {
		TranslationUtils.parse(lang, book.localeSeparator)
	}

	public ResourceDelegate getResourceDelegate() {
		return resourceDelegate
	}

	@Override
	Set<File> getDocumentFiles() {
		def files = [] as Set
		files << getRootDocumentFile()
		XIncludeHelper.findAllInclusionFiles(getRootDocumentFile(), files);
		return files

	}

	@Override
	Environment.DocBookXsltResolutionStrategy getDocBookXsltResolutionStrategy() {
		return Environment.DocBookXsltResolutionStrategy.INCLUSIVE
	}

	private ClassLoader buildResourceDelegateClassLoader() {
		List<URL> urls = new ArrayList<URL>();

		if ( stagingDirectory.exists() ) {
			try {
				urls.add(stagingDirectory.toURI().toURL());
			}
			catch (MalformedURLException e) {
				throw new JDocBookProcessException("Unable to resolve staging directory to URL", e);
			}
		}
		def feedingClasspathWithDependencies = { configuration ->
			configuration.files.each { File file ->
				try {
					urls.add(file.toURI().toURL());
				}
				catch (MalformedURLException e) {
					log.warn("Unable to retrieve file url [" + file.getAbsolutePath() + "]; ignoring");
				}
			}
		}
		feedingClasspathWithDependencies(project.configurations."${JDocBookPlugin.DOCBOOK_CONFIG_NAME}")
		feedingClasspathWithDependencies(project.buildscript.configurations."${ScriptHandler.CLASSPATH_CONFIGURATION}")
		feedingClasspathWithDependencies(project.configurations."${JDocBookPlugin.STYLES_CONFIG_NAME}")
		return new URLClassLoader(
				urls.toArray(new URL[urls.size()]),
				Thread.currentThread().getContextClassLoader()
		);
	}

	File getWorkDirectory() {
		project.file(workDirName)
	}

	File getWorkDirPerLang(lang) {
		new File(workDirectory, lang)
	}

	File getProfileDirPerLang(lang) {
		new File(profileDirName, lang)
	}

	File getPublishDirPerLang(lang) {
		project.file("$publishDirName/$lang")
	}

	File getStagingDirectory() {
		project.file(stageDirName)
	}

	List<File> getFontDirectories() {
		def list = []
		if ( book.fontsDirName == null ) {
			book.fontsDirName = "${book.baseDirName}/${book.masterLanguage}/fonts"
		}
		def font = project.file(book.fontsDirName)
		if ( font.exists() && font.isDirectory() ) {
			list << project.file(book.fontsDirName)
		}
		list << new File(stagingDirectory, "fonts")
		return list
	}

	File getPotDirectory() {
		project.file(book.potDirName)
	}

	File getImagesDirectory() {
		if ( book.imagesDirName == null ) {
			book.imagesDirName = "${book.baseDirName}/${book.masterLanguage}/images"
		}
		project.file(book.imagesDirName)
	}

	File getCssDirectory() {
		if ( book.cssDirName == null ) {
			book.cssDirName = "${book.baseDirName}/${book.masterLanguage}/css"
		}
		project.file(book.cssDirName)
	}

	File getSourceDirPerLang(lang) {
		project.file("${book.baseDirName}/$lang")
	}

	File getBaseSourceDirectory() {
		getSourceDirPerLang(book.masterLanguage)
	}

	File getRootDocumentFile() {
		new File(getBaseSourceDirectory(), book.masterSourceDocumentName)
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
