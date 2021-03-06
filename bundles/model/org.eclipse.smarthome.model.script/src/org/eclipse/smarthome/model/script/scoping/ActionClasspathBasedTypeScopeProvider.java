/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschränkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.model.script.scoping;

import org.eclipse.xtext.common.types.access.ClasspathTypeProviderFactory;
import org.eclipse.xtext.common.types.access.IJvmTypeProvider;
import org.eclipse.xtext.common.types.access.impl.ClasspathTypeProvider;
import org.eclipse.xtext.common.types.xtext.AbstractConstructorScope;
import org.eclipse.xtext.common.types.xtext.AbstractTypeScopeProvider;
import org.eclipse.xtext.common.types.xtext.ClasspathBasedConstructorScope;
import org.eclipse.xtext.common.types.xtext.ClasspathBasedTypeScope;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * Same implementation as {@link ClasspathBasedTypeScopeProvider}, just that it needs a
 * {@link ActionClasspathTypeProviderFactory} injected instead of a {@link ClasspathTypeProviderFactory}
 * 
 * @author Kai Kreuzer - Initial contribution and API
 */
@SuppressWarnings("restriction")
public class ActionClasspathBasedTypeScopeProvider extends AbstractTypeScopeProvider {

	@Inject
	private ActionClasspathTypeProviderFactory typeProviderFactory;

	@Inject 
	private IQualifiedNameConverter qualifiedNameConverter;
	
	@Override
	public ClasspathBasedTypeScope createTypeScope(IJvmTypeProvider typeProvider, Predicate<IEObjectDescription> filter) {
		return new ClasspathBasedTypeScope((ClasspathTypeProvider) typeProvider, qualifiedNameConverter, filter);
	}
	
	@Override
	public AbstractConstructorScope createConstructorScope(IJvmTypeProvider typeProvider, Predicate<IEObjectDescription> filter) {
		ClasspathBasedTypeScope typeScope = createTypeScope(typeProvider, filter);
		return new ClasspathBasedConstructorScope(typeScope);
	}

	public void setTypeProviderFactory(ActionClasspathTypeProviderFactory typeProviderFactory) {
		this.typeProviderFactory = typeProviderFactory;
	}

	@Override
	public ClasspathTypeProviderFactory getTypeProviderFactory() {
		return typeProviderFactory;
	}

}
