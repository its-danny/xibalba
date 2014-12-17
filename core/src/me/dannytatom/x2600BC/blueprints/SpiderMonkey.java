package me.dannytatom.x2600BC.blueprints;

import java.util.HashMap;

public final class SpiderMonkey {
  public static final String name = "Spider Monkey";

  public static final HashMap<String, String> visual = new HashMap<String, String>() {{
    put("spritePath", "sprites/spiderMonkey.png");
  }};

  public static final HashMap<String, Integer> attributes = new HashMap<String, Integer>() {{
    put("speed", 100);
    put("health", 50);
    put("damage", 10);
  }};
}
