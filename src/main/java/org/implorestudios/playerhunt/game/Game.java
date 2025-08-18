package org.implorestudios.playerhunt.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.implorestudios.playerhunt.PlayerHuntPlugin;
import org.implorestudios.playerhunt.language.Replacer;
import org.implorestudios.playerhunt.language.StringReplacer;

import java.util.Collection;

@Getter
public class Game {
    private final PlayerHuntPlugin plugin = PlayerHuntPlugin.getPlugin(PlayerHuntPlugin.class);
    private final YamlConfiguration mainConfig =  plugin.getConfigurationManager().get("config.yml");
    public enum GameState {
        NOT_STARTED,
        COUNTDOWN,
        IN_PROGRESS,
    }
    private static Game game;
    private final PlayerManager playerManager = PlayerManager.getInstance();
    @Setter
    private GameState state = GameState.NOT_STARTED;
    public static synchronized Game getInstance() {
        if(game == null) game = new Game();
        return game;
    }
    public void init() {
        Countdown countdown = new Countdown(this);
        countdown.start(getMainConfig().getInt("countdown"));
    }
    public void start() {
        game.broadcast(false,
                "gameStarted",
                new StringReplacer("%hunters%", String.join(", ", getPlayerManager().getHunters().stream().map(Player::getName).toList())),
                new StringReplacer("%runners%", String.join(", ", getPlayerManager().getRunners().stream().map(Player::getName).toList()))
        );
        for (Player player : getPlayerManager().getPlayers()) {
            plugin.getLanguageManager().sendTitle(player, "titles.gameStarted", new StringReplacer("%team%", getPlayerManager().isHunter(player) ? "Hunter" : "Runner"));
        }
        game.setState(GameState.IN_PROGRESS);
        if(getMainConfig().getBoolean("compass.enabled")) {
            for(Player hunter : getPlayerManager().getHunters()) {
                this.giveCompass(hunter);
            }
        }
        getPlayerManager().setStartingPlayers();
    }

    public void broadcast(String message) {
        for (Player player : getPlayerManager().getPlayers()) {
            plugin.getLanguageManager().sendMessageWithPrefix(player, message);
        }
    }
    public void broadcast(boolean prefix, String message, Replacer... replacer) {
        for (Player player : getPlayerManager().getPlayers()) {
            if(prefix) {
                plugin.getLanguageManager().sendMessageWithPrefix(player, message, replacer);
            } else {
                plugin.getLanguageManager().sendMessage(player, message, replacer);
            }
        }
    }
    public void broadcast(String message, Replacer... replacer) {
        for (Player player : getPlayerManager().getPlayers()) {
            plugin.getLanguageManager().sendMessageWithPrefix(player, message, replacer);
        }
    }
    public void broadcast(String message, boolean title, Replacer... replacer) {
        for (Player player : getPlayerManager().getPlayers()) {
            if(title) {
                plugin.getLanguageManager().sendTitle(player, message, replacer);
            } else {
                plugin.getLanguageManager().sendMessageWithPrefix(player, message, replacer);
            }
        }
    }
    public static boolean isRunning() {
        return game != null && game.getState().equals(GameState.IN_PROGRESS);
    }
    public void end(String winningTeam, Boolean abrupt) {
        if(getState() != GameState.IN_PROGRESS) return;
        if(Boolean.TRUE.equals(abrupt)) {
            game.broadcast("commands.stop.gameStopped");
            game.broadcast("titles.gameEndedAbrupt", true);
        } else {
            game.broadcast("gameEnd.message", new StringReplacer("%team%", winningTeam));
            game.broadcast("titles.gameEnded", true, new StringReplacer("%team%", winningTeam));
        }
        for (Player player : getPlayerManager().getPlayers()) {
            player.getInventory().remove(Items.getCompass());
        }
        setState(GameState.NOT_STARTED);
        getPlayerManager().resetPlayers();
        game = null;
    }
    public void giveCompass(Player player) {
        if(!getPlayerManager().isHunter(player) || player.getInventory().contains(Items.getCompass())) return;
        player.getInventory().addItem(Items.getCompass());
        this.setCompassTarget(player);
    }
    private int calculateDistance(Location from, Location to) {
        from.setY(0);
        to.setY(0);
        return (int) from.distance(to);
    }
    public Location setCompassTarget(Player player) {
        World world = player.getWorld();
        Player nearestPlayer = null;
        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
        int nearestDistance = Integer.MAX_VALUE;
        for (Player otherPlayer : players) {
            if(otherPlayer == player || !getPlayerManager().isRunner(otherPlayer) || otherPlayer.getWorld() != world) continue;
            int distance = calculateDistance(otherPlayer.getLocation(), player.getLocation());
            if(distance < nearestDistance) {
                nearestDistance = distance;
                nearestPlayer = otherPlayer;
            }
        }
        if(nearestPlayer == null) {
            plugin.getLanguageManager().sendModifiableMessageWithPrefix(player, "compass.noRunners");
            return null;
        }
        player.setCompassTarget(nearestPlayer.getLocation());
        return nearestPlayer.getLocation();
    }
}
