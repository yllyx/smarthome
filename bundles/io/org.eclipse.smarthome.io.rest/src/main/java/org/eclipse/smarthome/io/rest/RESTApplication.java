/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschränkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.io.rest;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.io.rest.internal.resources.RootResource;
import org.eclipse.smarthome.io.servicediscovery.DiscoveryService;
import org.eclipse.smarthome.io.servicediscovery.ServiceDescription;
import org.eclipse.smarthome.model.core.ModelRepository;
import org.eclipse.smarthome.ui.items.ItemUIRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.core.util.FeaturesAndProperties;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * This is the main component of the REST API; it gets all required services injected,
 * registers itself as a servlet on the HTTP service and adds the different rest resources
 * to this service.
 * 
 * @author Kai Kreuzer - Initial contribution and API
 */
@ApplicationPath(RESTApplication.REST_SERVLET_ALIAS)
public class RESTApplication extends Application {

	public static final String REST_SERVLET_ALIAS = "/rest";

	private static final Logger logger = LoggerFactory.getLogger(RESTApplication.class);
	
	private int httpSSLPort;

	private int httpPort;

	private HttpService httpService;

	private DiscoveryService discoveryService;

	static private EventPublisher eventPublisher;
	
	static private ItemUIRegistry itemUIRegistry;

	static private ModelRepository modelRepository;
	
	static private List<RESTResource> restResources = new ArrayList<RESTResource>();

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}
	
	public void unsetHttpService(HttpService httpService) {
		this.httpService = null;
	}

	public void setEventPublisher(EventPublisher eventPublisher) {
		RESTApplication.eventPublisher = eventPublisher;
	}
	
	public void unsetEventPublisher(EventPublisher eventPublisher) {
		RESTApplication.eventPublisher = null;
	}

	static public EventPublisher getEventPublisher() {
		return eventPublisher;
	}

	public void setItemUIRegistry(ItemUIRegistry itemUIRegistry) {
		RESTApplication.itemUIRegistry = itemUIRegistry;
	}
	
	public void unsetItemUIRegistry(ItemRegistry itemUIRegistry) {
		RESTApplication.itemUIRegistry = null;
	}

	static public ItemUIRegistry getItemUIRegistry() {
		return itemUIRegistry;
	}

	public void setModelRepository(ModelRepository modelRepository) {
		RESTApplication.modelRepository = modelRepository;
	}
	
	public void unsetModelRepository(ModelRepository modelRepository) {
		RESTApplication.modelRepository = null;
	}

	static public ModelRepository getModelRepository() {
		return modelRepository;
	}

	public void setDiscoveryService(DiscoveryService discoveryService) {
		this.discoveryService = discoveryService;
	}
	
	public void unsetDiscoveryService(DiscoveryService discoveryService) {
		this.discoveryService = null;
	}

	public void addRESTResource(RESTResource resource) {
		RESTApplication.restResources.add(resource);
	}

	public void removeRESTResource(RESTResource resource) {
		RESTApplication.restResources.remove(resource);
	}

	public void activate() {			    
        try {
        	BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass())
                    .getBundleContext();

    		com.sun.jersey.spi.container.servlet.ServletContainer servletContainer =
    			       new ServletContainer(this);
    		
			httpService.registerServlet(REST_SERVLET_ALIAS,
					servletContainer, getJerseyServletParams(), createHttpContext());

 			logger.info("Started REST API at /rest");

 			if (discoveryService != null) {
 	        	try {
 	        		httpPort = Integer.parseInt(bundleContext.getProperty("jetty.port"));
 	 				discoveryService.registerService(getDefaultServiceDescription());
 	        	} catch(NumberFormatException e) {}
 	        	try {
 	        		httpSSLPort = Integer.parseInt(bundleContext.getProperty("jetty.port.ssl"));
 	 				discoveryService.registerService(getSSLServiceDescription());
 	        	} catch(NumberFormatException e) {}
			}
        } catch (ServletException se) {
            throw new RuntimeException(se);
        } catch (NamespaceException se) {
            throw new RuntimeException(se);
        }
	}
	
	public void deactivate() {
        if (this.httpService != null) {
            httpService.unregister(REST_SERVLET_ALIAS);
            logger.info("Stopped REST API");
        }
        
        if (discoveryService != null) {
 			discoveryService.unregisterService(getDefaultServiceDescription());
			discoveryService.unregisterService(getSSLServiceDescription()); 			
 		}
        restResources.clear();
	}
	
	protected HttpContext createHttpContext() {
		HttpContext defaultHttpContext = httpService.createDefaultHttpContext();
		return defaultHttpContext;
	}
	
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> result = new HashSet<Class<?>>();
        result.add(RootResource.class);
        for(RESTResource resource : restResources) {
        	result.add(resource.getClass());
        }
        return result;
    }

    public static List<RESTResource> getRestResources() {
		return restResources;
	}

	private Dictionary<String, String> getJerseyServletParams() {
        Dictionary<String, String> jerseyServletParams = new Hashtable<String, String>();
        jerseyServletParams.put("javax.ws.rs.Application", RESTApplication.class.getName());
        // required because of bug http://java.net/jira/browse/JERSEY-361
        jerseyServletParams.put(FeaturesAndProperties.FEATURE_XMLROOTELEMENT_PROCESSING, "true");

        return jerseyServletParams;
    }
    
    private ServiceDescription getDefaultServiceDescription() {
		Hashtable<String, String> serviceProperties = new Hashtable<String, String>();
		serviceProperties.put("uri", REST_SERVLET_ALIAS);
		return new ServiceDescription("_smarthome-server._tcp.local.", "Eclipse SmartHome", httpPort, serviceProperties);
    }

    private ServiceDescription getSSLServiceDescription() {
    	ServiceDescription description = getDefaultServiceDescription();
    	description.serviceType = "_smarthome-server-ssl._tcp.local.";
    	description.serviceName = "smarthome-ssl";
		description.servicePort = httpSSLPort;
		return description;
    }
}
