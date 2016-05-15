package ovh.corail.recycler.handler;

import net.minecraftforge.common.config.Configuration;
import ovh.corail.recycler.core.Helper;

public class ConfigurationHandler {
	public static Configuration config;

	public static boolean recyclerRecycled, craftPodzol, craftClay, craftWeb, craftChainmail, unbalancedRecipes;

	public static void refreshConfig() {
		recyclerRecycled=config.getBoolean("recyclerRecycled", config.CATEGORY_GENERAL, false, Helper.getTranslation("config.recyclerRecycled"));
		craftPodzol=config.getBoolean("craftPodzol", config.CATEGORY_GENERAL, true, Helper.getTranslation("config.craftPodzol"));
		craftClay=config.getBoolean("craftClay", config.CATEGORY_GENERAL, true, Helper.getTranslation("config.craftClay"));
		craftWeb=config.getBoolean("craftWeb", config.CATEGORY_GENERAL, true, Helper.getTranslation("config.craftWeb"));
		craftChainmail=config.getBoolean("craftChainmail", config.CATEGORY_GENERAL, true, Helper.getTranslation("config.craftChainmail"));
		unbalancedRecipes=config.getBoolean("unbalancedRecipes", config.CATEGORY_GENERAL, false, Helper.getTranslation("config.unbalancedRecipes"));
		if (config.hasChanged()) {
			config.save();
		}
	}
}
