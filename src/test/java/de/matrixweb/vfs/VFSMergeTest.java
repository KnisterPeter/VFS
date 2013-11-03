package de.matrixweb.vfs;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.matrixweb.vfs.internal.IOHelper;
import de.matrixweb.vfs.wrapped.JavaFile;
import de.matrixweb.vfs.wrapped.MergingVFS;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;

/**
 * @author marwol
 */
public class VFSMergeTest {

  private VFS vfs;

  private File dir1;

  private File dir2;

  /**
   * @throws IOException
   */
  @Before
  public void setUp() throws IOException {
    this.vfs = new VFS();

    this.dir1 = File.createTempFile("vfs-merge", ".dir");
    this.dir2 = File.createTempFile("vfs-merge", ".dir");
    this.dir1.delete();
    this.dir1.mkdirs();
    this.dir2.delete();
    this.dir2.mkdirs();
    IOHelper.write(new File(this.dir1, "file1.txt"), "file1");
    IOHelper.write(new File(this.dir2, "file2.txt"), "file2");
    new File(this.dir1, "folder").mkdir();
    new File(this.dir1, "folder/sub").mkdir();
    new File(this.dir2, "folder").mkdir();

    this.vfs.mount(this.vfs.find("/"), new MergingVFS(new JavaFile(this.dir1),
        new JavaFile(this.dir2)));
  }

  /**
   * @throws IOException
   */
  @After
  public void tearDown() throws IOException {
    IOHelper.deleteDirectory(this.dir1);
    IOHelper.deleteDirectory(this.dir2);

    this.vfs.dispose();
  }

  /**
   * @throws IOException
   */
  @Test
  public void testDirectory() throws IOException {
    assertThat(this.vfs.find("/folder").isDirectory(), is(true));
    assertThat(this.vfs.find("/folder/sub").isDirectory(), is(true));
    assertThat(this.vfs.find("/folder/sub2").isDirectory(), is(false));
  }

  /**
   * @throws IOException
   */
  @Test
  public void testExistance() throws IOException {
    assertThat(this.vfs.find("/file1.txt").exists(), is(true));
    assertThat(this.vfs.find("/file2.txt").exists(), is(true));
  }

  /**
   * @throws IOException
   */
  @Test
  public void testName() throws IOException {
    assertThat(this.vfs.find("/file1.txt").getName(), is("file1.txt"));
    assertThat(this.vfs.find("/folder/sub").getName(), is("sub"));
  }

}
