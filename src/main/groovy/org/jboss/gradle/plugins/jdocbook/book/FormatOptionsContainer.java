package org.jboss.gradle.plugins.jdocbook.book;

import groovy.lang.Closure;
import org.gradle.api.internal.AutoCreateDomainObjectContainer;
import org.gradle.api.internal.ClassGenerator;
import org.gradle.util.ConfigureUtil;

/**
 * @author: Strong Liu
 */
public class FormatOptionsContainer extends AutoCreateDomainObjectContainer<FormatOption> {
	ClassGenerator generator;
	FormatOptionsContainer parent;

	public FormatOptionsContainer(ClassGenerator generator) {
		this( generator, null );
	}

	public FormatOptionsContainer(ClassGenerator generator, FormatOptionsContainer parent) {
		super( FormatOption.class, generator );
		this.generator = generator;
		this.parent = parent;
	}


	@Override
	public FormatOption create(String name) {
		return generator.newInstance( FormatOption.class, name );
	}


	@Override
	public FormatOption add(String name, Closure configureClosure) {
		FormatOption obj = findByNameWithoutRules( name );
		if ( obj == null ) {
			obj = create( name );
			addObject( name, obj );
		}
		return ConfigureUtil.configure( configureClosure, obj );
	}
}
