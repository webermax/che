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

import com.google.inject.binder.AnnotatedBindingBuilder;

import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.config.ConfigurationException;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;
import org.eclipse.che.commons.user.User;

import javax.servlet.ServletContext;
import java.util.Collection;

/**
 * @author Sergii Leschenko
 */
public class CheShiroWebModule extends ShiroWebModule {
    public CheShiroWebModule(ServletContext servletContext) {
        super(servletContext);
    }

    @Override
    protected void configureShiroWeb() {
        bindRealm().to(CheRealm.class);
    }

    protected void bindWebSecurityManager(AnnotatedBindingBuilder<? super WebSecurityManager> bind) {
        try {
            bind.toConstructor(CheSecurityManager.class.getConstructor(Collection.class)).asEagerSingleton();
        } catch (NoSuchMethodException e) {
            throw new ConfigurationException(
                    "This really shouldn't happen.  Either something has changed in Shiro, or there's a bug in ShiroModule.", e);
        }
    }
}
