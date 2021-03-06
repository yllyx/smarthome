/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschränkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/*
 * generated by Xtext
 */
package org.eclipse.smarthome.model.rule.scoping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.model.rule.internal.engine.RuleContextHelper;
import org.eclipse.smarthome.model.script.scoping.ScriptScopeProvider;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.MapBasedScope;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XVariableDeclaration;
import org.eclipse.xtext.xbase.XbaseFactory;
import org.eclipse.xtext.xbase.scoping.LocalVariableScopeContext;
import org.eclipse.xtext.xbase.scoping.featurecalls.LocalVarDescription;
import org.eclipse.smarthome.model.rule.rules.ChangedEventTrigger;
import org.eclipse.smarthome.model.rule.rules.CommandEventTrigger;
import org.eclipse.smarthome.model.rule.rules.EventTrigger;
import org.eclipse.smarthome.model.rule.rules.Rule;
import org.eclipse.smarthome.model.rule.rules.RuleModel;

import com.google.inject.Inject;


/**
 * This scope provider adds all things to the scope which are specific to rules.
 * 
 * @author Kai Kreuzer - Initial contribution and API
 *
 */
@SuppressWarnings("restriction")
public class RulesScopeProvider extends ScriptScopeProvider {

	@Inject
	private TypeReferences typeReferences;
	
	@Override
	protected IScope createLocalVarScope(IScope parentScope,
			LocalVariableScopeContext scopeContext) {
		if(scopeContext.getContext() instanceof Rule) {
			IScope parent = super.createLocalVarScope(parentScope, scopeContext);
			List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
			descriptions.addAll(createVarFeatures(scopeContext.getContext().eResource()));
			descriptions.addAll(createTriggerSpecificVars((Rule) scopeContext.getContext()));
			return MapBasedScope.createScope(parent, descriptions);
		} else {
			return super.createLocalVarScope(parentScope, scopeContext);
		}
	}
	
	private Collection<? extends IEObjectDescription> createVarFeatures(Resource resource) {
		List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();

		if(resource.getContents().size()>0 && resource.getContents().get(0) instanceof RuleModel) {
			RuleModel ruleModel = (RuleModel) resource.getContents().get(0);
			for(XExpression expr : ruleModel.getVariables()) {
				if (expr instanceof XVariableDeclaration) {
					XVariableDeclaration var = (XVariableDeclaration) expr;
					if(var.getName()!=null && var.getType()!=null) {
						descriptions.add(createLocalVarDescription(var));
					}
				}
			}
		}
		
		return descriptions;
	}

	private Collection<? extends IEObjectDescription> createTriggerSpecificVars(Rule rule) {
		List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
		Resource varResource = new XtextResource(URI.createURI("event://specific.vars"));
		if(containsCommandTrigger(rule)) {
			JvmTypeReference commandTypeRef = typeReferences.getTypeForName(Command.class, rule);
			XVariableDeclaration varDecl = XbaseFactory.eINSTANCE.createXVariableDeclaration();
			varDecl.setName(RuleContextHelper.VAR_RECEIVED_COMMAND);
			varDecl.setType(commandTypeRef);
			varDecl.setWriteable(false);
			varResource.getContents().add(varDecl);
			descriptions.add(new LocalVarDescription(QualifiedName.create(varDecl.getName()), varDecl));
		}
		if(containsStateChangeTrigger(rule)) {
			JvmTypeReference stateTypeRef = typeReferences.getTypeForName(State.class, rule);
			XVariableDeclaration varDecl = XbaseFactory.eINSTANCE.createXVariableDeclaration();
			varDecl.setName(RuleContextHelper.VAR_PREVIOUS_STATE);
			varDecl.setType(stateTypeRef);
			varDecl.setWriteable(false);
			varResource.getContents().add(varDecl);
			descriptions.add(new LocalVarDescription(QualifiedName.create(varDecl.getName()), varDecl));
		}
		return descriptions;
	}

	private boolean containsCommandTrigger(Rule rule) {
		for(EventTrigger trigger : rule.getEventtrigger()) {
			if(trigger instanceof CommandEventTrigger) {
				return true;
			}
		}
		return false;
	}

	private boolean containsStateChangeTrigger(Rule rule) {
		for(EventTrigger trigger : rule.getEventtrigger()) {
			if(trigger instanceof ChangedEventTrigger) {
				return true;
			}
		}
		return false;
	}
}
