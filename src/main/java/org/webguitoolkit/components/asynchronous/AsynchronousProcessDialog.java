package org.webguitoolkit.components.asynchronous;

import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractPopup;
import org.webguitoolkit.ui.controls.Page;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.container.ICanvasWindowListener;
import org.webguitoolkit.ui.controls.event.ClientEvent;
import org.webguitoolkit.ui.controls.event.IServerEventListener;
import org.webguitoolkit.ui.controls.event.ServerEvent;
import org.webguitoolkit.ui.controls.form.ILabel;
import org.webguitoolkit.ui.controls.layout.ITableLayout;

public class AsynchronousProcessDialog extends AbstractPopup {

	private IAsynchronousProcess process = null;
	private ILabel counter, total, percent;
	private ICanvas done = null;

	public AsynchronousProcessDialog(WebGuiFactory factory, Page page, String titel, int width, int height, IAsynchronousProcess process) {
		super(factory, page, titel, width, height);
		setWindowActionListener(new CanvasListener());
		this.process = process;
	}

	private class CanvasListener implements ICanvasWindowListener {

		public void onClose(ClientEvent event) {
			// do nothing
		}

		public void onMaximize(ClientEvent event) {

		}

		public void onMinimize(ClientEvent event) {

		}

		public void onResize(ClientEvent event) {

		}

	}

	@Override
	protected void createControls(WebGuiFactory factory, ICanvas viewConnector) {
		ITableLayout layout = factory.createTableLayout(viewConnector);

		if (process.isShowProcessItemCount()) {
			getFactory().createLabel(layout, "AsynchronousProcessDialog.processing@Processing...");
			counter = getFactory().createLabel(layout, String.valueOf(process.getCounter()));
			getFactory().createLabel(layout, "AsynchronousProcessDialog.of@of");
			total = getFactory().createLabel(layout, String.valueOf(process.getTotal()));
		}

		if (process.isShowProcessPercentage() && !process.isShowProcessBar()) {
			percent = getFactory().createLabel(layout, "(0%)");
		}

		if (process.isShowProcessBar()) {
			ITableLayout layout2 = factory.createTableLayout(viewConnector);
			layout2.addCssClass("processDialogBarLayout");
			ICanvas full = factory.createCanvas(layout2);
			full.addCssClass("processDialogBarUnprocessed");
			done = factory.createCanvas(full);
			if (process.getPercentage() >= 1)
				done.setWidth(process.getPercentage());
			done.addCssClass("processDialogBarProcessed");

			if (process.isShowProcessPercentage()) {
				percent = getFactory().createLabel(full, "0%");
				// move the label over the progress bar
				percent.addCssClass("processDialogBarLabel");
			}
		}

		runTimer();
	}

	private void runTimer() {
		IServerEventListener timer = null;
		timer = new IServerEventListener() {
			public void handle(ServerEvent event) {
				if (!process.hasFinished()) {
					if (process.isShowProcessItemCount()) {
						counter.setText(String.valueOf(process.getCounter()));
						total.setText(String.valueOf(process.getTotal()));
					}

					getPage().timer(1000, this);

					if (process.isShowProcessBar()) {
						if (process.getPercentage() >= 1) {
							done.getStyle().addWidth(process.getPercentage() + "%");
						}

						if (process.isShowProcessPercentage()) {
							// if (process.getPercentage() >= 15){
							percent.setText(process.getPercentage() + "%");
							// }
						}
					}
					else if (process.isShowProcessPercentage()) {
						// if (process.getPercentage() >= 15){
						percent.setText("(" + process.getPercentage() + "%)");
						// }
					}
				}
				else {
					AsynchronousProcessDialog.this.close();
				}
			}
		};
		getPage().timer(1000, timer);
	}

}
