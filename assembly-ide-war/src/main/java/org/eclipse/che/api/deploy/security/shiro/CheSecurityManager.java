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

import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.subject.WebSubjectContext;

import java.util.Collection;

/**
 * @author Sergii Leschenko
 */
public class CheSecurityManager extends DefaultWebSecurityManager {
    public CheSecurityManager() {
        super();
        setCacheManager(new MemoryConstrainedCacheManager());
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public CheSecurityManager(Collection<Realm> realms) {
        this();
        setRealms(realms);
    }

    protected SubjectContext copy(SubjectContext subjectContext) {
        if (subjectContext instanceof WebSubjectContext) {
            return new CheDefaultSubjectContext((WebSubjectContext)subjectContext);
        }
        return super.copy(subjectContext);
    }

    @Override
    protected SubjectContext createSubjectContext() {
        return new CheDefaultSubjectContext();
    }
}