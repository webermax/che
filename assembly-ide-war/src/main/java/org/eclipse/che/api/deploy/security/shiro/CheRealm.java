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
package org.eclipse.che.api.deploy.security.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.eclipse.che.api.deploy.security.roles.CheRolePermissionResolver;
import org.eclipse.che.api.deploy.security.roles.LocalRolesDao;

import javax.inject.Inject;
import java.util.HashSet;

/**
 * @author Sergii Leschenko
 */
public class CheRealm extends AuthorizingRealm {
    private final LocalRolesDao rolesDao;

    @Inject
    public CheRealm(LocalRolesDao rolesDao,
                    CheRolePermissionResolver permissionsResolver) {
        this.rolesDao = rolesDao;
        setRolePermissionResolver(permissionsResolver);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        final String userId = principalCollection.getPrimaryPrincipal().toString();
        return new SimpleAuthorizationInfo(new HashSet<>(rolesDao.get(userId)));
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        throw new UnsupportedOperationException("Realm doesn't support authentication");
    }

    //Disable authentication for this realm
    @Override
    public boolean supports(AuthenticationToken token) {
        return false;
    }
}
