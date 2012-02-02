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
import org.webguitoolkit.ui.controls.container.HtmlElement;
import org.webguitoolkit.ui.controls.container.ICanvas;


public class ImageView extends AbstractView {

	protected HtmlElement html = null;
	protected String source;

	public ImageView(WebGuiFactory factory, ICanvas viewConnector) {
		super(factory, viewConnector);
	}
	public ImageView(WebGuiFactory factory, ICanvas viewConnector, String source ) {
		super(factory, viewConnector);
		this.source = source;
	}

	@Override
	protected void createControls( WebGuiFactory factory, ICanvas viewConnector ) {
		html = getFactory().newHtmlElement( viewConnector );
		html.setTagName( "IMG" );
		html.setAttribute( "src", source );
	}
	
	public void setSource( String source ){
		this.source = source;
		if( html != null )
			html.setAttribute("src", source);
	}
}
