package de.matrixweb.vfs.internal;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import de.matrixweb.vfs.VFSException;

/**
 * A bunch of io helper functions.
 * 
 * @author markusw
 */
public final class IOHelper {

  private IOHelper() {
  }

  /**
   * @param closable
   */
  public static void close(final Closeable closable) {
    try {
      closable.close();
    } catch (final IOException e) {
      // Ignore this
    }
  }

  /**
   * @param cs
   * @param out
   * @param encoding
   * @throws IOException
   */
  public static void write(final CharSequence cs, final OutputStream out,
      final String encoding) throws IOException {
    final Writer writer = new OutputStreamWriter(out, encoding);
    writer.write(cs.toString());
    writer.flush();
  }

  /**
   * @param file
   * @param str
   * @throws IOException
   */
  public static void write(final File file, final String str)
      throws IOException {
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
    final OutputStream out = new FileOutputStream(file);
    try {
      write(str, out, "UTF-8");
    } finally {
      out.close();
    }
  }

  /**
   * @param file
   * @return Returns the file content as {@link String}
   * @throws IOException
   */
  public static String readToString(final File file) throws IOException {
    final InputStream in = new FileInputStream(file);
    try {
      return toString(in, "UTF-8");
    } finally {
      in.close();
    }
  }

  /**
   * @param reader
   * @param writer
   * @throws IOException
   */
  public static void copy(final Reader reader, final Writer writer)
      throws IOException {
    final BufferedReader from = new BufferedReader(reader);
    String line = from.readLine();
    while (line != null) {
      writer.write(line);
      line = from.readLine();
    }
  }

  /**
   * @param in
   * @param encoding
   * @return Returns the content of the {@link InputStream} as {@link String}
   * @throws IOException
   */
  public static String toString(final InputStream in, final String encoding)
      throws IOException {
    final StringBuilder sb = new StringBuilder();

    final BufferedReader reader = new BufferedReader(new InputStreamReader(in,
        encoding));
    String line = reader.readLine();
    while (line != null) {
      sb.append(line);
      line = reader.readLine();
    }

    return sb.toString();
  }

  /**
   * @param file
   */
  public static void deleteDirectory(final File file) {
    if (!file.isDirectory()) {
      throw new VFSException("File is no directory");
    }
    for (final File child : file.listFiles()) {
      if (child.isDirectory()) {
        deleteDirectory(child);
      } else {
        child.delete();
      }
    }
    file.delete();
  }

}
