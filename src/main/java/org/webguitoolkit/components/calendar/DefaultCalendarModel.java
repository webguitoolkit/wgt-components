package org.webguitoolkit.components.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateUtils;

public class DefaultCalendarModel implements ICalendarModel {
	private Set<GregorianCalendar> selectedDates = null;
	private Collection<CalendarEvent> calendarEvents = null;
	private final TimeZone timeZone;

	public DefaultCalendarModel() {
		this.timeZone = TimeZone.getDefault();
	}
	public DefaultCalendarModel( TimeZone timeZone ) {
		this.timeZone = timeZone;
	}

	public Collection<CalendarEvent> getCalendarEvents() {
		if (calendarEvents == null)
			calendarEvents = new ArrayList<CalendarEvent>();
		return calendarEvents;
	}

	public void setCalendarEvents(Collection<CalendarEvent> calendarEvents) {
		this.calendarEvents = calendarEvents;
	}
	
	/**
	 * This method returns all events for a specified date (year, month, date)<br>
	 * 
	 * @param year e.g. 2010
	 * @param month e.g. 11 for November
	 * @param date e.g. 12
	 * @return Collection of all events, which are planned for the specified date.
	 */
	public Collection<CalendarEvent> getAllEventsForDate(int year, int month, int date, TimeZone timeZone ) {
		// Get all events
		Collection<CalendarEvent> events = getCalendarEvents();
		Collection<CalendarEvent> dateEvents = new ArrayList<CalendarEvent>();

		java.util.Calendar dateCompareCalendar = new GregorianCalendar(timeZone);
		dateCompareCalendar.set(java.util.Calendar.YEAR, year);
		dateCompareCalendar.set(java.util.Calendar.MONTH, month);
		dateCompareCalendar.set(java.util.Calendar.DATE, date);
		dateCompareCalendar.set(java.util.Calendar.HOUR_OF_DAY, 00);
		dateCompareCalendar = DateUtils.truncate(dateCompareCalendar, java.util.Calendar.HOUR_OF_DAY);

		Iterator<CalendarEvent> eventsIter = events.iterator();
		while (eventsIter.hasNext()) {
			CalendarEvent event = eventsIter.next();
			java.util.Calendar eventCalendar = new GregorianCalendar(timeZone);
			eventCalendar.setTimeInMillis(event.getTimestampInMillis());

			// If selected month and year equals event in collection
			if (DateUtils.isSameDay(dateCompareCalendar, eventCalendar)) {
				dateEvents.add(event);
			}
		}
		// TODO Sort per hours
		return dateEvents;
	}
	
	public TimeZone getTimeZone(){
		return timeZone;
	}

	public Set<GregorianCalendar> getSelectedDates() {
		if (selectedDates == null) {
			selectedDates = new HashSet<GregorianCalendar>();
		}
		return selectedDates;
	}

	public void setSelectedDates(Set<GregorianCalendar> selectedDates) {
		this.selectedDates = selectedDates;
	}

}
