package org.jboss.gradle.plugins.jdocbook.book;

import org.gradle.api.internal.AutoCreateDomainObjectContainer;
import org.gradle.api.internal.ClassGenerator;

/**
 * http://jira.codehaus.org/browse/GROOVY-4415
 * @author: Strong Liu
 */
abstract class AbstractBooksContainer extends AutoCreateDomainObjectContainer<Book> {
	protected AbstractBooksContainer(Class<Book> type, ClassGenerator classGenerator) {
		super( type, classGenerator );
	}
}
