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

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.helpers.FileUtils;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * Initializes and configure the Logging system.
 */
public final class Configurator {

    protected static final StatusLogger LOGGER = StatusLogger.getLogger();

    private Configurator() {
    }


    /**
     * Initializes the Logging Context.
     * @param name The Context name.
     * @param loader The ClassLoader for the Context (or null).
     * @param configLocation The configuration for the logging context.
     * @return The LoggerContext.
     */
    public static LoggerContext initialize(final String name, final ClassLoader loader, final String configLocation) {
        return initialize(name, loader, configLocation, null);

    }

    /**
     * Initializes the Logging Context.
     * @param name The Context name.
     * @param loader The ClassLoader for the Context (or null).
     * @param configLocation The configuration for the logging context.
     * @param externalContext The external context to be attached to the LoggerContext
     * @return The LoggerContext.
     */
    public static LoggerContext initialize(final String name, final ClassLoader loader, final String configLocation,
                                           final Object externalContext) {

        try {
            final URI uri = configLocation == null ? null : FileUtils.getCorrectedFilePathUri(configLocation);
            return initialize(name, loader, uri, externalContext);
        } catch (final URISyntaxException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Initializes the Logging Context.
     * @param name The Context name.
     * @param configLocation The configuration for the logging context.
     * @return The LoggerContext.
     */
    public static LoggerContext initialize(final String name, final String configLocation) {
        return initialize(name, null, configLocation);
    }

    /**
     * Initializes the Logging Context.
     * @param name The Context name.
     * @param loader The ClassLoader for the Context (or null).
     * @param configLocation The configuration for the logging context.
     * @return The LoggerContext.
     */
    public static LoggerContext initialize(final String name, final ClassLoader loader, final URI configLocation) {
        return initialize(name, loader, configLocation, null);
    }

    /**
     * Initializes the Logging Context.
     * @param name The Context name.
     * @param loader The ClassLoader for the Context (or null).
     * @param configLocation The configuration for the logging context.
     * @param externalContext The external context to be attached to the LoggerContext
     * @return The LoggerContext.
     */
    public static LoggerContext initialize(final String name, final ClassLoader loader, final URI configLocation,
                                           final Object externalContext) {

        try {
            final org.apache.logging.log4j.spi.LoggerContext context = LogManager.getContext(loader, false,
                externalContext, configLocation, name);
            if (context instanceof LoggerContext) {
                return (LoggerContext) context;
            } else {
                LOGGER.error("LogManager returned an instance of {} which does not implement {}. Unable to initialize Log4j",
                    context.getClass().getName(), LoggerContext.class.getName());
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Initializes the Logging Context.
     * @param loader The ClassLoader for the Context (or null).
     * @param source The InputSource for the configuration.
     * @return The LoggerContext.
     */
    public static LoggerContext initialize(final ClassLoader loader,
                                           final ConfigurationFactory.ConfigurationSource source) {
        return initialize(loader, source, null);
    }

    /**
     * Initializes the Logging Context.
     * @param loader The ClassLoader for the Context (or null).
     * @param source The InputSource for the configuration.
     * @param externalContext The external context to be attached to the LoggerContext.
     * @return The LoggerContext.
     */

    public static LoggerContext initialize(final ClassLoader loader,
                                           final ConfigurationFactory.ConfigurationSource source,
                                           final Object externalContext)
    {

        try {
            URI configLocation = null;
            try {
                configLocation = source.getLocation() == null ?
                        null : FileUtils.getCorrectedFilePathUri(source.getLocation());
            } catch (final Exception ex) {
                // Invalid source location.
            }
            final LoggerContextFactory f = LogManager.getFactory();
            if (f instanceof Log4jContextFactory) {
                Log4jContextFactory factory = (Log4jContextFactory) f;
                final org.apache.logging.log4j.spi.LoggerContext context = factory.getContext(Configurator.class.getName(),
                    loader, externalContext, false, source);
                if (context instanceof LoggerContext) {
                    return (LoggerContext) context;
                } else {
                    LOGGER.error("LogManager returned an instance of {} which does not implement {}. Unable to initialize Log4j",
                        context.getClass().getName(), LoggerContext.class.getName());
                }
            } else {
                LOGGER.error("LogManager is not using a Log4j Context Factory. Unable to initialize Log4j");
                return null;
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Shuts down the given logging context.
     * @param ctx the logging context to shut down, may be null.
     */
    public static void shutdown(final LoggerContext ctx) {
        if (ctx != null) {
            ctx.stop();
        }
    }
}
