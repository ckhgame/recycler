package ovh.corail.recycler.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonParser;

import jdk.nashorn.internal.parser.JSONParser;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
	public void preInit(FMLPreInitializationEvent event) throws IOException {
		GameRegistry.registerTileEntity(RecyclerTile.class, "inventory");
		ConfigurationHandler.config = new Configuration(event.getSuggestedConfigurationFile());
		ConfigurationHandler.config.load();
		ConfigurationHandler.refreshConfig();
		MainUtil.init();
		PacketHandler.init();
		File questFile = new File(event.getModConfigurationDirectory(), "recyclingRecipes.json");
		if (!questFile.exists()) {
			questFile.createNewFile();
			FileWriter fw = new FileWriter(questFile);
			List<jsonRecipe> jsonRecipesList = new ArrayList<jsonRecipe>();
			//jsonRecipesList
			//		.add(new jsonRecipe("minecraft:iron_ingot:1:0", new String[] { "minecraft:diamond:1:0" }));
			fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonRecipesList));
			fw.close();
		}
		// TODO
		List<jsonRecipe> recipes = new Gson().fromJson(new BufferedReader(new FileReader(questFile)),
				new TypeToken<List<jsonRecipe>>() {}.getType());
		Recipe recipe = null;
		for (int i = 0; i < recipes.size(); i++) {
			jsonRecipe currentRecipe = recipes.get(i);
			recipe = MainUtil.convertJsonRecipe(currentRecipe);
			RecyclingManager.getInstance().addRecipe(recipe);
		}

	}

	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance, new GuiHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {
	}
}
