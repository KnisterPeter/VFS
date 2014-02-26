package de.matrixweb.vfs;

/**
 * @author markusw
 */
public interface Logger {

  /**
   * @param message
   */
  void debug(String message);

  /**
   * @param messsage
   */
  void info(String messsage);

  /**
   * @param message
   * @param e
   */
  void error(String message, Exception e);

}
