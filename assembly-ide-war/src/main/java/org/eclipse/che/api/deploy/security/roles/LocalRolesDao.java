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
public class LocalRolesDao {
    private final ListMultimap<String, String> userIdToRoles;
    private final ReadWriteLock                lock;
    private final LocalStorage                 localStorage;

    @Inject
    public LocalRolesDao(LocalStorageFactory storageFactory) throws IOException {
        userIdToRoles = ArrayListMultimap.create();
        lock = new ReentrantReadWriteLock();
        localStorage = storageFactory.create("userIdToRoles.json");
    }

    public void add(String userId, String role) throws ConflictException {
        lock.writeLock().lock();
        try {
            userIdToRoles.put(userId, role);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public List<String> get(String userId) {
        lock.readLock().lock();
        try {
            return userIdToRoles.get(userId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @PostConstruct
    void loadRoles() {
        lock.writeLock().lock();
        try {
            final Map<String, List<String>> ownerToPairs = localStorage.loadMap(new TypeToken<Map<String, List<String>>>() {});
            for (Map.Entry<String, List<String>> stringListEntry : ownerToPairs.entrySet()) {
                for (String role : stringListEntry.getValue()) {
                    userIdToRoles.put(stringListEntry.getKey(), role);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @PreDestroy
    void saveRoles() throws IOException {
        lock.readLock().lock();
        try {
            final HashMap<String, List<String>> userIdToRoles = new HashMap<>();
            for (Map.Entry<String, String> entry : this.userIdToRoles.entries()) {
//                userIdToRoles.computeIfAbsent(entry.getKey(), s -> new ArrayList<>());
                if(userIdToRoles.get(entry.getKey()) == null) {
                    userIdToRoles.put(entry.getKey(), new ArrayList<String>());
                }
                userIdToRoles.get(entry.getKey()).add(entry.getValue());
            }
            localStorage.store(userIdToRoles);
        } finally {
            lock.readLock().unlock();
        }
    }
}
