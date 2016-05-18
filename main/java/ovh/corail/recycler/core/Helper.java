package ovh.corail.recycler.core;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.recycler.handler.ConfigurationHandler;

public class Helper {
	public static ItemStack addToInventoryWithLeftover(ItemStack stack, IInventory inventory, boolean simulate) {
		int left = stack.stackSize;
		int minus = inventory instanceof InventoryPlayer ? 4 : 0;
		int max = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
		for (int i = 0; i < inventory.getSizeInventory() - minus; i++) {
			ItemStack in = inventory.getStackInSlot(i);
			// if (!inventory.isItemValidForSlot(i, stack))
			// continue;
			if (in != null && stack.isItemEqual(in) && ItemStack.areItemStackTagsEqual(stack, in)) {
				int space = max - in.stackSize;
				int add = Math.min(space, stack.stackSize);
				if (add > 0) {
					if (!simulate)
						in.stackSize += add;
					left -= add;
					if (left <= 0)
						return null;
				}
			}
		}
		for (int i = 0; i < inventory.getSizeInventory() - minus; i++) {
			ItemStack in = inventory.getStackInSlot(i);
			// if (!inventory.isItemValidForSlot(i, stack))
			// continue;
			if (in == null) {
				int add = Math.min(max, left);
				if (!simulate)
					inventory.setInventorySlotContents(i, copyStack(stack, add));
				left -= add;
				if (left <= 0)
					return null;
			}
		}
		return copyStack(stack, left);
	}
	
	private static ItemStack copyStack(ItemStack stack, int size) {
		if (stack == null || size == 0)
			return null;
		ItemStack tmp = stack.copy();
		tmp.stackSize = Math.min(size, stack.getMaxStackSize());
		return tmp;
	}
	
	public static void addChatMessage(String message, EntityPlayer currentPlayer, boolean translate) {
		if (currentPlayer != null) {
			if (translate) {
				message = getTranslation(message);
			}
			currentPlayer.addChatMessage(new TextComponentString(message));
		}
	}
	
	public static String getTranslation(String message) {
		return I18n.translateToLocal(message);
	}

	public static void render() {
		/** blocks */
		render(Main.recycler);
		/** items */
		render(Main.iron_nugget);
		render(Main.diamond_nugget);
		render(Main.diamond_disk);
		render(Main.itemAchievement001);
	}

	private static void render(Block block) {
		render(Item.getItemFromBlock(block), 0);
	}
	
	private static void render(Item item) {
		render(item, 0);
	}

	private static void render(Block block, int meta) {
		render(Item.getItemFromBlock(block), meta);
	}
	
	private static void render(Item item, int meta) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta,
				new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
	public static void register() {
		/** blocks */
		register(Main.recycler);
		/** items */
		register(Main.iron_nugget);
		register(Main.diamond_nugget);
		register(Main.diamond_disk);
		register(Main.itemAchievement001);
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
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 4, 2), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 4, 2), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 4, 2), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 1), });
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 4, 2), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.dirt, 1, 0), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 1), });
		}
		/** clay ball (2 granit 2 sand in cross) */
		if (ConfigurationHandler.craftClay) {
		GameRegistry.addRecipe(new ItemStack(Items.clay_ball, 4, 0), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Items.clay_ball, 4, 0), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 0), });
		GameRegistry.addRecipe(new ItemStack(Items.clay_ball, 4, 0), new Object[] { "01", "10", Character.valueOf('0'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 1), });
		GameRegistry.addRecipe(new ItemStack(Items.clay_ball, 4, 0), new Object[] { "01", "10", Character.valueOf('1'),
				new ItemStack(Blocks.stone, 1, 1), Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 1), });
		}
		/** cobweb (5 string in cross) */
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
