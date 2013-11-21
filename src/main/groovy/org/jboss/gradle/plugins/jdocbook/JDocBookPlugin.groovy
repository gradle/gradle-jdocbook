/*
 * jDocBook, processing of DocBook sources
 *
 * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
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
package org.jboss.gradle.plugins.jdocbook;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.jboss.gradle.plugins.jdocbook.book.Book
import org.jboss.gradle.plugins.jdocbook.task.CreateTasksPerBookAction
import org.gradle.api.artifacts.Configuration

/**
 * The Gradle plugin for jDocBook
 *
 * @author Steve Ebersole
 * @author Strong Liu
 */
public class JDocBookPlugin implements Plugin<Project> {
    private static final Logger log = Logging.getLogger(JDocBookPlugin);
    public static final String STYLES_CONFIG_NAME = "jdocbookStyles";
    public static final String XSL_CONFIG_NAME = "jdocbookXsl";
    public static final String DOCBOOK_CONFIG_NAME = "jdocbook";

    public void apply(final Project project) {
        applyConfiguration( project )
        def books = project.container(Book) { String name -> new Book( name, project ) }
        JDocBookConvention convention = new JDocBookConvention( project, books )
        project.convention.plugins.put( DOCBOOK_CONFIG_NAME, convention )
        convention.configBookActions.add( new CreateTasksPerBookAction( project ) )
    }

    private static void applyConfiguration(Project project) {
        Configuration deprecatedConfig = project.configurations.create( DOCBOOK_CONFIG_NAME )
				.setVisible( false )
				.setTransitive( false )
				.setDescription( "The DocBook artifact(s) to use (deprecated, use jdocbookXsl instead)." );
        project.configurations.create( XSL_CONFIG_NAME )
				.extendsFrom( deprecatedConfig )
				.setVisible( false )
				.setTransitive( true )
				.setDescription( "Defines any DocBook XSL artifacts to make available to the build" );
        project.configurations.create( STYLES_CONFIG_NAME )
				.setVisible( false )
				.setTransitive( true )
				.setDescription( "Defines any jDocBook styles artifacts to apply" );
    }
}
