package fr.hydrosnow.lightprotect;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	private ScheduledFuture<?> boulimook;
	private Protector protector;

	@Override
	public void onEnable() {
		final File root = new File(Resources.DATA_PATH);

		if (!root.exists())
			root.mkdirs();

		protector = new Protector();
		getServer().getPluginManager().registerEvents(protector, this);
		boulimook = Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(() -> Area.saveAeras(), 120, 120, TimeUnit.SECONDS);
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		return CommandHandler.onCommand(sender, cmd, label, args);
	}
	
	@Override
	public void onDisable() {
		boulimook.cancel(false);
		Area.saveAeras();
	}
}