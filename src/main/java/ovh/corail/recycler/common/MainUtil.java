package ovh.corail.recycler.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.recycler.common.blocks.RecyclerBlock;
import ovh.corail.recycler.common.items.BasicItem;
import ovh.corail.recycler.common.items.Disk;

public class MainUtil {
	public static boolean showMessages = true;
	public static I18n translation = new I18n();
	public static Item iron_nugget, diamond_nugget, diamond_disk;
	public static Block recycler;

	public static void init() {
		iron_nugget = new BasicItem("iron_nugget");
		diamond_nugget = new BasicItem("diamond_nugget");
		diamond_disk = new Disk();
		recycler = new RecyclerBlock();
		getNewRecipes();
	}

	public static void renderItems() {
		renderItem(MainUtil.iron_nugget);
		renderItem(MainUtil.diamond_nugget);
		renderItem(MainUtil.diamond_disk);
	}

	public static void renderBlocks() {
		renderBlock(recycler);
	}

	public static void renderBlock(Block block) {
		renderItem(Item.getItemFromBlock(block));
	}

	public static void renderItem(Item item) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0,
				new ModelResourceLocation(Main.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}

	public static void render() {
		renderItems();
		renderBlocks();
	}

	public static void sendMessage(String content, boolean translate) {
		content = MainUtil.getTranslation(content);
		sendMessage(content);
	}

	public static void sendMessage(String content) {
		if (showMessages) {
			Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentString(content));
		}
	}

	public static String getTranslation(String key) {
		return translation.translateToLocal(key);
	}

	public static void getNewRecipes() {
		/* Pépite en Lingot */
		GameRegistry.addRecipe(new ItemStack(Items.iron_ingot, 1),
				new Object[] { "000", "000", "000", Character.valueOf('0'), new ItemStack(iron_nugget, 1), });
		GameRegistry.addRecipe(new ItemStack(Items.diamond, 1),
				new Object[] { "000", "000", "000", Character.valueOf('0'), new ItemStack(diamond_nugget, 1), });
		/* Lingot en pépite */
		GameRegistry.addRecipe(new ItemStack(iron_nugget, 9),
				new Object[] { "0", Character.valueOf('0'), new ItemStack(Items.iron_ingot, 1), });
		GameRegistry.addRecipe(new ItemStack(diamond_nugget, 9),
				new Object[] { "0", Character.valueOf('0'), new ItemStack(Items.diamond, 1), });
		/* Podzol (2 terres 2 sables en croix) */
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 1), });
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 1), });
		/* Argile (2 granits 2 sables en croix) */
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 1), });
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 1), });
		/* Toile (5 ficelles en croix) */
		GameRegistry.addRecipe(new ItemStack(Blocks.web, 1, 0),
				new Object[] { "0 0", " 0 ", "0 0", Character.valueOf('0'), new ItemStack(Items.string, 1, 0), });
		/* Cottes de mailles (lingot de fer et barreaux de fer) */
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
		/* Recette du Recycleur */
		GameRegistry.addRecipe(new ItemStack(recycler, 1), new Object[] { "000", "111", "000", Character.valueOf('0'),
				new ItemStack(Blocks.cobblestone, 1), Character.valueOf('1'), new ItemStack(Items.iron_ingot, 1), });
		/* Recette du Disque de diamant */
		GameRegistry.addRecipe(new ItemStack(diamond_disk, 1),
				new Object[] { " 0 ", "010", " 0 ", Character.valueOf('0'), new ItemStack(diamond_nugget, 1),
						Character.valueOf('1'), new ItemStack(Items.iron_ingot, 1), });
	}
}
