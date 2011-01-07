package org.jboss.gradle.plugins.jdocbook.task

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.util.ObservableUrlClassLoader
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.jboss.jdocbook.render.FormatOptions
import org.jboss.jdocbook.render.RenderingSource
import org.jboss.jdocbook.util.TranslationUtils
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin

/**
 * Task for performing DocBook rendering.
 *
 * @author Strong Liu
 */
class RenderTask extends org.jboss.gradle.plugins.jdocbook.tasks.BookTask {

	private static final Logger log = Logging.getLogger(org.jboss.gradle.plugins.jdocbook.tasks.RenderTask.class);

//	Book book;
	@Input
	def language;

	def format;
	RenderingSourceImpl renderingSource

	public void configure(Book book, def language, def format) {
		this.book = book
		this.language = language
		this.format = format
		this.renderingSource = new RenderingSourceImpl(book, project, language)
	}

	@Input
	public String getBookName() {
		return book.name
	}

	@Input
	public String getFormatName() {
		return format.name
	}

	@TaskAction
	public void render() {
		prepareForRendering()
		log.lifecycle("rendering {} / {}", language, format.name);
		book.componentRegistry.renderer.render(renderingSource, (FormatOptions) format)
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
			Thread.currentThread().setContextClassLoader classloader
		}
	}

	@OutputDirectory
	File getPublishingDirectory() {
		renderingSource.resolvePublishingBaseDirectory()
	}

	class RenderingSourceImpl implements RenderingSource {
		Book book
		Project project
		def lang

		RenderingSourceImpl(Book book, Project project, def lang) {
			this.book = book
			this.project = project
			this.lang = lang
		}

		@Override
		Locale getLanguage() {
			return TranslationUtils.parse(lang, book.localeSeparator)
		}

		@Override
		File resolveSourceDocument() {
			return project.file(book.sourceSet.master())
		}

		@Override
		File resolvePublishingBaseDirectory() {
			return project.file(book.sourceSet.publish(lang))
		}

		@Override
		File getXslFoDirectory() {
			return null
		}
	}
}
