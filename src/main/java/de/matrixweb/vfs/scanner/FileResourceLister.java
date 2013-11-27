package de.matrixweb.vfs.scanner;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author markusw
 */
public class FileResourceLister implements ResourceLister {

  private final File base;

  /**
   * @param base
   */
  public FileResourceLister(final File base) {
    super();
    this.base = base;
  }

  /**
   * @see de.matrixweb.vfs.scanner.ResourceLister#list(java.lang.String)
   */
  @Override
  public Set<String> list(String path) {
    if (!path.endsWith("/")) {
      path = path + '/';
    }
    final Set<String> list = new HashSet<String>();
    for (final File file : new File(this.base, path).listFiles()) {
      if (file.isDirectory()) {
        list.add(path + file.getName() + '/');
      } else {
        list.add(path + file.getName());
      }
    }
    return list;
  }

}
