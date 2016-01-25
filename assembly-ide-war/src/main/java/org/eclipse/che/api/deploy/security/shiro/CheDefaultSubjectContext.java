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

import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;
import org.eclipse.che.commons.user.User;

/**
 * @author Sergii Leschenko
 */
public class CheDefaultSubjectContext extends DefaultWebSubjectContext {
    public CheDefaultSubjectContext() {
    }

    public CheDefaultSubjectContext(WebSubjectContext ctx) {
        super(ctx);
    }

    @Override
    public PrincipalCollection resolvePrincipals() {
        Session session = resolveSession();
        if (session != null) {
            User user = (User)session.getAttribute("codenvy_user");
            return new SimplePrincipalCollection(user.getId(), "codenvy");
        }

        return null;
    }
}