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
package org.jboss.osgi.jmx;

import org.osgi.jmx.framework.FrameworkMBean;

//$Id$


/**
 * An extension to {@link FrameworkMBean}.
 * 
 * @author thomas.diesler@jboss.com
 * @since 04-Mar-2009
 */
public interface FrameworkMBeanExt extends FrameworkMBean
{
   /** The default object name: jboss.osgi:service=jmx,type=Framework */
   String OBJECTNAME = "jboss.osgi:service=jmx,type=Framework";

   /**
    * Refresh packages through the PackageAdmin service
    * 
    * JMX FrameworkMBean does not allow to resolve/refresh all bundles
    * https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1586
    */
   void refreshAllPackages();

   /**
    * Resolve bundles through the PackageAdmin service
    * 
    * JMX FrameworkMBean does not allow to resolve/refresh all bundles
    * https://www.osgi.org/members/bugzilla/show_bug.cgi?id=1586
    */
   boolean resolveAllBundles();
}