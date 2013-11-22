package org.jboss.gradle.plugins.jdocbook.task;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin;

/**
 * @author Steve Ebersole
 */
public class ScriptClassLoaderExtender {
    private static final Logger log = Logging.getLogger( ScriptClassLoaderExtender.class.getName() );

	/**
	 * A memento
	 */
	public static class Result {
		private final ClassLoader originalTccl;

		private Result(ClassLoader originalTccl) {
			this.originalTccl = originalTccl;
		}
	}

    public static synchronized Result extendScriptClassLoader(Project project) {
		log.lifecycle( "Extending script classloader with the {} dependencies", JDocBookPlugin.XSL_CONFIG_NAME );

		List<URL> xslDepUrls = null;

		final java.util.Set<java.io.File> xslDepFiles = project.getConfigurations()
				.getByName( JDocBookPlugin.XSL_CONFIG_NAME )
				.getFiles();
		if ( ! xslDepFiles.isEmpty() ) {
			// This will be used to collect the 'jdocbookXsl' Configuration files (dependencies) to build our extended
			// ClassLoader
			xslDepUrls = new ArrayList<URL>();

			for ( File xslDepFile : xslDepFiles ) {
				try {
					log.trace( "  Adding artifact to script classloader extension : " + xslDepFile.getPath() );
					xslDepUrls.add( xslDepFile.toURI().toURL() );
				}
				catch (MalformedURLException e) {
					log.warn( "Unable to retrieve file url [{}]; ignoring", xslDepFile.getAbsolutePath() );
				}
			}
		}

		final Result tcclResult = new Result( Thread.currentThread().getContextClassLoader() );

		if ( xslDepUrls == null ) {
			Thread.currentThread().setContextClassLoader( project.getBuildscript().getClassLoader() );
		}
		else {
			// build a new ClassLoader to use as TCCL.  This new ClassLoader will use the "buildscript" ClassLoader from
			// Gradle as its parent
			final URLClassLoader extendedClassLoader = new URLClassLoader(
					xslDepUrls.toArray( new URL[ xslDepUrls.size() ] ),
					project.getBuildscript().getClassLoader()
			);
			Thread.currentThread().setContextClassLoader( extendedClassLoader );
		}

		return tcclResult;
    }

	public static synchronized void unextendScriptClassLoader(Result extensionResult) {
		Thread.currentThread().setContextClassLoader( extensionResult.originalTccl );
	}
}
