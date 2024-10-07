package dev.slne.surf.friends.velocity;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalVelocityProvider {
  private static final File friendJson = new File("plugins/surf-friends-velocity/friends.json");
  private static final Logger logger = LoggerFactory.getLogger(ExternalVelocityProvider.class);

  public static File friendJson(){
    if(!friendJson.exists()){
      try {
        friendJson.getParentFile().mkdirs();
        friendJson.createNewFile();
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
    }

    return friendJson;
  }
}
