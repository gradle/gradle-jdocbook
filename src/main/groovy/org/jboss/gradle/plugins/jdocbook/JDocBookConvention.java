package org.jboss.gradle.plugins.jdocbook;

import groovy.lang.Closure;
import org.gradle.util.ConfigureUtil;

/**
 * Provides a convention object for exposure to users for the purpose of configuring jDocBook.  Acts as
 * the bridge between the user and a {@link JDocBookConfiguration}.
 *
 * @author Steve Ebersole
 * @author Hans Dockter
 */
public class JDocBookConvention {
	private final JDocBookPlugin plugin;
	private int count = 0;

	public JDocBookConvention(JDocBookPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * This is the method performed by users to configure jDocBook exceptions to the jDocBook
	 * conventions (defaults).
	 *
	 * @param closure The configuration closure
	 */
	public void jdocbook(Closure closure) {
		ConfigureUtil.configure( closure, plugin.getConfiguration() );
		plugin.applyConfiguration( ++count > 1 );
	}
}
