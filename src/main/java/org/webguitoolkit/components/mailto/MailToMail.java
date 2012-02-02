package org.webguitoolkit.components.mailto;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class MailToMail {
	private List<String> to;
	private String subject;
	private String text;
	private String from;

	public List<String> getTo() {
		return to;
	}

	public String getToAsString() {
		String result = "";
		if (to != null)
			for (String s : to) {
				result += s + ", ";
			}

		if(result.trim().endsWith(","))
			result = StringUtils.substring(result, 0, result.length()-2);

		return result;
	}

	protected void setTo(List<String> to) {
		this.to = to;
	}

	protected void addTo(String newTo) {
		if(to==null)
			to= new ArrayList<String>();
		this.to.add(newTo);
	}

	public String getSubject() {
		return subject;
	}

	protected void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	protected void setText(String text) {
		this.text = text;
	}

	public String getFrom() {
		return from;
	}

	protected void setFrom(String from) {
		this.from = from;
	}

}
