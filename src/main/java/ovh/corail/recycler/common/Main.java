package ovh.corail.recycler.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ovh.corail.recycler.common.blocks.RecyclerBlock;
import ovh.corail.recycler.common.items.BasicItem;
import ovh.corail.recycler.common.items.Disk;

@Mod(modid = Main.MODID, name = Main.MODNAME, version = Main.VERSION)
public class Main {
	public static final String MODID = "recycler";
	public static final String MODNAME = "Mod Recycler";
	public static final String VERSION = "1.0";
	@Instance(Main.MODID)
	public static Main instance = new Main();
	@SidedProxy(clientSide = "ovh.corail.recycler.client.ClientProxy", serverSide = "ovh.corail.recycler.common.CommonProxy")
	public static CommonProxy proxy;
	public static Configuration config;
	public static boolean recyclerRecycled;
	public static boolean existGrinder;
	public static List<Block> blocks = new ArrayList<Block>();
	public static List<Item> items = new ArrayList<Item>();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		recyclerRecycled=config.getBoolean("recyclerRecycled", config.CATEGORY_GENERAL, false, "Recycler le recycleur ");
		Main.items.add(new BasicItem("iron_nugget"));
		Main.items.add(new BasicItem("diamond_nugget"));
		Main.items.add(new Disk()); 
		Main.blocks.add(new RecyclerBlock());
		MainUtil.getNewRecipes(); 
		proxy.preInit(event);
		config.save();
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
