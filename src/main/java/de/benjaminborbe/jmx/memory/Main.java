package de.benjaminborbe.jmx.memory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Main {

  public static void main(final String[] args) {
    try {
      if (args.length == 2) {
        exec(args[0], Integer.parseInt(args[1]), "", "");
      } else if (args.length == 4) {
        exec(args[0], Integer.parseInt(args[1]), args[2], args[3]);
      } else {
        System.err.println("usage: host port [user] [password]");
        System.exit(1);
        return;
      }
    } catch (final Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void exec(final String host, final int port, final String user, final String password) throws IOException,
      MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException,
      IntrospectionException {
    final JMXConnector jmxConnector = getJmxConnector(host, port, user, password);
    jmxConnector.connect();
    {
      final CompositeData attribute = (CompositeData) jmxConnector.getMBeanServerConnection().getAttribute(
          new ObjectName("java.lang:type=Memory"),
          "HeapMemoryUsage");
      System.out.println("HeapMemoryUsage.commited    = " + toMb(attribute.get("committed")));
      System.out.println("HeapMemoryUsage.used        = " + toMb(attribute.get("used")));
      System.out.println("HeapMemoryUsage.max         = " + toMb(attribute.get("max")));
    }
    {
      final CompositeData attribute = (CompositeData) jmxConnector.getMBeanServerConnection().getAttribute(
          new ObjectName("java.lang:type=Memory"),
          "NonHeapMemoryUsage");
      System.out.println("NonHeapMemoryUsage.commited = " + toMb(attribute.get("committed")));
      System.out.println("NonHeapMemoryUsage.used     = " + toMb(attribute.get("used")));
      System.out.println("NonHeapMemoryUsage.max      = " + toMb(attribute.get("max")));
    }
  }

  public static String toMb(final Object object) {
    final String string = String.valueOf(object);
    try {
      final long value = Long.parseLong(string);
      if (value <= 0) {
        return string;
      }
      return (value / 1024 / 1024) + "MB";
    } catch (final Exception e) {
      return string;
    }
  }

  private static JMXConnector getJmxConnector(final String host, final int port, final String user, final String password)
      throws IOException {
    final HashMap map = new HashMap();
    final String[] credentials = new String[2];
    credentials[0] = user;
    credentials[1] = password;
    map.put("jmx.remote.credentials", credentials);
    return JMXConnectorFactory.newJMXConnector(createConnectionURL(host, port), map);
  }

  private static JMXServiceURL createConnectionURL(final String host, final int port) throws MalformedURLException {
    return new JMXServiceURL("rmi", "", 0, "/jndi/rmi://" + host + ":" + port + "/jmxrmi");
  }

}
