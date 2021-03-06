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

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

import javax.management.MBeanServer;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.management.remote.rmi.RMIJRMPServerImpl;

/**
 * A RMI/JRMP connector service.
 * 
 * @author thomas.diesler@jboss.com
 * @since 24-Apr-2009
 */
public class JMXConnectorService
{
   private JMXServiceURL serviceURL;
   private RMIConnectorServer connectorServer;
   private RMIJRMPServerImpl rmiServer;
   private boolean shutdownRegistry;
   private Registry rmiRegistry;
   private String rmiHost;
   private int rmiPort;

   public JMXConnectorService(JMXServiceURL serviceURL, int rmiPort) throws IOException
   {
      this.serviceURL = serviceURL;
      this.rmiHost = serviceURL.getHost();
      this.rmiPort = rmiPort;
      
      // Check to see if registry already created
      rmiRegistry = LocateRegistry.getRegistry(rmiHost, rmiPort);
      try
      {
         rmiRegistry.list();
         LOGGER.debugf("RMI registry running at host=%s,port=%d", rmiHost, rmiPort);
      }
      catch (Exception ex)
      {
         LOGGER.debugf("No RMI registry running at host=%s,port=%d. Will create one.", rmiHost, rmiPort);
         rmiRegistry = LocateRegistry.createRegistry(rmiPort, null, new DefaultSocketFactory(InetAddress.getByName(rmiHost)));
         shutdownRegistry = true;
      }
   }

   public void start(MBeanServer mbeanServer) throws IOException
   {
      boolean jmxConnectorAvailable = false;
      try
      {
         rmiRegistry.lookup("jmxrmi");
         jmxConnectorAvailable = true;
      }
      catch (NotBoundException ex)
      {
         // ignore
      }
      
      if (jmxConnectorAvailable == false)
      {
         // create new connector server and start it
         RMIServerSocketFactory serverSocketFactory = new DefaultSocketFactory(InetAddress.getByName(rmiHost));
         rmiServer = new RMIJRMPServerImpl(rmiPort, null, serverSocketFactory, null);
         connectorServer = new RMIConnectorServer(serviceURL, null, rmiServer, mbeanServer);
         LOGGER.debugf("JMXConnectorServer created: %s", serviceURL);

         connectorServer.start();
         rmiRegistry.rebind("jmxrmi", rmiServer.toStub());
         LOGGER.debugf("JMXConnectorServer started: %s", serviceURL);
      }
   }

   public void stop()
   {
      try
      {
         if (connectorServer != null)
         {
            connectorServer.stop();
            rmiRegistry.unbind("jmxrmi");
         }

         // Shutdown the registry if this service created it
         if (shutdownRegistry == true)
         {
            LOGGER.debugf("Shutdown RMI registry");
            UnicastRemoteObject.unexportObject(rmiRegistry, true);
         }

         LOGGER.debugf("JMXConnectorServer stopped");
      }
      catch (Exception ex)
      {
         LOGGER.warnCannotStopConnectorServer(ex);
      }
   }
}