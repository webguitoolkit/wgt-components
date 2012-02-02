package org.webguitoolkit.components.calendar;

public class CalendarEventDisplayProperties {
	private String bgColor = "#99CCFF";
	private String bgColorHover = "#0099FF";
	private String imageSrc = "";
	private String cssClass;
	private String color = "#000000";
	private boolean showEventTitle = true;
	private boolean showEventImage = false;
	
	public String getBgColor() {
		return bgColor;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public boolean isShowEventTitle() {
		return showEventTitle;
	}

	public void setShowEventTitle(boolean showEventTitle) {
		this.showEventTitle = showEventTitle;
	}

	public boolean isShowEventImage() {
		return showEventImage;
	}

	public void setShowEventImage(boolean showEventImage) {
		this.showEventImage = showEventImage;
	}

	public String getBgColorHover() {
		return bgColorHover;
	}

	public void setBgColorHover(String bgColorHover) {
		this.bgColorHover = bgColorHover;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
