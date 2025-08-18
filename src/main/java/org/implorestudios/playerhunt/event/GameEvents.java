package org.implorestudios.playerhunt.event;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.implorestudios.playerhunt.PlayerHuntPlugin;
import org.implorestudios.playerhunt.game.Cooldown;
import org.implorestudios.playerhunt.game.Game;
import org.implorestudios.playerhunt.game.Items;
import org.implorestudios.playerhunt.game.PlayerManager;
import org.implorestudios.playerhunt.language.IntegerReplacer;
import org.implorestudios.playerhunt.language.StringReplacer;

import java.util.HashMap;

public class GameEvents implements Listener {
    private final PlayerHuntPlugin plugin = PlayerHuntPlugin.getPlugin(PlayerHuntPlugin.class);
    @Getter
    private final YamlConfiguration mainConfig  =  plugin.getConfigurationManager().get("config.yml");

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        if(!Game.isRunning()) return;
        Player player = event.getEntity();
        Player killer = player.getKiller();
        Game game = Game.getInstance();
        if(!game.getPlayerManager().isPlayer(player)) return;
        if(game.getPlayerManager().isHunter(player)) {
            event.getDrops().remove(Items.getCompass());
            if(plugin.getMainConfig().getBoolean("deathMessages.enableHunter")) {
                if(killer != null) {
                    event.deathMessage(PlayerHuntPlugin.mm(player, "deathMessages.hunterKilled",
                            new StringReplacer("%hunter%", player.getName()),
                            new StringReplacer("%runner%", killer.getName())));
                } else {
                    event.deathMessage(PlayerHuntPlugin.mm(player, "deathMessages.hunterDied",
                            new StringReplacer("%hunter%", player.getName())));
                }
            }
            return;
        }
        game.getPlayerManager().removePlayer(player);
        if(plugin.getMainConfig().getBoolean("deathMessages.enableRunner")) {
            if(killer != null) {
                event.deathMessage(PlayerHuntPlugin.mm(player, "deathMessages.runnerKilled",
                        new StringReplacer("%runner%", player.getName()),
                        new IntegerReplacer("%runners%", game.getPlayerManager().getRunners().size()),
                        new StringReplacer("%hunter%", killer.getName())));
            } else {
                event.deathMessage(PlayerHuntPlugin.mm(player, "deathMessages.runnerDied",
                        new StringReplacer("%runner%", player.getName()),
                        new IntegerReplacer("%runners%", game.getPlayerManager().getRunners().size())));
            }
        }
    }
    @EventHandler
    public void onDragonDeath(EntityDeathEvent event) {
        if(!event.getEntityType().equals(EntityType.ENDER_DRAGON) || !Game.isRunning()) return;
        Game game = Game.getInstance();
        game.end("runners", false);
    }

    private final HashMap<Player, Cooldown> cooldowns = new HashMap<>();
    @EventHandler
    public void onCompassInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.getInventory().getItemInMainHand().isSimilar(Items.getCompass())) return;
        if(!Game.isRunning()) {
            player.getInventory().remove(Items.getCompass());
            return;
        }
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        int cooldownTime = plugin.getMainConfig().getInt("compass.cooldown");
        if(cooldowns.containsKey(player)) {
            Cooldown cooldown = cooldowns.get(player);
            plugin.getLanguageManager().sendModifiableMessageWithPrefix(player, "compass.cooldownMessage", new IntegerReplacer("%time%", cooldown.getTime()));
            return;
        }
        Game game = Game.getInstance();
        Location location = game.setCompassTarget(player);
        if(location == null) return;
        Cooldown cooldown = new Cooldown();
        cooldown.start(plugin, cooldownTime, () -> cooldowns.remove(player));
        cooldowns.put(player, cooldown);
        plugin.getLanguageManager().sendModifiableMessageWithPrefix(player, "compass.message", new IntegerReplacer("%x%", location.getBlockX()), new IntegerReplacer("z", location.getBlockZ()));
    }
    @EventHandler
    public void onCompassDrop(PlayerDropItemEvent event) {
        Item itemDrop = event.getItemDrop();
        if(!Game.isRunning() || !itemDrop.getItemStack().isSimilar(Items.getCompass())) return;
        event.setCancelled(true);
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if(!Game.isRunning()) return;
        Game game = Game.getInstance();
        game.giveCompass(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        Player player = event.getPlayer();
        if(!playerManager.isPlayer(player)) return;
        playerManager.removeOfflinePlayers();
    }
}
