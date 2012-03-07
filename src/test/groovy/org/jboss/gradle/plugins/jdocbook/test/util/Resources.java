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

package org.jboss.gradle.plugins.jdocbook.test.util;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * A JUnit rule which helps locate test resources.
 */
public class Resources implements MethodRule {
    private Class<?> testClass;

    public Resources() {
    }

    public Resources(Class<?> clazz) {
        testClass = clazz;
    }

    /**
     * Locates the resource with the given name, relative to the current test class. Asserts that the resource exists.
     */
    public TestFile getResource(String name) {
        assertNotNull(testClass);
        TestFile file = findResource(name);
        assertNotNull(String.format("Could not locate resource '%s' for test class %s.", name, testClass.getName()), file);
        return file;
    }

    /**
     * Locates the resource with the given name, relative to the current test class.
     *
     * @return the resource, or null if not found.
     */
    public TestFile findResource(String name) {
        assertNotNull(testClass);
        URL resource = testClass.getResource(name);
        if (resource == null) {
            return null;
        }
        assertEquals("file", resource.getProtocol());
        File file;
        try {
            file = new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return new TestFile(file);
    }

    public Statement apply(final Statement statement, FrameworkMethod frameworkMethod, Object o) {
        testClass = frameworkMethod.getMethod().getDeclaringClass();
        return statement;
    }
}
