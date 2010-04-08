package org.jboss.gradle.plugins.jdocbook;

/**
 * User configuration of a particular output format
 *
 * @author Steve Ebersole
 */
public class Format {
	private String formatName;
	private String finalName;
	private String stylesheetResource;
	private Boolean imagePathSettingRequired;
	private Boolean imageCopyingRequired;
	private Boolean doingChunking;

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	public String getFinalName() {
		return finalName;
	}

	public void setFinalName(String finalName) {
		this.finalName = finalName;
	}

	public String getStylesheetResource() {
		return stylesheetResource;
	}

	public void setStylesheetResource(String stylesheetResource) {
		this.stylesheetResource = stylesheetResource;
	}

	public Boolean isImagePathSettingRequired() {
		return imagePathSettingRequired;
	}

	public void setImagePathSettingRequired(Boolean imagePathSettingRequired) {
		this.imagePathSettingRequired = imagePathSettingRequired;
	}

	public Boolean isImageCopyingRequired() {
		return imageCopyingRequired;
	}

	public void setImageCopyingRequired(Boolean imageCopyingRequired) {
		this.imageCopyingRequired = imageCopyingRequired;
	}

	public Boolean isDoingChunking() {
		return doingChunking;
	}

	public void setDoingChunking(Boolean doingChunking) {
		this.doingChunking = doingChunking;
	}
}
