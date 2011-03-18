package org.jboss.gradle.plugins.jdocbook.task

import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.util.ObservableUrlClassLoader
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.jboss.jdocbook.render.RenderingSource
import org.gradle.api.tasks.OutputDirectory

/**
 * Task for performing DocBook rendering.
 *
 * @author Strong Liu
 */
class RenderTask extends BookTask implements RenderingSource {
	static final Logger log = Logging.getLogger(RenderTask);
	def format;

	public void configure(Book book, def language, def format) {
		configure(book, language)
		this.format = format
	}

	@Input public String getFormatName() {format.name}

	@Input @Optional public String getFormatFinalName() {format.finalName}

	@Input @Optional public String getStylesheet() {return format.stylesheet}


	@TaskAction
	public void render() {
		prepareForRendering()
		log.lifecycle("rendering Book({}) {} / {}", book.name, lang, format.name);
		book.componentRegistry.renderer.render(this, format)
	}

	private boolean scriptClassLoaderExtended = false;

	private void prepareForRendering() {
		if ( scriptClassLoaderExtended ) {
			return;
		}
		scriptClassLoaderExtended = true;
		log.lifecycle("Extending script classloader with the {} dependencies", JDocBookPlugin.STYLES_CONFIG_NAME);
		ClassLoader classloader = project.buildscript.classLoader
		if ( classloader instanceof ObservableUrlClassLoader ) {
			ObservableUrlClassLoader scriptClassloader = (ObservableUrlClassLoader) classloader;
			for ( File file: project.configurations.getByName(JDocBookPlugin.STYLES_CONFIG_NAME).getFiles() ) {
				try {
					scriptClassloader.addURL(file.toURI().toURL());
				}
				catch (MalformedURLException e) {
					log.warn("Unable to retrieve file url [" + file.getAbsolutePath() + "]; ignoring");
				}
			}
			Thread.currentThread().setContextClassLoader(classloader)
		}
	}

	@Override
	@InputDirectory
	@Optional
	File getXslFoDirectory() {
		existsOrNull(book.environment.getWorkDirPerLang("xsl-fo"))
	}

	@InputFile
	public File getSourceDocument(){
		if ( book.profiling.enabled ) {
			return new File(book.environment.getProfileDirPerLang(lang), book.masterSourceDocumentName)
		}
		else if ( lang == book.masterLanguage ) {
			return book.environment.rootDocumentFile
		}
		else {
			return new File(book.environment.getWorkDirPerLang(lang), book.masterSourceDocumentName)
		}
	}
	public File resolveSourceDocument() {
		getSourceDocument()
	}
	@OutputDirectory
	File getPublishingBaseDirectory(){
		resolvePublishingBaseDirectory()
	}
	File resolvePublishingBaseDirectory() {
		book.environment.getPublishDirPerLang(lang)
	}
}
