package dev.slne.surf.friends.paper.mysqlbased.api.util;

import dev.slne.surf.friends.paper.PaperInstance;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ByteUtil {
  public static byte[] toBytes(UUID uuid) {
    long mostSigBits = uuid.getMostSignificantBits();
    long leastSigBits = uuid.getLeastSignificantBits();
    byte[] bytes = new byte[16];
    for (int i = 0; i < 8; i++) {
      bytes[i] = (byte) (mostSigBits >>> 8 * (7 - i));
      bytes[i + 8] = (byte) (leastSigBits >>> 8 * (7 - i));
    }
    return bytes;
  }

  public static UUID fromBytes(byte[] bytes) {
    long mostSigBits = 0;
    long leastSigBits = 0;
    for (int i = 0; i < 8; i++) {
      mostSigBits <<= 8;
      mostSigBits |= (bytes[i] & 0xff);
      leastSigBits <<= 8;
      leastSigBits |= (bytes[i + 8] & 0xff);
    }
    return new UUID(mostSigBits, leastSigBits);
  }

  public static byte[] fromList(List<UUID> uuids) {
    try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream)) {

      objStream.writeInt(uuids.size());
      for (UUID uuid : uuids) {
        objStream.writeLong(uuid.getMostSignificantBits());
        objStream.writeLong(uuid.getLeastSignificantBits());
      }
      return byteStream.toByteArray();
    } catch (IOException e) {
      PaperInstance.instance().logger().error(e.getMessage());

      return new byte[0];
    }
  }

  public static List<UUID> toList(byte[] data) {
    List<UUID> list = new ArrayList<>();

    try (ByteArrayInputStream byteStream = new ByteArrayInputStream(data); ObjectInputStream objStream = new ObjectInputStream(byteStream)) {
      int size = objStream.readInt();

      for (int i = 0; i < size; i++) {
        long mostSigBits = objStream.readLong();
        long leastSigBits = objStream.readLong();

        list.add(new UUID(mostSigBits, leastSigBits));
      }

    } catch (IOException e) {
      PaperInstance.instance().logger().error(e.getMessage());
    }

    return list;
  }

}
