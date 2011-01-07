package org.jboss.gradle.plugins.jdocbook.book

import org.jboss.jdocbook.render.FormatOptions

//Immutable??
class FormatOption implements FormatOptions{
	@Override
	String getTargetFinalName() {
		return finalName
	}

	@Override
	String getStylesheetResource() {
		return stylesheet
	}

	String name
	String finalName
	String stylesheet
	boolean enable = true
	FormatOption(){
	}
	FormatOption(String name){
		this.name = name
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null)
				? 0 : name.hashCode());
		result = prime * result + ((finalName == null)
				? 0 : finalName.hashCode());
		result = prime * result + ((stylesheet == null)
				? 0 : stylesheet.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormatOption other = (FormatOption) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (finalName == null) {
			if (other.finalName != null)
				return false;
		} else if (!finalName.equals(other.finalName))
			return false;
		if (stylesheet == null) {
			if (other.stylesheet != null)
				return false;
		} else if (!stylesheet.equals(other.stylesheet))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "$name";
	}
}
