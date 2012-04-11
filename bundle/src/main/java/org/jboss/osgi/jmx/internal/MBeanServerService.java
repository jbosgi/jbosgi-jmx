/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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



import static org.jboss.osgi.jmx.internal.JMXLogger.LOGGER;

import java.util.ArrayList;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A service that registers an MBeanServer
 * 
 * @author thomas.diesler@jboss.com
 * @since 24-Apr-2009
 */
public class MBeanServerService
{
   private BundleContext context;

   public MBeanServerService(BundleContext context)
   {
      this.context = context;
   }

   public MBeanServer registerMBeanServer()
   {
      // Check if there is an MBeanServer service already
      ServiceReference sref = context.getServiceReference(MBeanServer.class.getName());
      if (sref != null)
      {
         MBeanServer mbeanServer = (MBeanServer)context.getService(sref);
         LOGGER.debugf("Found MBeanServer fom service: %s", mbeanServer.getDefaultDomain());
         return mbeanServer;
      }

      // Get or create th MBeanServer
      MBeanServer mbeanServer = getMBeanServer();

      // Register the MBeanServer 
      context.registerService(MBeanServer.class.getName(), mbeanServer, null);
      LOGGER.debugf("MBeanServer registered");

      return mbeanServer;
   }

   private MBeanServer getMBeanServer()
   {
      MBeanServer mbeanServer = null;
      
      ArrayList<MBeanServer> serverArr = MBeanServerFactory.findMBeanServer(null);
      if (serverArr.size() > 1)
         LOGGER.warnMultipleMBeanServerInstances(serverArr);
   
      if (serverArr.size() > 0)
      {
         mbeanServer = serverArr.get(0);
         LOGGER.debugf("Found MBeanServer: %s", mbeanServer);
      }
   
      if (mbeanServer == null)
      {
         LOGGER.debugf("No MBeanServer, create one ...");
         mbeanServer = MBeanServerFactory.createMBeanServer();
         LOGGER.debugf("Created MBeanServer: %s", mbeanServer);
      }
      
      return mbeanServer;
   }
}