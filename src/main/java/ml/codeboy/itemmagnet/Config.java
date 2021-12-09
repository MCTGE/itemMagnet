package ml.codeboy.itemmagnet;

import ml.codeboy.bukkitbootstrap.config.Configurable;

import java.util.Arrays;
import java.util.List;

@Configurable(name = "config.yml", comments = "I hope this is self explanatory :)\n otherwise just let me know and I'll help you")
public class Config {
    public static String magnetMaterial = "STICK";
    public static String name = "Magnet";
    public static List<String> lore = Arrays.asList("It's a magnet", "it pulls stuff");
    public static boolean glow = true;

    public static int range = 10;
    public static int usagePerMagnet = 100;
    public static double pullForce = 1;
    public static String bossbarMessage = "{usesLeft}/{totalUses}";
}
