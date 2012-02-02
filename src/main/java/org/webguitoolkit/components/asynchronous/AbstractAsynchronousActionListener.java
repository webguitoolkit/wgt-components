package org.webguitoolkit.components.asynchronous;

import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.Page;
import org.webguitoolkit.ui.controls.event.IActionListener;

public abstract class AbstractAsynchronousActionListener implements IActionListener {
	private Page page = null;
	private WebGuiFactory factory = null;

	public AbstractAsynchronousActionListener(Page page, WebGuiFactory factory) {
		this.page = page;
		this.factory = factory;
	}

	protected void startProcess(IAsynchronousProcess process) {
		process.start();
		AsynchronousProcessDialog dialog = new AsynchronousProcessDialog(factory, page,
				"AsynchronousProcessDialog.processing@Processing...", 220, 80, process);
		dialog.show();
	}
}
