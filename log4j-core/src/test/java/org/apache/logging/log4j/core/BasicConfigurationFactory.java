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
package org.apache.logging.log4j.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.BaseConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.net.URI;

/**
 *
 */
public class BasicConfigurationFactory extends ConfigurationFactory {

    @Override
    public Configuration getConfiguration(final String name, final URI configLocation) {
        return new BasicConfiguration();
    }

    @Override
    public String[] getSupportedTypes() {
        return null;
    }

    @Override
    public Configuration getConfiguration(final ConfigurationSource source) {
        return null;
    }

    public class BasicConfiguration extends BaseConfiguration {

        private static final String DEFAULT_LEVEL = "org.apache.logging.log4j.level";

        public BasicConfiguration() {

            final LoggerConfig root = getRootLogger();
            final String l = System.getProperty(DEFAULT_LEVEL);
            final Level level = (l != null && Level.getLevel(l) != null) ? Level.getLevel(l) : Level.ERROR;
            root.setLevel(level);
        }
    }
}
