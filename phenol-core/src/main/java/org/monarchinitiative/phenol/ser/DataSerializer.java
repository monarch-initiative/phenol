package org.monarchinitiative.phenol.ser;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager for serializing and deserializing objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class DataSerializer {

  /** Logger object to use */
  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  /** Path to file to serialize to or deserialize from */
  private final String filename;

  /** Magic bytes */
  private final byte[] magicBytes;

  /** Minimal supported version of Jannovar in this version */
  private final String minVersion;

  /** @return Version string loaded from {@code /project.properties}. */
  public static String getVersion() {
    final Properties properties = new Properties();
    try {
      properties.load(DataSerializer.class.getResourceAsStream("/project.properties"));
    } catch (IOException e) {
      throw new SerializationRuntimeException(
          "Could not load project.properties for obtaining version", e);
    }
    return properties.getProperty("version");
  }

  /**
   * Initialize the (de)serializer with the path to the file to load/save.
   *
   * @param filename path to the file to deserialize from or serialize to
   * @param magicBytes magic bytes to expect at beginning of file
   * @param minVersion String with minimal
   */
  public DataSerializer(byte[] magicBytes, String minVersion, String filename) {
    this.magicBytes = magicBytes;
    this.minVersion = minVersion;
    this.filename = filename;
  }

  /**
   * Serialize a data object to a file.
   *
   * @param data the object to serialize
   * @throws SerializationException on problems with the serialization
   */
  public void save(Object data) throws SerializationException {
    LOGGER.info("Attempting serialization to {}", new Object[] {filename});
    final long startTime = System.nanoTime();

    try (FileOutputStream fos = new FileOutputStream(filename)) {
      // write magic bytes at top of file (before compression)
      fos.write(magicBytes);
      fos.flush();
      try (GZIPOutputStream gzos = new GZIPOutputStream(fos);
          ObjectOutputStream oos = new ObjectOutputStream(gzos)) {
        // Write version
        final String version = getVersion();
        oos.writeObject(version);
        // Write actual data
        oos.writeObject(data);
        // Make sure everything is written out.
        oos.flush();
        gzos.flush();
        fos.flush();
      }
    } catch (Exception e) {
      throw new SerializationException("Could not serialize data file.", e);
    }

    LOGGER.info(
        "Serialization took {} sec.",
        new Object[] {(System.nanoTime() - startTime) / 1_000_000_000.0});
  }

  /**
   * Deserialize a data object from a file.
   *
   * @return object yielded by deserialization
   * @throws SerializationException on problems with the deserialization
   */
  public Object load() throws SerializationException {
    LOGGER.info("Attempting deserialization from {}", new Object[] {filename});
    final long startTime = System.nanoTime();
    final Object result;

    try (FileInputStream fileIn = new FileInputStream(filename)) {
      // Check magic bytes at top of file
      final byte[] word = new byte[magicBytes.length];
      fileIn.read(word);
      if (!Arrays.equals(word, magicBytes)) {
        throw new SerializationException(
            filename + " does not look like a data file, magic number incorrect!");
      }
      try (GZIPInputStream gzIn = new GZIPInputStream(fileIn);
          ObjectInputStream in = new ObjectInputStream(gzIn)) {
        final String dbVersion = (String) in.readObject();
        final VersionComparator comp = new VersionComparator();
        if (comp.compare(dbVersion, minVersion) < 0) {
          throw new SerializationException(
              filename + " was created by " + dbVersion + " but we need at least " + minVersion);
        }
        result = in.readObject();
      }
    } catch (Exception e) {
      throw new SerializationException("Could not deserialize data from file", e);
    }

    LOGGER.info(
        "Done with deserialization, took {} sec.",
        new Object[] {(System.nanoTime() - startTime) / 1_000_000_000.0});
    return result;
  }
}
