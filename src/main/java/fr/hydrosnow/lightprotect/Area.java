package fr.hydrosnow.lightprotect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;

class Area {
	private static final HashMap<Chunk, Area> chunks = new HashMap<>();
	
	public final Chunk chunk;
	public final String identifier;

	private HashMap<OfflinePlayer, Integer> members;
	
	private boolean changed;
	
	public static void saveAeras() {
		for (final Area area : Area.chunks.values())
			if (area.hasChanged())
				area.save();
	}
	
	public static Area getArea(final Chunk chunk) {
		if (Area.chunks.containsKey(chunk))
			return Area.chunks.get(chunk);
		else {
			final Area area = new Area(chunk);
			new Thread(() -> Area.chunks.put(chunk, area)).start();
			return area;
		}
	}
	
	private Area(final Chunk chunk) {
		this.chunk = chunk;
		identifier = chunk.getWorld().getName() + Resources.DATA_SEPARATOR + chunk.getX() + Resources.DATA_SEPARATOR + chunk.getZ();
		load();
	}

	public void load() {
		final YAML yaml = new YAML(identifier, false);
		members = new HashMap<>();

		if (yaml.isValid())
			try {
				members.put(Bukkit.getOfflinePlayer(UUID.fromString(yaml.config.getString("3"))), 3);

				for (final String uuid : yaml.config.getStringList("2"))
					members.put(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), 2);
				
				for (final String uuid : yaml.config.getStringList("1"))
					members.put(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), 1);
			} catch (final IllegalArgumentException | NullPointerException e) {}

		changed = false;
	}
	
	public void save() {
		final YAML yaml = new YAML(identifier, true);
		final OfflinePlayer owner = get3();
		
		if (yaml.isValid())
			if (owner != null) {
				yaml.config.set("3", owner.getUniqueId().toString());
				yaml.config.set("2", uuidToString(get2()));
				yaml.config.set("1", uuidToString(get1()));
				yaml.save();
			} else {
				yaml.config.set("3", null);
				yaml.config.set("2", null);
				yaml.config.set("1", null);
			}

		changed = false;
	}

	public List<String> uuidToString(final List<OfflinePlayer> input) {
		final Vector<String> output = new Vector<>();
		for (final OfflinePlayer player : input)
			output.add(player.getUniqueId().toString());
		return output;
	}
	
	public boolean hasChanged() {
		return changed;
	}
	
	public OfflinePlayer get3() {
		for (final Map.Entry<OfflinePlayer, Integer> e : members.entrySet())
			if (e.getValue() == 3)
				return e.getKey();

		return null;
	}
	
	public List<OfflinePlayer> get2() {
		final List<OfflinePlayer> players = new Vector<>();

		for (final Map.Entry<OfflinePlayer, Integer> e : members.entrySet())
			if (e.getValue() == 2)
				players.add(e.getKey());

		return players;
	}
	
	public List<OfflinePlayer> get1() {
		final List<OfflinePlayer> players = new Vector<>();

		for (final Map.Entry<OfflinePlayer, Integer> e : members.entrySet())
			if (e.getValue() == 1)
				players.add(e.getKey());

		return players;
	}
	
	public void set3(final OfflinePlayer player) {
		final OfflinePlayer owner = get3();
		changed = true;
		
		if (owner != null)
			members.remove(owner);
		
		if (player != null)
			members.put(player, 3);
		else
			members = new HashMap<>();
	}
	
	public void set2(final OfflinePlayer player) {
		changed = true;
		members.put(player, 2);
	}
	
	public void set1(final OfflinePlayer player) {
		changed = true;
		members.put(player, 1);
	}
	
	public void remove(final OfflinePlayer player) {
		changed = true;
		members.remove(player);
	}
	
	public int permissionLevel(final OfflinePlayer player) {
		return members.getOrDefault(player, 0);
	}
}
