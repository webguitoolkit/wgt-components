package org.webguitoolkit.components.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.time.DateUtils;

public class DefaultCalendarModel implements ICalendarModel {
	private Set<GregorianCalendar> selectedDates = null;
	private Collection<CalendarEvent> calendarEvents = null;
	private Map<Long, String> cellStyles = null;
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

		GregorianCalendar dateCompareCalendar = new GregorianCalendar(timeZone);
		dateCompareCalendar.set(GregorianCalendar.YEAR, year);
		dateCompareCalendar.set(GregorianCalendar.MONTH, month);
		dateCompareCalendar.set(GregorianCalendar.DATE, date);
		dateCompareCalendar.set(GregorianCalendar.HOUR_OF_DAY, 00);
		dateCompareCalendar = (GregorianCalendar)DateUtils.truncate(dateCompareCalendar, GregorianCalendar.HOUR_OF_DAY);

		Iterator<CalendarEvent> eventsIter = events.iterator();
		while (eventsIter.hasNext()) {
			CalendarEvent event = eventsIter.next();
			GregorianCalendar eventCalendar = new GregorianCalendar(timeZone);
			eventCalendar.setTimeInMillis(event.getTimestampInMillis());

			// If selected month and year equals event in collection
			if (DateUtils.isSameDay(dateCompareCalendar, eventCalendar)) {
				dateEvents.add(event);
			}
		}
		// TODO Sort per hours
		return dateEvents;
	}
	
	/**
	 * This method returns all styles for a specified date (year, month, date)<br>
	 * 
	 * @param year e.g. 2010
	 * @param month e.g. 11 for November
	 * @param date e.g. 12
	 * @return style for the specified date as CSS-string
	 */
	public String getStyleForDate(int year, int month, int date) {
		return getStyleForDate(year, month, date, null);
	}

	public String getStyleForDate(int year, int month, int date, TimeZone timeZone) {
		String cellStyle = null;
		if (timeZone == null) {
			timeZone = getTimeZone();
		}

		GregorianCalendar dateCompareCalendar = new GregorianCalendar(timeZone);
		dateCompareCalendar.set(GregorianCalendar.YEAR, year);
		dateCompareCalendar.set(GregorianCalendar.MONTH, month);
		dateCompareCalendar.set(GregorianCalendar.DATE, date);
		dateCompareCalendar = (GregorianCalendar)DateUtils.truncate(dateCompareCalendar, GregorianCalendar.DATE);

		for (Entry<Long, String> entryCellStyle : getCellStyles().entrySet()) {
			GregorianCalendar cellStyleCalendar = new GregorianCalendar(timeZone);
			cellStyleCalendar.setTimeInMillis(entryCellStyle.getKey());

			// If selected month and year equals timestamp of cell style
			if (DateUtils.isSameDay(dateCompareCalendar, cellStyleCalendar)) {
				cellStyle = entryCellStyle.getValue();
			}
		}

		return cellStyle;
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

	public Map<Long, String> getCellStyles() {
		if (cellStyles == null) {
			cellStyles = new HashMap<Long, String>();
		}

		return cellStyles;
	}

	public void setCellStyles(Map<Long, String> cellStyles) {
		this.cellStyles = cellStyles;
	}

}
