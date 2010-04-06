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

import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import javax.management.openmbean.CompositeData;

import org.jboss.osgi.jmx.FrameworkMBeanExt;
import org.jboss.osgi.jmx.ObjectNameFactory;
import org.osgi.framework.BundleContext;
import org.osgi.jmx.framework.FrameworkMBean;

/**
 * An extension to {@link FrameworkMBean}.
 * 
 * @author thomas.diesler@jboss.com
 * @since 23-Feb-2009
 */
public class FrameworkStateExt extends AbstractState implements FrameworkMBeanExt
{
   
   public FrameworkStateExt(BundleContext context, MBeanServer mbeanServer)
   {
      super(context, mbeanServer);
   }

   @Override
   ObjectName getObjectName()
   {
      return ObjectNameFactory.create(OBJECTNAME);
   }

   @Override
   StandardMBean getStandardMBean() throws NotCompliantMBeanException
   {
      return new StandardMBean(this, FrameworkMBeanExt.class);
   }

   @Override
   public void refreshBundles(long[] bundleIdentifiers) throws IOException
   {
      getFrameworkMBean().refreshBundles(bundleIdentifiers);
   }

   @Override
   public void refreshBundle(long bundleIdentifier) throws IOException
   {
      getFrameworkMBean().refreshBundle(bundleIdentifier);
   }

   @Override
   public boolean resolveBundles(long[] bundleIdentifiers) throws IOException
   {
      return getFrameworkMBean().resolveBundles(bundleIdentifiers);
   }

   @Override
   public boolean resolveBundle(long arg0) throws IOException
   {
      return getFrameworkMBean().resolveBundle(arg0);
   }

   @Override
   public int getFrameworkStartLevel() throws IOException
   {
      return getFrameworkMBean().getFrameworkStartLevel();
   }

   @Override
   public int getInitialBundleStartLevel() throws IOException
   {
      return getFrameworkMBean().getInitialBundleStartLevel();
   }

   @Override
   public long installBundleFromURL(String arg0, String arg1) throws IOException
   {
      return getFrameworkMBean().installBundleFromURL(arg0, arg1);
   }

   @Override
   public long installBundle(String arg0) throws IOException
   {
      return getFrameworkMBean().installBundle(arg0);
   }

   @Override
   public CompositeData installBundlesFromURL(String[] arg0, String[] arg1) throws IOException
   {
      return getFrameworkMBean().installBundlesFromURL(arg0, arg1);
   }

   @Override
   public CompositeData installBundles(String[] arg0) throws IOException
   {
      return getFrameworkMBean().installBundles(arg0);
   }

   @Override
   public void restartFramework() throws IOException
   {
      getFrameworkMBean().restartFramework();
   }

   @Override
   public void setBundleStartLevel(long arg0, int arg1) throws IOException
   {
      getFrameworkMBean().setBundleStartLevel(arg0, arg1);
   }

   @Override
   public CompositeData setBundleStartLevels(long[] arg0, int[] arg1) throws IOException
   {
      return getFrameworkMBean().setBundleStartLevels(arg0, arg1);
   }

   @Override
   public void setFrameworkStartLevel(int arg0) throws IOException
   {
      getFrameworkMBean().setFrameworkStartLevel(arg0);
   }

   @Override
   public void setInitialBundleStartLevel(int arg0) throws IOException
   {
      getFrameworkMBean().setInitialBundleStartLevel(arg0);
   }

   @Override
   public void shutdownFramework() throws IOException
   {
      getFrameworkMBean().shutdownFramework();
   }

   @Override
   public void startBundle(long arg0) throws IOException
   {
      getFrameworkMBean().startBundle(arg0);
   }

   @Override
   public CompositeData startBundles(long[] arg0) throws IOException
   {
      return getFrameworkMBean().startBundles(arg0);
   }

   @Override
   public void stopBundle(long arg0) throws IOException
   {
      getFrameworkMBean().stopBundle(arg0);
   }

   @Override
   public CompositeData stopBundles(long[] arg0) throws IOException
   {
      return getFrameworkMBean().stopBundles(arg0);
   }

   @Override
   public void uninstallBundle(long arg0) throws IOException
   {
      getFrameworkMBean().uninstallBundle(arg0);
   }

   @Override
   public CompositeData uninstallBundles(long[] arg0) throws IOException
   {
      return getFrameworkMBean().uninstallBundles(arg0);
   }

   @Override
   public void updateBundleFromURL(long arg0, String arg1) throws IOException
   {
      getFrameworkMBean().updateBundleFromURL(arg0, arg1);
   }

   @Override
   public void updateBundle(long arg0) throws IOException
   {
      getFrameworkMBean().updateBundle(arg0);
   }

   @Override
   public CompositeData updateBundlesFromURL(long[] arg0, String[] arg1) throws IOException
   {
      return getFrameworkMBean().updateBundlesFromURL(arg0, arg1);
   }

   @Override
   public CompositeData updateBundles(long[] arg0) throws IOException
   {
      return getFrameworkMBean().updateBundles(arg0);
   }

   @Override
   public void updateFramework() throws IOException
   {
      getFrameworkMBean().updateFramework();
   }
}