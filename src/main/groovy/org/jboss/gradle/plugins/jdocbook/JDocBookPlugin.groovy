package org.jboss.gradle.plugins.jdocbook;


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.jboss.gradle.plugins.jdocbook.task.CreateTasksPerBookAction

/**
 * The Gradle plugin for jDocbook
 *
 * @author Steve Ebersole
 * @author Strong Liu
 */
public class JDocBookPlugin implements Plugin<Project> {
	Logger log = Logging.getLogger(JDocBookPlugin);
	public static final String STYLES_CONFIG_NAME = "jdocbookStyles";
	public static final String DOCBOOK_CONFIG_NAME = "jdocbook";
	Project project

	public void apply(final Project project) {
		this.project = project
		applyConfiguration(project)
		def books = project.container(Book) { name -> new Book(name, project) }
		JDocBookConvention convention = new JDocBookConvention(project, books)
		project.convention.plugins.put(DOCBOOK_CONFIG_NAME, convention)
		convention.configBookActions.add(new CreateTasksPerBookAction(project))
	}

	private void applyConfiguration(Project project) {
		project.configurations.add(DOCBOOK_CONFIG_NAME).setVisible(false).setTransitive(false).setDescription("The DocBook artifact(s) to use.");
		project.configurations.add(STYLES_CONFIG_NAME).setVisible(false).setTransitive(true).setDescription("Defines any jDocBook styles artifacts to apply");
	}
}
