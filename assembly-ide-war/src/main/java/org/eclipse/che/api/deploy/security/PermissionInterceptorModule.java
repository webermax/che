/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.api.deploy.security;

import com.google.inject.AbstractModule;

import org.eclipse.che.api.workspace.server.WorkspaceService;
import org.eclipse.che.inject.DynaModule;

import static com.google.inject.matcher.Matchers.subclassesOf;
import static org.eclipse.che.inject.Matchers.names;

/**
 * @author Sergii Leschenko
 */
@DynaModule
public class PermissionInterceptorModule extends AbstractModule {

    @Override
    protected void configure() {
        final WorkspaceSecurityInterceptor workspaceSecurityInterceptor = new WorkspaceSecurityInterceptor("workspace:read:%s");
        bindInterceptor(subclassesOf(WorkspaceService.class), names("getRuntimeWorkspaceById"), workspaceSecurityInterceptor);
    }
}
