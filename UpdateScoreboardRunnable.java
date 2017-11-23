package org.masteriaboard;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateScoreboardRunnable extends BukkitRunnable {
	private final Main main;

	public UpdateScoreboardRunnable(Main main) {
		this.main = main;
	}

	@Override
	public void run() {
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();
		for (Player p : players) {
			main.setScoreBoardForPlayer(p);
			
		}
	}

}
