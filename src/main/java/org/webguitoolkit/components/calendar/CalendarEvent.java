package org.webguitoolkit.components.calendar;

public class CalendarEvent {
	private int durationInMinutes;
	private String description;
	private String eventTitle;
	private String eventTooltip;
	private long timestampInMillis;

	private CalendarEventDisplayProperties displayProperties;

	private Object delegate;

	public CalendarEvent() {
	}

	public CalendarEvent(long timestampInMillis, int durationInMinutes, String eventTitle, String description, Object delegate) {
		this.timestampInMillis = timestampInMillis;
		this.durationInMinutes = durationInMinutes;
		this.eventTitle = eventTitle;
		this.description = description;
		this.delegate = delegate;
	}

	public int getDurationInMinutes() {
		return durationInMinutes;
	}

	public void setDurationInMinutes(int durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getDelegate() {
		return delegate;
	}

	public void setDelegate(Object delegate) {
		this.delegate = delegate;
	}

	public CalendarEventDisplayProperties getDisplayProperties() {
		return displayProperties;
	}

	public void setDisplayProperties(CalendarEventDisplayProperties displayProperties) {
		this.displayProperties = displayProperties;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getEventTooltip() {
		return eventTooltip;
	}

	public void setEventTooltip(String eventTooltip) {
		this.eventTooltip = eventTooltip;
	}

	public long getTimestampInMillis() {
		return timestampInMillis;
	}

	public void setTimestampInMillis(long timestampInMillis) {
		this.timestampInMillis = timestampInMillis;
	}
}
