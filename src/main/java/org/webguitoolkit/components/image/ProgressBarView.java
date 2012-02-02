/*
Copyright 2008 Endress+Hauser Infoserve GmbH&Co KG 
Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
implied. See the License for the specific language governing permissions 
and limitations under the License.
*/ 
package org.webguitoolkit.components.image;

import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractView;
import org.webguitoolkit.ui.controls.container.Canvas;
import org.webguitoolkit.ui.controls.container.ICanvas;


/**
 * USe this view to show a simple Progress bar. This might be useful, when using a wizard 
 * or some other step by step function. 
 * 
 * @author i102455
 *
 */
public class ProgressBarView extends AbstractView {
	private int done;
	private String width;

	/**
	 * provide this class with information of width of the Progress bar and the 
	 * status in percent.
	 * 
	 * @param factory
	 * @param viewConnector
	 * @param percentDone
	 * @param width
	 */
	public ProgressBarView(WebGuiFactory factory, ICanvas viewConnector,
			int percentDone, String width) {
		super(factory, viewConnector);
		this.done = percentDone;
		this.width = width;
	}

	@Override
	protected void createControls( WebGuiFactory factory, ICanvas viewConnector ) {
		Canvas sourrounding = getFactory().newCanvas(viewConnector);
		Canvas doneArea = getFactory().newCanvas(sourrounding);
		Canvas openArea = getFactory().newCanvas(sourrounding);

		sourrounding.getStyle().addWidth(width);

		doneArea.getStyle().addWidth(done + "%");
		doneArea.getStyle().add("background-color", "#0099FF");
		doneArea.getStyle().add("text-align", "center");
		doneArea.getStyle().add("color", "white");
		doneArea.getStyle().add("font-weight", "bold");
		doneArea.getStyle().add("float", "left");
		if (done > 49) {
			getFactory().newLabel(doneArea, done + "%");
			getFactory().newLabel(openArea, "&#160;");
		} else {
			getFactory().newLabel(openArea, done + "%");
			getFactory().newLabel(doneArea, "&#160;");
		}
		openArea.getStyle().addWidth((100 - done) + "%");
		openArea.getStyle().add("background-color", "#99CCFF");
		openArea.getStyle().add("float", "left");
		openArea.getStyle().add("text-align", "center");
		openArea.getStyle().add("color", "black");
		openArea.getStyle().add("font-weight", "bold");		

	}

}
