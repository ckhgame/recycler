package ovh.corail.recycler.core;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.recycler.handler.ConfigurationHandler;

public class Helper {
	public static boolean showMessages = true;

	public static void render() {
		/** blocks */
		render(Main.recycler);
		/** items */
		render(Main.iron_nugget);
		render(Main.diamond_nugget);
		render(Main.diamond_disk);
	}

	private static void render(Block block) {
		render(Item.getItemFromBlock(block));
	}

	private static void render(Item item) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
	public static void register() {
		/** blocks */
		register(Main.recycler);
		/** items */
		register(Main.iron_nugget);
		register(Main.diamond_nugget);
		register(Main.diamond_disk);
	}
	
	private static void register(Block block) {
		GameRegistry.register(block);
		GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}

	private static void register(Item item) {
		GameRegistry.register(item);
	}

	public static void getNewRecipes() {
		/** nugget => ingot */
		GameRegistry.addRecipe(new ItemStack(Items.iron_ingot, 1),
				new Object[] { "000", "000", "000", Character.valueOf('0'), 
						new ItemStack(Main.iron_nugget, 1), });
		GameRegistry.addRecipe(new ItemStack(Items.diamond, 1),
				new Object[] { "000", "000", "000", Character.valueOf('0'), new ItemStack(Main.diamond_nugget, 1), });
		/** ingot => nugget */
		GameRegistry.addRecipe(new ItemStack(Main.iron_nugget, 9),
				new Object[] { "0", Character.valueOf('0'), new ItemStack(Items.iron_ingot, 1), });
		GameRegistry.addRecipe(new ItemStack(Main.diamond_nugget, 9),
				new Object[] { "0", Character.valueOf('0'), new ItemStack(Items.diamond, 1), });
		/** podzol (2 dirt 2 sand in cross) */
		if (ConfigurationHandler.craftPodzol) {
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 1), });
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 1), });
		}
		/** clay (2 granit 2 sand in cross) */
		if (ConfigurationHandler.craftClay) {
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 1), });
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 1), });
		}
		/** web (5 string in cross) */
		if (ConfigurationHandler.craftWeb) {
		GameRegistry.addRecipe(new ItemStack(Blocks.web, 1, 0),
				new Object[] { "0 0", " 0 ", "0 0", Character.valueOf('0'), new ItemStack(Items.string, 1, 0), });
		}
		/** chainmail (iron ingot and iron bar) */
		if (ConfigurationHandler.craftChainmail) {
		GameRegistry.addRecipe(new ItemStack(Items.chainmail_helmet, 1, 0),
				new Object[] { "000", "1 1", Character.valueOf('0'), new ItemStack(Items.iron_ingot, 1, 0),
						Character.valueOf('1'), new ItemStack(Blocks.iron_bars, 1, 0), });
		GameRegistry
				.addRecipe(new ItemStack(Items.chainmail_chestplate, 1, 0),
						new Object[] { "0 0", "010", "010", Character.valueOf('0'),
								new ItemStack(Items.iron_ingot, 1, 0), Character.valueOf('1'),
								new ItemStack(Blocks.iron_bars, 1, 0), });
		GameRegistry
				.addRecipe(new ItemStack(Items.chainmail_leggings, 1, 0),
						new Object[] { "000", "1 1", "1 1", Character.valueOf('0'),
								new ItemStack(Items.iron_ingot, 1, 0), Character.valueOf('1'),
								new ItemStack(Blocks.iron_bars, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Items.chainmail_boots, 1, 0),
				new Object[] { "1 1", "0 0", Character.valueOf('0'), new ItemStack(Items.iron_ingot, 1, 0),
						Character.valueOf('1'), new ItemStack(Blocks.iron_bars, 1, 0), });
		}
		/** recycler recipe */
		GameRegistry.addRecipe(new ItemStack(Main.recycler, 1), new Object[] { "000", "111", "000", Character.valueOf('0'),
				new ItemStack(Blocks.cobblestone, 1), Character.valueOf('1'), new ItemStack(Items.iron_ingot, 1), });
		/** diamond disk recipe */
		GameRegistry.addRecipe(new ItemStack(Main.diamond_disk, 1),
				new Object[] { " 0 ", "010", " 0 ", Character.valueOf('0'), new ItemStack(Main.diamond_nugget, 1),
						Character.valueOf('1'), new ItemStack(Items.iron_ingot, 1), });
	}
}
