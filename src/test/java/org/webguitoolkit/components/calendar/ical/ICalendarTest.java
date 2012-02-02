package org.webguitoolkit.components.calendar.ical;

import java.io.InputStream;

import junit.framework.TestCase;

public class ICalendarTest extends TestCase {
	
	public void testRead() throws Exception{
		InputStream is = getClass().getResourceAsStream("/ical/holiday.ics");
		ICalendar calendar = ICalendar.createFrom( is );
		
		assertTrue( calendar.getEvents().size() > 0 );
	}
	public void testCreate() throws Exception{
		
//		Calendar testDate = new GregorianCalendar();
//		
//		ICalendar cal = new ICalendar();
//		cal.setMethod("method");
//		cal.setProdid("prodId");
//		cal.setVersion("version");
//		
//		VEvent ev = new VEvent();
//		
//		testDate.set(Calendar.YEAR, 2000 );
//		ev.setDtstamp( testDate );
//		testDate.set(Calendar.YEAR, 2001 );
//		ev.setCreated( testDate );
//		testDate.set(Calendar.YEAR, 2002 );
//		ev.setLastModified( testDate );
//		testDate.set(Calendar.YEAR, 2003 );
//		ev.setStart( testDate );
//		testDate.set(Calendar.YEAR, 2004 );
//		ev.setEnd( testDate );
//		ev.setFullDay(true);
//		ev.setUID("uid");
//		ev.setDescription("description");
//		ev.setEventClass("eventClass");
//		ev.setOrganizer("organizer");
//		ev.setSummary("summary");
//		ev.setTransp("transp");
//		ev.setUrl("url");
//		
//		
//		cal.addVEvnet(ev);
//		
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		cal.writeTo( out );
//		cal.writeTo( System.out );
//
//		ICalendar cal2 = ICalendar.createFrom( new ByteArrayInputStream( out.toByteArray() ));
//	
//		assertEquals( cal.getMethod() , cal2.getMethod() );
//		assertEquals( cal.getProdid() , cal2.getProdid() );
//		assertEquals( cal.getVersion() , cal2.getVersion() );
//		VEvent e1 = cal.getEvents().get(0);
//		VEvent e2 = cal2.getEvents().get(0);
//		assertEquals( e1.getDescription() , e2.getDescription() );
//		assertEquals( e1.getEventClass() , e2.getEventClass() );
//		assertEquals( e1.getOrganizer() , e2.getOrganizer() );
//		assertEquals( e1.getSummary() , e2.getSummary() );
//		assertEquals( e1.getTransp() , e2.getTransp() );
//		assertEquals( e1.getUID() , e2.getUID() );
//		assertEquals( e1.getUrl() , e2.getUrl() );
//		e1.getCreated().set(Calendar.MILLISECOND, 0);
//		e2.getCreated().set(Calendar.MILLISECOND, 0);
//		assertEquals( e1.getCreated(), e2.getCreated() );
//		e1.getDtstamp().set(Calendar.MILLISECOND, 0);
//		e2.getDtstamp().set(Calendar.MILLISECOND, 0);
//		assertEquals( e1.getDtstamp() , e2.getDtstamp() );
//		assertEquals( e1.getEnd().get(Calendar.YEAR) , e2.getEnd().get(Calendar.YEAR) );
//		e1.getLastModified().set(Calendar.MILLISECOND, 0);
//		e2.getLastModified().set(Calendar.MILLISECOND, 0);
//		assertEquals( e1.getLastModified() , e2.getLastModified() );
//		assertEquals( e1.getStart().get(Calendar.YEAR) , e2.getStart().get(Calendar.YEAR) );
	
	}
	
	
}
