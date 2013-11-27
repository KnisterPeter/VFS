package de.matrixweb.vfs.scanner;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import de.matrixweb.vfs.VFS;
import de.matrixweb.vfs.wrapped.JavaFile;

/**
 * @author marwol
 */
public class ResourceScannerTest {

  /**
   * 
   */
  @Test
  public void testFileGetResources() {
    final ResourceLister lister = new FileResourceLister(new File(
        "src/test/resources"));

    String[] includes = new String[] { "**/css/**", "private/a.css",
        "external/*/a.css" };
    final String[] excludes = new String[] { "**/css/b.css" };
    Set<String> resources = new ResourceScanner(lister, includes, excludes)
        .getResources();
    Assert.assertThat(resources, Matchers.hasItems("/css/a.css",
        "/private/css/a.css", "/private/a.css", "/external/v2/a.css"));
    Assert.assertThat(resources,
        Matchers.not(Matchers.hasItems("/external/a.css")));
    Assert.assertThat(resources, Matchers.not(Matchers.hasItems("/css/b.css")));

    includes = new String[] { "js/*.js" };
    resources = new ResourceScanner(lister, includes, excludes).getResources();
    Assert.assertThat(resources, Matchers.hasItems("/js/test.js"));
    Assert.assertThat(resources,
        Matchers.not(Matchers.hasItems("/existing/a.js")));
  }

  /**
   * @throws IOException
   */
  @Test
  public void testVFSGetResources() throws IOException {
    final VFS vfs = new VFS();
    try {
      vfs.mount(vfs.find("/"), new JavaFile(new File("src/test/resources")));
      final ResourceLister lister = new VFSResourceLister(vfs);

      String[] includes = new String[] { "**/css/**", "private/a.css",
          "external/*/a.css" };
      final String[] excludes = new String[] { "**/css/b.css" };
      Set<String> resources = new ResourceScanner(lister, includes, excludes)
          .getResources();
      Assert.assertThat(resources, Matchers.hasItems("/css/a.css",
          "/private/css/a.css", "/private/a.css", "/external/v2/a.css"));
      Assert.assertThat(resources,
          Matchers.not(Matchers.hasItems("/external/a.css")));
      Assert.assertThat(resources,
          Matchers.not(Matchers.hasItems("/css/b.css")));

      includes = new String[] { "js/*.js" };
      resources = new ResourceScanner(lister, includes, excludes)
          .getResources();
      Assert.assertThat(resources, Matchers.hasItems("/js/test.js"));
      Assert.assertThat(resources,
          Matchers.not(Matchers.hasItems("/existing/a.js")));
    } finally {
      vfs.dispose();
    }
  }

}
