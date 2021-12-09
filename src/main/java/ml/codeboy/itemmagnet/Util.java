package ml.codeboy.itemmagnet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Util {
    private static final NamespacedKey magnetKey = new NamespacedKey(ItemMagnet.getInstance(), "magnet");

    public static void makeMagnet(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(magnetKey, PersistentDataType.INTEGER, 0);
        item.setItemMeta(meta);
    }

    public static boolean isMagnet(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return false;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(magnetKey, PersistentDataType.INTEGER);
    }

    public static int updateMagnet(ItemStack magnet) {
        ItemMeta meta = magnet.getItemMeta();
        if (meta == null)
            return 0;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        int uses = container.get(magnetKey, PersistentDataType.INTEGER) + 1;
        container.set(magnetKey, PersistentDataType.INTEGER, uses);
        magnet.setItemMeta(meta);
        return uses;
    }

    public static boolean isAllowed(Player player, Location location) {
        if (Bukkit.getPluginManager().isPluginEnabled("GriefPrevention"))
            return GriefUtil.isAllowed(player, location);
        return true;
    }
}
