package org.webguitoolkit.components.calendar;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.StringUtils;
import org.webguitoolkit.components.calendar.ical.ICalendar;
import org.webguitoolkit.components.calendar.ical.VEvent;
import org.webguitoolkit.ui.base.WGTException;

public class ICalCalendarModel extends DefaultCalendarModel {
	
	private Hashtable<String, CalendarEventDisplayProperties> displayProperties = new Hashtable<String, CalendarEventDisplayProperties>();
	
	private boolean isInitialized = false;
	
	private final String[] urls;

	private final Proxy proxy;
	
	public ICalCalendarModel( String... urls ){
		this(Proxy.NO_PROXY, urls);
	}
	public ICalCalendarModel(  String proxyHost, int port, String... urls ){
		this(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, port)), urls );
	}
	public ICalCalendarModel( Proxy proxy, String... urls ){
		this.urls = urls;
		this.proxy = proxy;
	}

	public ICalCalendarModel( TimeZone timeZone, String... urls ){
		this(timeZone, Proxy.NO_PROXY, urls);
	}
	public ICalCalendarModel( TimeZone timeZone, String proxyHost, int port, String... urls ){
		this(timeZone, new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, port)), urls );
	}
	public ICalCalendarModel( TimeZone timeZone, Proxy proxy, String... urls ){
		super(timeZone);
		this.urls = urls;
		this.proxy = proxy;
	}

	@Override
	public Collection<CalendarEvent> getAllEventsForDate(int year, int month, int date, TimeZone timeZone) {
		if( !isInitialized ){
			for(String url: urls )
				fetchEvents(url, proxy);
			isInitialized = true;
		}
		return super.getAllEventsForDate(year, month, date, timeZone);
	}
	
	
	private void fetchEvents(String url, Proxy proxy ){
		CalendarEventDisplayProperties props = getDisplayProperties().get(url);
		try{
			HttpURLConnection con = null;
			if (url.startsWith("https")) {
				HttpsURLConnection.setFollowRedirects(true);
				con = (HttpURLConnection)new URL(url).openConnection(proxy);
			}
			else {
				HttpURLConnection.setFollowRedirects(true);
				con = (HttpURLConnection)new URL(url).openConnection(proxy);
			}
			con.setDoOutput(true);
			con.setDoInput(true);
			con.setRequestMethod("GET");
			con.setConnectTimeout(3000); // set timeout
			con.setInstanceFollowRedirects(true);
			
			int responseCode = con.getResponseCode();
			if( responseCode == 200 ){
				// Parse response to calendar object
				ICalendar calendar = ICalendar.createFrom( con.getInputStream() );
				
				Collection<CalendarEvent> events = getCalendarEvents();
				for( VEvent ev : calendar.getEvents() ){
					
					java.util.Calendar start = ev.getStart();
					java.util.Calendar end = ev.getEnd();
					
					CalendarEvent event = new CalendarEvent();
					if( StringUtils.isNotEmpty( ev.getDescription() ) )
						event.setDescription( ev.getDescription() );
					else
						event.setDescription( ev.getSummary() );		
					event.setEventTitle( ev.getSummary() );
					if( ev.isFullDay() )
						event.setDurationInMinutes(60*24);
					else
						event.setDurationInMinutes( (int) ( ( end.getTimeInMillis() - start.getTimeInMillis() ) * 60000 ) );
					event.setTimestampInMillis( start.getTimeInMillis() );
					event.setDisplayProperties(props);
					events.add(event);

					
					if( end !=null ){
						start.add(java.util.Calendar.DAY_OF_YEAR, 1);
						while( start.before(end) ){
							CalendarEvent e = new CalendarEvent();
							e.setEventTitle(ev.getSummary());
							if( StringUtils.isNotEmpty( ev.getDescription() ) )
								e.setDescription( ev.getDescription() );
							else
								e.setDescription( ev.getSummary() );
							e.setDurationInMinutes(60*24);
							e.setTimestampInMillis(start.getTimeInMillis());
							e.setDisplayProperties(props);
							events.add(e);
							start.add(java.util.Calendar.DAY_OF_YEAR, 1);
						}
					}
					
				}
			}
			else
				throw new WGTException("Failed loading iCalendar from url: " + url + " response code: " + responseCode);
		}
		catch(IOException ex ){
			throw new WGTException("Error loading ICal Calendar Model", ex);
		}
	}
	private java.util.Calendar parseDate( String dateString ){
		String year = dateString.substring(0,4);
		String month = dateString.substring(4,6);
		String day = dateString.substring(6,8);
		
		java.util.Calendar calendar = new GregorianCalendar();
		calendar.set(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day));
		return calendar;
	}
	private java.util.Calendar parseDateTime( String dateTimeString ){
		int timeIndex = dateTimeString.indexOf('T');
		String date = dateTimeString.substring(0,timeIndex);
		String time = dateTimeString.substring(timeIndex+1);
		
		String hour = time.substring(0,2);
		String min = time.substring(2,4);
		String second = time.substring(4,6);
		
		java.util.Calendar d = parseDate(date);
		d.set(java.util.Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
		d.set(java.util.Calendar.MINUTE, Integer.parseInt(min));
		d.set(java.util.Calendar.SECOND, Integer.parseInt(second));
		
		return d;
	}
	/**
	 * @param dispalyInformation the dispalyInformation to set
	 */
	public void setDisplayProperties(Hashtable<String, CalendarEventDisplayProperties> displayProperties) {
		this.displayProperties = displayProperties;
	}
	/**
	 * @return the dispalyInformation
	 */
	public Hashtable<String, CalendarEventDisplayProperties> getDisplayProperties() {
		return displayProperties;
	}
}
