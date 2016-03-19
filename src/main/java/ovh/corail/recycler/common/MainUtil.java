package ovh.corail.recycler.common;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MainUtil {
	public static boolean showMessages=true;
	public static void sendMessage(String content) {
		if (showMessages) {
			EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
			player.addChatComponentMessage(new TextComponentString(content));
		}
	}
	public static Block getNewBlock(String name) {
		for (int i=0;i<Main.blocks.size();i++) {
			if (Main.blocks.get(i).getRegistryName().compareTo(name)==0) {
				return Main.blocks.get(i);
			}
		}
		return null;
	}
	public static Item getNewItem(String name) {
		for (int i=0;i<Main.items.size();i++) {
			if (Main.items.get(i).getRegistryName().compareTo(name)==0) {
				return Main.items.get(i);
			}
		}
		return null;
	}
	
	public static void getNewRecipes() {
		Item iron_nugget = Main.items.get(0);
		Item diamond_nugget = Main.items.get(1);
		Item diamond_disk = Main.items.get(2);
		Block recycler = Main.blocks.get(0);
		/* Pépite en Lingot */
		GameRegistry.addRecipe(new ItemStack(Items.iron_ingot, 1), new Object[]{"000", "000", "000", 
			Character.valueOf('0'), new ItemStack(iron_nugget, 1), 
		});
		GameRegistry.addRecipe(new ItemStack(Items.diamond, 1), new Object[]{"000", "000", "000", 
			Character.valueOf('0'), new ItemStack(diamond_nugget, 1), 
		});
		/* Podzol (2 terres 2 sables en croix) */
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[]{"01", "10",  
			Character.valueOf('0'), new ItemStack(Blocks.dirt, 1, 0), 
			Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 0), 
		});
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[]{"01", "10",  
				Character.valueOf('1'), new ItemStack(Blocks.dirt, 1, 0), 
				Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 0), 
			});
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[]{"01", "10",  
				Character.valueOf('0'), new ItemStack(Blocks.dirt, 1, 0), 
				Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 1), 
		});
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 1, 2), new Object[]{"01", "10",  
				Character.valueOf('1'), new ItemStack(Blocks.dirt, 1, 0), 
				Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 1), 
		});
		/* Argile (2 granits 2 sables en croix) */
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[]{"01", "10",  
				Character.valueOf('0'), new ItemStack(Blocks.stone, 1, 1), 
				Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 0), 
		});
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[]{"01", "10",  
				Character.valueOf('1'), new ItemStack(Blocks.stone, 1, 1), 
				Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 0), 
		});	
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[]{"01", "10",  
				Character.valueOf('0'), new ItemStack(Blocks.stone, 1, 1), 
				Character.valueOf('1'), new ItemStack(Blocks.sand, 1, 1), 
		});
		GameRegistry.addRecipe(new ItemStack(Blocks.clay, 1, 0), new Object[]{"01", "10",  
				Character.valueOf('1'), new ItemStack(Blocks.stone, 1, 1), 
				Character.valueOf('0'), new ItemStack(Blocks.sand, 1, 1), 
		});
		/* Toile (5 ficelles en croix) */
		GameRegistry.addRecipe(new ItemStack(Blocks.web, 1, 0), new Object[]{"0 0", " 0 ", "0 0",  
				Character.valueOf('0'), new ItemStack(Items.string, 1, 0), 
		});
		/* Cottes de mailles (lingot de fer et barreaux de fer) */
		GameRegistry.addRecipe(new ItemStack(Items.chainmail_helmet, 1, 0), new Object[]{"000", "1 1",  
				Character.valueOf('0'), new ItemStack(Items.iron_ingot, 1, 0),
				Character.valueOf('1'), new ItemStack(Blocks.iron_bars, 1, 0), 
		});
		GameRegistry.addRecipe(new ItemStack(Items.chainmail_chestplate, 1, 0), new Object[]{"0 0", "010", "010",  
				Character.valueOf('0'), new ItemStack(Items.iron_ingot, 1, 0),
				Character.valueOf('1'), new ItemStack(Blocks.iron_bars, 1, 0), 
		});
		GameRegistry.addRecipe(new ItemStack(Items.chainmail_leggings, 1, 0), new Object[]{"000", "1 1", "1 1",  
				Character.valueOf('0'), new ItemStack(Items.iron_ingot, 1, 0),
				Character.valueOf('1'), new ItemStack(Blocks.iron_bars, 1, 0), 
		});
		GameRegistry.addRecipe(new ItemStack(Items.chainmail_boots, 1, 0), new Object[]{"1 1", "0 0",  
				Character.valueOf('0'), new ItemStack(Items.iron_ingot, 1, 0),
				Character.valueOf('1'), new ItemStack(Blocks.iron_bars, 1, 0), 
		});
		/* Recette du Recycleur */
		GameRegistry.addRecipe(new ItemStack(recycler,1), new Object[]{"000", "121", "000", 
			Character.valueOf('0'), new ItemStack(Blocks.cobblestone, 1), 
			Character.valueOf('1'), new ItemStack(Items.iron_sword, 1), 
			Character.valueOf('2'), new ItemStack(Blocks.redstone_torch, 1), 
		});
		/* Recette du Disque de diamant */
		GameRegistry.addRecipe(new ItemStack(diamond_disk,1), new Object[]{" 0 ", "010", " 0 ", 
			Character.valueOf('0'), new ItemStack(diamond_nugget, 1), 
			Character.valueOf('1'), new ItemStack(Items.iron_ingot, 1), 
		});
	}
}
