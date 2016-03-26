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

	// TODO
	public static List<JsonRecipe> getJsonRecipes() {
		List<JsonRecipe> jsonRecipesList = new ArrayList<JsonRecipe>();
		/* Roche en pierre */
		jsonRecipesList.add(new JsonRecipe("minecraft:stone:1:0", new String[] { "minecraft:cobblestone:1:0", }));
		/* Pierre moussue en Pierre */
		jsonRecipesList.add(new JsonRecipe("minecraft:mossy_cobblestone:1:0",
				new String[] { "minecraft:cobblestone:1:0", "minecraft:vine:1:0", }));
		/* Pierre taillée en pierre */
		jsonRecipesList.add(new JsonRecipe("minecraft:stonebrick:1:0", new String[] { "minecraft:cobblestone:1:0", }));
		/* Pierre craquelée, moussue, sculptée en pierre taillée */
		for (int i = 1; i <= 3; i++) {
			jsonRecipesList.add(new JsonRecipe("minecraft:stonebrick:1:" + i,
					new String[] { "minecraft:stonebrick:1:0", (i == 1 ? "minecraft:vine:1:0" : "") // Moussue
					}));
		}
		/* Grès en sable */
		jsonRecipesList.add(new JsonRecipe("minecraft:sandstone:1:0", new String[] { "minecraft:sand:4:0", }));
		/* Quartz rayé/sculpté */
		jsonRecipesList
				.add(new JsonRecipe("minecraft:quartz_block:1:1", new String[] { "minecraft:quartz_block:1:0", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:quartz_block:1:2", new String[] { "minecraft:quartz_block:1:0", }));
		/* Purpur sculpté */
		jsonRecipesList
				.add(new JsonRecipe("minecraft:purpur_pillar:1:0", new String[] { "minecraft:purpur_block:1:0", }));
		/* Granite/diorite/andésite poli */
		for (int i = 1; i <= 3; i++) {
			jsonRecipesList.add(new JsonRecipe("minecraft:stone:1:" + (2 * i),
					new String[] { "minecraft:stone:1:" + (2 * i - 1), }));
		}
		/* Grès poli/sculpté */
		jsonRecipesList.add(new JsonRecipe("minecraft:sandstone:1:1", new String[] { "minecraft:sandstone:1:0", }));
		jsonRecipesList.add(new JsonRecipe("minecraft:sandstone:1:2", new String[] { "minecraft:sandstone:1:0", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:red_sandstone:1:1", new String[] { "minecraft:red_sandstone:1:0", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:red_sandstone:1:2", new String[] { "minecraft:red_sandstone:1:0", }));
		/* Escalier en dalle */
		jsonRecipesList.add(new JsonRecipe("minecraft:oak_stairs:1:0", new String[] { "minecraft:wooden_slab:3:0", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:spruce_stairs:1:0", new String[] { "minecraft:wooden_slab:3:1", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:birch_stairs:1:0", new String[] { "minecraft:wooden_slab:3:2", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:jungle_stairs:1:0", new String[] { "minecraft:wooden_slab:3:3", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:acacia_stairs:1:0", new String[] { "minecraft:wooden_slab:3:4", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:dark_oak_stairs:1:0", new String[] { "minecraft:wooden_slab:3:5", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:sandstone_stairs:1:0", new String[] { "minecraft:stone_slab:3:1", }));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_stairs:1:0", new String[] { "minecraft:stone_slab:3:3", }));
		jsonRecipesList.add(new JsonRecipe("minecraft:brick_stairs:1:0", new String[] { "minecraft:stone_slab:3:4", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:stone_brick_stairs:1:0", new String[] { "minecraft:stone_slab:3:5", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:nether_brick_stairs:1:0", new String[] { "minecraft:stone_slab:3:6", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:quartz_stairs:1:0", new String[] { "minecraft:stone_slab:3:7", }));
		jsonRecipesList.add(
				new JsonRecipe("minecraft:red_sandstone_stairs:1:0", new String[] { "minecraft:stone_slab2:3:0", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:purpur_stairs:1:0", new String[] { "minecraft:purpur_slab:3:0", }));
		/* Dalle en bloc */
		for (int i = 0; i <= 5; i++) {
			jsonRecipesList
					.add(new JsonRecipe("minecraft:wooden_slab:2:" + i, new String[] { "minecraft:planks:1:" + i, }));
		}
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_slab:2:0", new String[] { "minecraft:stone:1:0", }));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_slab:2:1", new String[] { "minecraft:sandstone:1:0", }));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_slab:2:3", new String[] { "minecraft:cobblestone:1:0", }));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_slab:2:4", new String[] { "minecraft:brick_block:1:0", }));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_slab:2:5", new String[] { "minecraft:brick_block:1:0", }));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_slab:2:6", new String[] { "minecraft:nether_brick:1:0", }));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_slab:2:7", new String[] { "minecraft:quartz_block:1:0", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:stone_slab2:2:0", new String[] { "minecraft:red_sandstone:1:0", }));
		jsonRecipesList
				.add(new JsonRecipe("minecraft:purpur_slab:2:0", new String[] { "minecraft:purpur_block:1:0", }));
		/* Porte */
		jsonRecipesList.add(new JsonRecipe("minecraft:wooden_door:1:0", new String[] { 
				"minecraft:planks:2:0", 
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:spruce_door:1:0", new String[] { 
				"minecraft:planks:2:1", 
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:birch_door:1:0", new String[] { 
				"minecraft:planks:2:2", 
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:jungle_door:1:0", new String[] { 
				"minecraft:planks:2:3", 
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:acacia_door:1:0", new String[] { 
				"minecraft:planks:2:4", 
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:dark_oak_door:1:0", new String[] { 
				"minecraft:planks:2:5", 
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_door:1:0", new String[] { 
				"minecraft:iron_ingot:2:0", 
		}));
		/* Barreau de fer */
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_bars:1:0", new String[] { 
				Main.MODID+":iron_nugget:3:0", 
		}));
		/* Plaque de pression (+ pondérée) */
		jsonRecipesList.add(new JsonRecipe("minecraft:wooden_pressure_plate:1:0", new String[] { 
				"minecraft:planks:2:0", 
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_pressure_plate:1:0", new String[] { 
				"minecraft:stone:2:0", 
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:heavy_weighted_pressure_plate:1:0", new String[] { 
				"minecraft:iron_ingot:2:0", 
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:light_weighted_pressure_plate:1:0", new String[] { 
				"minecraft:gold_ingot:2:0", 
		}));
		/* Barrière */
		jsonRecipesList.add(new JsonRecipe("minecraft:fence:3:0", new String[] { 
				"minecraft:planks:4:0",
				"minecraft:stick:2:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:spruce_fence:3:0", new String[] { 
				"minecraft:planks:4:1",
				"minecraft:stick:2:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:birch_fence:3:0", new String[] { 
				"minecraft:planks:4:2",
				"minecraft:stick:2:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:jungle_fence:3:0", new String[] { 
				"minecraft:planks:4:3",
				"minecraft:stick:2:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:acacia_fence:3:0", new String[] { 
				"minecraft:planks:4:4",
				"minecraft:stick:2:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:dark_oak_fence:3:0", new String[] { 
				"minecraft:planks:4:5",
				"minecraft:stick:2:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:nether_brick_fence:1:0", new String[] { 
				"minecraft:nether_brick:1:0",
		}));
		/* Bâton */
		jsonRecipesList.add(new JsonRecipe("minecraft:stick:2:0", new String[] { 
				"minecraft:planks:1:0",
		}));
		/* Planche en bûche */
		for (int i = 0; i < 4; i++) {
			jsonRecipesList.add(new JsonRecipe("minecraft:planks:4:"+i, new String[] { 
					"minecraft:log:1:"+i,
			}));
		}
		jsonRecipesList.add(new JsonRecipe("minecraft:planks:4:4", new String[] { 
				"minecraft:log2:1:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:planks:4:5", new String[] { 
				"minecraft:log2:1:1",
		}));
		/* Trappe */
		jsonRecipesList.add(new JsonRecipe("minecraft:trapdoor:1:0", new String[] { 
				"minecraft:planks:3:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_trapdoor:1:0", new String[] { 
				"minecraft:iron_ingot:2:0",
		}));
		/* Tapis */
		for (int i = 0; i < 16; i++) {
			jsonRecipesList.add(new JsonRecipe("minecraft:carpet:3:"+i, new String[] { 
					"minecraft:wool:2:"+i,
			}));
		}
		/* Coffre */
		jsonRecipesList.add(new JsonRecipe("minecraft:chest:1:0", new String[] { 
				"minecraft:planks:8:0",
		}));
		/* Etabli */
		jsonRecipesList.add(new JsonRecipe("minecraft:crafting_table:1:0", new String[] { 
				"minecraft:planks:4:0",
		}));
		/* Fourneau */
		jsonRecipesList.add(new JsonRecipe("minecraft:furnace:1:0", new String[] { 
				"minecraft:cobblestone:8:0",
		}));
		/* Enclume */
		// TODO enclume endommagée
		jsonRecipesList.add(new JsonRecipe("minecraft:anvil:1:0", new String[] { 
				"minecraft:iron_ingot:31:0",
		}));
		/* Table d'enchantement */
		jsonRecipesList.add(new JsonRecipe("minecraft:enchanting_table:1:0", new String[] { 
				"minecraft:diamond:2:0",
				"minecraft:obsidian:4:0",
				"minecraft:book:1:0",
		}));
		/* Alambic */
		jsonRecipesList.add(new JsonRecipe("minecraft:brewing_stand:1:0", new String[] { 
				"minecraft:cobblestone:3:0",
				"minecraft:blaze_rod:1:0",
		}));
		/* Pancarte */
		jsonRecipesList.add(new JsonRecipe("minecraft:sign:3:0", new String[] { 
				"minecraft:planks:6:0",
				"minecraft:stick:1:0",
		}));
		/* Laine en Ficelle (plus de couleur) */
		for (int i = 0; i < 16; i++) {
			jsonRecipesList.add(new JsonRecipe("minecraft:wool:1:"+i, new String[] { 
					"minecraft:string:4:0",
			}));
		}
		/* Torche */
		jsonRecipesList.add(new JsonRecipe("minecraft:torch:4:0", new String[] { 
				"minecraft:coal:1:0",
				"minecraft:stick:1:0",
		}));
		/* Torche redstone */
		jsonRecipesList.add(new JsonRecipe("minecraft:redstone_torch:1:0", new String[] { 
				"minecraft:redstone:1:0",
				"minecraft:stick:1:0",
		}));
		/* Boussole */
		jsonRecipesList.add(new JsonRecipe("minecraft:compass:1:0", new String[] { 
				"minecraft:redstone:1:0",
				"minecraft:iron_ingot:4:0",
		}));
		/* Carte vierge */
		jsonRecipesList.add(new JsonRecipe("minecraft:map:1:0", new String[] { 
				"minecraft:compass:1:0",
				"minecraft:paper:8:0",
		}));
		/* Entonnoir */
		jsonRecipesList.add(new JsonRecipe("minecraft:hopper:1:0", new String[] { 
				"minecraft:iron_ingot:5:0",
				"minecraft:chest:1:0",
		}));
		/* Canne à pêche */
		jsonRecipesList.add(new JsonRecipe("minecraft:fishing_rod:1:0", new String[] { 
				"minecraft:string:2:0",
				"minecraft:stick:3:0",
		}));
		/* Carotte sur un bâton */
		jsonRecipesList.add(new JsonRecipe("minecraft:carrot_on_a_stick:1:0", new String[] { 
				"minecraft:carrot:1:0",
				"minecraft:fishing_rod:1:0",
		}));
		/* Seau vide */
		jsonRecipesList.add(new JsonRecipe("minecraft:bucket:1:0", new String[] { 
				"minecraft:iron_ingot:3:0",
		}));
		/* Chaudron */
		jsonRecipesList.add(new JsonRecipe("minecraft:cauldron:1:0", new String[] { 
				"minecraft:iron_ingot:7:0",
		}));
		/* Echelle */
		jsonRecipesList.add(new JsonRecipe("minecraft:ladder:3:0", new String[] { 
				"minecraft:stick:7:0",
		}));
		/* Montre */
		jsonRecipesList.add(new JsonRecipe("minecraft:clock:1:0", new String[] { 
				"minecraft:gold_ingot:4:0",
				"minecraft:redstone:1:0",
		}));
		/* Dropper */
		jsonRecipesList.add(new JsonRecipe("minecraft:dropper:1:0", new String[] { 
				"minecraft:cobblestone:7:0",
				"minecraft:redstone:1:0",
		}));
		/* Porte armure */
		jsonRecipesList.add(new JsonRecipe("minecraft:armor_stand:1:0", new String[] { 
				"minecraft:stick:8:0",
				"minecraft:stone_slab:1:0",
		}));
		/* Distributeur */
		jsonRecipesList.add(new JsonRecipe("minecraft:dispenser:1:0", new String[] { 
				"minecraft:cobblestone:7:0",
				"minecraft:redstone:1:0",
				"minecraft:bow:1:0",
		}));
		/* Lanterne aquatique */
		jsonRecipesList.add(new JsonRecipe("minecraft:sea_lantern:1:0", new String[] { 
				"minecraft:prismarine_crystals:5:0",
				"minecraft:prismarine_shard:4:0",
		}));
		/* Citrouille lanterne */
		jsonRecipesList.add(new JsonRecipe("minecraft:lit_pumpkin:1:0", new String[] { 
				"minecraft:pumpkin:1:0",
				"minecraft:torch:1:0",
		}));
		/* Lit */
		jsonRecipesList.add(new JsonRecipe("minecraft:bed:1:0", new String[] { 
				"minecraft:wool:3:0",
				"minecraft:planks:3:0",
		}));
		/* Boutons */
		jsonRecipesList.add(new JsonRecipe("minecraft:wooden_button:1:0", new String[] { 
				"minecraft:planks:1:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_button:1:0", new String[] { 
				"minecraft:stone:1:0",
		}));
		/* Bâteau */
		jsonRecipesList.add(new JsonRecipe("minecraft:boat:1:0", new String[] { 
				"minecraft:planks:5:0",
		}));
		/* Jukebox */
		jsonRecipesList.add(new JsonRecipe("minecraft:jukebox:1:0", new String[] { 
				"minecraft:planks:8:0",
				"minecraft:diamond:1:0",
		}));
		/* TNT */
		jsonRecipesList.add(new JsonRecipe("minecraft:tnt:1:0", new String[] { 
				"minecraft:sandstone:4:0",
				"minecraft:gunpowder:5:0",
		}));
		/* Bol */
		jsonRecipesList.add(new JsonRecipe("minecraft:bowl:1:0", new String[] { 
				"minecraft:planks:3:0",
		}));
		/* Tableau */
		jsonRecipesList.add(new JsonRecipe("minecraft:painting:1:0", new String[] { 
				"minecraft:stick:8:0",
				"minecraft:wool:1:0",
		}));
		/* Crochet */
		jsonRecipesList.add(new JsonRecipe("minecraft:tripwire_hook:2:0", new String[] {
				"minecraft:stick:1:0",
				"minecraft:planks:1:0",
				"minecraft:iron_ingot:1:0",
		}));
		/* Levier */
		jsonRecipesList.add(new JsonRecipe("minecraft:lever:1:0", new String[] {
				"minecraft:stick:1:0",
				"minecraft:cobblestone:1:0",
		}));
		/* Pistons */
		jsonRecipesList.add(new JsonRecipe("minecraft:sticky_piston:1:0", new String[] {
				"minecraft:slime_ball:1:0",
				"minecraft:piston:1:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:piston:1:0", new String[] {
				"minecraft:iron_ingot:1:0",
				"minecraft:cobblestone:4:0",
				"minecraft:redstone:1:0",
				"minecraft:planks:3:0",
		}));
		/* Rails */
		jsonRecipesList.add(new JsonRecipe("minecraft:rail:16:0", new String[] {
				"minecraft:iron_ingot:6:0",
				"minecraft:stick:1:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:detector_rail:6:0", new String[] {
				"minecraft:iron_ingot:6:0",
				"minecraft:redstone:1:0",
				"minecraft:stone_pressure_plate:1:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:golden_rail:6:0", new String[] {
				"minecraft:gold_ingot:6:0",
				"minecraft:redstone:1:0",
				"minecraft:stick:1:0",
		}));
		jsonRecipesList.add(new JsonRecipe("minecraft:activator_rail:6:0", new String[] {
				"minecraft:iron_ingot:6:0",
				"minecraft:redstone:1:0",
				"minecraft:stick:2:0",
		}));
		/* Pioche */
		jsonRecipesList.add(new JsonRecipe("minecraft:wooden_pickaxe:1:0", new String[] {
				"minecraft:planks:3:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_pickaxe:1:0", new String[] {
				"minecraft:cobblestone:3:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_pickaxe:1:0", new String[] {
				"minecraft:cobblestone:3:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:golden_pickaxe:1:0", new String[] {
				"minecraft:gold_ingot:3:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:diamond_pickaxe:1:0", new String[] {
				"minecraft:diamond:3:0",
				"minecraft:stick:2:0",
		}, true));
		/* Hache */
		jsonRecipesList.add(new JsonRecipe("minecraft:wooden_axe:1:0", new String[] {
				"minecraft:planks:3:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_axe:1:0", new String[] {
				"minecraft:cobblestone:3:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_axe:1:0", new String[] {
				"minecraft:cobblestone:3:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:golden_axe:1:0", new String[] {
				"minecraft:gold_ingot:3:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:diamond_axe:1:0", new String[] {
				"minecraft:diamond:3:0",
				"minecraft:stick:2:0",
		}, true));
		/* Pelle */
		jsonRecipesList.add(new JsonRecipe("minecraft:wooden_shovel:1:0", new String[] {
				"minecraft:planks:1:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_shovel:1:0", new String[] {
				"minecraft:cobblestone:1:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_shovel:1:0", new String[] {
				"minecraft:cobblestone:1:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:golden_shovel:1:0", new String[] {
				"minecraft:gold_ingot:1:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:diamond_shovel:1:0", new String[] {
				"minecraft:diamond:1:0",
				"minecraft:stick:2:0",
		}, true));
		/* Epee */
		jsonRecipesList.add(new JsonRecipe("minecraft:wooden_sword:1:0", new String[] {
				"minecraft:planks:2:0",
				"minecraft:stick:1:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_sword:1:0", new String[] {
				"minecraft:cobblestone:2:0",
				"minecraft:stick:1:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_sword:1:0", new String[] {
				"minecraft:cobblestone:2:0",
				"minecraft:stick:1:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:golden_sword:1:0", new String[] {
				"minecraft:gold_ingot:2:0",
				"minecraft:stick:1:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:diamond_sword:1:0", new String[] {
				"minecraft:diamond:2:0",
				"minecraft:stick:1:0",
		}, true));
		/* Houe */
		jsonRecipesList.add(new JsonRecipe("minecraft:wooden_hoe:1:0", new String[] {
				"minecraft:planks:2:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:stone_hoe:1:0", new String[] {
				"minecraft:cobblestone:2:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_hoe:1:0", new String[] {
				"minecraft:cobblestone:2:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:golden_hoe:1:0", new String[] {
				"minecraft:gold_ingot:2:0",
				"minecraft:stick:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:diamond_hoe:1:0", new String[] {
				"minecraft:diamond:2:0",
				"minecraft:stick:2:0",
		}, true));
		/* Arc */
		jsonRecipesList.add(new JsonRecipe("minecraft:bow:1:0", new String[] {
				"minecraft:string:3:0",
				"minecraft:stick:3:0",
		}, true));
		/* Flèche */
		jsonRecipesList.add(new JsonRecipe("minecraft:arrow:1:0", new String[] {
				"minecraft:feather:1:0",
				"minecraft:stick:1:0",
				"minecraft:flint:1:0",
		}));
		/* Armure */
		jsonRecipesList.add(new JsonRecipe("minecraft:leather_boots:1:0", new String[] {
				"minecraft:leather:4:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:leather_helmet:1:0", new String[] {
				"minecraft:leather:5:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:leather_chestplate:1:0", new String[] {
				"minecraft:leather:8:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:leather_leggings:1:0", new String[] {
				"minecraft:leather:7:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_boots:1:0", new String[] {
				"minecraft:iron_ingot:4:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_helmet:1:0", new String[] {
				"minecraft:iron_ingot:5:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_chestplate:1:0", new String[] {
				"minecraft:iron_ingot:8:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:iron_leggings:1:0", new String[] {
				"minecraft:iron_ingot:7:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:golden_boots:1:0", new String[] {
				"minecraft:golden_ingot:4:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:golden_helmet:1:0", new String[] {
				"minecraft:golden_ingot:5:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:golden_chestplate:1:0", new String[] {
				"minecraft:golden_ingot:8:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:golden_leggings:1:0", new String[] {
				"minecraft:golden_ingot:7:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:diamond_boots:1:0", new String[] {
				"minecraft:diamond:4:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:diamond_helmet:1:0", new String[] {
				"minecraft:diamond:5:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:diamond_chestplate:1:0", new String[] {
				"minecraft:diamond:8:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:diamond_leggings:1:0", new String[] {
				"minecraft:diamond:7:0",
		}, true));
		if (ConfigurationHandler.craftChainmail) {
		jsonRecipesList.add(new JsonRecipe("minecraft:chainmail_boots:1:0", new String[] {
				"minecraft:iron_ingot:2:0",
				"minecraft:iron_bars:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:chainmail_helmet:1:0", new String[] {
				"minecraft:iron_ingot:3:0",
				"minecraft:iron_bars:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:chainmail_chestplate:1:0", new String[] {
				"minecraft:iron_ingot:6:0",
				"minecraft:iron_bars:2:0",
		}, true));
		jsonRecipesList.add(new JsonRecipe("minecraft:chainmail_leggings:1:0", new String[] {
				"minecraft:iron_ingot:3:0",
				"minecraft:iron_bars:4:0",
		}, true));
		}
		/* Bouclier */
		jsonRecipesList.add(new JsonRecipe("minecraft:shield:1:0", new String[] {
				"minecraft:iron_ingot:1:0",
				"minecraft:planks:6:0",
		}, true));
		/* Recycleur */
		if (ConfigurationHandler.recyclerRecycled) {
			jsonRecipesList.add(new JsonRecipe(Main.MODID+":recycler:1:0", new String[] {
					"minecraft:cobblestone:6:0",
					"minecraft:iron_ingot:3:0",
			}));
		}
		return jsonRecipesList;
	}
	private RecyclingManager() {
	}

	public static void loadJsonRecipes(List<JsonRecipe> jsonRecipes) {
		for (int i = 0; i < jsonRecipes.size(); i++) {
			Recipe recipe = convertJsonRecipe(jsonRecipes.get(i));
			if (recipe.getItemRecipe() != null && recipe.getCount() > 0) {
				instance.recipes.add(recipe);
			} else {
				
				//TODO Remove
				MainUtil.println("Erreur.... Recette : "+i+"  Item : "+jsonRecipes.get(i).inputItem);
			}
		}
	}

	public static Recipe convertJsonRecipe(JsonRecipe jRecipe) {
		ItemStack inputItem = StringToItemStack(jRecipe.inputItem);
		Recipe recipe = new Recipe(inputItem);
		for (int i = 0; i < jRecipe.outputItems.length; i++) {
			recipe.addStack(StringToItemStack(jRecipe.outputItems[i]));
		}
		recipe.setCanBeRepaired(jRecipe.canBeRepaired);
		return recipe;
	}

	public static ItemStack StringToItemStack(String value) {
		String[] parts = value.split(":");
		if (parts.length == 4) {
			Item item = (Item) GameRegistry.findItem(parts[0], parts[1]);
			if (item != null) {
				return new ItemStack(item, Integer.valueOf(parts[2]), Integer.valueOf(parts[3]));
			}
		}
		return null;

	}
}
