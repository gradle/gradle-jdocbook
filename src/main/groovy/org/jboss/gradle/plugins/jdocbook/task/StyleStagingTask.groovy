/*
 * jDocBook, processing of DocBook sources
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.jboss.gradle.plugins.jdocbook.task;


import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration.State
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin
import org.jboss.gradle.plugins.jdocbook.book.Book

/**
 * Applies staging of style artifacts
 *
 * @author Steve Ebersole
 */
@SuppressWarnings(["UnusedDeclaration"])
public class StyleStagingTask extends BookTask {
	private static final Logger log = Logging.getLogger(StyleStagingTask.class);
	org.jboss.gradle.plugins.jdocbook.book.Book book
	private JDocBookPlugin plugin;
	@Input
	@Optional
	def jdocbookStyle=project.configurations."$JDocBookPlugin.STYLES_CONFIG_NAME"



	@InputFiles
	@Optional
	public File getBookImages() {
		project.file(book.sourceSet.images())
	}

	@InputFiles
	@Optional
	public File getBookCss() {
		project.file(book.sourceSet.css())
	}

	@OutputDirectory
	@Optional
	public File getStagingDirectory() {
		return project.file(book.sourceSet.stage());
	}
	//FIXME skip this action if no jdocbook style defined
	@TaskAction
	public void stageJDocbookStyles() {
		log.lifecycle("Staging styles to {}", getStagingDirectory());
		if ( jdocbookStyle.state != State.RESOLVED ) {
			jdocbookStyle.resolve();
		}
		jdocbookStyle.filter(jdocbookStyleSpec).each {File file ->
			project.copy {
				into(getStagingDirectory())
				from(project.zipTree(file).matching { exclude "META-INF/**" })
			}
		}
	}

	@TaskAction
	public void stageBookImages() {
		def image = getBookImages()
		if ( image!=null && image.exists() && image.list() ) {
			log.lifecycle("Staging project images to {}", getStagingDirectory());
			project.copy {
				into project.file(book.sourceSet.stage()+'/images'+'/'+getBookImages().name)
				from getBookImages()
			}
		}
	}

	@TaskAction
	public void stageBookCSS() {
		def css = getBookCss()
		if ( css!=null && css.exists() && css.list() ) {
			log.lifecycle("Staging project css to {}", getStagingDirectory());
			project.copy {
				into project.file(book.sourceSet.stage()+'/css'+'/'+getBookCss().name)
				from getBookCss()
			}
		}
	}

	Spec<File> jdocbookStyleSpec = new Spec<File>() {
		boolean isSatisfiedBy(File file) {
			return file.name.endsWith("jdocbook-style")
		}
	}
}
