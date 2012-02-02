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
package org.webguitoolkit.components.gchart;

import java.util.Arrays;

import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractView;
import org.webguitoolkit.ui.controls.container.HtmlElement;
import org.webguitoolkit.ui.controls.container.ICanvas;

import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.PieChart;
import com.googlecode.charts4j.Plot;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Slice;

/**
 * 
 * 
 * @author peter
 * 
 */
public class PiechartView extends AbstractView {

	public PiechartView(WebGuiFactory factory, ICanvas viewConnector) {
		super(factory, viewConnector);
	}

	@Override
	protected void createControls(WebGuiFactory factory, ICanvas viewConnector) {
		HtmlElement img = factory.newHtmlElement(viewConnector);

		// PIE
		Slice s1 = Slice.newSlice(30, Color.newColor("CACACA"), "GWT");
		Slice s2 = Slice.newSlice(30, Color.newColor("DF7417"), "RAP");
		Slice s3 = Slice.newSlice(30, Color.newColor("951800"), "WGT");
		Slice s4 = Slice.newSlice(10, Color.newColor("01A1DB"), "Other");

		PieChart pchart = GCharts.newPieChart(s1, s2, s3, s4);
		pchart.setTitle("A Better WebGUI with http://charts4j.googlecode.com", Color.newColor("000000"), 16);
		pchart.setSize(500, 200);
		pchart.setThreeD(true);
		String url = pchart.toURLString();
		img.setTagName("IMG");
		img.setAttribute("src", url);

		// GRAPH
		Plot plot = Plots.newPlot(Data.newData(77, 22, 88, 10));
		
		LineChart lchart = GCharts.newLineChart(plot);
		lchart.addHorizontalRangeMarker(20, 40, Color.newColor("01A1DB"));
		lchart.setGrid(20, 20, 5, 5);
		lchart.addXAxisLabels(AxisLabelsFactory.newAxisLabels(Arrays.asList(
				"CW 5", "CW 6", "CW 7","CW 8")));
		lchart.addYAxisLabels(AxisLabelsFactory.newNumericAxisLabels(0, 33.3,
				66.6, 100));
		url = lchart.toURLString();
		HtmlElement limg = factory.newHtmlElement(viewConnector);
		limg.setTagName("IMG");
		limg.setAttribute("src", url);
		
		
	}

}
