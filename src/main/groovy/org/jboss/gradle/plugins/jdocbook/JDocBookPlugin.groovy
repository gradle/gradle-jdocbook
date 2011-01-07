package org.jboss.gradle.plugins.jdocbook;


import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.jboss.gradle.plugins.jdocbook.tasks.RenderTask
import org.jboss.gradle.plugins.jdocbook.tasks.StyleStagingTask
import org.jboss.gradle.plugins.jdocbook.tasks.SynchronizePoTask
import org.jboss.gradle.plugins.jdocbook.tasks.SynchronizePotTask
import org.jboss.gradle.plugins.jdocbook.tasks.TranslateTask

/**
 * The Gradle plugin for jDocbook
 *
 * @author Steve Ebersole
 * @author Strong Liu
 */
public class JDocBookPlugin implements Plugin<Project> {
	Logger log = Logging.getLogger(JDocBookPlugin.class);
	public static final String STYLES_CONFIG_NAME = "jdocbookStyles";
	public static final String DOCBOOK_CONFIG_NAME = "jdocbook";

	public static final String STAGE_TASK_GROUP = "stageStyles";
	public static final String TRANSLATE_TASK_GROUP = "translateDocBook";
	public static final String UPDATE_TRANSLATION_TASK_GROUP = "updateTranslations";
	public static final String PROFILE_TASK_GROUP = "profileDocBook";
	public static final String RENDER_TASK_GROUP = "renderDocBook";
	public static final String JDOCBOOK_TASK_GROUP = "DocBook";
	public static final String UPDATE_PO_TASK_GROUP = "updatePo";
	public static final String UPDATE_POT_TASK_GROUP = "updatePot";
	public static final String BUILDDOC_TASK_GROUP = "buildDocs";
	private JDocBookConvention convention;
	Project project

	private String getDescriptionName(String displayName) {
		String.format("Docbook %s", (displayName) ? "($displayName)" : "")
	}

	private String getTaskName(String perfix, String displayName) {
		(displayName) ? String.format("%s_%s", perfix, displayName) : perfix
	}

	public void apply(Project project) {
		this.project = project
		applyConfiguration(project);
		convention = new JDocBookConvention(project);
		project.convention.plugins.put(DOCBOOK_CONFIG_NAME, convention);
		convention.conventionActions.add(new Action<JDocBookConvention>() {
			@Override
			public void execute(JDocBookConvention convention) {
				addGroupTask(BUILDDOC_TASK_GROUP, "Builds all documention")
				convention.books.all.each { book ->
					if ( book.concrete ) {
						//stage style task per book
						String bookTaskName = getTaskName(STAGE_TASK_GROUP, book.displayName)
						StyleStagingTask styleStageTask = addTask(bookTaskName, StyleStagingTask)
						styleStageTask.setDescription(String.format("Stage %s styles to the staging directory", getDescriptionName(book.displayName)))
						styleStageTask.book = book
						//update pot task per book
						bookTaskName = getTaskName(UPDATE_POT_TASK_GROUP, book.displayName)
						SynchronizePotTask updatePotTask = addTask(bookTaskName, SynchronizePotTask)
						updatePotTask.setDescription(String.format("Update the %s POT files from the current state of the master language sources", getDescriptionName(book.displayName)))
						updatePotTask.configure(book)
						//it is same if there is only one common book
						if ( bookTaskName != UPDATE_POT_TASK_GROUP ) {
							getTask(UPDATE_POT_TASK_GROUP).dependsOn updatePotTask
						}
						//render task per book
						Task renderBookTask = addGroupTask(getTaskName(RENDER_TASK_GROUP, book.displayName), String.format("Perform %s formatting", getDescriptionName(book.displayName)))
						getTask(BUILDDOC_TASK_GROUP).dependsOn renderBookTask
						//render/translation/updatepo task for master lang
						configBookPerLang(book.masterLanguage, book, styleStageTask)
						//translation task per book
						if ( !book.translations.empty ) {
							Task translationTask = addGroupTask(getTaskName(TRANSLATE_TASK_GROUP, book.displayName), String.format("Perform %s translation", getDescriptionName(book.displayName)))
							if ( getTask(TRANSLATE_TASK_GROUP) == null ) {
								addGroupTask(TRANSLATE_TASK_GROUP, "Perform all DocBook translations").dependsOn translationTask
							}
							//update po task per book
							Task updatePoTask = addGroupTask(getTaskName(UPDATE_PO_TASK_GROUP, book.displayName), String.format("Update %s PO files", getDescriptionName(book.displayName)))
							if ( getTask(UPDATE_PO_TASK_GROUP) == null ) {
								addGroupTask(UPDATE_PO_TASK_GROUP, 'Update the PO files for all translations from the current state of the POT files').dependsOn updatePoTask
							}
							//render/translation/updatepo task per translation lang
							book.translations.each { lang ->
								configBookPerLang(lang, book, styleStageTask)
							}

						}
					}
					//Group POT task for updating all pot files in the project
					Task potTask = addGroupTask(UPDATE_POT_TASK_GROUP, 'Update the POT files from the current state of the master language sources')
					Task updateTransTask = addGroupTask(UPDATE_TRANSLATION_TASK_GROUP, "Update POT and all PO files").dependsOn(potTask);
					if ( getTask(UPDATE_PO_TASK_GROUP) ) {
						updateTransTask.dependsOn(getTask(UPDATE_PO_TASK_GROUP))
					}
				}
			}

		})
	}


	def configBookPerLang(String lang, Book book, Task styleStageTask) {
		TranslateTask translateTask;

		if ( lang != book.masterLanguage ) {
			//translation task per lang per book
			translateTask = addTask(String.format("%s_%s", getTaskName(TRANSLATE_TASK_GROUP, book.displayName), lang), TranslateTask)
			translateTask.setDescription(String.format("Perform %s translation for language %s", getDescriptionName(book.displayName), lang))
			translateTask.configure(book, lang)
			getTask(getTaskName(TRANSLATE_TASK_GROUP, book.displayName)).dependsOn translateTask
			//synchronize po task per lang per book
			SynchronizePoTask poTask = addTask(String.format("%s_%s", getTaskName(UPDATE_PO_TASK_GROUP, book.displayName), lang), SynchronizePoTask)
			poTask.setDescription(String.format("Update %s PO files from current POT for language %s", getDescriptionName(book.displayName), lang));
			poTask.configure(book, lang);
			getTask(getTaskName(UPDATE_PO_TASK_GROUP, book.displayName)).dependsOn poTask
		}
		book.formats.allObjects {format ->
			if ( format.enable ) {
				RenderTask render = addTask(String.format("%s_%s_%s", getTaskName(RENDER_TASK_GROUP, book.displayName), lang, format.name),
						RenderTask)

				render.setDescription(String.format(
						"Perform %s %s formatting for language %s", getDescriptionName(book.displayName), format.name, book.masterLanguage
				))
				render.configure(book, book.masterLanguage, format)
				render.dependsOn styleStageTask
				if ( lang != book.masterLanguage && translateTask != null ) {
					render.dependsOn translateTask
				}
				getTask(getTaskName(RENDER_TASK_GROUP, book.displayName)).dependsOn render
			}
		}

	}

	private Task getTask(String name) {
		project.tasks.findByName(name)
	}

	private <T extends Task> T addTask(String name, Class<T> type) {
		project.tasks.add(name, type)
	}

	private Task addGroupTask(String name, String description) {
		if ( getTask(name) == null ) {
			Task task = project.tasks.add(name)
			task.setDescription(description)
			task.setGroup(JDOCBOOK_TASK_GROUP)
			return task
		}
		else {
			return getTask(name)
		}
	}

	private void applyConfiguration(Project project) {
		project.configurations.add(DOCBOOK_CONFIG_NAME).setVisible(false).setTransitive(false).setDescription("The DocBook artifact(s) to use.");
		project.configurations.add(STYLES_CONFIG_NAME).setVisible(false).setTransitive(true).setDescription("Defines any jDocBook styles artifacts to apply");
	}
}
