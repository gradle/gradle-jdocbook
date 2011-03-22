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

class TestDirHelper {
    def TestFile baseDir

    def TestDirHelper(TestFile baseDir) {
        this.baseDir = baseDir
    }

    def apply(Closure cl) {
        cl.delegate = this
        cl.resolveStrategy = Closure.DELEGATE_FIRST
        cl()
    }

    def file(String name) {
        TestFile file = baseDir.file(name)
        file.write('some content')
        file
    }

    def methodMissing(String name, Object args) {
        if (args.length == 1 && args[0] instanceof Closure) {
            baseDir.file(name).create(args[0])
        }
        else {
            throw new MissingMethodException(name, getClass(), args)
        }
    }
}
