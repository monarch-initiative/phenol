package org.monarchinitiative.phenol.io.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;

import org.monarchinitiative.phenol.base.PhenolRuntimeException;

/**
 * Helper class with static methods for handling resources in tests
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ResourceUtils {

  /**
   * Helper function for reading resources into memory
   *
   * @param path to resource to read.
   */
  public static String readResource(String path) {
    StringWriter writer = new StringWriter();
    try {
      IOUtils.copy(ResourceUtils.class.getResourceAsStream(path), writer, "UTF-8");
    } catch (IOException e) {
      throw new PhenolRuntimeException("Problem reading resource " + path, e);
    }
    return writer.toString();
  }

  /**
   * Copy resource at the given path to the given output {@link File}.
   *
   * @param path Source resource path.
   * @param outFile File with output description.
   */
  public static void copyResourceToFile(String path, File outFile) {
    try (InputStream input = ResourceUtils.class.getResourceAsStream(path);
        OutputStream os = new FileOutputStream(outFile)) {
      byte[] buffer = new byte[1024];
      int length;
      while ((length = input.read(buffer)) > 0) {
        os.write(buffer, 0, length);
      }
    } catch (IOException e) {
      throw new PhenolRuntimeException("Problem with copying resource to file", e);
    }
  }
}
