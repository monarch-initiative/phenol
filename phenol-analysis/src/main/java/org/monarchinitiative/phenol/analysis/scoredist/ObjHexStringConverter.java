package org.monarchinitiative.phenol.analysis.scoredist;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.*;

public class ObjHexStringConverter {

  public static String object2hex(Serializable obj) throws IOException {
    ByteArrayOutputStream bytesout = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bytesout);
    oos.writeObject(obj);
    oos.flush();
    String hexString = Hex.encodeHexString(bytesout.toByteArray());
    bytesout.close();
    oos.close();
    return hexString;
  }

  public static Object hex2obj(String hexString) throws DecoderException, IOException, ClassNotFoundException {
    byte[] bytes = Hex.decodeHex(hexString.toCharArray());
    ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
    ObjectInputStream objIn = new ObjectInputStream(bytesIn);
    Object obj = objIn.readObject();
    bytesIn.close();
    objIn.close();
    return obj;
  }

}
