/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.osgi.jmx.internal;

import static org.jboss.logging.Logger.Level.WARN;
import static org.jboss.logging.Logger.Level.ERROR;

import java.util.List;

import javax.management.MBeanServer;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Cause;
import org.jboss.logging.LogMessage;
import org.jboss.logging.Logger;
import org.jboss.logging.Message;
import org.jboss.logging.MessageLogger;

/**
 * Logging Id ranges: 20000-20099
 *
 * https://docs.jboss.org/author/display/JBOSGI/JBossOSGi+Logging
 *
 * @author Thomas.Diesler@jboss.com
 */
@MessageLogger(projectCode = "JBOSGI")
public interface JMXLogger extends BasicLogger {

    JMXLogger LOGGER = Logger.getMessageLogger(JMXLogger.class, "org.jboss.osgi.jmx");

    @LogMessage(level = WARN)
    @Message(id = 20000, value = "Cannot stop JMXConnectorServer")
    void warnCannotStopConnectorServer(@Cause Throwable cause);

    @LogMessage(level = WARN)
    @Message(id = 20001, value = "Multiple MBeanServer instances: %s")
    void warnMultipleMBeanServerInstances(List<MBeanServer> servers);

    @LogMessage(level = ERROR)
    @Message(id = 20002, value = "Cannot start JMXConnectorServer")
    void errorCannotStartConnectorServer(@Cause Throwable cause);
}
