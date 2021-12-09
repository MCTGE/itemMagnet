package ml.codeboy.itemmagnet;

import ml.codeboy.bukkitbootstrap.config.ConfigReader;
import ml.codeboy.bukkitbootstrap.gui.Gui;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public final class ItemMagnet extends JavaPlugin implements Listener {
    private static ItemMagnet instance;
    private final HashMap<Player, BossBar> bossbars = new HashMap<>();
    private final NamespacedKey recipe = new NamespacedKey(this, "magnet");

    public static ItemMagnet getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        ConfigReader.readConfig(Config.class);
        registerRecipe();
        getServer().getPluginManager().registerEvents(this, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers())
                    sendRecipe(player);
            }
        }.runTask(this);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        sendRecipe(event.getPlayer());
    }

    private void sendRecipe(Player player) {
        player.discoverRecipe(recipe);
    }

    private void registerRecipe() {
        ItemStack magnet = Gui.createItem(Material.valueOf(Config.magnetMaterial.toUpperCase()),
                ChatColor.translateAlternateColorCodes('&', Config.name),
                Config.glow, Config.lore.toArray(new String[0]));
        Util.makeMagnet(magnet);
        ShapedRecipe recipe = new ShapedRecipe(this.recipe, magnet);
        recipe.shape("BGB", "DGD", "GGG");
        recipe.setIngredient('B', Material.BEACON);
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        getServer().addRecipe(recipe);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && Util.isMagnet(item)) {
            event.setCancelled(true);
            pull(player, item);
        }
    }

    private void pull(Player player, ItemStack magnet) {
        Location location = player.getLocation();
        int range = Config.range;
        for (Entity entity : location.getWorld().getNearbyEntities(location, range, range, range)) {
            if (entity instanceof Item || entity instanceof ExperienceOrb) {
                Location entityLoc = entity.getLocation();
                if (Util.isAllowed(player, entityLoc)) {
                    double xDif = location.getX() - entityLoc.getX();
                    double yDif = location.getY() - entityLoc.getY();
                    double zDif = location.getZ() - entityLoc.getZ();
                    entity.setVelocity(new Vector(xDif, yDif, zDif).normalize().multiply(Config.pullForce));
                }
            }
        }
        int uses = Util.updateMagnet(magnet);
        if (uses >= Config.usagePerMagnet) {
            if (player.getInventory().getItemInMainHand().equals(magnet))
                player.getInventory().setItemInMainHand(null);
            else if (player.getInventory().getItemInOffHand().equals(magnet))
                player.getInventory().setItemInOffHand(null);
        } else {
            updateBossbar(player, uses);
        }
    }

    private void updateBossbar(Player player, int usage) {
        double usagePercentage = 1 - (double) usage / Config.usagePerMagnet;
        BossBar bar = bossbars.computeIfAbsent(player, p -> {
            BossBar b = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
            b.setProgress(usagePercentage);
            b.addPlayer(player);
            return b;
        });
        bar.setProgress(usagePercentage);
        bar.setTitle(Config.bossbarMessage.replace("{usesLeft}", String.valueOf(Config.usagePerMagnet - usage))
                .replace("{totalUses}", String.valueOf(Config.usagePerMagnet)));
        if (usagePercentage < 0.1)
            bar.setColor(BarColor.RED);
        else if (usagePercentage < 0.5)
            bar.setColor(BarColor.YELLOW);

        double finalUsagePercentage = bar.getProgress();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (bar.getProgress() == finalUsagePercentage) {
                    bar.removeAll();
                    bossbars.remove(player);
                }
            }
        }.runTaskLater(this, 20 * 2);
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
