/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.jmx;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusData;
import org.apache.logging.log4j.status.StatusListener;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Implementation of the {@code StatusLoggerAdminMBean} interface.
 */
public class StatusLoggerAdmin extends NotificationBroadcasterSupport implements
        StatusListener, StatusLoggerAdminMBean {

    private final AtomicLong sequenceNo = new AtomicLong();
    private final ObjectName objectName;

    /**
     * Constructs a new {@code StatusLoggerAdmin} with the {@code Executor} to
     * be used for sending {@code Notification}s asynchronously to listeners.
     * 
     * @param executor
     */
    public StatusLoggerAdmin(Executor executor) {
        super(executor, createNotificationInfo());
        try {
            objectName = new ObjectName(NAME);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        StatusLogger.getLogger().registerListener(this);
    }

    private static MBeanNotificationInfo createNotificationInfo() {
        String[] notifTypes = new String[] { NOTIF_TYPE_DATA,
                NOTIF_TYPE_MESSAGE };
        String name = Notification.class.getName();
        String description = "StatusLogger has logged an event";
        return new MBeanNotificationInfo(notifTypes, name, description);
    }

    @Override
    public String[] getStatusDataHistory() {
        List<StatusData> data = getStatusData();
        String[] result = new String[data.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = data.get(i).getFormattedStatus();
        }
        return result;
    }

    @Override
    public List<StatusData> getStatusData() {
        return StatusLogger.getLogger().getStatusData();
    }

    @Override
    public String getLevel() {
        return StatusLogger.getLogger().getLevel().name();
    }

    @Override
    public void setLevel(String level) {
        StatusLogger.getLogger().setLevel(Level.valueOf(level));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.logging.log4j.status.StatusListener#log(org.apache.logging
     * .log4j.status.StatusData)
     */
    @Override
    public void log(StatusData data) {
        Notification notifMsg = new Notification(NOTIF_TYPE_MESSAGE,
                getObjectName(), nextSeqNo(), now(), data.getFormattedStatus());
        sendNotification(notifMsg);

        Notification notifData = new Notification(NOTIF_TYPE_DATA,
                getObjectName(), nextSeqNo(), now());
        notifData.setUserData(data);
        sendNotification(notifData);
    }

    /** @see StatusLoggerAdminMBean#NAME */
    public ObjectName getObjectName() {
        return objectName;
    }

    private long nextSeqNo() {
        return sequenceNo.getAndIncrement();
    }

    private long now() {
        return System.currentTimeMillis();
    }
}