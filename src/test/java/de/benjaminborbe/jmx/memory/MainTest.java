package de.benjaminborbe.jmx.memory;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

public class MainTest {

  @Test(expected = IOException.class)
  public void testExecException() throws Exception {
    Main.exec("localhost", 1234, "", "");
  }

  @Ignore
  @Test
  public void testExec() throws Exception {
    Main.exec("192.168.99.100", 1099, "", "");
  }
}
