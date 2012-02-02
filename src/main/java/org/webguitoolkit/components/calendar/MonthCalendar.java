package org.webguitoolkit.components.calendar;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.ecs.html.Div;
import org.apache.ecs.html.IMG;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.webguitoolkit.ui.controls.util.JSUtil;

/**
 * 
 * @author i102497 (Timo Dreier)
 */
public class MonthCalendar extends Calendar {
	// private static final long serialVersionUID = 1L;
	//
	// // CSS CLASSES AND IDS
	private static final String CAL_TABLE = "wgt_cal_table";
	private static final String CAL_TABLE_HEADER_CELL_FIRST = "wgt_cal_table_header_cell_first";
	private static final String CAL_TABLE_HEADER_CELL = "wgt_cal_table_header_cell";
	private static final String CAL_TABLE_CELL = "wgt_cal_table_cell_month";
	private static final String CAL_TABLE_CELL_ACTUAL = "wgt_cal_table_cell_actual";
	private static final String CAL_TABLE_CELL_DATE = "wgt_cal_table_cell_date";
	private static final String CAL_TABLE_CELL_DATE_ACTUAL = "wgt_cal_table_cell_date_actual";
	private static final String CAL_TABLE_CELL_ADD = "wgt_cal_table_cell_add";
	private static final String CAL_TABLE_CELL_ADD_IMG = "wgt_cal_table_cell_add_img";
	private static final String CAL_TABLE_CELL_CONTENT = "wgt_cal_table_cell_content";
	private static final String CAL_TABLE_EVENT = "wgt_cal_table_event";
	private static final String CAL_TABLE_CONTENT = "wgt_cal_table_content";

	public MonthCalendar() {
		super();
	}

	public MonthCalendar(boolean selectionMode) {
		super(selectionMode);
	}

	public MonthCalendar(boolean selectionMode, boolean pocketMode) {
		super(selectionMode, pocketMode);
	}

	public MonthCalendar(boolean selectionMode, boolean pocketMode, boolean fireClickEvent) {
		super(selectionMode, pocketMode, fireClickEvent);
	}

	// Constructor
	public MonthCalendar(int year, int month) {
		super(year, month);
	}

	protected void createTableContent() {
		getContext().innerHtml(getId() + CAL_TABLE_CONTENT, "");

		// Set new values to calendar start at date = 1
		calendar.set(java.util.Calendar.MONTH, getCurrentMonth());
		calendar.set(java.util.Calendar.YEAR, getCurrentYear());
		calendar.set(java.util.Calendar.DATE, 1);

		// array of all event ids
		Collection<String> eventIDs = new ArrayList<String>();

		// content table
		org.apache.ecs.html.Table table = new org.apache.ecs.html.Table();
		stdParameter(table);
		table.setCellPadding(0).setCellSpacing(0);

		// root css class of table
		table.setClass(CAL_TABLE);

		// Cell width
		TR tr01 = new TR();
		// get short weekdays, the first day of the week depends on the current locale, e.g. US - Monday, GER - Sunday
		String[] newShortWeekdays = getShortWeekdays();

		/**** TABLE HEADER ****/
		for (int i = 0; i < 7; i++) {
			TD td01 = new TD();
			tr01.addElement(td01);
			if (i == 0) {
				td01.setClass(CAL_TABLE_HEADER_CELL_FIRST);
			}
			else {
				td01.setClass(CAL_TABLE_HEADER_CELL);
			}
			td01.addAttribute("width", getSingleCellWidth());

			if (getSingleCellHeight() > 0) {
				td01.addAttribute("height", getSingleCellHeight());
			}

			td01.addElement(newShortWeekdays[i]);
		}
		table.addElement(tr01);

		/***** TABLE CONTENT *****/
		Table cellContentTable = null;
		int idCnt = 0;
		// Sunday - Saturday 1 - 7
		int dayOffset = calendar.get(java.util.Calendar.DAY_OF_WEEK) - firstDayOfWeek;
		if (dayOffset < 0)
			dayOffset = dayOffset + 7;

		int maximumDaysInMonth = calendar.getActualMaximum(java.util.Calendar.DATE);

		for (int i = 0; i < 6; i++) {
			TR tr = new TR();

			// Sunday - Saturday it dependes on the locale what the first day in the week is
			for (int j = 1; j < 8; j++) {
				// ID
				String cellId = CAL_TABLE_CELL + idCnt;

				// actual date
				int date = idCnt - dayOffset + 1;

				java.util.Calendar cellCal = new GregorianCalendar(getModel().getTimeZone());
				cellCal.set(java.util.Calendar.YEAR, getCurrentYear());
				cellCal.set(java.util.Calendar.MONTH, getCurrentMonth());
				cellCal.set(java.util.Calendar.DAY_OF_MONTH, date);

				// check if date is part of month...
				boolean isDatePartOfMonth = false;
				if (date > 0 && date <= maximumDaysInMonth) {
					isDatePartOfMonth = true;
				}
				else {
					isDatePartOfMonth = false;
				}

				// fetch all date events
				Collection<CalendarEvent> events = getModel().getAllEventsForDate(getCurrentYear(), getCurrentMonth(), date,
						getModel().getTimeZone());

				TD td = new TD();
				td.setWidth(getSingleCellWidth());
				if (getSingleCellHeight() > 0) {
					td.setHeight(getSingleCellHeight());
				}

				// if current day
				String cellStyle;
				if (getActualDay() != -1 && getActualDay() == date) {
					cellStyle = CAL_TABLE_CELL + " " + CAL_TABLE_CELL_ACTUAL;
				}
				else {
					cellStyle = CAL_TABLE_CELL;
				}
				if (pocketMode) {
					cellStyle += " " + CAL_TABLE_POCKET;
				}
				if (selectionMode) {
					cellStyle += " " + CAL_TABLE_SELECTION;

					if (fireClickEvent) {
						td.setOnClick("calSelectDate('" + getId() + "','" + getCurrentYear() + "','" + getCurrentMonth()
								+ "','" + date + "',this);");
					}
					else {
						cellStyle += " cursorDefault";
					}
				}
				if (isDaySelected(date)) {
					cellStyle += " " + CAL_TABLE_CELL_HIGHLIGHTED;
				}
				td.setClass(cellStyle);

				td.setID(cellId);
				// only show plus icon if date is part of month, max events not reached...
				// further, check if flag addEventsInPast is false and current date is in past -> no plus icon
				if (isDatePartOfMonth && events.toArray().length < maxEventPerDays) {

					if (!selectionMode && getEditMode() == EDITMODE_ADD_ANYWHERE
							|| (getEditMode() == EDITMODE_DONT_ADD_IN_PAST && !isDateInPast(cellCal))) {
						// show plus icon
						td.setOnMouseOver("jQuery('#" + cellId + "_addicon" + "').show(0);");
						td.setOnMouseOut("jQuery('#" + cellId + "_addicon" + "').hide(0);");
					}
					else {
						td.setOnMouseOver("");
						td.setOnMouseOut("");
					}

					td.setBgColor("#FFFFFF");

				}
				else if (isDatePartOfMonth && events.toArray().length >= maxEventPerDays) {
					td.setOnMouseOver("");
					td.setOnMouseOut("");
					td.setBgColor("#FFFFFF");
				}
				else {
					td.setOnMouseOver("");
					td.setOnMouseOut("");
					td.setBgColor("#EEEEEE");

					// blank cells (with no date) should not be selectable at all!
					if (selectionMode) {
						td.setStyle("cursor: default;");
						td.setOnClick("");
					}
				}

				// Inner Cell Table
				cellContentTable = new Table();
				td.addElement(cellContentTable);

				cellContentTable.setHeight("100%");
				cellContentTable.setWidth("100%");
				// First Row
				TR cTR = new TR();
				cellContentTable.addElement(cTR);
				TD cTD = new TD();
				cTR.addElement(cTD);
				cTD.addElement("&nbsp;");
				cellStyle = CAL_TABLE_CELL_ADD;
				if (pocketMode) {
					cellStyle += " " + CAL_TABLE_POCKET;
				}
				cTD.setClass(cellStyle);
				cTD.setID(cellId + "_add");

				if (!selectionMode) {
					// plus icon
					IMG img = new IMG();
					cTD.addElement(img);
					img.setSrc("./images/components/calendar/add_event.gif");
					img.setID(cellId + "_addicon");
					img.setOnClick("calAddEvent('" + getId() + "','" + getCurrentYear() + "','" + getCurrentMonth() + "','" + date + "');");
					img.setClass(CAL_TABLE_CELL_ADD_IMG);
				}

				cTD = new TD();
				cTR.addElement(cTD);
				cTD.addElement("&nbsp;");
				cTD.setID(cellId + "_date");

				// if current day
				if (getActualDay() == date) {
					cellStyle = CAL_TABLE_CELL_DATE + " " + CAL_TABLE_CELL_DATE_ACTUAL;
				}
				else {
					cellStyle = CAL_TABLE_CELL_DATE;
				}
				if (pocketMode) {
					cellStyle += " " + CAL_TABLE_POCKET;
				}
				cTD.setClass(cellStyle);

				// set date
				if (isDatePartOfMonth) {
					cTD.addElement(String.valueOf(date));
				}
				else {
					cTD.addElement("");
				}

				// Add empty events
				int availableEvents = events.toArray().length;

				for (int k = 0; k < (maxEventPerDays - availableEvents); k++) {
					// Second row
					cTR = new TR();
					cellContentTable.addElement(cTR);

					cTD = new TD();
					cTR.addElement(cTD);
					cellStyle = CAL_TABLE_CELL_CONTENT;
					if (pocketMode) {
						cellStyle += " " + CAL_TABLE_POCKET;
					}
					cTD.setClass(cellStyle);
					cTD.setColSpan(2);
					cTD.addElement("&nbsp;");
				}

				// Create events in calendar (only for this month)
				if (isDatePartOfMonth) {
					Iterator<CalendarEvent> eventsIter = events.iterator();
					int eventsIdCnt = 1;
					while (eventsIter.hasNext()) {
						if (eventsIdCnt > maxEventPerDays) {
							break;
						}
						CalendarEvent se = eventsIter.next();
						CalendarEventDisplayProperties displayProperties = se.getDisplayProperties();
						if (displayProperties == null) {
							displayProperties = defaultEventDisplayProperties;
						}

						// Second row
						cTR = new TR();
						cellContentTable.addElement(cTR);

						cTD = new TD();
						cTR.addElement(cTD);
						cTD.setClass(CAL_TABLE_CELL_CONTENT);
						cTD.setColSpan(2);

						// Div event
						Div div = new Div();
						cTD.addElement(div);
						String longTitle = se.getEventTitle();

						String shortTitle = "";
						if (longTitle.length() > eventTitleMaxChars) {
							shortTitle = longTitle.substring(0, eventTitleMaxChars).concat("...");
						}
						else {
							shortTitle = longTitle;
						}

						String eventTooltip = se.getEventTooltip();
						String tooltip = "";
						if (!StringUtils.isBlank(eventTooltip)) {
							tooltip = eventTooltip;
						}
						else {
							tooltip = longTitle;
						}

						div.setOnClick("calChangeEvent('" + this.getId() + "','" + getCurrentYear() + "','" + getCurrentMonth() + "','"
								+ date + "','" + eventsIdCnt + "')");
						div.setID(cellId + "_content_" + eventsIdCnt);
						div.setTitle(tooltip);

						Table divContentTable = new Table();
						divContentTable.setWidth(getSingleCellWidth() - 10);

						div.addElement(divContentTable);
						divContentTable.setCellPadding(0).setCellSpacing(0);
						TR contentTR = new TR();
						divContentTable.addElement(contentTR);

						if (!StringUtils.isEmpty(displayProperties.getBgColorHover())) {
							div.setOnMouseOver("jQuery('#" + cellId + "_content_" + eventsIdCnt + "').css('background-color','"
									+ displayProperties.getBgColorHover() + "');");
							div.setOnMouseOut("jQuery('#" + cellId + "_content_" + eventsIdCnt + "').css('background-color','"
									+ displayProperties.getBgColor() + "');");
						}
						if (displayProperties.isShowEventTitle()) {
							div.setClass(CAL_TABLE_EVENT);

							TD eventTitleTD = new TD();
							eventTitleTD.setWidth((getSingleCellWidth() - 32));
							contentTR.addElement(eventTitleTD);
							eventTitleTD.setAlign("left");
							eventTitleTD.addElement(shortTitle);
							eventTitleTD.setStyle("color: " + displayProperties.getColor());
						}

						if (displayProperties.isShowEventImage()) {
							div.setClass(CAL_TABLE_EVENT);

							TD eventImageTD = new TD();
							contentTR.addElement(eventImageTD);
							eventImageTD.setAlign("middle");
							IMG eventImage = new IMG();
							eventImageTD.addElement(eventImage);
							eventImage.setStyle("vertical-align: middle; text-align:left;");
							eventImage.setSrc(displayProperties.getImageSrc());
						}

						if (!StringUtils.isEmpty(displayProperties.getBgColor())) {
							div.setStyle("background-color: " + displayProperties.getBgColor());
						}

						// remind all event ids to set tooltips scripts at the
						// end
						eventIDs.add(cellId + "_content_" + eventsIdCnt);

						eventsIdCnt++;
					}
				}

				tr.addElement(td);
				idCnt++;
			}
			table.addElement(tr);
		}

		// Now print table
		StringWriter sw = new StringWriter();
		table.output(sw);
		getContext().innerHtml(getId() + CAL_TABLE_CONTENT, sw.getBuffer().toString());

		// And finally set javascripts
		Iterator<String> eventIDsIter = eventIDs.iterator();
		while (eventIDsIter.hasNext()) {
			String tempId = eventIDsIter.next();
			String js = JSUtil.jQuery(tempId) + ".tooltip({track: true, delay: 0, showURL: false, showBody: ' - ', fade: 250});";
			getContext().sendJavaScript(tempId + ".tooltip", js);
		}

		// Write Month Label
		String monthDisplayName = calendar.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, locale);
		String monthLabel = monthDisplayName + "&nbsp; " + String.valueOf(getCurrentYear());
		// first remove existing label and set it again to new value
		getContext().innerHtml(getId() + CAL_TABLE_MONTH_LABEL, "&nbsp;");
		getContext().innerHtml(getId() + CAL_TABLE_MONTH_LABEL, monthLabel);
	}

	private boolean isDaySelected(int dayOfMonth) {
		boolean isDaySelected = false;
		for (GregorianCalendar currentCalDate : getModel().getSelectedDates()) {
			if (getCurrentYear() == currentCalDate.get(GregorianCalendar.YEAR)
					&& getCurrentMonth() == currentCalDate.get(GregorianCalendar.MONTH)
					&& dayOfMonth == currentCalDate.get(GregorianCalendar.DAY_OF_MONTH)) {
				isDaySelected = true;
				break;
			}
		}

		return isDaySelected;
	}

	private int getSingleCellWidth() {
		return (width / 7);
	}

	private int getSingleCellHeight() {
		return (height / 6);
	}

	/**
	 * To iterate over the different months, this method resets all events.<br>
	 */
	public void nextPage() {
		if (getCurrentMonth() == 11) {
			setCurrentYear(getCurrentYear() + 1);
			setCurrentMonth(0);
		}
		else {
			setCurrentMonth(getCurrentMonth() + 1);
		}
	}

	/**
	 * To iterate over the different months, this method resets all events.<br>
	 */
	public void lastPage() {
		if (getCurrentMonth() == 0) {
			setCurrentYear(getCurrentYear() - 1);
			setCurrentMonth(11);
		}
		else {
			setCurrentMonth(getCurrentMonth() - 1);
		}
	}

	/**
	 * @return the actual date if currentyear and currentmonth equals the calendar model year and month. otherwise -1
	 */
	protected int getActualDay() {
		// get the current day - check if this is part of actual month (in model)
		java.util.Calendar tempCalendar = GregorianCalendar.getInstance(getModel().getTimeZone());
		int currentYear = tempCalendar.get(java.util.Calendar.YEAR);
		int currentMonth = tempCalendar.get(java.util.Calendar.MONTH);

		if (currentYear == getCurrentYear()) {
			if (currentMonth == getCurrentMonth()) {
				return tempCalendar.get(java.util.Calendar.DATE);
			}
		}
		return -1;
	}

	protected String getNextPageLabel() {
		return "calendar.next.month@Next Month";
	}

	protected String getLastPageLabel() {
		return "calendar.last.month@Last Month";
	}

}
