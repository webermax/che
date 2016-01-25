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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.SecurityUtils;

/**
 * @author Sergii Leschenko
 */
public class WorkspaceSecurityInterceptor implements MethodInterceptor {

    //Template of permission that contains %s instead of instance id, e.g. 'workspace:read:%s'
    private final String permissionTemplate;

    public WorkspaceSecurityInterceptor(String permissionTemplate) {
        this.permissionTemplate = permissionTemplate;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final String workspaceId = (String)invocation.getArguments()[0];

        final String permission = String.format(permissionTemplate, workspaceId);
        SecurityUtils.getSubject().checkPermission(permission);
        return invocation.proceed();
    }
}
