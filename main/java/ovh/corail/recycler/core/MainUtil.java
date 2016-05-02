package ovh.corail.recycler.core;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.recycler.common.handler.ConfigurationHandler;

public class MainUtil {
	public static void getNewRecipes() {
		/* Pépite en Lingot */
		GameRegistry.addRecipe(new ItemStack(Items.iron_ingot, 1),
				new Object[] { "000", "000", "000", Character.valueOf('0'), 
						new ItemStack(Main.iron_nugget, 1), });
		GameRegistry.addRecipe(new ItemStack(Items.diamond, 1),
				new Object[] { "000", "000", "000", Character.valueOf('0'), new ItemStack(Main.diamond_nugget, 1), });
		/* Lingot en pépite */
		GameRegistry.addRecipe(new ItemStack(Main.iron_nugget, 9),
				new Object[] { "0", Character.valueOf('0'), new ItemStack(Items.iron_ingot, 1), });
		GameRegistry.addRecipe(new ItemStack(Main.diamond_nugget, 9),
				new Object[] { "0", Character.valueOf('0'), new ItemStack(Items.diamond, 1), });
		/* Podzol (2 terres 2 sables en croix) */
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
		/* Argile (2 granits 2 sables en croix) */
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
		/* Toile (5 ficelles en croix) */
		if (ConfigurationHandler.craftWeb) {
		GameRegistry.addRecipe(new ItemStack(Blocks.web, 1, 0),
				new Object[] { "0 0", " 0 ", "0 0", Character.valueOf('0'), new ItemStack(Items.string, 1, 0), });
		}
		/* Cottes de mailles (lingot de fer et barreaux de fer) */
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
		/* Recette du Recycleur */
		GameRegistry.addRecipe(new ItemStack(Main.recycler, 1), new Object[] { "000", "111", "000", Character.valueOf('0'),
				new ItemStack(Blocks.cobblestone, 1), Character.valueOf('1'), new ItemStack(Items.iron_ingot, 1), });
		/* Recette du Disque de diamant */
		GameRegistry.addRecipe(new ItemStack(Main.diamond_disk, 1),
				new Object[] { " 0 ", "010", " 0 ", Character.valueOf('0'), new ItemStack(Main.diamond_nugget, 1),
						Character.valueOf('1'), new ItemStack(Items.iron_ingot, 1), });
	}
}
