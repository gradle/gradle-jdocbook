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

import org.apache.commons.lang.StringUtils;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.io.File;

/**
 * A JUnit rule which provides a unique temporary folder for the test.
 */
public class TemporaryFolder implements MethodRule, TestFileContext {
    private TestFile dir;
    private String prefix;
    private static TestFile root;

    static {
        root = new TestFile(new File("build/tmp/tests"));
    }

    public TestFile getDir() {
        if (dir == null) {
            if (prefix == null) {
                // This can happen if this is used in a constructor or a @Before method. It also happens when using
                // @RunWith(SomeRunner) when the runner does not support rules.
                prefix = determinePrefix();
            }
            for (int counter = 1; true; counter++) {
                dir = root.file(counter == 1 ? prefix : String.format("%s%d", prefix, counter));
                if (dir.mkdirs()) {
                    break;
                }
            }
        }
        return dir;
    }

    private String determinePrefix() {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getClassName().endsWith("Test")) {
                return StringUtils.substringAfterLast(element.getClassName(), ".") + "/unknown-test";
            }
        }
        return "unknown-test-class";
    }

    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        init(method, target);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
                getDir().maybeDeleteDir();
                // Don't delete on failure
            }
        };
    }

    private void init(FrameworkMethod method, Object target) {
        if (prefix == null) {
            prefix = String.format("%s/%s", target.getClass().getSimpleName(), method.getName());
        }
    }

    public static TemporaryFolder newInstance() {
        return new TemporaryFolder();
    }

    public static TemporaryFolder newInstance(FrameworkMethod method, Object target) {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        temporaryFolder.init(method, target);
        return temporaryFolder;
    }

    public TestFile getTestDir() {
        return getDir();
    }

    public TestFile file(Object... path) {
        return getDir().file((Object[]) path);
    }

    public TestFile createFile(Object... path) {
        return file((Object[]) path).createFile();
    }

    public TestFile createDir(Object... path) {
        return file((Object[]) path).createDir();
    }
}