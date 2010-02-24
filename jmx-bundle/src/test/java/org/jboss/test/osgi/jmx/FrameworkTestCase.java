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
package org.jboss.test.osgi.jmx;

//$Id$

import static org.junit.Assert.assertTrue;

import javax.management.ObjectName;

import org.jboss.osgi.jmx.FrameworkMBeanExt;
import org.jboss.osgi.spi.management.ObjectNameFactory;
import org.junit.Test;
import org.osgi.jmx.framework.FrameworkMBean;

/**
 * A test that excercises the FramworkMBean
 * 
 * @author thomas.diesler@jboss.com
 * @since 23-Feb-2010
 */
public class FrameworkTestCase extends AbstractTestCase
{
   @Test
   public void testMBeanAccess() throws Exception
   {
      ObjectName oname = ObjectNameFactory.create(FrameworkMBean.OBJECTNAME);
      assertTrue("Registerd " + oname, getMBeanServer().isRegistered(oname));
      
      oname = ObjectNameFactory.create(FrameworkMBeanExt.OBJECTNAME);
      assertTrue("Registerd " + oname, getMBeanServer().isRegistered(oname));
   }
}