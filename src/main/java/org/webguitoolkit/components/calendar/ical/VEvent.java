package org.webguitoolkit.components.calendar.ical;

import java.util.Calendar;
import java.util.List;

public class VEvent {

	private List<String> categories;
	private String description;
	private String summary;
	private Calendar start;
	private Calendar end;
	private Calendar created;
	private Calendar lastModified;
	private Calendar dtstamp;
	private String organizer;
	private String eventClass;
	private String uid;
	private String transp;
	private String url;
	private boolean isFullDay = false;

	public String getEventClass() {
		return eventClass;
	}

	public void setEventClass(String eventClass) {
		this.eventClass = eventClass;
	}

	public Calendar getDtstamp() {
		return dtstamp;
	}

	public String getOrganizer() {
		return organizer;
	}

	public List<String> getCategories() {
		return categories;
	}

	public String getDescription() {
		return description;
	}

	public String getSummary() {
		return summary;
	}

	public Calendar getStart() {
		return start;
	}

	public Calendar getEnd() {
		return end;
	}

	public Calendar getCreated() {
		return created;
	}

	public Calendar getLastModified() {
		return lastModified;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setStart(Calendar start) {
		this.start = start;
	}

	public void setEnd(Calendar end ) {
		this.end = end;
	}

	public void setCreated(Calendar created ) {
		this.created = created;
	}

	public void setLastModified(Calendar lastModified ) {
		this.lastModified = lastModified;
	}
	
	public void setCategories( List<String> categories ){
		this.categories = categories;
		
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public void setDtstamp(Calendar dtstamp) {
		this.dtstamp = dtstamp;
	}

	public void setUID(String uid) {
		this.uid = uid;
	}
	
	public String getUID() {
		return uid;
	}

	public void setTransp(String transp) {
		this.transp = transp;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getTransp() {
		return transp;
	}

	public String getUrl() {
		return url;
	}

	public void setFullDay(boolean isFullDay) {
		this.isFullDay = isFullDay;
	}

	public boolean isFullDay() {
		return isFullDay;
	}

}
