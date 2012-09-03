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
package org.apache.logging.log4j.core.javaee;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Saves the LoggerContext into the ServletContext as an attribute.
 */
public class Log4jContextListener implements ServletContextListener {

    /**
     * The name of the attribute to use to store the LoggerContext into the ServletContext.
     */
    public static final String LOG4J_CONTEXT_ATTRIBUTE = "Log4JContext";

    public static final String LOG4J_CONFIG = "log4jConfiguration";

    public static final String LOG4J_CONTEXT_NAME = "log4jContextName";

    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        String locn = context.getInitParameter(LOG4J_CONFIG);
        String name = context.getInitParameter(LOG4J_CONTEXT_NAME);
        if (name == null) {
            name = context.getServletContextName();
        }
        if (name == null && locn == null) {
            context.log("No Log4j context configuration provided");
            return;
        }
        context.setAttribute(LOG4J_CONTEXT_ATTRIBUTE, Configurator.intitalize(name, locn));
    }

    public void contextDestroyed(ServletContextEvent event) {
        event.getServletContext().removeAttribute(LOG4J_CONTEXT_ATTRIBUTE);
        Configurator.shutdown();
    }
}