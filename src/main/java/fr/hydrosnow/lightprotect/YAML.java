package fr.hydrosnow.lightprotect;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

class YAML {
	public final File configFile;
	public FileConfiguration config;

	public YAML(final String name, final boolean create) {
		configFile = new File(Resources.DATA_PATH + name + ".yml");
		
		if (!configFile.exists() && create)
			try {
				configFile.createNewFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}

		if (configFile.exists())
			config = YamlConfiguration.loadConfiguration(configFile);
	}

	public boolean isValid() {
		return configFile.exists() && configFile.canRead() && configFile.canWrite();
	}
	
	public void save() {
		try {
			config.save(configFile);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
