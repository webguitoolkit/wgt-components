package org.webguitoolkit.components.calendar.ical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ICalendar {
	private String method = "PUBLISH";
	private String prodid = "-//Mozilla.org/NONSGML Mozilla Calendar V1.1//EN";
	private String version = "2.0";
	private List<VEvent> events = new ArrayList<VEvent>();
	
	public String getMethod() {
		return method;
	}

	public String getProdid() {
		return prodid;
	}

	public String getVersion() {
		return version;
	}

	public List<VEvent> getEvents() {
		return events;
	}

	public void addVEvnet( VEvent event ){
		events.add(event);
	}
	
	public void setMethod(String method) {
		this.method = method;		
	}
	public void setProdid(String prodid) {
		this.prodid = prodid;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	public void writeTo( OutputStream out ) throws IOException {
		writeTo( new OutputStreamWriter(out) );
	}
	public void writeTo( Writer out ) throws IOException {
		PrintWriter printWriter = new PrintWriter(out);
		printWriter.println("BEGIN:VCALENDAR");
		printWriter.println("VERSION:"+getVersion());
		printWriter.println("PRODID:"+getProdid());
		printWriter.println("METHOD:"+getMethod());

		writeEvents( printWriter );
		
		printWriter.print("END:VCALENDAR");
//		printWriter.flush();
	}
	
	protected void writeEvents(PrintWriter printWriter) throws IOException {
		for( VEvent ev : getEvents() ){
			printWriter.println("BEGIN:VEVENT");
			
			if(StringUtils.isNotEmpty(ev.getUID()))
				printWriter.println("UID:"+ev.getUID());
			if( ev.getDtstamp()!=null )
				printWriter.println("DTSTAMP:"+ toDateTimeString(ev.getDtstamp()));
			if( ev.getCreated()!=null )
				printWriter.println("CREATED:"+ toDateTimeString(ev.getCreated()));
			if( ev.getLastModified()!=null )
				printWriter.println("LAST-MODIFIED:"+ toDateTimeString(ev.getLastModified()));
			if(StringUtils.isNotEmpty(ev.getOrganizer()))
				printWriter.println("ORGANIZER:"+ev.getOrganizer());
			if(StringUtils.isNotEmpty(ev.getSummary()))
				printWriter.println("SUMMARY:"+ev.getSummary());
			if(StringUtils.isNotEmpty(ev.getDescription()))
				printWriter.println("DESCRIPTION:"+ev.getDescription());
			if(StringUtils.isNotEmpty(ev.getEventClass()))
				printWriter.println("CLASS:"+ev.getEventClass());
			if( ev.getStart()!=null ){
				if( ev.isFullDay() )
					printWriter.println("DTSTART;VALUE=DATE:"+ toDateString(ev.getStart()));
				else
					printWriter.println("DTSTART:"+ toDateTimeString(ev.getStart()));
					
			}
			if( ev.getEnd()!=null ){
				if( ev.isFullDay() )
					printWriter.println("DTEND;VALUE=DATE:"+ toDateString(ev.getEnd()));
				else
					printWriter.println("DTEND:"+ toDateTimeString(ev.getEnd()));
			}
			if(StringUtils.isNotEmpty(ev.getUrl()))
				printWriter.println("URL:"+ev.getUrl());
			if(StringUtils.isNotEmpty(ev.getTransp()))
				printWriter.println("TRANSP:"+ev.getTransp());
			if(ev.getCategories()!= null && !ev.getCategories().isEmpty() ){
				String cats = StringUtils.join(ev.getCategories().toArray(), ",");
				printWriter.println("CATEGORIES:"+cats);
			}
			printWriter.println("END:VEVENT");
		}
	}

	public static ICalendar createFrom(InputStream in) throws IOException{
		return createFrom(new InputStreamReader(in));
	}
	public static ICalendar createFrom(Reader in) throws IOException{
		ICalendar cal = new ICalendar();
		
		BufferedReader rd = new BufferedReader(in);

		String line;
		while ((line = rd.readLine()) != null) {
			if( line.startsWith("BEGIN:")){
				String eventType = line.replaceAll("BEGIN:", "");
				if( eventType.startsWith("VEVENT") ){
					cal.events.add(createVEvent(rd));
				}
			}
			else if( line.startsWith("METHOD:")){
				cal.setMethod(line.replaceAll("METHOD:", ""));
			}
			else if( line.startsWith("PRODID:")){
				cal.setProdid(line.replaceAll("PRODID:", ""));
			}
			else if( line.startsWith("VERSION:")){
				cal.setVersion(line.replaceAll("VERSION:", ""));
			}
		}
		rd.close();
		
		return cal;
	}

	private static java.util.Calendar parseDate(String dateString) {
		String year = dateString.substring(0, 4);
		String month = dateString.substring(4, 6);
		String day = dateString.substring(6, 8);

		java.util.Calendar calendar = new GregorianCalendar();
		calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day));
		return calendar;
	}
	private String toDateTimeString(java.util.Calendar date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
		String dateString = dateFormat.format(date.getTime());
		String timeString = timeFormat.format(date.getTime());
		return dateString+"T"+timeString+"Z";
	}
	private String toDateString(java.util.Calendar date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String dateString = dateFormat.format(date.getTime());
		return dateString;
	}

	private static java.util.Calendar parseDateTime(String dateTimeString) {
		int timeIndex = dateTimeString.indexOf('T');
		String date = dateTimeString.substring(0, timeIndex);
		String time = dateTimeString.substring(timeIndex + 1);

		String hour = time.substring(0, 2);
		String min = time.substring(2, 4);
		String second = time.substring(4, 6);

		java.util.Calendar d = parseDate(date);
		d.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
		d.set(java.util.Calendar.MINUTE, Integer.parseInt(min));
		d.set(java.util.Calendar.SECOND, Integer.parseInt(second));

		return d;
	}

	protected static VEvent createVEvent( BufferedReader rd ) throws IOException{
		VEvent vEvent = new VEvent();
		String line;
		while ((line = rd.readLine()) != null) {
			if(line.startsWith("END:VEVENT"))
				return vEvent;

			if( line.startsWith("DESCRIPTION:")){
				vEvent.setDescription( line.replaceAll("DESCRIPTION:", "") );
			}
			else if( line.startsWith("SUMMARY:")){
				vEvent.setSummary( line.replaceAll("SUMMARY:", "") );
			}
			else if( line.startsWith("DTSTART")){
				if( line.startsWith("DTSTART;VALUE=DATE:") ){
					vEvent.setStart( parseDate(line.replaceAll("DTSTART;VALUE=DATE:", "")) );
					vEvent.setFullDay(true);
				}
				else if(line.startsWith("DTSTART:")){
					vEvent.setStart( parseDateTime(line.replaceAll("DTSTART:", "")) );
				}
			}
			else if( line.startsWith("DTEND")){
				if( line.startsWith("DTEND;VALUE=DATE:") ){
					vEvent.setEnd( parseDate(line.replaceAll("DTEND;VALUE=DATE:", "")));
				}
				else if(line.startsWith("DTEND:")){
					vEvent.setEnd( parseDateTime(line.replaceAll("DTEND:", "")) );
				}
			}
			else if( line.startsWith("URL:")){
				vEvent.setUrl( line.replaceAll("URL:", "") );
			}
			else if( line.startsWith("TRANSP:")){
				vEvent.setTransp( line.replaceAll("TRANSP:", "") );
			}
			else if( line.startsWith("UID:")){
				vEvent.setUID( line.replaceAll("UID:", "") );
			}
			else if( line.startsWith("CREATED:")){
				vEvent.setCreated( parseDateTime( line.replaceAll("CREATED:", "") ) );
			}
			else if( line.startsWith("LAST-MODIFIED:")){
				vEvent.setLastModified( parseDateTime( line.replaceAll("LAST-MODIFIED:", "") ) );
			}
			else if( line.startsWith("ORGANIZER:")){
				vEvent.setOrganizer( line.replaceAll("ORGANIZER:", "") );
			}
			else if( line.startsWith("CLASS:")){
				vEvent.setEventClass( line.replaceAll("CLASS:", "") );
			}
			else if( line.startsWith("DTSTAMP:")){
				vEvent.setDtstamp( parseDateTime( line.replaceAll("DTSTAMP:", "") ) );
			}
			else if( line.startsWith("CATEGORIES:")){
				String catString = line.replaceAll("CATEGORIES:", "");
				String[] cats = catString.split(",");
				vEvent.setCategories( Arrays.asList(cats) );
			}
		}
		throw new IOException("Missing End of VEVENT");
	}
}

