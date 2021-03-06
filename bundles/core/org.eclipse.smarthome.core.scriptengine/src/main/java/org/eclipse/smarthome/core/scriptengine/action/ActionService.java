/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschränkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.core.scriptengine.action;

/**
 * This interface must be implemented by services that want to contribute script actions.
 * 
 * @author Kai Kreuzer - Initial contribution and API
 */
public interface ActionService {

	/**
	 * returns the FQCN of the action class.
	 * 
	 * @return the FQCN of the action class
	 */
	String getActionClassName();
	
	/**
	 * Returns the action class itself
	 * 
	 * @return the action class
	 */
	Class<?> getActionClass();
	
}
