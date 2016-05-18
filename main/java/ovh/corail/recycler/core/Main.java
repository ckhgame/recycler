package ovh.corail.recycler.core;

import java.io.IOException;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ovh.corail.recycler.block.BlockRecycler;
import ovh.corail.recycler.handler.EventHandler;
import ovh.corail.recycler.item.ItemAchievement001;
import ovh.corail.recycler.item.ItemDiamondDisk;
import ovh.corail.recycler.item.ItemDiamondNugget;
import ovh.corail.recycler.item.ItemIronNugget;

@Mod(modid = Main.MOD_ID, name = Main.MOD_NAME, version = Main.MOD_VER, guiFactory = "ovh.corail.recycler.gui.GuiFactoryRecycler")
public class Main {
	public static final String MOD_ID = "recycler";
	public static final String MOD_NAME = "Corail Recycler";
	public static final String MOD_VER = "1.2";
	@Instance(Main.MOD_ID)
	public static Main instance;
	@SidedProxy(clientSide = "ovh.corail.recycler.core.ClientProxy", serverSide = "ovh.corail.recycler.core.CommonProxy")
	public static CommonProxy proxy;
	
	public static CreativeTabs tabRecycler = new CreativeTabs(Main.MOD_ID) {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(Main.recycler);
		}

		@Override
		public String getTranslatedTabLabel() {
			return "tab" + Main.MOD_NAME;
		}
	};
	public static ItemIronNugget iron_nugget = new ItemIronNugget();
	public static ItemDiamondNugget diamond_nugget = new ItemDiamondNugget();
	public static ItemDiamondDisk diamond_disk = new ItemDiamondDisk();
	public static BlockRecycler recycler = new BlockRecycler();
	
	public static ItemAchievement001 itemAchievement001 = new ItemAchievement001();
	public static Achievement achievementPlaceRecycler = new Achievement("achievement.PlaceRecycler", "PlaceRecycler", 0, 0, Main.itemAchievement001, (Achievement) null);
	public static Achievement achievementBuildDisk = new Achievement("achievement.BuildDisk", "BuildDisk", 1, 1, Main.diamond_disk, achievementPlaceRecycler);
	public static Achievement achievementFirstRecycle = new Achievement("achievement.FirstRecycle", "FirstRecycle", 2, 2, Main.iron_nugget, achievementBuildDisk);
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException {
		MinecraftForge.EVENT_BUS.register(new EventHandler());
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
