package org.jboss.gradle.plugins.jdocbook;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class JDocBookPlugin implements Plugin<Project> {
	/**
	 * {@inheritDoc}
	 */
	public void apply(Project project) {
		// TODO : can this be called multiple times on *a plugin instance*?
		//		the implication is whether it is safe to store instance state based on this method

		// set up the configuration used to define jDocBook-based artifacts (docbook, styles, etc).
        project.getConfigurations().add( "jDocBook" )
				.setVisible( false )
				.setTransitive( false )
				.setDescription( "The DocBook and jDocBook artifacts to use." );

		// set up our convention and configuration objects
		JDocBookConfiguration configuration = new JDocBookConfiguration();
		project.getConvention().getPlugins().put( "jdocbook", new JDocBookConvention( configuration, this ) );

		DirectoryLayout directoryLayout = new DirectoryLayout( project, this, configuration );
		MasterSourceFileResolver masterSourceFileResolver = new MasterSourceFileResolver( directoryLayout, configuration );


		// from here we need to iterate the formats and (translation) languages specified by the user and set up
		// the needed tasks.  Ideally I'd like to have each task handle one thing.  So for example, instantiate
		// an instance of the "apply translation" task for each translation language.  This allows the greatest
		// flexibility in terms of partial doc builds (e.g. "build the english docs", "build all pdf docs",
		// "build the english html docs").
		//
		// However, there is a disjoint here.  We have just registered the convention/config object above which
		// implies to me that the user config settings are not yet available.  So how can we know those settings
		// *before* setting up tasks?  Changes to core gradle code aside, the only one that comes to my mind
		// is a form of listening on the JDocBookConvention class.  Namely, whenever the
		// JDocBookConvention#configure method is executed (user initiated) after setting the config values
		// we could call back to the plugin to setup tasks on the project at that time.  I have to assume this messes
		// up task reporting/listing (gradle -t), but afaik it should work otherwise.
	}
}
