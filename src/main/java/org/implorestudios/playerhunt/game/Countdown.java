package org.implorestudios.playerhunt.game;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.implorestudios.playerhunt.PlayerHuntPlugin;
import org.implorestudios.playerhunt.language.IntegerReplacer;

@Getter
public class Countdown extends BukkitRunnable {
    private final PlayerHuntPlugin plugin = PlayerHuntPlugin.getPlugin(PlayerHuntPlugin.class);
    private int time;
    private final Game game;
    public Countdown(Game game) {
        this.game = game;
        this.time = 0;
    }
    public void start(int time) {
        game.setState(Game.GameState.COUNTDOWN);
        this.time = time;
        this.runTaskTimer(plugin, 0L, 20L);
    }
    public void run() {
        if (time == 0) {
            cancel();
            game.start();
            return;
        }
        if (time % 15 == 0 || time <= 10) {
            game.broadcast("countdown.message", new IntegerReplacer("%time%", time));
            if(plugin.getMainConfig().getBoolean("title")) game.broadcast("titles.countdown", true, new IntegerReplacer("%time%", time));
        }
        if (game.getPlayerManager().getPlayers().size() < 2) {
            cancel();
            game.setState(Game.GameState.NOT_STARTED);
            game.broadcast("commands.start.notEnoughPlayers");
            return;
        }
        time--;
    }
}
