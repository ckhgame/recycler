package ovh.corail.recycler.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.recycler.common.handler.GuiHandler;
import ovh.corail.recycler.common.handler.PacketHandler;
import ovh.corail.recycler.common.tileentity.RecyclerTile;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerTileEntity(RecyclerTile.class, "inventory");
		PacketHandler.init();
	}
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
	}
	public void postInit(FMLPostInitializationEvent event) {
	}
}
