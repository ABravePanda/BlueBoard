package org.masteriaboard;

import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;


public class Main extends JavaPlugin implements Listener {
	private UpdateScoreboardRunnable updateScoreboardRunnable;

	private Player p;


	public void onEnable() {
		updateScoreboardRunnable = new UpdateScoreboardRunnable(this);
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this);
		// add version tag
		Bukkit.getLogger().info(ChatColor.GREEN + "Made by Lertu");
		Bukkit.getLogger().info(ChatColor.GRAY + "Version:" + ChatColor.GREEN + getConfig().getString("Version"));
		Bukkit.getPluginManager().registerEvents(this, this);
		if (!new File(getDataFolder() + File.separator + "config.yml").exists()) {
			getConfig().options().copyDefaults(true);
			saveConfig();
		}

		this.scheduleScoreBoardUpdate();
	}

	private void scheduleScoreBoardUpdate() {

		this.updateScoreboardRunnable.runTaskTimer(this, 0L, getConfig().getInt("refresh") * 20L);
	}
	// replace int with refresh
	public void onDisable() {
		Bukkit.getLogger().info(ChatColor.RED + "BlueBoard Disabled Successfully");
		Bukkit.getLogger().info(ChatColor.GREEN + "Made by Lertu");
		Bukkit.getLogger().info(ChatColor.GRAY + "Version:" + ChatColor.GREEN + getConfig().getInt("Version"));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length == 0){
				if(p.hasPermission("BlueBoard.help")){
					if (cmd.getName().equalsIgnoreCase("BlueBoard")) {
						p.sendMessage(getColor(getConfig().getString("prefix")) + ChatColor.YELLOW + "/BlueBoard help");
					}  else if (!p.hasPermission("BlueBoard.reload")) {
						p.sendMessage(ChatColor.DARK_RED + "You do not have permission to perform this command!");
					}
				}
				return true;
			}
			if (p.hasPermission("BlueBoard.reload")) {
				if (cmd.getName().equalsIgnoreCase("BlueBoard")) {
					if (args[0].equalsIgnoreCase("reload")) {
						if (p.hasPermission("BlueBoard.reload")) {
							this.reloadConfig();
							this.saveConfig();
							this.getConfig();
							p.sendMessage(getColor(getConfig().getString("prefix")) + ChatColor.GREEN + "Config reloaded!");
							return true;
						} else if (!p.hasPermission("BlueBoard.reload")) {
							p.sendMessage(
									ChatColor.DARK_RED + "You do not have permission to perform this command!");
							return true;
						}
					}
				}
				if (args[0].equalsIgnoreCase("help")) {
					if (p.hasPermission("BlueBoard.help")) {
						p.sendMessage(ChatColor.DARK_BLUE + "Blue" + ChatColor.BLUE + "Board");
						p.sendMessage(ChatColor.GRAY + "/BlueBoard");
						p.sendMessage(ChatColor.GRAY + "/BlueBoard help - Displays this.");
						p.sendMessage(ChatColor.GRAY + "/BlueBoard reload - Reloads plugin.");

						return true;
					} else if (!p.hasPermission("BlueBoard.help")) {
						p.sendMessage(
								ChatColor.DARK_RED + "You do not have permission to perform this command!");
						return true;
					}
				}
			}


        }return false;}

    public String getVar(String msg) {
	    Player p = this.p;
		int max = Bukkit.getServer().getMaxPlayers();
		int online = Bukkit.getServer().getOnlinePlayers().size();
		msg = msg.replace("{playername}", p.getName());
		msg = msg.replace("{displayname}", p.getDisplayName());
		msg = msg.replace("{online}", String.valueOf(online));
		msg = msg.replace("{maxplayers}", String.valueOf(max));
		msg = msg.replace("{hp}", String.valueOf(Math.round(p.getHealth())));
		msg = msg.replace("{exp}", String.valueOf(Math.round(p.getTotalExperience())));
		msg = msg.replace("{hunger}", String.valueOf(Math.round(p.getFoodLevel())));
		msg = msg.replace("{ip}", String.valueOf(p.getAddress()));
		msg = msg.replace("{x-pos}", String.valueOf(Math.round(p.getLocation().getBlockX())));
		msg = msg.replace("{y-pos}", String.valueOf(Math.round(p.getLocation().getBlockY())));
		msg = msg.replace("{z-pos}", String.valueOf(Math.round(p.getLocation().getBlockZ())));

		if (getConfig().getBoolean("PlaceholderAPI")) {
            PlaceholderAPI.getExternalPlaceholderPlugins();
			PlaceholderAPI.setPlaceholders(p, msg);
		}

		return msg;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void join(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		setScoreBoardForPlayer(p);

	}
    @EventHandler(priority = EventPriority.HIGHEST)
    public void setScoreBoardForPlayer(Player p) {

		FileConfiguration config = this.getConfig();
		Scoreboard scoreBoard = this.getScoreBoard(p, config);

		p.setScoreboard(scoreBoard);
	}

    public Scoreboard getScoreBoard(Player p, FileConfiguration config) {
		this.p = p;
		ScoreboardManager m = Bukkit.getScoreboardManager();
		Scoreboard b = m.getNewScoreboard();
		Objective o = b.registerNewObjective("BlueBoard", "dummy");

		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(getColor(config.getString("Title")));

		List<String> stringList = getConfig().getStringList("Lines");
        IntStream.range(0, stringList.size()).forEach(i -> {

        Score score = o.getScore(getColor(getVar(stringList.get(i))));
        score.setScore(stringList.size() - i);
        });

		return b;
		}

    public String getColor(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
		}
}
