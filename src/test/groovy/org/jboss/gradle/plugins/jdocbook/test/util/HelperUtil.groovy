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



package org.jboss.gradle.plugins.jdocbook.test.util

import java.rmi.server.UID
import org.apache.ivy.core.module.descriptor.Configuration
import org.apache.ivy.core.module.descriptor.DefaultDependencyDescriptor
import org.apache.ivy.core.module.descriptor.DefaultExcludeRule
import org.apache.ivy.core.module.descriptor.DefaultModuleDescriptor
import org.apache.ivy.core.module.id.ArtifactId
import org.apache.ivy.core.module.id.ModuleId
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.plugins.matcher.ExactPatternMatcher
import org.apache.ivy.plugins.matcher.PatternMatcher
import org.codehaus.groovy.control.CompilerConfiguration
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.internal.AsmBackedClassGenerator
import org.gradle.api.internal.ClassGenerator
import org.gradle.api.internal.artifacts.configurations.DefaultConfiguration
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.internal.artifacts.publish.DefaultPublishArtifact
import org.gradle.api.internal.project.DefaultProject
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.project.taskfactory.AnnotationProcessingTaskFactory
import org.gradle.api.internal.project.taskfactory.ITaskFactory
import org.gradle.api.internal.project.taskfactory.TaskFactory
import org.gradle.api.specs.AndSpec
import org.gradle.api.specs.Spec
import org.gradle.groovy.scripts.DefaultScript
import org.gradle.groovy.scripts.Script
import org.gradle.groovy.scripts.ScriptSource
import org.gradle.groovy.scripts.StringScriptSource
import org.gradle.testfixtures.ProjectBuilder

/**
 * @author Hans Dockter
 */
class HelperUtil {

    public static final Closure TEST_CLOSURE = {}
    public static final Spec TEST_SEPC = new AndSpec()
    private static final ClassGenerator CLASS_GENERATOR = new AsmBackedClassGenerator()
    private static final ITaskFactory TASK_FACTORY = new AnnotationProcessingTaskFactory(new TaskFactory(CLASS_GENERATOR))

    static <T extends Task> T createTask(Class<T> type) {
        return createTask(type, createRootProject())
    }

    static <T extends Task> T createTask(Class<T> type, ProjectInternal project) {
        return createTask(type, project, 'name')
    }

    static <T extends Task> T createTask(Class<T> type, ProjectInternal project, String name) {
        return TASK_FACTORY.createTask(project, [name: name, type: type])
    }

    static DefaultProject createRootProject() {
        createRootProject(TemporaryFolder.newInstance().dir)
    }

    static DefaultProject createRootProject(File rootDir) {
        return ProjectBuilder.builder().withProjectDir(rootDir).build()
    }

    static DefaultProject createChildProject(DefaultProject parentProject, String name, File projectDir = null) {
        DefaultProject project = CLASS_GENERATOR.newInstance(
                DefaultProject.class,
                name,
                parentProject,
                projectDir ?: new File(parentProject.getProjectDir(), name),
                new StringScriptSource("test build file", null),
                parentProject.gradle,
                parentProject.gradle.services
        )
        parentProject.addChildProject project
        parentProject.projectRegistry.addProject project
        return project
    }

    static def pureStringTransform(def collection) {
        collection.collect {
            it.toString()
        }
    }

    static DefaultExcludeRule getTestExcludeRule() {
        new DefaultExcludeRule(new ArtifactId(
                new ModuleId('org', 'module'), PatternMatcher.ANY_EXPRESSION,
                PatternMatcher.ANY_EXPRESSION,
                PatternMatcher.ANY_EXPRESSION),
                ExactPatternMatcher.INSTANCE, null)
    }

    static DefaultDependencyDescriptor getTestDescriptor() {
        new DefaultDependencyDescriptor(ModuleRevisionId.newInstance('org', 'name', 'rev'), false)
    }

    static DefaultModuleDescriptor createModuleDescriptor(Set confs) {
        DefaultModuleDescriptor moduleDescriptor = new DefaultModuleDescriptor(ModuleRevisionId.newInstance('org', 'name', 'rev'), "status", null)
        confs.each { moduleDescriptor.addConfiguration(new Configuration(it)) }
        return moduleDescriptor;
    }

    static BuildResult createBuildResult(Throwable t) {
        return new BuildResult(null, t);
    }

    static ModuleDependency createDependency(String group, String name, String version) {
        new DefaultExternalModuleDependency(group, name, version)
    }

    static DefaultPublishArtifact createPublishArtifact(String name, String extension, String type, String classifier) {
        new DefaultPublishArtifact(name, extension, type, classifier, new Date(), new File(""))
    }

    static groovy.lang.Script createScript(String code) {
        new GroovyShell().parse(code)
    }

    static Object call(String text, Object params) {
        toClosure(text).call(params)
    }

    static Closure toClosure(String text) {
        return new GroovyShell().evaluate("return " + text)
    }

    static Closure toClosure(ScriptSource source) {
        CompilerConfiguration configuration = new CompilerConfiguration();
        configuration.setScriptBaseClass(TestScript.getName());

        GroovyShell shell = new GroovyShell(configuration)
        Script script = shell.parse(source.resource.text)
        script.setScriptSource(source)
        return script.run()
    }

    static Closure toClosure(TestClosure closure) {
        return { param -> closure.call(param) }
    }

    static Closure returns(Object value) {
        return { value }
    }

    static Closure createSetterClosure(String name, String value) {
        return {
            "set$name"(value)
        }
    }

    static String createUniqueId() {
        return new UID().toString();
    }

    static org.gradle.api.artifacts.Configuration createConfiguration(String name) {
        return new DefaultConfiguration(name, name, null, null)
    }
}

public interface TestClosure {
    Object call(Object param)

    ;
}

public abstract class TestScript extends DefaultScript {
}
