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

//import org.jruby.ext.posix.FileStat


import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import org.apache.commons.lang.StringUtils
import org.apache.tools.ant.taskdefs.Expand
import org.apache.tools.ant.taskdefs.Untar
import org.gradle.api.UncheckedIOException
import static org.hamcrest.Matchers.equalTo
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

class TestFileHelper {
    TestFile file

    public TestFileHelper(TestFile file) {
        this.file = file
    }

    public void unzipTo(File target, boolean nativeTools) {
        // Check that each directory in hierarchy is present
        file.withInputStream {InputStream instr ->
            Set<String> dirs = [] as Set
            ZipInputStream zipStr = new ZipInputStream(instr)
            ZipEntry entry
            while (entry = zipStr.getNextEntry()) {
                if (entry.isDirectory()) {
                    assertTrue("Duplicate directory '$entry.name'", dirs.add(entry.name))
                }
                if (!entry.name.contains('/')) {
                    continue
                }
                String parent = StringUtils.substringBeforeLast(entry.name, '/') + '/'
                assertTrue("Missing dir '$parent'", dirs.contains(parent))
            }
        }

        if (nativeTools && OperatingSystem.current().isUnix()) {
            Process process = ['unzip', '-o', file.absolutePath, '-d', target.absolutePath].execute()
            process.consumeProcessOutput(System.out, System.err)
            assertThat(process.waitFor(), equalTo(0))
            return
        }

        Expand unzip = new Expand();
        unzip.src = file;
        unzip.dest = target;
        AntUtil.execute(unzip);
    }

    public void untarTo(File target, boolean nativeTools) {
        if (nativeTools && OperatingSystem.current().isUnix()) {
            target.mkdirs()
            ProcessBuilder builder = new ProcessBuilder(['tar', '-xf', file.absolutePath])
            builder.directory(target)
            Process process = builder.start()
            process.consumeProcessOutput()
            assertThat(process.waitFor(), equalTo(0))
            return
        }

        Untar untar = new Untar();
        untar.setSrc(file);
        untar.setDest(target);

        if (file.getName().endsWith(".tgz")) {
            Untar.UntarCompressionMethod method = new Untar.UntarCompressionMethod();
            method.setValue("gzip");
            untar.setCompression(method);
        } else if (file.getName().endsWith(".tbz2")) {
            Untar.UntarCompressionMethod method = new Untar.UntarCompressionMethod();
            method.setValue("bzip2");
            untar.setCompression(method);
        }

        AntUtil.execute(untar);
    }

//    public String getPermissions() {
//        FileStat stat = PosixUtil.current().stat(file.absolutePath)
//        [6, 3, 0].collect {
//            def m = stat.mode() >> it
//            [m & 4 ? 'r' : '-', m & 2 ? 'w' : '-', m & 1 ? 'x' : '-']
//        }.flatten().join('')
//    }

    def setPermissions(String permissions) {
        def m = [6, 3, 0].inject(0) { mode, pos ->
            mode |= permissions[9 - pos - 3] == 'r' ? 4 << pos : 0
            mode |= permissions[9 - pos - 2] == 'w' ? 2 << pos : 0
            mode |= permissions[9 - pos - 1] == 'x' ? 1 << pos : 0
            return mode
        }
        int retval = PosixUtil.current().chmod(file.absolutePath, m)
        if (retval != 0) {
            throw new UncheckedIOException("Could not set permissions of '${file}' to '${permissions}'.")
        }
    }
}