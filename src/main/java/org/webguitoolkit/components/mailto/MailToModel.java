package org.webguitoolkit.components.mailto;

import java.util.ArrayList;
import java.util.List;

import org.webguitoolkit.ui.base.DataBag;
import org.webguitoolkit.ui.base.IDataBag;
import org.webguitoolkit.ui.controls.event.IServerEventListener;

/**
 * Model to use the MailTo component
 *
 * @author i102455
 *
 */
public class MailToModel {
	private List<Object> tos = null;
	private List<String> subjects = null;
	private String text = null;
	private String displayProperty = null;
	private String addressProperty = null;
	private String from = null;
	private IServerEventListener listener = null;

	/**
	 * @param displayPropertyTo required if you provide some receivers (Objects)
	 * @param addressPropertyTo required if you provide some receivers (Objects)
	 * @param listener ServerListener to handle mail
	 */
	public MailToModel(String displayPropertyTo, String addressPropertyTo, IServerEventListener listener) {
		this.displayProperty = displayPropertyTo;
		this.addressProperty = addressPropertyTo;
		this.listener = listener;
	}

	/**
	 * add recipients, objects get accessed by properties passed to the constructor
	 *
	 * @param to
	 */
	public void addTo(Object to) {
		if (tos == null)
			tos = new ArrayList<Object>();
		tos.add(to);
	}

	/**
	 * add recipients, objects get accessed by properties passed to the constructor
	 *
	 * @param tos
	 */
	public void setTos(List<Object> tos) {
		this.tos = tos;
	}

	protected List<Object> getTos() {
		return tos;
	}

	protected List<IDataBag> getToBags() {
		if (getTos() == null)
			return null;

		List<IDataBag> toBags = new ArrayList<IDataBag>();
		List<Object> toObjects = getTos();
		for (Object o : toObjects) {
			if (o instanceof IDataBag)
				toBags.add((IDataBag)o);
			else
				toBags.add(new DataBag(o));
		}
		return toBags;
	}

	protected int getNumberOfTo() {
		if (this.getTos() == null || this.getTos().isEmpty()) {
			return 0;
		}
		else {
			return this.getTos().size();
		}
	}

	/**
	 * add predefined subjects
	 *
	 * @param subject
	 */
	public void addSubject(String subject) {
		if (subjects == null)
			subjects = new ArrayList<String>();
		subjects.add(subject);
	}



	/**
	 * set predefined subjects
	 *
	 * @param subject
	 */
	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	protected List<String> getSubjects() {
		return subjects;
	}

	/**
	 * add a optional predefined text
	 *
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	protected String getText() {
		return text;
	}

	protected String getDisplayProperty() {
		return displayProperty;
	}

	protected String getAddressProperty() {
		return addressProperty;
	}

	protected IServerEventListener getListener() {
		return listener;
	}

	protected String getFrom() {
		return from;
	}

	/**
	 * define a sender. this is optional and for display only. The application need to take care of sender.
	 *
	 * @param from
	 */
	public void setFrom(String from) {
		this.from = from;
	}

}
