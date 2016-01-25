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
package org.eclipse.che.api.deploy.security.roles;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.eclipse.che.api.deploy.security.permissions.LocalRolesDescriptionDaoImpl;

import javax.inject.Inject;
import java.util.Collection;

/**
 * @author Sergii Leschenko
 */
public class CheRolePermissionResolver implements RolePermissionResolver {
    private final LocalRolesDescriptionDaoImpl rolesDescriptionDao;

    @Inject
    public CheRolePermissionResolver(LocalRolesDescriptionDaoImpl rolesDescriptionDao) {
        this.rolesDescriptionDao = rolesDescriptionDao;
    }

    @Override
    public Collection<Permission> resolvePermissionsInRole(String role) {
        return FluentIterable.from(rolesDescriptionDao.get(role))
                             .transform(new Function<String, Permission>() {
                                 @Override
                                 public Permission apply(String input) {
                                     return new WildcardPermission(input);
                                 }
                             })
                             .toList();
    }
}
