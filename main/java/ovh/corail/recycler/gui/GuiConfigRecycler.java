package ovh.corail.recycler.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import ovh.corail.recycler.core.Main;
import ovh.corail.recycler.handler.ConfigurationHandler;

public class GuiConfigRecycler extends GuiConfig {
	public GuiConfigRecycler(GuiScreen parentScreen) {
		super(parentScreen, new ConfigElement(ConfigurationHandler.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), Main.MOD_ID, false, false, GuiConfig.getAbridgedConfigPath(ConfigurationHandler.config.toString()));
	}
}
