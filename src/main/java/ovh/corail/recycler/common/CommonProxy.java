package ovh.corail.recycler.common;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.recycler.common.blocks.RecyclerBlock;
import ovh.corail.recycler.common.handler.ConfigurationHandler;
import ovh.corail.recycler.common.handler.GuiHandler;
import ovh.corail.recycler.common.handler.PacketHandler;
import ovh.corail.recycler.common.items.BasicItem;
import ovh.corail.recycler.common.items.Disk;
import ovh.corail.recycler.common.tileentity.RecyclerTile;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerTileEntity(RecyclerTile.class, "inventory");
		ConfigurationHandler.config = new Configuration(event.getSuggestedConfigurationFile());
		ConfigurationHandler.config.load();
		ConfigurationHandler.refreshConfig();	
		Main.items.add(new BasicItem("iron_nugget"));
		Main.items.add(new BasicItem("diamond_nugget"));
		Main.items.add(new Disk()); 
		Main.blocks.add(new RecyclerBlock());
		MainUtil.getNewRecipes(); 
		PacketHandler.init();
	}
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
	}
	public void postInit(FMLPostInitializationEvent event) {
	}
}
