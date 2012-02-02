package org.webguitoolkit.components.calendar;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TimeZone;

public interface ICalendarModel {
	
	/**
	 * This method returns all events for a specified date (year, month, date)<br>
	 * 
	 * @param year e.g. 2010
	 * @param month e.g. 11 for November
	 * @param date e.g. 12
	 * @return Collection of all events, which are planned for the specified date.
	 */
	Collection<CalendarEvent> getAllEventsForDate(int year, int month, int date, TimeZone timeZone);


	/**
	 * 
	 * @return the timezone of this calendar model
	 */
	TimeZone getTimeZone();

	public Set<GregorianCalendar> getSelectedDates();

	/**
	 * @param selectedDates the selectedDates to set
	 */
	public void setSelectedDates(Set<GregorianCalendar> selectedDates);
}
