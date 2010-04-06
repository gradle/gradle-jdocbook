package org.jboss.gradle.plugins.jdocbook.i18n;

import java.io.File;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public interface TranslationEnvironment {
	public File getMasterSourceFileBase();
	public File getTranslationBaseSourceDirectory();
	public File getTranslationBaseOutputDirectory();
}
