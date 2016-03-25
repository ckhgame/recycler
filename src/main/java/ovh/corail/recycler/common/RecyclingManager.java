package ovh.corail.recycler.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.recycler.common.handler.ConfigurationHandler;

public class RecyclingManager {
	private static final RecyclingManager instance = new RecyclingManager();
	private List<Recipe> recipes = Lists.<Recipe> newArrayList();

	public static RecyclingManager getInstance() {
		return instance;
	}
	public void init(FMLPreInitializationEvent event) {
	}
	public int getRecipesCount() {
		return recipes.size();
	}

	public Recipe getRecipe(int index) {
		return recipes.get(index);
	}
	public void addRecipe(Recipe recipe) {
		recipes.add(recipe);
	}
	public void addRecipe(ItemStack stack, Object... recipeComponents) {
		addRecipe(stack, false, recipeComponents);
	}
	public void addRecipe(ItemStack stack, boolean canBeRepaired, Object... recipeComponents) {
		Recipe recipe = new Recipe(stack, recipeComponents);
		recipe.setCanBeRepaired(canBeRepaired);
		recipes.add(recipe);
	}
	public void addRecipe(ItemStack stackIn, boolean canBeRepaired, ItemStack stackOut) {
		Recipe recipe = new Recipe(stackIn, stackOut);
		recipe.setCanBeRepaired(canBeRepaired);
		recipes.add(recipe);
	}
	public void addRecipe(ItemStack stackIn, ItemStack stackOut) {
		addRecipe(stackIn, false, stackOut);
	}

	public int hasRecipe(ItemStack stack) {
		if (stack == null || stack.stackSize <= 0) {
			return -1;
		}
		ItemStack testStack = stack.copy(); // For damaged items

		for (int recipe_num = 0; recipe_num < getRecipesCount(); recipe_num++) {
			if (recipes.get(recipe_num).canBeRepaired()) {
				testStack.setItemDamage(0);
			}
			boolean comp = testStack.isItemEqual(recipes.get(recipe_num).getItemRecipe());
			if (comp) {
				return recipe_num;
			}
		}
		return -1;
	}

	public List<ItemStack> getResultStack(ItemStack stack, int nb_input) {
		return getResultStack(stack, nb_input, false);
	}

	public List<ItemStack> getResultStack(ItemStack stack, int nb_input, boolean isGrind) {
		List<ItemStack> itemsList = new ArrayList<ItemStack>();
		int num_recipe = hasRecipe(stack);
		if (num_recipe < 0) {
			return itemsList;
		}
		Recipe currentRecipe = recipes.get(num_recipe);

		/* Calcul du résultat du stack */
		for (int i = 0; i < currentRecipe.getCount(); i++) {
			/* Pour chaque stack résultat de la recette */
			ItemStack currentStack = currentRecipe.getStack(i);
			int newStackCount = currentStack.stackSize * nb_input;
			/* Objets abimés */
			// TODO SWITCH + Main.items.get()
			if (currentRecipe.canBeRepaired() && stack.getItemDamage() > 0) {
				String name = currentStack.getItem().getRegistryName();
				int currentSize = currentStack.stackSize;
				/* Unités plus petites */
				if (name.compareTo("minecraft:iron_ingot") == 0) {
					currentStack = new ItemStack(MainUtil.iron_nugget, currentSize * 9, 0);
					newStackCount = currentStack.stackSize * nb_input;
				}
				if (name.compareTo("minecraft:gold_ingot") == 0) {
					currentStack = new ItemStack(Items.gold_nugget, currentSize * 9, 0);
					newStackCount = currentStack.stackSize * nb_input;
				}
				if (name.compareTo("minecraft:diamond") == 0) {
					currentStack = new ItemStack(MainUtil.diamond_nugget, currentSize * 9, 0);
					newStackCount = currentStack.stackSize * nb_input;
				}
				if (name.compareTo("minecraft:leather") == 0) {
					currentStack = new ItemStack(Items.rabbit_hide, currentSize * 4, 0);
					newStackCount = currentStack.stackSize * nb_input;
				}
				int maxDamage = currentRecipe.getItemRecipe().getMaxDamage();
				float pourcent = (float) (maxDamage - (stack.getItemDamage())) / maxDamage;
				newStackCount = (int) Math.floor(newStackCount * pourcent);
			}
			int slotCount = (int) Math.floor(newStackCount / currentStack.getMaxStackSize());
			ItemStack fullStack;
			/* Ajout des full stacks */
			for (int j = 0; j < slotCount; j++) {
				fullStack = currentStack.copy();
				fullStack.stackSize = fullStack.getMaxStackSize();
				itemsList.add(fullStack);
			}
			/* Reste de stack */
			int resteStackCount = newStackCount - (slotCount * currentStack.getMaxStackSize());
			if (resteStackCount > 0) {
				fullStack = currentStack.copy();
				fullStack.stackSize = resteStackCount;
				itemsList.add(fullStack);
			}

		}
		if (isGrind) { // TODO améliorer
			itemsList = this.getResultList(itemsList);
			itemsList = this.getResultList(itemsList);
			itemsList = this.getResultList(itemsList);
		}
		return itemsList;
	}

	public List<ItemStack> getResultList(List<ItemStack> itemsList) {
		List<ItemStack> newItemsList = new ArrayList<ItemStack>();
		for (int i = 0; i < itemsList.size(); i++) {
			ItemStack currentStack = itemsList.get(i);
			int numRecipe = hasRecipe(currentStack);
			if (numRecipe < 0) {
				newItemsList.add(itemsList.get(i).copy());
				continue;
			}
			/* Calcul du résultat */
			Recipe currentRecipe = recipes.get(numRecipe);
			if (currentStack.stackSize < currentRecipe.getItemRecipe().stackSize) {
				newItemsList.add(currentStack.copy());
				continue;
			}
			// TODO CODE en trop
			int nb_input = (int) Math.floor(currentStack.stackSize / currentRecipe.getItemRecipe().stackSize);
			List<ItemStack> itemsList2 = getResultStack(currentStack, nb_input);
			for (int j = 0; j < itemsList2.size(); j++) {
				if (itemsList2.get(j) != null) {
					newItemsList.add(itemsList2.get(j).copy());
				}
			}
		}
		return newItemsList;
	}
	//TODO
	public static List<JsonRecipe> getJsonRecipes() {
		List<JsonRecipe> jsonRecipesList = new ArrayList<JsonRecipe>();
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_ingot:1:0", new String[] {
			"minecraft:diamond:1:0",
		}));
		return jsonRecipesList;
	}
	private RecyclingManager() {
		/* Roche en pierre */
		this.addRecipe(new ItemStack(Blocks.stone, 1, 0), new Object[] { new ItemStack(Blocks.cobblestone, 1, 0) });
		/* Pierre moussue en Pierre */
		this.addRecipe(new ItemStack(Blocks.mossy_cobblestone, 1, 0),
				new Object[] { new ItemStack(Blocks.cobblestone, 1, 0), new ItemStack(Blocks.vine, 1, 0) });
		/* Pierre taillée en pierre */
		this.addRecipe(new ItemStack(Blocks.stonebrick, 1, 0),
				new Object[] { new ItemStack(Blocks.cobblestone, 1, 0), });
		/* Pierre craquelée, moussue, sculptée en pierre taillée */
		for (int i = 1; i <= 3; i++) {
			this.addRecipe(new ItemStack(Blocks.stonebrick, 1, i), new Object[] {
					new ItemStack(Blocks.stonebrick, 1, 0), (i == 1 ? new ItemStack(Blocks.vine, 1, 0) : "") // Moussue
			});
		}
		/* Grès en sable */
		//this.addRecipe(new ItemStack(Blocks.sandstone, 1, 0), new Object[] { new ItemStack(Blocks.sand, 4, 0), });
		/* Quartz rayé/sculpté */
		this.addRecipe(new ItemStack(Blocks.quartz_block, 1, 1),
				new Object[] { new ItemStack(Blocks.quartz_block, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.quartz_block, 1, 2),
				new Object[] { new ItemStack(Blocks.quartz_block, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.purpur_pillar, 1, 0), 
				new Object[] { new ItemStack(Blocks.purpur_block, 1, 0), });
		/* Granite/diorite/andésite poli */
		this.addRecipe(new ItemStack(Blocks.stone, 1, 2), new Object[] { new ItemStack(Blocks.stone, 1, 1), });
		this.addRecipe(new ItemStack(Blocks.stone, 1, 4), new Object[] { new ItemStack(Blocks.stone, 1, 3), });
		this.addRecipe(new ItemStack(Blocks.stone, 1, 6), new Object[] { new ItemStack(Blocks.stone, 1, 5), });
		/* Grès poli/sculpté */
		this.addRecipe(new ItemStack(Blocks.sandstone, 1, 1), new Object[] { new ItemStack(Blocks.sandstone, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.sandstone, 1, 2), new Object[] { new ItemStack(Blocks.sandstone, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.red_sandstone, 1, 1),
				new Object[] { new ItemStack(Blocks.red_sandstone, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.red_sandstone, 1, 2),
				new Object[] { new ItemStack(Blocks.red_sandstone, 1, 0), });
		/* Escalier en dalle */
		this.addRecipe(new ItemStack(Blocks.oak_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.wooden_slab, 3, 0), });
		this.addRecipe(new ItemStack(Blocks.spruce_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.wooden_slab, 3, 1), });
		this.addRecipe(new ItemStack(Blocks.birch_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.wooden_slab, 3, 2), });
		this.addRecipe(new ItemStack(Blocks.jungle_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.wooden_slab, 3, 3), });
		this.addRecipe(new ItemStack(Blocks.acacia_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.wooden_slab, 3, 4), });
		this.addRecipe(new ItemStack(Blocks.dark_oak_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.wooden_slab, 3, 5), });
		this.addRecipe(new ItemStack(Blocks.sandstone_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.stone_slab, 3, 1), });
		this.addRecipe(new ItemStack(Blocks.stone_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.stone_slab, 3, 3), });
		this.addRecipe(new ItemStack(Blocks.brick_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.stone_slab, 3, 4), });
		this.addRecipe(new ItemStack(Blocks.stone_brick_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.stone_slab, 3, 5), });
		this.addRecipe(new ItemStack(Blocks.nether_brick_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.stone_slab, 3, 6), });

		this.addRecipe(new ItemStack(Blocks.quartz_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.stone_slab, 3, 7), });
		this.addRecipe(new ItemStack(Blocks.red_sandstone_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.stone_slab2, 3, 0), });
		this.addRecipe(new ItemStack(Blocks.nether_brick_stairs, 1, 0),
				new Object[] { new ItemStack(Blocks.stone_slab, 3, 6), });
		this.addRecipe(new ItemStack(Blocks.purpur_stairs, 1, 0), 
				new Object[] { new ItemStack(Blocks.purpur_slab, 3, 0), });
		/* Dalle en bloc */
		for (int i = 0; i <= 5; i++) {
			this.addRecipe(new ItemStack(Blocks.wooden_slab, 2, i),
					new Object[] { new ItemStack(Blocks.planks, 1, i), });
		}
		this.addRecipe(new ItemStack(Blocks.stone_slab, 2, 0), new Object[] { new ItemStack(Blocks.stone, 1, 0), });

		this.addRecipe(new ItemStack(Blocks.stone_slab, 2, 1), new Object[] { new ItemStack(Blocks.sandstone, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.stone_slab, 2, 3),
				new Object[] { new ItemStack(Blocks.cobblestone, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.stone_slab, 2, 4),
				new Object[] { new ItemStack(Blocks.brick_block, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.stone_slab, 2, 5),
				new Object[] { new ItemStack(Blocks.stonebrick, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.stone_slab, 2, 6),
				new Object[] { new ItemStack(Blocks.nether_brick, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.stone_slab, 2, 7),
				new Object[] { new ItemStack(Blocks.quartz_block, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.stone_slab2, 2, 0),
				new Object[] { new ItemStack(Blocks.red_sandstone, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.purpur_slab, 2, 0), 
				new Object[] { new ItemStack(Blocks.purpur_block, 1, 0), });
		/* Porte */
		this.addRecipe(new ItemStack(Blocks.oak_door, 1, 0), new Object[] { new ItemStack(Blocks.planks, 2, 0), });
		this.addRecipe(new ItemStack(Blocks.spruce_door, 1, 0), new Object[] { new ItemStack(Blocks.planks, 2, 1), });
		this.addRecipe(new ItemStack(Blocks.birch_door, 1, 0), new Object[] { new ItemStack(Blocks.planks, 2, 2), });
		this.addRecipe(new ItemStack(Blocks.jungle_door, 1, 0), new Object[] { new ItemStack(Blocks.planks, 2, 3), });
		this.addRecipe(new ItemStack(Blocks.acacia_door, 1, 0), new Object[] { new ItemStack(Blocks.planks, 2, 4), });
		this.addRecipe(new ItemStack(Blocks.dark_oak_door, 1, 0), new Object[] { new ItemStack(Blocks.planks, 2, 5), });
		this.addRecipe(new ItemStack(Blocks.iron_door, 1, 0), new Object[] { new ItemStack(Items.iron_ingot, 2, 0), });
		/* Barreau de fer */
		this.addRecipe(new ItemStack(Blocks.iron_bars, 1, 0), new Object[] { new ItemStack(MainUtil.iron_nugget, 3, 0), });
		/* Plaque de pression (+ pondérée) */
		this.addRecipe(new ItemStack(Blocks.wooden_pressure_plate, 1, 0),
				new Object[] { new ItemStack(Blocks.planks, 2, 0), });
		this.addRecipe(new ItemStack(Blocks.stone_pressure_plate, 1, 0),
				new Object[] { new ItemStack(Blocks.stone, 2, 0), });
		this.addRecipe(new ItemStack(Blocks.heavy_weighted_pressure_plate, 1, 0),
				new Object[] { new ItemStack(Items.iron_ingot, 2, 0), });
		this.addRecipe(new ItemStack(Blocks.light_weighted_pressure_plate, 1, 0),
				new Object[] { new ItemStack(Items.gold_ingot, 2, 0), });
		/* Barrière */
		this.addRecipe(new ItemStack(Blocks.oak_fence, 3, 0),
				new Object[] { new ItemStack(Blocks.planks, 4, 0), new ItemStack(Items.stick, 2, 0), });
		this.addRecipe(new ItemStack(Blocks.spruce_fence, 3, 0),
				new Object[] { new ItemStack(Blocks.planks, 4, 1), new ItemStack(Items.stick, 2, 0), });
		this.addRecipe(new ItemStack(Blocks.birch_fence, 3, 0),
				new Object[] { new ItemStack(Blocks.planks, 4, 2), new ItemStack(Items.stick, 2, 0), });
		this.addRecipe(new ItemStack(Blocks.jungle_fence, 3, 0),
				new Object[] { new ItemStack(Blocks.planks, 4, 3), new ItemStack(Items.stick, 2, 0), });
		this.addRecipe(new ItemStack(Blocks.acacia_fence, 3, 0),
				new Object[] { new ItemStack(Blocks.planks, 4, 4), new ItemStack(Items.stick, 2, 0), });
		this.addRecipe(new ItemStack(Blocks.dark_oak_fence, 3, 0),
				new Object[] { new ItemStack(Blocks.planks, 4, 5), new ItemStack(Items.stick, 2, 0), });
		this.addRecipe(new ItemStack(Blocks.nether_brick_fence, 3, 0),
				new Object[] { new ItemStack(Blocks.nether_brick, 1, 0), });
		/* Bâton */
		this.addRecipe(new ItemStack(Items.stick, 2, 0), new Object[] { new ItemStack(Blocks.planks, 1, 0), });
		/* Planche en bûche */
		for (int i = 0; i < 4; i++) {
			this.addRecipe(new ItemStack(Blocks.planks, 4, i), new Object[] { new ItemStack(Blocks.log, 1, i), });
		}
		this.addRecipe(new ItemStack(Blocks.planks, 4, 4), new Object[] { new ItemStack(Blocks.log2, 1, 0), });
		this.addRecipe(new ItemStack(Blocks.planks, 4, 5), new Object[] { new ItemStack(Blocks.log2, 1, 1), });
		/* Trappe */
		this.addRecipe(new ItemStack(Blocks.trapdoor, 1, 0), new Object[] { new ItemStack(Blocks.planks, 3, 0), });
		this.addRecipe(new ItemStack(Blocks.iron_trapdoor, 1, 0),
				new Object[] { new ItemStack(Items.iron_ingot, 2, 0), });
		/* Tapis */
		for (int i = 0; i < 16; i++) {
			this.addRecipe(new ItemStack(Blocks.carpet, 3, i), new Object[] { new ItemStack(Blocks.wool, 2, i), });
		}
		/* Coffre */
		this.addRecipe(new ItemStack(Blocks.chest, 1, 0), new Object[] { new ItemStack(Blocks.planks, 8, 0), });
		/* Etabli */
		this.addRecipe(new ItemStack(Blocks.crafting_table, 1, 0),
				new Object[] { new ItemStack(Blocks.planks, 4, 0), });
		/* Fourneau */
		this.addRecipe(new ItemStack(Blocks.furnace, 1, 0), new Object[] { new ItemStack(Blocks.cobblestone, 8, 0), });
		/* Enclume */
		this.addRecipe(new ItemStack(Blocks.anvil, 1, 0), new Object[] { new ItemStack(Items.iron_ingot, 31, 0), });
		// TODO enclume endommagée
		/* Table d'enchantement */
		this.addRecipe(new ItemStack(Blocks.enchanting_table, 1, 0), new Object[] { new ItemStack(Items.diamond, 2, 0),
				new ItemStack(Blocks.obsidian, 4, 0), new ItemStack(Items.book, 1, 0), });
		/* Alambic */
		this.addRecipe(new ItemStack(Items.brewing_stand, 1, 0), new Object[] {
				new ItemStack(Blocks.cobblestone, 3, 0),
				new ItemStack(Items.blaze_rod, 1, 0),
		});
		/* Pancarte */
		this.addRecipe(new ItemStack(Items.sign, 3, 0), new Object[] {
				new ItemStack(Blocks.planks, 6, 0),
				new ItemStack(Items.stick, 1, 0),
		});
		/* Laine en Ficelle (plus de couleur) */
		for (int i = 0; i < 16; i++) {
			this.addRecipe(new ItemStack(Blocks.wool, 1, i), new Object[] {
					new ItemStack(Items.string, 4, 0),
			});
		}
		/* Torche */
		this.addRecipe(new ItemStack(Blocks.torch, 4, 0), new Object[] {
				new ItemStack(Items.coal, 1, 0),
				new ItemStack(Items.stick, 1, 0),
		});
		/* Torche redstone */
		this.addRecipe(new ItemStack(Blocks.redstone_torch, 1, 0), new Object[] {
				new ItemStack(Items.redstone, 1, 0),
				new ItemStack(Items.stick, 1, 0),
		});
		/* Boussole */
		this.addRecipe(new ItemStack(Items.compass, 1, 0), new Object[] {
				new ItemStack(Items.redstone, 1, 0),
				new ItemStack(Items.iron_ingot, 4, 0),
		});
		/* Carte vierge */
		this.addRecipe(new ItemStack(Items.map, 1, 0), new Object[] {
				new ItemStack(Items.compass, 1, 0),
				new ItemStack(Items.paper, 8, 0),
		});
		/* Entonnoir */
		this.addRecipe(new ItemStack(Blocks.hopper, 1, 0), new Object[] {
				new ItemStack(Items.iron_ingot, 5, 0),
				new ItemStack(Blocks.chest, 1, 0),
		});
		/* Canne à pêche */
		this.addRecipe(new ItemStack(Items.fishing_rod, 1, 0), new Object[] {
				new ItemStack(Items.string, 2, 0),
				new ItemStack(Items.stick, 3, 0),
		});
		/* Carotte sur un bâton */
		this.addRecipe(new ItemStack(Items.carrot_on_a_stick, 1, 0), new Object[] {
				new ItemStack(Items.carrot, 1, 0),
				new ItemStack(Items.fishing_rod, 1, 0),
		});
		/* Seau vide */
		this.addRecipe(new ItemStack(Items.bucket, 1, 0), new Object[] {
				new ItemStack(Items.iron_ingot, 3, 0),
		});
		/* Chaudron */
		this.addRecipe(new ItemStack(Items.cauldron, 1, 0), new Object[] {
				new ItemStack(Items.iron_ingot, 7, 0),
		});
		/* Echelle */
		this.addRecipe(new ItemStack(Blocks.ladder, 3, 0), new Object[] {
				new ItemStack(Items.stick, 7, 0),
		});
		/* Montre */
		this.addRecipe(new ItemStack(Items.clock, 1, 0), new Object[] {
				new ItemStack(Items.gold_ingot, 4, 0),
				new ItemStack(Items.redstone, 1, 0),
		});
		/* Dropper */
		this.addRecipe(new ItemStack(Blocks.dropper, 1, 0), new Object[] {
				new ItemStack(Blocks.cobblestone, 7, 0),
				new ItemStack(Items.redstone, 1, 0),
		});
		/* Porte armure */
		this.addRecipe(new ItemStack(Items.armor_stand, 1, 0), new Object[] {
				new ItemStack(Items.stick, 8, 0),
				new ItemStack(Blocks.stone_slab, 1, 0),
		});
		/* Distributeur */
		this.addRecipe(new ItemStack(Blocks.dispenser, 1, 0), new Object[] {
				new ItemStack(Blocks.cobblestone, 7, 0),
				new ItemStack(Items.redstone, 1, 0),
				new ItemStack(Items.bow, 1, 0),
		});
		/* Lanterne aquatique */
		this.addRecipe(new ItemStack(Blocks.sea_lantern, 1, 0), new Object[] {
				new ItemStack(Items.prismarine_crystals, 5, 0),
				new ItemStack(Items.prismarine_shard, 4, 0),
		});
		/* Citrouille lanterne */
		this.addRecipe(new ItemStack(Blocks.lit_pumpkin, 1, 0), new Object[] {
				new ItemStack(Blocks.pumpkin, 1, 0),
				new ItemStack(Blocks.torch, 1, 0),
		});
		/* Lit */
		this.addRecipe(new ItemStack(Items.bed, 1, 0), new Object[] {
				new ItemStack(Blocks.wool, 3, 0),
				new ItemStack(Blocks.planks, 3, 0),
		});
		/* Boutons */
		this.addRecipe(new ItemStack(Blocks.wooden_button, 1, 0), new Object[] {
				new ItemStack(Blocks.planks, 1, 0),
		});
		this.addRecipe(new ItemStack(Blocks.stone_button, 1, 0), new Object[] {
				new ItemStack(Blocks.stone, 1, 0),
		});
		/* Bâteau */
		this.addRecipe(new ItemStack(Items.boat, 1, 0), new Object[] {
				new ItemStack(Blocks.planks, 5, 0),
		});
		/* Jukebox */
		this.addRecipe(new ItemStack(Blocks.jukebox, 1, 0), new Object[] {
				new ItemStack(Blocks.planks, 8, 0),
				new ItemStack(Items.diamond, 1, 0),
		});
		/* TNT */
		this.addRecipe(new ItemStack(Blocks.tnt, 1, 0), new Object[] {
				new ItemStack(Blocks.sandstone, 4, 0),
				new ItemStack(Items.gunpowder, 5, 0),
		});
		/* Bol */
		this.addRecipe(new ItemStack(Items.bowl, 1, 0), new Object[] {
				new ItemStack(Blocks.planks, 3, 0),
		});
		/* Tableau */
		this.addRecipe(new ItemStack(Items.painting, 1, 0), new Object[] {
				new ItemStack(Items.stick, 8, 0),
				new ItemStack(Blocks.wool, 1, 0),
		});
		/* Crochet */
		this.addRecipe(new ItemStack(Blocks.tripwire_hook, 2, 0), new Object[] {
				new ItemStack(Items.stick, 1, 0),
				new ItemStack(Blocks.planks, 1, 0),
				new ItemStack(Items.iron_ingot, 1, 0),
		});
		/* Levier */
		this.addRecipe(new ItemStack(Blocks.lever, 1, 0), new Object[] {
				new ItemStack(Items.stick, 1, 0),
				new ItemStack(Blocks.cobblestone, 1, 0),
		});
		/* Pistons */
		this.addRecipe(new ItemStack(Blocks.sticky_piston, 1, 0), new Object[] {
				new ItemStack(Items.slime_ball, 1, 0),
				new ItemStack(Blocks.piston, 1, 0),
		});
		this.addRecipe(new ItemStack(Blocks.piston, 1, 0), new Object[] {
				new ItemStack(Items.iron_ingot, 1, 0),
				new ItemStack(Blocks.cobblestone, 4, 0),
				new ItemStack(Items.redstone, 1, 0),
				new ItemStack(Blocks.planks, 3, 0),
		});
		/* Rails */
		this.addRecipe(new ItemStack(Blocks.rail, 16, 0), new Object[] {
				new ItemStack(Items.iron_ingot, 6, 0),
				new ItemStack(Items.stick, 1, 0),
		});
		this.addRecipe(new ItemStack(Blocks.detector_rail, 6, 0), new Object[] {
				new ItemStack(Items.iron_ingot, 6, 0),
				new ItemStack(Items.redstone, 1, 0),
				new ItemStack(Blocks.stone_pressure_plate, 1, 0),
		});
		this.addRecipe(new ItemStack(Blocks.golden_rail, 6, 0), new Object[] {
				new ItemStack(Items.gold_ingot, 6, 0),
				new ItemStack(Items.redstone, 1, 0),
				new ItemStack(Items.stick, 1, 0),
		});
		this.addRecipe(new ItemStack(Blocks.activator_rail, 6, 0), new Object[] {
				new ItemStack(Items.iron_ingot, 6, 0),
				new ItemStack(Blocks.redstone_torch, 1, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		/* Pioche */
		this.addRecipe(new ItemStack(Items.wooden_pickaxe, 1, 0), true, new Object[] {
				new ItemStack(Blocks.planks, 3, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.stone_pickaxe, 1, 0), true, new Object[] {
				new ItemStack(Blocks.cobblestone, 3, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.iron_pickaxe, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 3, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.golden_pickaxe, 1, 0), true, new Object[] {
				new ItemStack(Items.gold_ingot, 3, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.diamond_pickaxe, 1, 0), true, new Object[] {
				new ItemStack(Items.diamond, 3, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		/* Hache */
		this.addRecipe(new ItemStack(Items.wooden_axe, 1, 0), true, new Object[] {
				new ItemStack(Blocks.planks, 3, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.stone_axe, 1, 0), true, new Object[] {
				new ItemStack(Blocks.cobblestone, 3, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.iron_axe, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 3, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.golden_axe, 1, 0), true, new Object[] {
				new ItemStack(Items.gold_ingot, 3, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.diamond_axe, 1, 0), true, new Object[] {
				new ItemStack(Items.diamond, 3, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		/* Pelle */
		this.addRecipe(new ItemStack(Items.wooden_shovel, 1, 0), true, new Object[] {
				new ItemStack(Blocks.planks, 1, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.stone_shovel, 1, 0), true, new Object[] {
				new ItemStack(Blocks.cobblestone, 1, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.iron_shovel, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 1, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.golden_shovel, 1, 0), true, new Object[] {
				new ItemStack(Items.gold_ingot, 1, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.diamond_shovel, 1, 0), true, new Object[] {
				new ItemStack(Items.diamond, 1, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		/* Epee */
		this.addRecipe(new ItemStack(Items.wooden_sword, 1, 0), true, new Object[] {
				new ItemStack(Blocks.planks, 2, 0),
				new ItemStack(Items.stick, 1, 0),
		});
		this.addRecipe(new ItemStack(Items.stone_sword, 1, 0), true, new Object[] {
				new ItemStack(Blocks.cobblestone, 2, 0),
				new ItemStack(Items.stick, 1, 0),
		});
		this.addRecipe(new ItemStack(Items.iron_sword, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 2, 0),
				new ItemStack(Items.stick, 1, 0),
		});
		this.addRecipe(new ItemStack(Items.golden_sword, 1, 0), true, new Object[] {
				new ItemStack(Items.gold_ingot, 2, 0),
				new ItemStack(Items.stick, 1, 0),
		});
		this.addRecipe(new ItemStack(Items.diamond_sword, 1, 0), true, new Object[] {
				new ItemStack(Items.diamond, 2, 0),
				new ItemStack(Items.stick, 1, 0),
		});
		/* Houe */
		this.addRecipe(new ItemStack(Items.wooden_hoe, 1, 0), true, new Object[] {
				new ItemStack(Blocks.planks, 2, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.stone_hoe, 1, 0), true, new Object[] {
				new ItemStack(Blocks.cobblestone, 2, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.iron_hoe, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 2, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.golden_hoe, 1, 0), true, new Object[] {
				new ItemStack(Items.gold_ingot, 2, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.diamond_hoe, 1, 0), true, new Object[] {
				new ItemStack(Items.diamond, 2, 0),
				new ItemStack(Items.stick, 2, 0),
		});
		/* Arc */
		this.addRecipe(new ItemStack(Items.bow, 1, 0), true, new Object[] {
				new ItemStack(Items.string, 3, 0),
				new ItemStack(Items.stick, 3, 0),
		});
		/* Flèche */
		this.addRecipe(new ItemStack(Items.arrow, 1, 0), new Object[] {
				new ItemStack(Items.feather, 1, 0),
				new ItemStack(Items.stick, 1, 0),
				new ItemStack(Items.flint, 1, 0),
		});
		/* Armure */
		this.addRecipe(new ItemStack(Items.leather_boots, 1, 0), true, new Object[] {
				new ItemStack(Items.leather, 4, 0),
		});
		this.addRecipe(new ItemStack(Items.leather_helmet, 1, 0), true, new Object[] {
				new ItemStack(Items.leather, 5, 0),
		});
		this.addRecipe(new ItemStack(Items.leather_chestplate, 1, 0), true, new Object[] {
				new ItemStack(Items.leather, 8, 0),
		});
		this.addRecipe(new ItemStack(Items.leather_leggings, 1, 0), true, new Object[] {
				new ItemStack(Items.leather, 7, 0),
		});
		this.addRecipe(new ItemStack(Items.iron_boots, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 4, 0),
		});
		this.addRecipe(new ItemStack(Items.iron_helmet, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 5, 0),
		});
		this.addRecipe(new ItemStack(Items.iron_chestplate, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 8, 0),
		});
		this.addRecipe(new ItemStack(Items.iron_leggings, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 7, 0),
		});
		this.addRecipe(new ItemStack(Items.golden_boots, 1, 0), true, new Object[] {
				new ItemStack(Items.gold_ingot, 4, 0),
		});
		this.addRecipe(new ItemStack(Items.golden_helmet, 1, 0), true, new Object[] {
				new ItemStack(Items.gold_ingot, 5, 0),
		});
		this.addRecipe(new ItemStack(Items.golden_chestplate, 1, 0), true, new Object[] {
				new ItemStack(Items.gold_ingot, 8, 0),
		});
		this.addRecipe(new ItemStack(Items.golden_leggings, 1, 0), true, new Object[] {
				new ItemStack(Items.gold_ingot, 7, 0),
		});
		this.addRecipe(new ItemStack(Items.diamond_boots, 1, 0), true, new Object[] {
				new ItemStack(Items.diamond, 4, 0),
		});
		this.addRecipe(new ItemStack(Items.diamond_helmet, 1, 0), true, new Object[] {
				new ItemStack(Items.diamond, 5, 0),
		});
		this.addRecipe(new ItemStack(Items.diamond_chestplate, 1, 0), true, new Object[] {
				new ItemStack(Items.diamond, 8, 0),
		});
		this.addRecipe(new ItemStack(Items.diamond_leggings, 1, 0), true, new Object[] {
				new ItemStack(Items.diamond, 7, 0),
		});
		this.addRecipe(new ItemStack(Items.chainmail_boots, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 2, 0),
				new ItemStack(Blocks.iron_bars, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.chainmail_helmet, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 3, 0),
				new ItemStack(Blocks.iron_bars, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.chainmail_chestplate, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 6, 0),
				new ItemStack(Blocks.iron_bars, 2, 0),
		});
		this.addRecipe(new ItemStack(Items.chainmail_leggings, 1, 0), true, new Object[] {
				new ItemStack(Items.iron_ingot, 3, 0),
				new ItemStack(Blocks.iron_bars, 4, 0),
		});
		/* Recycleur */
		if (ConfigurationHandler.recyclerRecycled) {
			this.addRecipe(new ItemStack(MainUtil.recycler, 1, 0), new Object[] {
					new ItemStack(Blocks.cobblestone, 6, 0),
					new ItemStack(Items.iron_ingot, 3, 0),
			});
		}
		
	}
	public static void loadJsonRecipes(List<JsonRecipe> jsonRecipes) {
		for (int i=0;i<jsonRecipes.size();i++) {
			Recipe recipe=convertJsonRecipe(jsonRecipes.get(i));
			if (recipe.getItemRecipe()!=null && recipe.getCount()>0) {
				instance.recipes.add(recipe);
			}
		}
	}
	public static Recipe convertJsonRecipe(JsonRecipe jRecipe) {
		ItemStack inputItem = StringToItemStack(jRecipe.inputItem);
		Recipe recipe=new Recipe(inputItem);
		for (int i=0;i<jRecipe.outputItems.length;i++) {
			recipe.addStack(StringToItemStack(jRecipe.outputItems[i]));
		}
		recipe.setCanBeRepaired(jRecipe.canBeRepaired);
		return recipe;
	}
	public static ItemStack StringToItemStack(String value) {
		String[] parts = value.split(":");
		if (parts.length==4) {
			Item item =(Item) GameRegistry.findItem(parts[0], parts[1]);
			if (item!=null) {
				return new ItemStack(item, Integer.valueOf(parts[2]), Integer.valueOf(parts[3]));
			}
		}
		return null;
		
	}
}
