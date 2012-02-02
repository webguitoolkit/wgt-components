package org.webguitoolkit.components.dialog;

import org.apache.ecs.html.Div;
import org.apache.ecs.html.IMG;
import org.apache.ecs.html.TD;
import org.webguitoolkit.ui.controls.Page;
import org.webguitoolkit.ui.controls.dialog.DynamicDialog;
import org.webguitoolkit.ui.controls.event.ClientEvent;
import org.webguitoolkit.ui.controls.event.IActionListener;
import org.webguitoolkit.ui.controls.layout.ITableLayout;

public class DetailedMessageDialog extends DynamicDialog {

	public static final String IMG_INFO = "./images/wgt/icons/msg_icon_info.gif";
	public static final String IMG_WARN = "./images/wgt/icons/msg_icon_warn.gif";
	public static final String IMG_ERROR = "./images/wgt/icons/msg_icon_error.gif";

	private static final int DEFAULT_WIDTH = 300;
	private static final int DEFAULT_WIDTH_MESSAGEDETAILS = DEFAULT_WIDTH - 15;
	private static final int DEFAULT_HEIGHT_MESSAGEDETAILS = 200;

	private ITableLayout layout;
	private int widthMessageDetails = -1;
	private int heightMessageDetails = -1;

	private String imageSource;
	private String message;
	private String messageDetails;

	public DetailedMessageDialog(Page body, String title) {
		super(body);

		setWindowTitle(title);
		setWidth(DEFAULT_WIDTH);

		layout = getFactory().createTableLayout(getWindow());
		layout.setLayoutMode(false);
	}

	public void show() {
		layout.setLayoutMode(true);

		TD headerCellImg = new TD();
		headerCellImg.setStyle("padding-left: 6px; vertical-align: middle;");
		layout.addCell(headerCellImg);

		Div headerContentImg = new Div();
		headerCellImg.addElement(headerContentImg);

		if (imageSource != null) {
			headerContentImg.addElement(new IMG().setSrc(imageSource));
		}
		else {
			// use info icon by default
			headerContentImg.addElement(new IMG().setSrc(IMG_INFO));
		}

		TD headerCellMsg = new TD();
		headerCellMsg.setStyle("padding-left: 6px; vertical-align: middle;");
		layout.addCell(headerCellMsg);

		Div headerContentMsg = (Div)new Div().setClass("wgtDetailedMessageHeader");
		headerCellMsg.addElement(headerContentMsg);

		if (message != null) {
			headerContentMsg.addElement(message);
		}

		layout.newRow();

		TD bodyCell = new TD();
		layout.addCell(bodyCell);
		bodyCell.setColSpan(2);

		Div bodyContent = new Div();
		bodyCell.addElement(bodyContent);
		bodyContent.setClass("wgtDetailedMessageBody");

		if (messageDetails != null) {
			bodyContent.setStyle("width: " + getWidthMessageDetails() + "px; height: " + getHeightMessageDetails() + "px;");
			bodyContent.addElement(messageDetails);
		}

		layout.newRow();

		// ok-button
		getFactory().createButton(layout, null, "button.ok", "", new IActionListener() {

			public void onAction(ClientEvent event) {
				destroy();
			}
		});

		layout.getCurrentCell().setColSpan(2).setAlign("center");
	}

	public String getImageSource() {
		return imageSource;
	}

	public void setImageSource(String imageSource) {
		this.imageSource = imageSource;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessageDetails() {
		return messageDetails;
	}

	public void setMessageDetails(String messageDetails) {
		this.messageDetails = messageDetails;
	}

	public int getWidthMessageDetails() {
		if (widthMessageDetails < 0) {
			return DEFAULT_WIDTH_MESSAGEDETAILS;
		}
		return widthMessageDetails;
	}

	public void setWidthMessageDetails(int widthMessageDetails) {
		this.widthMessageDetails = widthMessageDetails;
	}

	public int getHeightMessageDetails() {
		if (heightMessageDetails < 0) {
			return DEFAULT_HEIGHT_MESSAGEDETAILS;
		}
		return heightMessageDetails;
	}

	public void setHeightMessageDetails(int heightMessageDetails) {
		this.heightMessageDetails = heightMessageDetails;
	}

}
