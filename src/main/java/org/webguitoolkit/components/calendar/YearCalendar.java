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
public class YearCalendar extends Calendar {
//	private static final long serialVersionUID = 1L;
//
//	// CSS CLASSES AND IDS
	private static final String CAL_TABLE = "wgt_cal_table";
	private static final String CAL_TABLE_HEADER_CELL_FIRST = "wgt_cal_table_header_cell_first";
	private static final String CAL_TABLE_HEADER_CELL = "wgt_cal_table_header_cell";
	private static final String CAL_TABLE_CELL = "wgt_cal_table_cell_year";
	private static final String CAL_TABLE_CELL_ACTUAL = "wgt_cal_table_cell_actual";
	private static final String CAL_TABLE_CELL_DATE = "wgt_cal_table_cell_date";
	private static final String CAL_TABLE_CELL_DATE_ACTUAL = "wgt_cal_table_cell_date_actual";
	private static final String CAL_TABLE_CELL_ADD = "wgt_cal_table_cell_add";
	private static final String CAL_TABLE_CELL_ADD_IMG = "wgt_cal_table_cell_add_img";
	private static final String CAL_TABLE_CELL_CONTENT = "wgt_cal_table_cell_content";
	private static final String CAL_TABLE_EVENT = "wgt_cal_table_event";
	private static final String CAL_TABLE_CONTENT = "wgt_cal_table_content";
	private static final String CAL_TABLE_DAY_OF_WEEK = "wgt_cal_table_day_of_week";

	// Constructor
	public YearCalendar() {
		super();
	}
	// Constructor
	public YearCalendar(int year) {
		super(year);
	}

	protected void createTableContent() {
		getContext().innerHtml( getId() + CAL_TABLE_CONTENT, "");

		// Set new values to calendar start at date = 1
		calendar.set(java.util.Calendar.MONTH, getCurrentMonth()-1 );
		calendar.set(java.util.Calendar.YEAR, getCurrentYear() );
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
		String[] newShortMonth = getShortMonth();

		/**** TABLE HEADER ****/
		for (int i = 0; i < 12; i++) {
			TD td01 = new TD();
			tr01.addElement(td01);
			if (i == 0) {
				td01.setClass(CAL_TABLE_HEADER_CELL_FIRST);
			}
			else {
				td01.setClass(CAL_TABLE_HEADER_CELL);
			}
			td01.addAttribute("width", getSingleCellWidth());
			td01.addElement(newShortMonth[i]);
		}
		table.addElement(tr01);

		/***** TABLE CONTENT *****/
		Table cellContentTable = null;

		for (int d = 1; d < 32; d++) {
			TR tr = new TR();

			// Sunday - Saturday it dependes on the locale what the first day in the week is
			for (int m = 0; m < 12; m++) {
				java.util.Calendar cellCal = new GregorianCalendar(getCurrentYear(), m, d);
				
				calendar.set(java.util.Calendar.MONTH, m );
				int maximumDaysInMonth = calendar.getActualMaximum(java.util.Calendar.DATE);
				
				// ID
				String cellId = CAL_TABLE_CELL + getCurrentYear() + twoDigits(m) + twoDigits(d);

				// check if date is part of month...
				boolean isDatePartOfMonth = false;
				if (d <= maximumDaysInMonth ) {
					isDatePartOfMonth = true;
				}
				else {
					isDatePartOfMonth = false;
				}

				// fetch all date events
				Collection<CalendarEvent> events = getModel().getAllEventsForDate(getCurrentYear(), m, d, getModel().getTimeZone());

				TD td = new TD();
				td.setWidth(getSingleCellWidth());

				// if current day
				if ( isCurrentDay(cellCal) ){
					td.setClass(CAL_TABLE_CELL + " " + CAL_TABLE_CELL_ACTUAL);
				}
				else if(isDatePartOfMonth ) {
					td.setClass(CAL_TABLE_CELL + " " + CAL_TABLE_DAY_OF_WEEK + cellCal.get(java.util.Calendar.DAY_OF_WEEK));
				}
				else {
					td.setClass(CAL_TABLE_CELL );
				}

				td.setID(cellId);
				// only show plus icon if date is part of month, max events not reached...
				// further, check if flag addEventsInPast is false and current date is in past -> no plus icon
				if (isDatePartOfMonth && events.toArray().length < maxEventPerDays) {

					if (getEditMode() == EDITMODE_ADD_ANYWHERE || ( getEditMode() == EDITMODE_DONT_ADD_IN_PAST && !isDateInPast(cellCal) ) ) {
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
				cTD.setClass(CAL_TABLE_CELL_ADD);
				cTD.setID(cellId + "_add");

				// plus icon
				IMG img = new IMG();
				cTD.addElement(img);
				img.setSrc("./images/components/calendar/add_event.gif");
				img.setID(cellId + "_addicon");
				img.setOnClick("calAddEvent('" + getId() + "','" + getCurrentYear() + "','" + m + "','" + d + "');");
				img.setClass(CAL_TABLE_CELL_ADD_IMG);

				cTD = new TD();
				cTR.addElement(cTD);
				cTD.addElement("&nbsp;");
				cTD.setID(cellId + "_date");

				// if current day
				if ( isCurrentDay(cellCal)) {
					cTD.setClass(CAL_TABLE_CELL_DATE + " " + CAL_TABLE_CELL_DATE_ACTUAL);
				}
				else {
					cTD.setClass(CAL_TABLE_CELL_DATE);
				}

				// set date
				if (isDatePartOfMonth) {
					cTD.addElement(String.valueOf(d)+"&nbsp;" +shortWeekdays[cellCal.get(java.util.Calendar.DAY_OF_WEEK)] );
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
					cTD.setClass(CAL_TABLE_CELL_CONTENT);
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

						div.setOnClick("calChangeEvent('" + this.getId() + "','" + getCurrentYear() + "','" + m + "','" + d + "','" + eventsIdCnt + "')");
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
			}
			table.addElement(tr);
		}

		// Now print table
		StringWriter sw = new StringWriter();
		table.output(sw);
		getContext().innerHtml( getId() + CAL_TABLE_CONTENT, sw.getBuffer().toString());

		// And finally set javascripts
		Iterator<String> eventIDsIter = eventIDs.iterator();
		while (eventIDsIter.hasNext()) {
			String tempId = eventIDsIter.next();
			String js = JSUtil.jQuery(tempId) + ".tooltip({track: true, delay: 0, showURL: false, showBody: ' - ', fade: 250});";
			getContext().sendJavaScript(tempId + ".tooltip", js);
		}

		// Write Year Label
		String yearLabel =  String.valueOf(getCurrentYear());
		// first remove existing label and set it again to new value
		getContext().innerHtml( getId() + CAL_TABLE_MONTH_LABEL, "&nbsp;");
		getContext().innerHtml( getId() + CAL_TABLE_MONTH_LABEL, yearLabel);
	}



	private int getSingleCellWidth() {
		return (width / 12);
	}

	
	/**
	 * To iterate over the different months, this method resets all events.<br>
	 */
	public void nextPage() {
		setCurrentYear( getCurrentYear()+1 );
	}

	/**
	 * To iterate over the different months, this method resets all events.<br>
	 */
	public void lastPage() {
		setCurrentYear( getCurrentYear()-1 );
	}


	/**
	 * @return the actual date if currentyear and currentmonth equals the calendar model year and month. otherwise -1
	 */
	protected int getActualDay() {
		// get the current day - check if this is part of actual month (in model)
		java.util.Calendar tempCalendar = GregorianCalendar.getInstance(getModel().getTimeZone());
		int currentYear = tempCalendar.get(java.util.Calendar.YEAR);
		int currentMonth = tempCalendar.get(java.util.Calendar.MONTH);

		if (currentYear == getCurrentYear() ) {
			if (currentMonth == getCurrentMonth()-1 ) {
				return tempCalendar.get(java.util.Calendar.DATE);
			}
		}
		return -1;
	}

	protected String twoDigits(int in ){
		String str = Integer.toString(in);
		if( str.length() == 1 )
			str = "0" + str;
		return str;
	}
	
	protected String getNextPageLabel() {
		return "calendar.next.year@Next Year";
	}

	protected String getLastPageLabel() {
		return "calendar.last.year@Last Year";
	}
}
