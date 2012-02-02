package org.webguitoolkit.components.calendar;

import java.io.PrintWriter;
import java.text.DateFormatSymbols;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.lang.time.DateUtils;
import org.apache.ecs.html.A;
import org.apache.ecs.html.Span;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.webguitoolkit.ui.controls.BaseControl;
import org.webguitoolkit.ui.controls.event.ClientEvent;
import org.webguitoolkit.ui.controls.util.TextService;

/**
 * 
 * @author i102497 (Timo Dreier)
 */
public abstract class Calendar extends BaseControl {
	private static final long serialVersionUID = 1L;

	// CSS CLASSES AND IDS
	protected static final String CAL_TABLE_MONTH_LABEL = "wgt_cal_table_month_label";
	protected static final String CAL_TABLE_MONTH_LABEL_POCKET = "wgt_cal_table_month_label_pocket";
	private static final String CAL_TABLE_BUTTONS = "wgtButton2 wgtButton2-l wgt_cal_table_buttons";
	private static final String CAL_TABLE_CONTENT = "wgt_cal_table_content";
	protected static final String CAL_TABLE_POCKET = "wgt_cal_pocket";
	protected static final String CAL_TABLE_SELECTION = "wgt_cal_selection";
	protected static final String CAL_TABLE_CELL_HIGHLIGHTED = "highlighted";

	// EVENT TYPES
	private static final int EVENT_TYPE_ADD_EVENT = 1;
	private static final int EVENT_TYPE_NEXT_MONTH = 2;
	private static final int EVENT_TYPE_LAST_MONTH = 3;
	private static final int EVENT_TYPE_CHANGE_EVENT = 4;
	private static final int EVENT_TYPE_SELECT_DATE = 5;

	public static final int EDITMODE_ADD_ANYWHERE = 0;
	public static final int EDITMODE_DONT_ADD_IN_PAST = 1;
	public static final int EDITMODE_DONT_ADD = 2;

	// Default height and width in pixel
	protected int height = -1;
	protected int width = 780;

	// First day of week, belongs to Locale
	protected int firstDayOfWeek;
	
	protected int maxEventPerDays = 5;
	protected int eventTitleMaxChars = 10;

	private int editMode = EDITMODE_DONT_ADD_IN_PAST;

	private int currentYear;
	private int currentMonth;
	private int currentWeek;
	private int currentDay;

	// Locale
	protected Locale locale = TextService.getLocale();
	// Display Names Day of weeks
	protected String[] shortWeekdays;

	// Calendar Model
	private ICalendarModel calendarModel;
	// Gregorian Calendar Instance
	protected GregorianCalendar calendar = new GregorianCalendar();
	// And the appended listener
	private ICalendarListener listener = null;

	protected CalendarEventDisplayProperties defaultEventDisplayProperties = new CalendarEventDisplayProperties();

	private Span span;

	protected String[] shortMonth;

	protected boolean selectionMode = false;
	protected boolean pocketMode = false;
	protected boolean fireClickEvent = true;

	// Constructor
	public Calendar() {
		super();
		newCalendar(new GregorianCalendar());
	}

	public Calendar(boolean selectionMode) {
		this();

		this.selectionMode = selectionMode;
	}

	public Calendar(boolean selectionMode, boolean pocketMode) {
		this(selectionMode);

		this.pocketMode = pocketMode;

		if (pocketMode) {
			width = 120;
			maxEventPerDays = 0;
		}
	}

	public Calendar(boolean selectionMode, boolean pocketMode, boolean fireClickEvent) {
		this(selectionMode, pocketMode);

		this.fireClickEvent = fireClickEvent;
	}

	// Constructor
	public Calendar(int year) {
		super();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.YEAR, year);
		newCalendar(calendar);
	}

	// Constructor
	public Calendar(int year, int month) {
		super();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(GregorianCalendar.YEAR, year);
		calendar.set(GregorianCalendar.MONTH, month);
		newCalendar(calendar);
	}

	private void newCalendar(GregorianCalendar calendar) {
		setCurrentYear(calendar.get(java.util.Calendar.YEAR));
		setCurrentMonth(calendar.get(java.util.Calendar.MONTH));
		setCurrentWeek(calendar.get(java.util.Calendar.WEEK_OF_YEAR));
		setCurrentDay(calendar.get(java.util.Calendar.DAY_OF_MONTH));

		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
		// short weekdays [Sun, Mon, etc]
		shortWeekdays = dateFormatSymbols.getShortWeekdays();
		shortMonth = dateFormatSymbols.getShortMonths();
		firstDayOfWeek = GregorianCalendar.getInstance(locale).getFirstDayOfWeek();
	}

	/**
	 * @param max int of how many events have to be displayed per day
	 */
	public void setMaxEventsPerDay(int max) {
		maxEventPerDays = max;
	}

	/**
	 * Sets the locale for this calendar instance<br>
	 * 
	 * @param locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;

		// to overwrite local
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
		// short weekdays [Sun, Mon, etc]
		shortWeekdays = dateFormatSymbols.getShortWeekdays();
		firstDayOfWeek = GregorianCalendar.getInstance(locale).getFirstDayOfWeek();
	}

	@Override
	protected void draw(PrintWriter out) {
		// Root Table
		org.apache.ecs.html.Table rootTable = new org.apache.ecs.html.Table();
		stdParameter(rootTable);
		rootTable.setCellPadding(0).setCellSpacing(0);

		TR tr = new TR();
		rootTable.addElement(tr);
		TD td = new TD();
		tr.addElement(td);

		// Header table
		org.apache.ecs.html.Table headerTable = new org.apache.ecs.html.Table();
		td.addElement(headerTable);

		headerTable.setWidth("100%");

		tr = new TR();
		headerTable.addElement(tr);

		// Last Month Button
		td = new TD();
		tr.addElement(td);
		A lastMonth = new A();
		lastMonth.setClass(CAL_TABLE_BUTTONS);
		lastMonth.setOnClick("calLastPage('" + getId() + "');");
		td.setWidth("20%");
		td.addElement(lastMonth);
		// Span for Button
		Span lastMonthOuterSpan = new Span();
		lastMonth.addElement(lastMonthOuterSpan);
		Span lastMonthInnerSpan = new Span();
		lastMonthOuterSpan.addElement(lastMonthInnerSpan);
		if (pocketMode) {
			lastMonthInnerSpan.addElement("&lt;");
		}
		else {
			lastMonthInnerSpan.addElement(TextService.getInstance().getString(getLastPageLabel(), locale));
		}

		// Month Label
		td = new TD();
		tr.addElement(td);
		td.setAlign("center");
		span = new Span();
		td.addElement(span);
		span.setID(getId() + CAL_TABLE_MONTH_LABEL);
		String labelStyle;
		if (pocketMode) {
			labelStyle = CAL_TABLE_MONTH_LABEL_POCKET;
		}
		else {
			labelStyle = CAL_TABLE_MONTH_LABEL;
		}
		span.setClass(labelStyle);

		// Next Month Button
		td = new TD();
		tr.addElement(td);
		A nextMonth = new A();
		nextMonth.setClass(CAL_TABLE_BUTTONS);
		nextMonth.setOnClick("calNextPage('" + getId() + "');");
		td.setWidth("20%");
		td.setAlign("right");
		td.addElement(nextMonth);
		// Span for Button
		Span nextMonthOuterSpan = new Span();
		nextMonth.addElement(nextMonthOuterSpan);
		Span nextMonthInnerSpan = new Span();
		nextMonthOuterSpan.addElement(nextMonthInnerSpan);
		if (pocketMode) {
			nextMonthInnerSpan.addElement("&gt;");
		}
		else {
			nextMonthInnerSpan.addElement(TextService.getInstance().getString(getNextPageLabel(), locale));
		}

		tr = new TR();
		rootTable.addElement(tr);
		td = new TD();
		td.setID(getId() + CAL_TABLE_CONTENT);
		tr.addElement(td);

		rootTable.output(out);
	}

	protected String getNextPageLabel() {
		return "calendar.next.page@Next Page";
	}

	protected String getLastPageLabel() {
		return "calendar.last.page@Last Page";
	}

	@Override
	protected void endHTML(PrintWriter out) {
		// Muss nicht überschrieben werden, da die draw Methode
		// überschrieben wurde.
	}

	@Override
	public void dispatch(ClientEvent event) {
		int type = event.getTypeAsInt();

		if (listener == null) {
			listener = new AbstractCalendarListener() {
				@Override
				public void onChangeEvent(Calendar cal, CalendarEvent calendarDate) {
					// nothing to do
				}
			};
		}

		if (type == EVENT_TYPE_ADD_EVENT) {
			int year = Integer.parseInt(event.getParameter(0));
			int month = Integer.parseInt(event.getParameter(1));
			int day = Integer.parseInt(event.getParameter(2));

			java.util.Calendar newEventCalendar = new GregorianCalendar(getModel().getTimeZone());
			newEventCalendar.set(java.util.Calendar.YEAR, year);
			newEventCalendar.set(java.util.Calendar.MONTH, month);
			newEventCalendar.set(java.util.Calendar.DATE, day);
			newEventCalendar.set(java.util.Calendar.HOUR_OF_DAY, 12);
			newEventCalendar = DateUtils.truncate(newEventCalendar, java.util.Calendar.HOUR_OF_DAY);

			CalendarEvent calendarEvent = new CalendarEvent();
			calendarEvent.setTimestampInMillis(newEventCalendar.getTimeInMillis());

			listener.onAddEvent(this, calendarEvent);
		}
		else if (type == EVENT_TYPE_NEXT_MONTH) {
			listener.onNextMonth(this);
		}
		else if (type == EVENT_TYPE_LAST_MONTH) {
			listener.onLastMonth(this);
		}
		else if (type == EVENT_TYPE_CHANGE_EVENT) {
			int year = Integer.parseInt(event.getParameter(0));
			int month = Integer.parseInt(event.getParameter(1));
			int day = Integer.parseInt(event.getParameter(2));
			int eventId = Integer.parseInt(event.getParameter(3));

			Collection<CalendarEvent> calendarEvents = calendarModel.getAllEventsForDate(year, month, day, getModel().getTimeZone());
			if (calendarEvents != null) {
				// fetch event
				CalendarEvent calendarEvent = (CalendarEvent)calendarEvents.toArray()[eventId - 1];
				listener.onChangeEvent(this, calendarEvent);
			}
		}
		else if (type == EVENT_TYPE_SELECT_DATE) {
			int year = Integer.parseInt(event.getParameter(0));
			int month = Integer.parseInt(event.getParameter(1));
			int day = Integer.parseInt(event.getParameter(2));
			boolean addDate = Boolean.valueOf(event.getParameter(3));

			java.util.Calendar newSelectedCalendar = new GregorianCalendar(getModel().getTimeZone());
			newSelectedCalendar.set(java.util.Calendar.YEAR, year);
			newSelectedCalendar.set(java.util.Calendar.MONTH, month);
			newSelectedCalendar.set(java.util.Calendar.DATE, day);
			newSelectedCalendar = DateUtils.truncate(newSelectedCalendar, java.util.Calendar.DATE);

			CalendarEvent calendarEvent = new CalendarEvent();
			calendarEvent.setTimestampInMillis(newSelectedCalendar.getTimeInMillis());

			listener.onSelectDate(this, calendarEvent, addDate);
		}
	}

	@Override
	protected void init() {
		getPage().addWgtJS("components/calendar.js");
		getPage().addWgtJS("jquery/jquery.tooltip.js");
		getPage().addWgtCSS("components/calendar.css");
	}

	public void setListener(ICalendarListener listener) {
		this.listener = listener;
	}

	public ICalendarListener getListener() {
		return listener;
	}

	public void setModel(ICalendarModel calendarModel) {
		this.calendarModel = calendarModel;
	}

	public ICalendarModel getModel() {
		return calendarModel;
	}

	/**
	 * Modify calendar after changes.
	 */
	public void load() {
		// create inner html of content
		createTableContent();
	}

	public void resetToCurrentDate() {
		GregorianCalendar today = (GregorianCalendar)GregorianCalendar.getInstance();
		today.setTime(new Date());
		setCurrentDay(today.get(GregorianCalendar.DAY_OF_MONTH));
		setCurrentMonth(today.get(GregorianCalendar.MONTH));
		setCurrentYear(today.get(GregorianCalendar.YEAR));

		load();
	}

	protected abstract void createTableContent();

	protected boolean isDateInPast(java.util.Calendar cal) {
		// get the current day - check if this is part of actual month (in model)
		java.util.Calendar tempCalendar = GregorianCalendar.getInstance(getModel().getTimeZone());
		tempCalendar.set(java.util.Calendar.HOUR, 0);
		tempCalendar.set(java.util.Calendar.MINUTE, 0);
		tempCalendar.set(java.util.Calendar.SECOND, 0);
		tempCalendar.set(java.util.Calendar.MILLISECOND, 0);

		return tempCalendar.after(cal);
	}

	protected boolean isCurrentDay(java.util.Calendar cal) {
		// get the current day - check if this is part of actual month (in model)
		java.util.Calendar tempCalendar = GregorianCalendar.getInstance(getModel().getTimeZone());

		return cal.get(java.util.Calendar.YEAR) == tempCalendar.get(java.util.Calendar.YEAR)
				&& cal.get(java.util.Calendar.MONTH) == tempCalendar.get(java.util.Calendar.MONTH)
				&& cal.get(java.util.Calendar.DAY_OF_MONTH) == tempCalendar.get(java.util.Calendar.DAY_OF_MONTH);
	}

	/**
	 * @return String[] filled with the short weekdays ordered by locale
	 */
	protected String[] getShortWeekdays() {
		String[] newShortWeekdays = new String[7];

		// shift calendar days.
		int l = 0;
		for (int j = 1; j < shortWeekdays.length; j++) {
			l = firstDayOfWeek - 1 + j;
			if (l > 7) {
				l = l - 7;
			}
			newShortWeekdays[j - 1] = shortWeekdays[l];
		}

		return newShortWeekdays;
	}

	protected String[] getShortMonth() {
		return shortMonth;
	}

	/***************************************** PUBLIC METHODS ************************************/
	public CalendarEventDisplayProperties getDefaultEventDisplayProperties() {
		return defaultEventDisplayProperties;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setDefaultEventDisplayProperties(CalendarEventDisplayProperties defaultEventDisplayProperties) {
		this.defaultEventDisplayProperties = defaultEventDisplayProperties;
	}

	public boolean isAddEventInPast() {
		return editMode == EDITMODE_ADD_ANYWHERE;
	}

	/**
	 * 
	 * @param addEventInPast
	 * @deprecated use setEdtiMode(Calendar.EDITMODE_ADD_ANYWHERE);
	 */
	public void setAddEventInPast(boolean addEventInPast) {
		if (addEventInPast)
			editMode = EDITMODE_ADD_ANYWHERE;
		else
			editMode = EDITMODE_DONT_ADD_IN_PAST;
	}

	public void setEditMode(int editMode) {
		this.editMode = editMode;
	}

	public int getEditMode() {
		return editMode;
	}

	public abstract void nextPage();

	public abstract void lastPage();

	public int getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	public int getCurrentMonth() {
		return currentMonth;
	}

	public void setCurrentMonth(int currentMonth) {
		this.currentMonth = currentMonth;
	}

	public int getCurrentWeek() {
		return currentWeek;
	}

	public void setCurrentWeek(int currentWeek) {
		this.currentWeek = currentWeek;
	}

	public int getCurrentDay() {
		return currentDay;
	}

	public void setCurrentDay(int currentDay) {
		this.currentDay = currentDay;
	}

	public GregorianCalendar getCalendar() {
		return calendar;
	}
}
