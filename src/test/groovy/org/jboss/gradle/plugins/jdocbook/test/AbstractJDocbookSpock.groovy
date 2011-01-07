package org.jboss.gradle.plugins.jdocbook.test

import org.gradle.api.Project
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
	def convention
	Resources resources = new Resources(this.class)

	def setup() {
		project = HelperUtil.createRootProject(new File("build/tmp/tests/" + this.getClass().getSimpleName()))
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
