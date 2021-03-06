/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschränkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.rest.sitemap.internal.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.smarthome.io.rest.item.beans.ItemBean;

/**
 * This is a java bean that is used with JAXB to serialize widgets
 * to XML or JSON.
 *  
 * @author Kai Kreuzer - Initial contribution and API
 * @author Chris Jackson
 *
 */
@XmlRootElement(name="widget")
public class WidgetBean {

	public String widgetId;
	public String type;
	public String name;
	
	public String label;
	public String icon;
	public String labelcolor;
	public String valuecolor;

	// widget-specific attributes
	@XmlElement(name="mapping")
	public List<MappingBean> mappings = new ArrayList<MappingBean>();
	public Boolean switchSupport;
	public Integer sendFrequency;
	public String separator;
	public Integer refresh;
	public Integer height;
	public BigDecimal minValue;
	public BigDecimal maxValue;
	public BigDecimal step;
	public String url;
	public String service;
	public String period;
	
	public ItemBean item;
	public PageBean linkedPage;


	// only for frames, other linkable widgets link to a page
	@XmlElement(name="widget")
	public final List<WidgetBean> widgets = new ArrayList<WidgetBean>();
	
	public WidgetBean() {}
		
}