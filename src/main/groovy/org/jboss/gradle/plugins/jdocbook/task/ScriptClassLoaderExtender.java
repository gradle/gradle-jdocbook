package org.jboss.gradle.plugins.jdocbook.task;

import java.io.File;
import java.net.MalformedURLException;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.util.MutableURLClassLoader;
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin;

/**
 * @author Steve Ebersole
 */
public class ScriptClassLoaderExtender {
    private static final Logger log = Logging.getLogger( ScriptClassLoaderExtender.class.getName() );

    private static boolean SCRIPT_CLASSLOADER_EXTENDED = false;

    public static synchronized void extendScriptClassLoader(Project project) {
        if ( SCRIPT_CLASSLOADER_EXTENDED ) {
            return;
        }

        SCRIPT_CLASSLOADER_EXTENDED = true;

		log.lifecycle( "Extending script classloader with the {} dependencies", JDocBookPlugin.XSL_CONFIG_NAME );
        final ClassLoader classloader = project.getBuildscript().getClassLoader();
        if ( classloader instanceof MutableURLClassLoader ) {
			final MutableURLClassLoader scriptClassloader = (MutableURLClassLoader) classloader;
            for ( File file : project.getConfigurations().getByName( JDocBookPlugin.XSL_CONFIG_NAME ).getFiles() ) {
                try {
					log.trace( "  Adding artifact to script classloader extension : " + file.getPath() );
                    scriptClassloader.addURL( file.toURI().toURL() );
                }
                catch (MalformedURLException e) {
                    log.warn( "Unable to retrieve file url [{}]; ignoring", file.getAbsolutePath() );
                }
            }
            Thread.currentThread().setContextClassLoader( classloader );
        }
		else {
			log.warn( "Not able to extend script classloader" );
		}
    }
}
