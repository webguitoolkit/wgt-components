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
package org.webguitoolkit.components.input;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.webguitoolkit.ui.ajax.IContext;
import org.webguitoolkit.ui.base.IDataBag;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.event.ClientEvent;
import org.webguitoolkit.ui.controls.event.IServerEventListener;
import org.webguitoolkit.ui.controls.event.ServerEvent;
import org.webguitoolkit.ui.controls.form.Compound;
import org.webguitoolkit.ui.controls.form.FormControl;
import org.webguitoolkit.ui.controls.util.JSUtil;
import org.webguitoolkit.ui.controls.util.PropertyAccessor;
import org.webguitoolkit.ui.controls.util.validation.ValidationException;

public class SelectionField extends FormControl {

	private static final String EVENT_TYPE_REMOVE = "remove";
	private static final String EVENT_TYPE_OPEN = "open";
	private static final String DOT_BUTTON_OPEN = ".open";
	private static final String DOT_BUTTON_REMOVE = ".remove";

	private String displayProperty;
//	private SelectionHelperModel selectionHelperModel;
	private List selectedObjects = new ArrayList();
	private List availableObjects = new ArrayList();
	private boolean isMultiSelect;
	private String windowTitle;
	private String[] columns;
	private String[] columnTitles;
	
	public SelectionHelperModel getSelectionHelperModel() {
		SelectionHelperModel selectionHelperModel = new SelectionHelperModel();
		selectionHelperModel.setRows(12);
		selectionHelperModel.setWindowTitle( windowTitle );
		selectionHelperModel.setColumns( columns );
		selectionHelperModel.setResourceKeys( columnTitles );
		return selectionHelperModel;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	public void setColumnTitles(String[] columnTitles) {
		this.columnTitles = columnTitles;
	}
	public void setDisplayProperty(String displayProperty) {
		this.displayProperty = displayProperty;
	}

	@Override
	protected void endHTML(PrintWriter out) {
        out.print("<input type=text");

        // will be rendered with a definite state on the beginning.
        out.print(" value=" + JSUtil.wr2(getContext().processValue(getId()) ) );
        out.print(" readonly " );

        String defaultCss = "wgtInputTextWith2Button";
        setDefaultCssClass(defaultCss);
        stdParameter(out);

        out.print(">");
        
        String openButtonSource = getContext().getValue( getId() + DOT_BUTTON_OPEN + ".src" );
        if( StringUtils.isEmpty( openButtonSource ))
        	openButtonSource = "images/wgt/selectfield_arrow.gif";
        String deleteButtonSource = getContext().getValue( getId() + DOT_BUTTON_REMOVE + ".src" );
        if( StringUtils.isEmpty( deleteButtonSource ))
        	deleteButtonSource = "images/wgt/selectfield_delete.gif";
    	out.print("<img border=\"0\" src=\""+openButtonSource+"\" class=\"wgtPointerCursor\" " + 
    			JSUtil.atId(getId() + DOT_BUTTON_OPEN )+
    			"style=\"vertical-align:middle;\" onclick=\""+ JSUtil.jsFireEvent( getId(), EVENT_TYPE_OPEN)+"\">");
    	out.print("<img border=\"0\" src=\""+deleteButtonSource+"\" class=\"wgtPointerCursor\" " + 
    			JSUtil.atId(getId() + DOT_BUTTON_REMOVE )+
    			"style=\"vertical-align:middle;\" onclick=\""+ JSUtil.jsFireEvent( getId(), EVENT_TYPE_REMOVE)+"\"> ");
	}

	@Override
	public void dispatch(ClientEvent event) {
		
		if( true ){
			if ( EVENT_TYPE_OPEN.equals( event.getType() ) ) {
				SelectionHelperModel model = getSelectionHelperModel();
				model.setData( availableObjects );
				SelectionHelper2 sh = new SelectionHelper2( WebGuiFactory.getInstance(), event.getSource().getPage(), model );
				sh.registerListener(SelectionHelper.SELECTION_EVENT, new SelectListener() );
				sh.show();
			} else if ( EVENT_TYPE_REMOVE.equals( event.getType() ) ) {
				if( getSelectionHelperModel().getSelectionMode() == SelectionHelperModel.SELECTION_MODE_SINGLE ){
					setValue( "" );
				}
				else{
					SelectionHelperModel model = getSelectionHelperModel();
					model.setData( selectedObjects );
					SelectionHelper2 sh = new SelectionHelper2( WebGuiFactory.getInstance(), event.getSource().getPage(), model );
					sh.registerListener(SelectionHelper.SELECTION_EVENT, new RemoveListener() );
					sh.show();
				}
			}
		}
	}
	
	
	@Override
	public void changeMode(int mode) {
		super.changeMode(mode);
		// also switch the visibility of the button for the calenndar
		IContext ctx = getContext();
		
		String openName = "images/wgt/selectfield_arrow.gif";
		if( mode==Compound.MODE_READONLY )
			openName = "images/wgt/selectfield_arrow_ro.gif";
		String removeName = "images/wgt/selectfield_delete.gif";
		if( mode==Compound.MODE_READONLY )
			removeName = "images/wgt/selectfield_delete_ro.gif";
		ctx.add( getId() + DOT_BUTTON_OPEN + ".src", openName ,IContext.TYPE_ATT, IContext.STATUS_NOT_EDITABLE );
		ctx.add( getId() + DOT_BUTTON_REMOVE + ".src", removeName ,IContext.TYPE_ATT, IContext.STATUS_NOT_EDITABLE );
	}

	@Override
	public void saveTo(Object dataObject) {
        try {
        	validate();
        	if( isMultiSelect ){
        		Collection coll = (Collection) PropertyAccessor.retrieveProperty( dataObject, getProperty() );
        		List tempSelected = new ArrayList( selectedObjects );
        		for (Iterator iter = coll.iterator(); iter.hasNext();) {
					Object object = (Object) iter.next();
					if( !tempSelected.contains( object ))
						iter.remove();
					else
						tempSelected.remove( object );
				}
        		for (Iterator iter = tempSelected.iterator(); iter.hasNext();) {
					Object object = (Object) iter.next();
					coll.add( object );
				}
        	}
        	else{
        		Object value = null;
        		if( selectedObjects!=null && !selectedObjects.isEmpty() ){
        			value = selectedObjects.get( 0 );
        		}
    			PropertyAccessor.storeProperty(dataObject, getProperty(), value );
        	}
		} catch (ValidationException e) {
			surroundingCompound().addError(e.getMessage(),getProperty());
		}
	}


	@Override
	public void loadFrom(Object data) {
		if( isMultiSelect ){
			selectedObjects = new ArrayList( (Collection) PropertyAccessor.retrieveProperty( data, getProperty() ) );
			setValue( createValue( selectedObjects ) );
		}
		else{
			selectedObjects = new ArrayList();
			Object selected = PropertyAccessor.retrieveProperty( data, getProperty() );
			if( selected!=null ){
				selectedObjects.add( selected );
		        setValue( PropertyAccessor.retrieveString( selected, displayProperty) );
			}
			else
		        setValue( "" );
		}
	}


	private String createValue( Collection objects ) {
		String value = "";
		for (Iterator iter = objects.iterator(); iter.hasNext();) {
			Object object = (Object) iter.next();
			value += PropertyAccessor.retrieveString( object, displayProperty)+", ";
		}
		return value;
	}


	public class SelectListener implements IServerEventListener{

		public void handle(ServerEvent event) {
			Object result = event.getParameter( SelectionHelper2.SELECTION_EVENT_PARAMETER );
			if( result instanceof Collection ){
				for (Iterator iter = ((Collection)result).iterator(); iter.hasNext();) {
					Object object = (Object) iter.next();
					if( !selectedObjects.contains( object ) )
						selectedObjects.add(object);
				}
				SelectionField.this.setValue( createValue( selectedObjects ) );
			}
			else{
				String value = PropertyAccessor.retrieveString( result, displayProperty);
				SelectionField.this.setValue( value );
				selectedObjects = new ArrayList();
				selectedObjects.add( result );
			}
		}
		
	}
	public class RemoveListener implements IServerEventListener{
		public void handle(ServerEvent event) {
			Object result = event.getParameter( SelectionHelper2.SELECTION_EVENT_PARAMETER );
			if( result instanceof Collection ){
				for (Iterator iter = ((Collection)result).iterator(); iter.hasNext();) {
					Object object = (Object) iter.next();
					selectedObjects.remove(object);
				}
				SelectionField.this.setValue( createValue(selectedObjects) );
			}
			else{
				selectedObjects.remove(result);
				SelectionField.this.setValue( createValue(selectedObjects) );
			}
		}
	}
	public List getSelectedObjects() {
		return selectedObjects;
	}
	public void setSelectedObjects(List selectedObjects) {
		this.selectedObjects = selectedObjects;
	}
	public void setAvailableObjects(List availableObjects) {
		this.availableObjects = availableObjects;
	}

}
