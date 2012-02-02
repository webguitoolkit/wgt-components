package org.webguitoolkit.components.calendar.ical;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ICalServlet extends HttpServlet {

	public static final String TASKSCHEDULER_ICAL_ID = "taskscheduler.ical.id";

	private static final long MONTH = 1000l * 60l * 60l * 24l * 31l;

	private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String username = req.getParameter("username");
		String calid = req.getParameter("calid");

		((HttpServletResponse)resp).setContentType("text/calendar");
		resp.setCharacterEncoding("UTF-8");

		Date current = new Date(System.currentTimeMillis());

		ICalendar ical = new ICalendar();
		ical.setMethod("PUBLISH");
		ical.setVersion("2.0");
		ical.setProdid("-//Endress.com/SchedulerTest//EN");

		PrintWriter writer = resp.getWriter();

		for (int i = 0; i < 20; i++) {
			VEvent vevent = new VEvent();
			vevent.setUID(Integer.toString(i+1000));
			GregorianCalendar currentCalendar = new GregorianCalendar();
			vevent.setDtstamp(currentCalendar);
			vevent.setFullDay(true);
			currentCalendar = new GregorianCalendar();
			currentCalendar.add(Calendar.DAY_OF_MONTH, i);
			vevent.setStart(currentCalendar);
			vevent.setSummary("Täsk " + i);
			if( i%2==0 ){
				vevent.setDescription("Description " + i + "\\nNew Line");
			}
			vevent.setCategories(Arrays.asList(new String[] { "Holiday" }));
			ical.addVEvnet(vevent);
		}

		ical.writeTo(writer);
		
		writer.flush();
//		writer.close();
	}
}
