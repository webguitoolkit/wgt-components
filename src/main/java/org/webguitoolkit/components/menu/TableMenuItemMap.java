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

package org.webguitoolkit.components.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.webguitoolkit.ui.controls.event.IActionListener;

/**
 * Helper Class to hold the menu entries
 *
 * @author BK
 */
public class TableMenuItemMap implements Iterable<String> {
	private List<String> keys = null;
	private Map<String, IActionListener> listeners = null;

	public Iterator<String> iterator() {
		if (keys != null)
			return keys.iterator();
		return null;
	}

	public int size() {
	 return	keys.size();
	}

	public TableMenuItemMap() {
		keys = new ArrayList<String>();
		listeners = new HashMap<String, IActionListener>();
	}

	public IActionListener get(String key) {
		return listeners.get(key);
	}

	public IActionListener get(int index) {
		return this.get(keys.get(index));
	}

	public int indexOf(String key) {
		return keys.indexOf(key);
	}

	public void put(String key, IActionListener listener) {
		//if (StringUtils.isBlank(key) || listener == null)
		if (StringUtils.isBlank(key))
			return;
		keys.remove(key);
		keys.add(key);
		listeners.put(key, listener);
	}

	public void put(String key, IActionListener listener, int index) {
		//if (StringUtils.isBlank(key) || listener == null)
		if (StringUtils.isBlank(key))
			return;
		keys.remove(key);
		keys.add(index, key);
		listeners.put(key, listener);
	}
}
