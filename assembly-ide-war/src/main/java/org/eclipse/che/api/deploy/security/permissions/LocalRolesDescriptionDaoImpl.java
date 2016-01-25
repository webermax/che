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
package org.eclipse.che.api.deploy.security.permissions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.reflect.TypeToken;

import org.eclipse.che.api.core.ConflictException;
import org.eclipse.che.api.local.storage.LocalStorage;
import org.eclipse.che.api.local.storage.LocalStorageFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Sergii Leschenko
 */
@Singleton
public class LocalRolesDescriptionDaoImpl {
    private final ListMultimap<String, String> rolesToPermissions;
    private final ReadWriteLock                lock;
    private final LocalStorage                 localStorage;

    @Inject
    public LocalRolesDescriptionDaoImpl(LocalStorageFactory storageFactory) throws IOException {
        rolesToPermissions = ArrayListMultimap.create();
        lock = new ReentrantReadWriteLock();
        localStorage = storageFactory.create("rolesDescription.json");
    }

    public void add(String role, String permission) throws ConflictException {
        lock.writeLock().lock();
        try {
            rolesToPermissions.put(role, permission);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<String> get(String role) {
        lock.readLock().lock();
        try {
            return rolesToPermissions.get(role);
        } finally {
            lock.readLock().unlock();
        }
    }

    @PostConstruct
    void loadRolesDescriptions() {
        lock.writeLock().lock();
        try {
            final Map<String, List<String>> ownerToPairs = localStorage.loadMap(new TypeToken<Map<String, List<String>>>() {});
            for (Map.Entry<String, List<String>> stringListEntry : ownerToPairs.entrySet()) {
                for (String permission : stringListEntry.getValue()) {
                    rolesToPermissions.put(stringListEntry.getKey(), permission);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @PreDestroy
    void saveRolesDescriptions() throws IOException {
        lock.readLock().lock();
        try {
            final HashMap<String, List<String>> roleToPermissions = new HashMap<>();
            for (Map.Entry<String, String> entry : rolesToPermissions.entries()) {
//                roleToPermissions.computeIfAbsent(entry.getKey(), s -> new ArrayList<>());
                if (roleToPermissions.get(entry.getKey()) == null) {
                    roleToPermissions.put(entry.getKey(), new ArrayList<String>());
                }
                roleToPermissions.get(entry.getKey()).add(entry.getValue());
            }
            localStorage.store(roleToPermissions);
        } finally {
            lock.readLock().unlock();
        }
    }
}
