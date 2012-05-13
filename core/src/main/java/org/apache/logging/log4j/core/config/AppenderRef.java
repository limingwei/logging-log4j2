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
package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttr;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * An Appender reference.
 */
@Plugin(name = "appender-ref", type = "Core", printObject = true)
public class AppenderRef {
    private static final Logger LOGGER = StatusLogger.getLogger();

    private final String ref;
    private final Level level;
    private final Filter filter;

    private AppenderRef(String ref, Level level, Filter filter) {
        this.ref = ref;
        this.level = level;
        this.filter = filter;
    }

    public String getRef() {
        return ref;
    }

    public Level getLevel() {
        return level;
    }

    public Filter getFilter() {
        return filter;
    }

    /**
     * Create an Appender reference.
     * @param ref The name of the Appender.
     * @return The name of the Appender.
     */
    @PluginFactory
    public static AppenderRef createAppenderRef(@PluginAttr("ref") String ref,
                                                @PluginAttr("level") String level,
                                                @PluginElement("filters") Filter filter) {

        if (ref == null) {
            LOGGER.error("Appender references must contain a reference");
            return null;
        }
        Level l = null;

        if (level != null) {
            l = Level.toLevel(level.toUpperCase(), null);
            if (l == null) {
                LOGGER.error("Invalid level " + level + " on Appender reference " + ref);
            }
        }

        return new AppenderRef(ref, l, filter);
    }
}