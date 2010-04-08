package org.jboss.gradle.plugins.jdocbook;

/**
 * Profiling configuration
 *
 * @author Steve Ebersole
 */
public class Profiling {
	private boolean enabled;
	private String attributeName;
	private String attributeValue;

	public Profiling() {
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
}
