package de.matrixweb.vfs;

/**
 * @author markusw
 */
public class VFSException extends RuntimeException {

  private static final long serialVersionUID = 3066242511091779036L;

  /**
   * @param message
   */
  public VFSException(final String message) {
    super(message);
  }

  /**
   * @param message
   * @param cause
   */
  public VFSException(final String message, final Throwable cause) {
    super(message, cause);
  }

}
