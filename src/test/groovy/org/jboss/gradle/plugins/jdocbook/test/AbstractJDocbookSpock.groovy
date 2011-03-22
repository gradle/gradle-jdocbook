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



package org.jboss.gradle.plugins.jdocbook.test

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.jboss.gradle.plugins.jdocbook.JDocBookConvention
import org.jboss.gradle.plugins.jdocbook.JDocBookPlugin
import org.jboss.gradle.plugins.jdocbook.test.util.HelperUtil
import org.jboss.gradle.plugins.jdocbook.test.util.Resources
import spock.lang.Specification

/**
 *
 * @author: Strong Liu
 */
class AbstractJDocbookSpock extends Specification {
    Project project
    JDocBookPlugin plugin
    JDocBookConvention convention
    Resources resources = new Resources(this.class)

    def setup() {
        project = HelperUtil.createRootProject(new File("build/tmp/tests/" + this.getClass().getSimpleName()))
        project.getLogging().setLevel(LogLevel.DEBUG)
        plugin = project.plugins.apply(JDocBookPlugin)
        convention = project.convention.plugins.jdocbook
    }

    def cleanup() {
        //project.delete "build"
    }

    def applyScript(def script) {
        project.apply from: resources.getResource(script)
    }

    def applyRepositories() {
        project.repositories {
            mavenCentral()
            mavenRepo name: "mavenCache", urls: "file://" + System.getProperty('user.home') + "/.m2/repository/"
            mavenRepo name: "jboss", urls: "http://repository.jboss.org/nexus/content/groups/public/"
        }
    }

    def applyJDocbookStyle() {
        project.dependencies {
            jdocbookStyles "org.hibernate:hibernate-jdocbook-style:2.0.1"
        }
    }


}
