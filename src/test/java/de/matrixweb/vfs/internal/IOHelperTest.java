package de.matrixweb.vfs.internal;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;

/**
 * @author markusw
 */
public class IOHelperTest {

  /**
   * @throws IOException
   */
  @Test
  public void testNewlines() throws IOException {
    final File temp = File.createTempFile("io-helper", ".txt");
    try {
      IOHelper.write(temp, "\n\n");
      assertThat(IOHelper.readToString(temp), is("\n\n"));
    } finally {
      temp.delete();
    }
  }

}
