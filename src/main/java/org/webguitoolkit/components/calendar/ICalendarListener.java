package org.webguitoolkit.components.calendar;

public interface ICalendarListener {
	public void onAddEvent(Calendar cal, CalendarEvent calendarDate);

	public void onChangeEvent(Calendar cal, CalendarEvent calendarDate);

	public void onNextMonth(Calendar cal);

	public void onLastMonth(Calendar cal);

	public void onSelectDate(Calendar cal, CalendarEvent calendarDate, boolean addDate);
}
