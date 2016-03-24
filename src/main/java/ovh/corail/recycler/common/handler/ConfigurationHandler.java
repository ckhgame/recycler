package ovh.corail.recycler.common.handler;

import net.minecraftforge.common.config.Configuration;
import ovh.corail.recycler.common.MainUtil;

public class ConfigurationHandler {
	public static Configuration config;

	public static boolean recyclerRecycled, craftPodzol, craftClay, craftWeb, craftChainmail;

	public static void refreshConfig() {
		recyclerRecycled=config.getBoolean("recyclerRecycled", config.CATEGORY_GENERAL, false, MainUtil.getTranslation("config.recyclerRecycled"));
		craftPodzol=config.getBoolean("craftPodzol", config.CATEGORY_GENERAL, true, MainUtil.getTranslation("config.craftPodzol"));
		craftClay=config.getBoolean("craftClay", config.CATEGORY_GENERAL, true, MainUtil.getTranslation("config.craftClay"));
		craftWeb=config.getBoolean("craftWeb", config.CATEGORY_GENERAL, true, MainUtil.getTranslation("config.craftWeb"));
		craftChainmail=config.getBoolean("craftChainmail", config.CATEGORY_GENERAL, true, MainUtil.getTranslation("config.craftChainmail"));
		if (config.hasChanged()) {
			config.save();
		}
	}
}
