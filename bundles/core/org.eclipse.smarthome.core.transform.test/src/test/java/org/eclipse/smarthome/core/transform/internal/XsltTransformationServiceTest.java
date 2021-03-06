/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschränkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.core.transform.internal;

import junit.framework.Assert;

import org.eclipse.smarthome.core.transform.TransformationException;
import org.eclipse.smarthome.core.transform.internal.service.XsltTransformationService;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Thomas.Eichstaedt-Engelen
 */
public class XsltTransformationServiceTest extends AbstractTransformationServiceTest {

	private XsltTransformationService processor;
	
	@Before
	public void init() {
		processor = new XsltTransformationService();
	}
	
	@Test
	public void testTransformByXSLT() throws TransformationException {

		// method under test
		String transformedResponse = 
			processor.transform("http/google_weather.xsl", source);
		
		// Asserts
		Assert.assertEquals("8", transformedResponse);
	}

}
