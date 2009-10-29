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

//$Id$

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.jboss.net.sockets.DefaultSocketFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Service Activator that registers an MBeanServer
 * 
 * @author thomas.diesler@jboss.com
 * @since 24-Apr-2009
 */
public class JMXConnectorService
{
   // Provide logging
   private Logger log = LoggerFactory.getLogger(JMXConnectorService.class);
   
   private JMXServiceURL serviceURL;
   private JMXConnectorServer jmxConnectorServer;
   private boolean shutdownRegistry;
   private Registry rmiRegistry;

   public JMXConnectorService(BundleContext context, MBeanServer mbeanServer, String host, int rmiPort) throws IOException
   {
      // check to see if registry already created
      rmiRegistry = LocateRegistry.getRegistry(host, rmiPort);
      try
      {
         rmiRegistry.list();
         log.debug("RMI registry running at host=" + host + ",port=" + rmiPort);
      }
      catch (RemoteException e)
      {
         log.debug("No RMI registry running at host=" + host + ",port=" + rmiPort + ".  Will create one.");
         rmiRegistry = LocateRegistry.createRegistry(rmiPort, null, new DefaultSocketFactory(InetAddress.getByName(host)));
         shutdownRegistry = true;
      }

      // create new connector server and start it
      serviceURL = getServiceURL(host, rmiPort);
      jmxConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(serviceURL, null, mbeanServer);

      log.debug("JMXConnectorServer created: " + serviceURL);
   }

   static JMXServiceURL getServiceURL(String host, int rmiPort)
   {
      String jmxConnectorURL = "service:jmx:rmi://" + host + "/jndi/rmi://" + host + ":" + rmiPort + "/jmxconnector";
      try
      {
         return new JMXServiceURL(jmxConnectorURL);
      }
      catch (MalformedURLException e)
      {
         throw new IllegalArgumentException("Invalid connector URL: " + jmxConnectorURL);
      }
   }

   public void start()
   {
      ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

         jmxConnectorServer.start();

         log.debug("JMXConnectorServer started: " + serviceURL);
      }
      catch (IOException ex)
      {
         log.error("Cannot start JMXConnectorServer", ex);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(ctxLoader);
      }
   }

   public void stop()
   {
      ClassLoader ctxLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

         jmxConnectorServer.stop();

         // Shutdown the registry if this service created it
         if (shutdownRegistry == true)
         {
            log.debug("Shutdown RMI registry");
            UnicastRemoteObject.unexportObject(rmiRegistry, true);
         }

         log.debug("JMXConnectorServer stopped");
      }
      catch (IOException ex)
      {
         log.warn("Cannot stop JMXConnectorServer", ex);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(ctxLoader);
      }
   }
}