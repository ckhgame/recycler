package ovh.corail.recycler.core;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ovh.corail.recycler.blocks.RecyclerBlock;
import ovh.corail.recycler.items.ItemDiamondDisk;
import ovh.corail.recycler.items.ItemDiamondNugget;
import ovh.corail.recycler.items.ItemIronNugget;

@Mod(modid = Main.MOD_ID, name = Main.MOD_NAME, version = Main.MOD_VER, guiFactory = "ovh.corail.recycler.gui.GuiFactoryRecycler")
public class Main {
	public static final String MOD_ID = "recycler";
	public static final String MOD_NAME = "Mod Recycler";
	public static final String MOD_VER = "1.1";
	@Instance(Main.MOD_ID)
	public static Main instance = new Main();
	@SidedProxy(clientSide = "ovh.corail.recycler.core.ClientProxy", serverSide = "ovh.corail.recycler.core.CommonProxy")
	public static CommonProxy proxy;
	public static Item iron_nugget = new ItemIronNugget();
	public static Item diamond_nugget = new ItemDiamondNugget();
	public static Item diamond_disk = new ItemDiamondDisk();
	public static Block recycler = new RecyclerBlock();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException {
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
