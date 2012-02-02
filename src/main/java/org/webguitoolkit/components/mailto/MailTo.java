package org.webguitoolkit.components.mailto;

import java.util.ArrayList;
import java.util.List;

import org.webguitoolkit.ui.base.DataBag;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractPopup;
import org.webguitoolkit.ui.controls.Page;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.event.ClientEvent;
import org.webguitoolkit.ui.controls.event.ServerEvent;
import org.webguitoolkit.ui.controls.form.AbstractButtonBarListener;
import org.webguitoolkit.ui.controls.form.ButtonBar;
import org.webguitoolkit.ui.controls.form.IButtonBar;
import org.webguitoolkit.ui.controls.form.ICompound;
import org.webguitoolkit.ui.controls.form.ILabel;
import org.webguitoolkit.ui.controls.form.ISelect;
import org.webguitoolkit.ui.controls.form.IText;
import org.webguitoolkit.ui.controls.form.ITextarea;
import org.webguitoolkit.ui.controls.form.popupselect.IPopupSelect;
import org.webguitoolkit.ui.controls.layout.ITableLayout;
import org.webguitoolkit.ui.controls.util.validation.ValidatorUtil;

/**
 * This component shows a popup window with a simple mail form containing a From, To, Subject and a textfield.
 * The form can be prefilled by using the MailToModel. The result is a MailToMail, wihch is bound to a ServerEvent.
 * You need to implement a IServerEventListener to handle the MailToMail (sending)
 *
 * From: This field is optional and only for display reasons, the application should know who is sending the message
 * To: 	You have three options to use the to field: leave empty (user has to enter an address), add one receiver or
 * 		add a list of receivers (user can chose one or many entries)
 * Subject: Add no (free subject for user), one (fixed subject) or many (user can make a selection) entries.
 * Text: Put a predefined text in the Textfield.This text can be edited by the user.
 *
 * @author i102455
 *
 */
public class MailTo extends AbstractPopup {
	private static final int EVENT_ID = 88887833;
	public static final String MAIL_PARAMETER_NAME = "m2mprmtr";
	private IText to = null, subject = null;
	private IPopupSelect toSelect = null;
	private ISelect subjectSelect = null;
	private ITextarea text = null;

	private MailToModel model = null;

	class ButtonbarListener extends AbstractButtonBarListener {

		public ButtonbarListener(ICompound compound) {
			super(compound);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean delete(Object delegate) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object newDelegate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int persist() {
			try {
				ServerEvent e = new ServerEvent(EVENT_ID);

				MailToMail m = new MailToMail();

				// fill mail here
				//to
				if(to !=null){
					m.addTo(compound.getBag().getString("to"));
				} else if (toSelect!=null){
					List<Object> l = toSelect.getSelectedObjects();
					for(Object o : l){
						m.addTo(new DataBag(o).getString(model.getAddressProperty()));
					}
				}

				m.setFrom(compound.getBag().getString("from"));

				//text
				m.setText(text.getValue());

				//subject
				if (subject != null)
					m.setSubject(subject.getValue());
				else {
					m.setSubject((String)subjectSelect.getConvertedValue());
				}
				e.putParameter(MAIL_PARAMETER_NAME, m);

				fireServerEvent(e);
			}
			catch (Exception e) {
			}
			return 0;
		}

		@Override
		public boolean refresh(Object delegate) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onCancel(ClientEvent event) {
			super.onCancel(event);
			MailTo.this.close();
		}

		@Override
		public void postSave() {
			super.postSave();
			MailTo.this.close();
		}

	}

	/**
	 * init component
	 *
	 * @param factory
	 * @param page
	 * @param titel
	 * 		define a title for the popup window
	 * @param model
	 * 		the model is required. see MoilToModel for details
	 */
	public MailTo(WebGuiFactory factory, Page page, String titel, MailToModel model) {
		super(factory, page, titel, 400, 500);
		this.model = model;
		this.registerListener(EVENT_ID, model.getListener());
	}

	@Override
	protected void createControls(WebGuiFactory factory, ICanvas viewConnector) {
		ILabel label = null;
		ICompound c = factory.createCompound(viewConnector);
		c.setBag(new DataBag(""));
		ITableLayout layout = getFactory().createTableLayout(c);
		layout.getStyle().addWidth("95%");

		if(model.getFrom()!=null){
			label = getFactory().createLabel(layout, "mailto.from@From:");
			IText from = getFactory().createText(layout, "from." + model.getDisplayProperty(), label);
			c.getBag().addProperty("from", model.getFrom());
			from.setEditable(false);
			from.setValue(model.getFrom());
			layout.newRow();
		}

		label = getFactory().createLabel(layout, "mailto.to@To:");
		layout.getCurrentCell().setStyle("vertical-align: top;");
		if (model.getNumberOfTo() == 0) {
			// text
			to = getFactory().createText(layout, "to", label);
			to.getStyle().addWidth("100%");
			to.addValidator(ValidatorUtil.MANDATORY_VALIDATOR);
			to.addValidator(ValidatorUtil.EMAIL_VALIDATOR);
		}
		else if (model.getNumberOfTo() == 1) {
			// single
			to = getFactory().createText(layout, "to." + model.getDisplayProperty(), label);
			c.getBag().addProperty("to", model.getToBags().get(0).getString(model.getAddressProperty()));
			to.setEditable(false);
			to.setValue(model.getToBags().get(0).getString(model.getDisplayProperty()));
		}
		else {
			// multi
			toSelect = factory.createPopupSelect(layout, "to", model.getDisplayProperty(), true,
					new String[] { model.getDisplayProperty(), }, new String[] { model.getDisplayProperty() }, label);
			toSelect.setAvailableObjects(model.getTos());
			toSelect.addValidator(ValidatorUtil.MANDATORY_VALIDATOR);
		}
		layout.newRow();



		label = getFactory().createLabel(layout, "mailto.subject@Subject:");
		layout.getCurrentCell().setStyle("vertical-align: top;");
		if (model.getSubjects() == null || model.getSubjects().size() == 0) {
			// text
			subject = getFactory().createText(layout, "subject", label);
			subject.getStyle().addWidth("100%");
			subject.addValidator(ValidatorUtil.MANDATORY_VALIDATOR);
		}
		else if (model.getSubjects().size() == 1) {
			// single
			subject = getFactory().createText(layout, "subject", label);
			subject.setEditable(false);
			c.getBag().addProperty("subject", model.getSubjects().get(0));
			subject.setValue(model.getSubjects().get(0));
		}
		else {
			subjectSelect = getFactory().createSelect(layout, "subject");
			subjectSelect.setDescribingLabel(label);
			subjectSelect.setPromptKey("mailto.subject.promt@Please select");
			List<String[]> options = new ArrayList<String[]>();
			for (String option : model.getSubjects()) {
				options.add(new String[] { option, option });
			}
			subjectSelect.getDefaultModel().setOptions(options);
			subjectSelect.loadList();
			subjectSelect.addValidator(ValidatorUtil.MANDATORY_VALIDATOR);
		}
		layout.newRow();
		label = getFactory().createLabel(layout, "mailto.body@Body:");
		layout.getCurrentCell().setStyle("vertical-align: top;");
		text = getFactory().createTextarea(layout, "txt");
		text.setDescribingLabel(label);
		text.getStyle().addWidth("100%");
		text.getStyle().addHeight("300px");
		text.addValidator(ValidatorUtil.MANDATORY_VALIDATOR);
		if(model.getText()!=null){
			text.setValue(model.getText());
		}
		layout.newRow();

	IButtonBar bar =	getFactory().createButtonBar(layout, new String[] { IButtonBar.BUTTON_SAVE, IButtonBar.BUTTON_CANCEL },
				IButtonBar.BUTTON_DISPLAY_MODE_TEXT, new ButtonbarListener(c));
	((ButtonBar) bar).getSaveButton().setLabelKey("send");
		c.changeElementMode(ICompound.MODE_NEW);
		layout.getCurrentCell().setColSpan(2);
	}

}
