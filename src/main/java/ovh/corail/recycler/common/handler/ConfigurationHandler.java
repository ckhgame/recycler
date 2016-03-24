package ovh.corail.recycler.common.handler;

import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {
	public static Configuration config;

	public static boolean recyclerRecycled;

	public static void refreshConfig() {
		recyclerRecycled=config.getBoolean("recyclerRecycled", config.CATEGORY_GENERAL, false, "Recycler le recycleur ");
		if (config.hasChanged()) {
			config.save();
		}
	}
}
