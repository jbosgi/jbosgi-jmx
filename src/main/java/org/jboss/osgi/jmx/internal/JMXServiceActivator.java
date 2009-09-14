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

import static org.jboss.osgi.jmx.Constants.REMOTE_JMX_HOST;
import static org.jboss.osgi.jmx.Constants.REMOTE_JMX_RMI_ADAPTOR;
import static org.jboss.osgi.jmx.Constants.REMOTE_JMX_RMI_PORT;

import java.io.IOException;
import java.net.Socket;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXServiceURL;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.jboss.osgi.common.log.LogServiceTracker;
import org.jboss.osgi.spi.management.ManagedBundleService;
import org.jboss.osgi.spi.management.ManagedFramework;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A BundleActivator for the MBeanServer related services
 * 
 * @author thomas.diesler@jboss.com
 * @since 24-Apr-2009
 */
public class JMXServiceActivator implements BundleActivator
{
   private JMXConnectorService jmxConnector;
   private String jmxHost;
   private String jmxRmiPort;
   private String rmiAdaptorPath;
   private MBeanServer mbeanServer;
   private ManagedFramework managedFramework;
   private ManagedBundleService managedBundleService;

   private LogService log;

   public void start(BundleContext context)
   {
      log = new LogServiceTracker(context);

      MBeanServerLocator locator = new MBeanServerLocator(context);
      mbeanServer = locator.getMBeanServer();

      // Register the MBeanServer 
      context.registerService(MBeanServer.class.getName(), mbeanServer, null);
      log.log(LogService.LOG_DEBUG, "MBeanServer registered");

      // Get the system BundleContext
      BundleContext sysContext = context.getBundle(0).getBundleContext();

      // Register the ManagedBundleService 
      managedBundleService = new ManagedBundleServiceImpl(sysContext, mbeanServer);
      context.registerService(ManagedBundleService.class.getName(), managedBundleService, null);
      log.log(LogService.LOG_DEBUG, "ManagedBundleService registered");

      // Register the ManagedFramework 
      managedFramework = new ManagedFramework(sysContext, mbeanServer);
      managedFramework.start();

      // Register all ManagedBundles 
      for (Bundle bundle : context.getBundles())
         managedBundleService.register(bundle);

      jmxHost = context.getProperty(REMOTE_JMX_HOST);
      if (jmxHost == null)
         jmxHost = "localhost";

      jmxRmiPort = context.getProperty(REMOTE_JMX_RMI_PORT);
      if (jmxRmiPort == null)
         jmxRmiPort = "1098";

      rmiAdaptorPath = context.getProperty(REMOTE_JMX_RMI_ADAPTOR);
      if (rmiAdaptorPath == null)
         rmiAdaptorPath = "jmx/invoker/RMIAdaptor";

      // Start tracking the NamingService
      InitialContextTracker tracker = new InitialContextTracker(context, rmiAdaptorPath);
      tracker.open();
   }

   public void stop(BundleContext context)
   {
      // Unregister all ManagedBundles 
      for (Bundle bundle : context.getBundles())
         managedBundleService.unregister(bundle);

      // Unregister the managed framework
      managedFramework.stop();

      if (jmxConnector != null)
      {
         jmxConnector.stop();
         jmxConnector = null;
      }
   }

   class InitialContextTracker extends ServiceTracker
   {
      private String rmiAdaptorPath;
      private boolean rmiAdaptorBound;

      public InitialContextTracker(BundleContext context, String rmiAdaptorPath)
      {
         super(context, InitialContext.class.getName(), null);
         this.rmiAdaptorPath = rmiAdaptorPath;
      }

      @Override
      public Object addingService(ServiceReference reference)
      {
         InitialContext iniCtx = (InitialContext)super.addingService(reference);
         
         try
         {
            // Assume that the JMXConnector is already running if we can connect to it 
            Socket socket = new Socket(jmxHost, new Integer(jmxRmiPort));
            socket.close();
         }
         catch (IOException ex)
         {
            // Start JMXConnectorService
            jmxConnector = new JMXConnectorService(context, mbeanServer, jmxHost, Integer.parseInt(jmxRmiPort));
            jmxConnector.start();
         }
         
         try
         {
            // Check if the RMIAdaptor is alrady bound
            iniCtx.lookup(rmiAdaptorPath);
         }
         catch (NamingException lookupEx)
         {
            // Bind the RMIAdaptor
            try
            {
               iniCtx.createSubcontext("jmx").createSubcontext("invoker");
               StringRefAddr addr = new StringRefAddr(JMXServiceURL.class.getName(), jmxConnector.getServiceURL().toString());
               Reference ref = new Reference(MBeanServerConnection.class.getName(), addr, RMIAdaptorFactory.class.getName(), null);
               iniCtx.bind(rmiAdaptorPath, ref);
               rmiAdaptorBound = true;

               log.log(LogService.LOG_INFO, "MBeanServerConnection bound to: " + rmiAdaptorPath);
            }
            catch (NamingException ex)
            {
               log.log(LogService.LOG_ERROR, "Cannot bind RMIAdaptor", ex);
            }
         }
         return iniCtx;
      }

      @Override
      public void removedService(ServiceReference reference, Object service)
      {
         InitialContext iniCtx = (InitialContext)service;

         // Stop JMXConnectorService
         if (jmxConnector != null)
         {
            jmxConnector.stop();
            jmxConnector = null;
         }

         // Unbind the RMIAdaptor
         if (rmiAdaptorBound == true)
         {
            try
            {
               iniCtx.unbind(rmiAdaptorPath);
               log.log(LogService.LOG_INFO, "MBeanServerConnection unbound from: " + rmiAdaptorPath);
            }
            catch (NamingException ex)
            {
               log.log(LogService.LOG_ERROR, "Cannot unbind RMIAdaptor", ex);
            }
         }

         super.removedService(reference, service);
      }
   }
}