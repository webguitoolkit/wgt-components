package org.webguitoolkit.components.calendar;

public abstract class AbstractCalendarListener implements ICalendarListener{

	public abstract void onChangeEvent(Calendar cal, CalendarEvent calendarDate);

	public void onNextMonth(Calendar cal) {
		cal.nextPage();
		cal.load();

	}
	
	public void onLastMonth(Calendar cal) {
		cal.lastPage();
		cal.load();
	}
	
	public void onAddEvent(Calendar cal, CalendarEvent calendarDate){
	}

	public void onSelectDate(Calendar cal, CalendarEvent calendarDate, boolean addDate) {
	}
}
