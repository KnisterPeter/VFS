package de.matrixweb.vfs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.matrixweb.vfs.internal.IOHelper;
import de.matrixweb.vfs.internal.Root;
import de.matrixweb.vfs.internal.VFSManager;
import de.matrixweb.vfs.internal.VFileImpl;
import de.matrixweb.vfs.wrapped.WrappedSystem;
import de.matrixweb.vfs.wrapped.WrappedVFS;

/**
 * This implements a file system abstraction which is able to mount native
 * {@link File}s as read-only parts into the {@link VFS}. The native files are
 * never overwritten. To create a native filesystem from the virtual use
 * {@link #exportFS(File)}, to import a native filesystem use
 * {@link #importFS(File)}.
 * 
 * @author markusw
 */
public class VFS {

  private Root root = new Root(this);

  private final String host;

  private final Logger logger;

  /**
   * 
   */
  public VFS() {
    this(null);
  }

  /**
   * @param logger
   */
  public VFS(final Logger logger) {
    this.host = VFSManager.register(this);
    this.logger = logger;
  }

  /**
   * 
   */
  public void dispose() {
    VFSManager.unregister(this.host);
  }

  /**
   * @param target
   *          The {@link VFile} to mount the native directory into
   * @param directory
   *          The resource directory to mount
   * @return Returns the {@link VFile} for the native directory
   */
  public VFile mount(final VFile target, final WrappedSystem directory) {
    if (!directory.exists()) {
      throw new VFSException("One of " + directory.getName() + " does not exists.");
    }
    if (!directory.isDirectory()) {
      throw new VFSException("Only directories cound be mounted in vfs; " + directory.getName() + " is not a directory");
    }
    if (this.logger != null) {
      this.logger.info("Mounting " + directory + " into " + target.getPath());
    }
    ((VFileImpl) target).mount(directory);
    return target;
  }

  /**
   * Stacks a new {@link VFS} root on-top of the current one. The result is a
   * new virtual file-system backed by the old one. The old one is read-only
   * afterwards.<br>
   * <b>Note: All current references to {@link VFile}s must be considered
   * outdated!</b>
   * 
   * @return Returns the root file of the old vfs status which could be used to
   *         rollback
   */
  public VFile stack() {
    final VFile oldroot = this.root;
    final WrappedSystem wrapped = new WrappedVFS(this.root);
    this.root = new Root(this);
    mount(this.root, wrapped);
    return oldroot;
  }

  /**
   * @param oldroot
   */
  public void rollback(final VFile oldroot) {
    if (!(oldroot instanceof Root)) {
      throw new VFSException("'" + oldroot + "' is not vfs root");
    }
    this.root = (Root) oldroot;
  }

  /**
   * Returns a {@link VFile} for the given path. If there is no file at that
   * path, then a new one is returned.
   * 
   * @param path
   *          The path to the file
   * @return Returns the requested {@link VFile}.
   * @throws IOException
   */
  public VFile find(final String path) throws IOException {
    if (!path.startsWith("/")) {
      throw new IOException("VFS path find should start with '/'");
    }
    if ("/".equals(path)) {
      return this.root;
    }
    return this.root.find(path.substring(1));
  }

  /**
   * @param target
   * @throws IOException
   */
  public void exportFS(final File target) throws IOException {
    internalExportFS(target, this.root);
  }

  private void internalExportFS(final File target, final VFile file) throws IOException {
    if (file.isDirectory()) {
      for (final VFile dir : file.getChildren()) {
        internalExportFS(target, dir);
      }
    } else if (file.exists()) {
      final File targetFile = new File(target, file.getPath().substring(1));
      targetFile.getParentFile().mkdirs();
      final FileOutputStream out = new FileOutputStream(targetFile);
      try {
        if (logger != null && file != null) {
          logger.info("VFS: Writing file " + file.getURL());
        }
        IOHelper.write(targetFile, VFSUtils.readToString(file));
      } finally {
        IOHelper.close(out);
      }
    }
  }

  /**
   * @param source
   * @throws IOException
   */
  public void importFS(final File source) throws IOException {
    internalImportFS(source, source);
  }

  private void internalImportFS(final File source, final File file) throws IOException {
    if (file.isDirectory()) {
      for (final File dir : file.listFiles()) {
        internalImportFS(source, dir);
      }
    } else {
      final VFile targetFile = find(file.getAbsolutePath().replace('\\', '/')
          .substring(source.getAbsolutePath().replace('\\', '/').length()));
      VFSUtils.write(targetFile, IOHelper.readToString(file));
    }
  }

  /**
   * @param file
   * @return Returns a {@link VFile} url
   */
  public URL createUrl(final VFile file) {
    String url = "vfs://" + this.host + file.getPath();
    try {
      return new URL(url);
    } catch (final MalformedURLException e) {
      throw new VFSException("Failed to create valid URL: " + url, e);
    }
  }

  /**
   * @return the logger
   */
  public Logger getLogger() {
    return this.logger;
  }

}
