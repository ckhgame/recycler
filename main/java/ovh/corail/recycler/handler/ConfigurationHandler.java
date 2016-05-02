package ovh.corail.recycler.handler;

import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {
	public static Configuration config;

	public static boolean recyclerRecycled, craftPodzol, craftClay, craftWeb, craftChainmail;

	public static void refreshConfig() {
		recyclerRecycled=config.getBoolean("recyclerRecycled", config.CATEGORY_GENERAL, false, I18n.translateToLocal("config.recyclerRecycled"));
		craftPodzol=config.getBoolean("craftPodzol", config.CATEGORY_GENERAL, true, I18n.translateToLocal("config.craftPodzol"));
		craftClay=config.getBoolean("craftClay", config.CATEGORY_GENERAL, true, I18n.translateToLocal("config.craftClay"));
		craftWeb=config.getBoolean("craftWeb", config.CATEGORY_GENERAL, true, I18n.translateToLocal("config.craftWeb"));
		craftChainmail=config.getBoolean("craftChainmail", config.CATEGORY_GENERAL, true, I18n.translateToLocal("config.craftChainmail"));
		if (config.hasChanged()) {
			config.save();
		}
	}
}
