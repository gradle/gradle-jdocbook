package org.jboss.gradle.plugins.jdocbook;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.jdocbook.util.XIncludeHelper;

/**
 * TODO : javadoc
 *
 * @author Steve Ebersole
 */
public class MasterSourceFileResolver {
	private final DirectoryLayout directoryLayout;
	private final JDocBookConfiguration jDocBookConfiguration;


	public MasterSourceFileResolver(DirectoryLayout directoryLayout, JDocBookConfiguration jDocBookConfiguration) {
		this.directoryLayout = directoryLayout;
		this.jDocBookConfiguration = jDocBookConfiguration;
	}

	private File mainMasterFile;

	public File getMainMasterFile() {
		if ( mainMasterFile == null ) {
			mainMasterFile = new File(
					directoryLayout.getMasterSourceDirectory(),
					jDocBookConfiguration.getMasterSourceDocumentName()
			);
		}
		return mainMasterFile;
	}

	private File[] masterFiles;

	public File[] getFiles() {
		if ( masterFiles == null ) {
			File mainMasterFile = getMainMasterFile();
			final Set<File> files = new TreeSet<File>();
			files.add( mainMasterFile );
			XIncludeHelper.findAllInclusionFiles( mainMasterFile, files );
			this.masterFiles = files.toArray( new File[ files.size() ] );
		}
		return masterFiles;
	}
}
