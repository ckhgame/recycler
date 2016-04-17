package ovh.corail.recycler.recycling;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import ovh.corail.recycler.core.Main;
import ovh.corail.recycler.handler.ConfigurationHandler;

public class RecyclingManager {
	private static final RecyclingManager instance = new RecyclingManager();
	private List<RecyclingRecipe> recipes = new ArrayList<RecyclingRecipe>();

	public static RecyclingManager getInstance() {
		return instance;
	}

	public int getRecipesCount() {
		return recipes.size();
	}

	public RecyclingRecipe getRecipe(int index) {
		if (index >= 0 && index < recipes.size()) {
			return recipes.get(index);
		}
		return null;
	}

	private void addRecipe(RecyclingRecipe recipe) {
		recipes.add(recipe);
	}

	/** add recipe of many stacks result */
	public void addRecipe(ItemStack stack, Object... recipeComponents) {
		addRecipe(stack, false, recipeComponents);
	}

	/** add recipe of many stacks result with canBeRepaired param */
	public void addRecipe(ItemStack stack, boolean canBeRepaired, Object... recipeComponents) {
		RecyclingRecipe recipe = new RecyclingRecipe(stack, recipeComponents);
		recipe.setCanBeRepaired(canBeRepaired);
		recipes.add(recipe);
	}

	/** add recipe of one stack result with canBeRepaired param */
	public void addRecipe(ItemStack stackIn, boolean canBeRepaired, ItemStack stackOut) {
		RecyclingRecipe recipe = new RecyclingRecipe(stackIn, stackOut);
		recipe.setCanBeRepaired(canBeRepaired);
		recipes.add(recipe);
	}

	/** add recipe of one stack result */
	public void addRecipe(ItemStack stackIn, ItemStack stackOut) {
		addRecipe(stackIn, false, stackOut);
	}

	/** check if stack has a recipe */
	public int hasRecipe(ItemStack stack) {
		if (stack == null || stack.stackSize <= 0) {
			return -1;
		}
		ItemStack testStack = stack.copy(); /** For damaged items */
		testStack.setItemDamage(0);
		for (int recipe_num = 0; recipe_num < getRecipesCount(); recipe_num++) {
			/** same item */
			if (testStack.getItem()==recipes.get(recipe_num).getItemRecipe().getItem()) {
				return recipe_num;
			}
		}
		return -1;
	}

	/** calcul the result for nb_input of the recycled stack */
	public List<ItemStack> getResultStack(ItemStack stack, int nb_input) {
		return getResultStack(stack, nb_input, false);
	}

	/** calcul the result for nb_input of the recycled stack with isGrind */
	public List<ItemStack> getResultStack(ItemStack stack, int nb_input, boolean isGrind) {
		List<ItemStack> itemsList = new ArrayList<ItemStack>();
		/** return an empty list if stack has no recipe */
		int num_recipe = hasRecipe(stack);
		if (num_recipe < 0) {
			return itemsList;
		}
		/** current recipe */
		RecyclingRecipe currentRecipe = recipes.get(num_recipe);
		/** for each stack result */
		for (int i = 0; i < currentRecipe.getCount(); i++) {
			ItemStack currentStack = currentRecipe.getStack(i);
			/** for damageable items that are actually damaged */
			if (currentRecipe.canBeRepaired() && stack.getItemDamage() > 0) {
				Item item = currentStack.getItem();
				int currentSize = currentStack.stackSize;
				/** percent of damage of the item */
				int maxDamage = currentRecipe.getItemRecipe().getMaxDamage();
				float pourcent = (float) (maxDamage - (stack.getItemDamage())) / maxDamage;
				/** replace by smaller units */
				/** TODO check if any bug when result of currentSize * X is greater than 64 */
				if (item == Items.iron_ingot) {
					currentStack = new ItemStack(Main.iron_nugget, (int) Math.floor(currentSize * 9 * pourcent), 0);
				} else if (item == Items.gold_ingot) {
					currentStack = new ItemStack(Items.gold_nugget, (int) Math.floor(currentSize * 9 * pourcent), 0);
				} else if (item == Items.diamond) {
					currentStack = new ItemStack(Main.diamond_nugget, (int) Math.floor(currentSize * 9 * pourcent), 0);
				} else if (item == Items.leather) {
					currentStack = new ItemStack(Items.rabbit_hide, (int) Math.floor(currentSize * 4 * pourcent), 0);
				}
			}
			
			/** multiply by nb_input */
			int newStackCount = currentStack.stackSize * nb_input;
			
			/** number of required slots */
			int slotCount = (int) Math.floor(newStackCount / currentStack.getMaxStackSize());
			ItemStack fullStack, lastStack;
			/** full stack */
			fullStack = currentStack.copy();
			fullStack.stackSize = fullStack.getMaxStackSize();
			for (int j = 0; j < slotCount; j++) {
				fullStack = currentStack.copy();
				fullStack.stackSize = fullStack.getMaxStackSize();
				itemsList.add(fullStack);
			}
			/** last stack */
			int resteStackCount = newStackCount - (slotCount * currentStack.getMaxStackSize());
			if (resteStackCount > 0) {
				lastStack = currentStack.copy();
				lastStack.stackSize = resteStackCount;
				itemsList.add(lastStack);
			}

		}
		/** TODO a way to multi recycle that need to be improved */
		if (isGrind) {
			itemsList = this.getResultList(itemsList);
			itemsList = this.getResultList(itemsList);
			itemsList = this.getResultList(itemsList);
		}
		return itemsList;
	}

	/** TODO multi recycle */
	public List<ItemStack> getResultList(List<ItemStack> itemsList) {
		List<ItemStack> newItemsList = new ArrayList<ItemStack>();
		/** for each result stack */
		for (int i = 0; i < itemsList.size(); i++) {
			ItemStack currentStack = itemsList.get(i);
			/** stack has no recipe */
			int numRecipe = hasRecipe(currentStack);
			if (numRecipe < 0) {
				newItemsList.add(itemsList.get(i).copy());
				continue;
			}
			/** if not enough stacksize for a recipe */
			RecyclingRecipe currentRecipe = recipes.get(numRecipe);
			if (currentStack.stackSize < currentRecipe.getItemRecipe().stackSize) {
				newItemsList.add(currentStack.copy());
				continue;
			}
			/** nb_input for this recipe */
			int nb_input = (int) Math.floor(currentStack.stackSize / currentRecipe.getItemRecipe().stackSize);
			/** calcul result */
			List<ItemStack> itemsList2 = getResultStack(currentStack, nb_input);
			/** add stack result to the total result */
			for (int j = 0; j < itemsList2.size(); j++) {
				if (itemsList2.get(j) != null) {
					newItemsList.add(itemsList2.get(j).copy());
				}
			}
		}
		return newItemsList;
	}

	public static List<JsonRecyclingRecipe> getJsonRecyclingRecipes() {
		/** create a json array of recycling recipes */
		List<JsonRecyclingRecipe> jsonRecipesList = new ArrayList<JsonRecyclingRecipe>();
		/* Roche en pierre */
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:stone:1:0", new String[] { "minecraft:cobblestone:1:0", }));
		/* Pierre moussue en Pierre */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:mossy_cobblestone:1:0",
				new String[] { "minecraft:cobblestone:1:0", "minecraft:vine:1:0", }));
		/* Pierre taillée en pierre */
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:stonebrick:1:0", new String[] { "minecraft:cobblestone:1:0", }));
		/* Pierre craquelée, moussue, sculptée en pierre taillée */
		for (int i = 1; i <= 3; i++) {
			jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:stonebrick:1:" + i,
					new String[] { "minecraft:stonebrick:1:0", (i == 1 ? "minecraft:vine:1:0" : "") // Moussue
			}));
		}
		/* Grès en sable */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:sandstone:1:0", new String[] { "minecraft:sand:4:0", }));
		/* Quartz rayé/sculpté */
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:quartz_block:1:1", new String[] { "minecraft:quartz_block:1:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:quartz_block:1:2", new String[] { "minecraft:quartz_block:1:0", }));
		/* Purpur sculpté */
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:purpur_pillar:1:0", new String[] { "minecraft:purpur_block:1:0", }));
		/* Granite/diorite/andésite poli */
		for (int i = 1; i <= 3; i++) {
			jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:stone:1:" + (2 * i),
					new String[] { "minecraft:stone:1:" + (2 * i - 1), }));
		}
		/* Grès poli/sculpté */
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:sandstone:1:1", new String[] { "minecraft:sandstone:1:0", }));
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:sandstone:1:2", new String[] { "minecraft:sandstone:1:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:red_sandstone:1:1",
				new String[] { "minecraft:red_sandstone:1:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:red_sandstone:1:2",
				new String[] { "minecraft:red_sandstone:1:0", }));
		/* Escalier en dalle */
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:oak_stairs:1:0", new String[] { "minecraft:wooden_slab:3:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:spruce_stairs:1:0", new String[] { "minecraft:wooden_slab:3:1", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:birch_stairs:1:0", new String[] { "minecraft:wooden_slab:3:2", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:jungle_stairs:1:0", new String[] { "minecraft:wooden_slab:3:3", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:acacia_stairs:1:0", new String[] { "minecraft:wooden_slab:3:4", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:dark_oak_stairs:1:0",
				new String[] { "minecraft:wooden_slab:3:5", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:sandstone_stairs:1:0",
				new String[] { "minecraft:stone_slab:3:1", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:stone_stairs:1:0", new String[] { "minecraft:stone_slab:3:3", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:brick_stairs:1:0", new String[] { "minecraft:stone_slab:3:4", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:stone_brick_stairs:1:0",
				new String[] { "minecraft:stone_slab:3:5", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:nether_brick_stairs:1:0",
				new String[] { "minecraft:stone_slab:3:6", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:quartz_stairs:1:0", new String[] { "minecraft:stone_slab:3:7", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:red_sandstone_stairs:1:0",
				new String[] { "minecraft:stone_slab2:3:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:purpur_stairs:1:0", new String[] { "minecraft:purpur_slab:3:0", }));
		/* Dalle en bloc */
		for (int i = 0; i <= 5; i++) {
			jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:wooden_slab:2:" + i,
					new String[] { "minecraft:planks:1:" + i, }));
		}
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:stone_slab:2:0", new String[] { "minecraft:stone:1:0", }));
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:stone_slab:2:1", new String[] { "minecraft:sandstone:1:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:stone_slab:2:3", new String[] { "minecraft:cobblestone:1:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:stone_slab:2:4", new String[] { "minecraft:brick_block:1:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:stone_slab:2:5", new String[] { "minecraft:brick_block:1:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:stone_slab:2:6", new String[] { "minecraft:nether_brick:1:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:stone_slab:2:7", new String[] { "minecraft:quartz_block:1:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:stone_slab2:2:0", new String[] { "minecraft:red_sandstone:1:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:purpur_slab:2:0", new String[] { "minecraft:purpur_block:1:0", }));
		/* Porte */
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:wooden_door:1:0", new String[] { "minecraft:planks:2:0", }));
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:spruce_door:1:0", new String[] { "minecraft:planks:2:1", }));
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:birch_door:1:0", new String[] { "minecraft:planks:2:2", }));
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:jungle_door:1:0", new String[] { "minecraft:planks:2:3", }));
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:acacia_door:1:0", new String[] { "minecraft:planks:2:4", }));
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:dark_oak_door:1:0", new String[] { "minecraft:planks:2:5", }));
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:iron_door:1:0", new String[] { "minecraft:iron_ingot:2:0", }));
		/* Barreau de fer */
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:iron_bars:1:0", new String[] { Main.MOD_ID + ":iron_nugget:3:0", }));
		/* Plaque de pression (+ pondérée) */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:wooden_pressure_plate:1:0",
				new String[] { "minecraft:planks:2:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:stone_pressure_plate:1:0", new String[] { "minecraft:stone:2:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:heavy_weighted_pressure_plate:1:0",
				new String[] { "minecraft:iron_ingot:2:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:light_weighted_pressure_plate:1:0",
				new String[] { "minecraft:gold_ingot:2:0", }));
		/* Barrière */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:fence:3:0",
				new String[] { "minecraft:planks:4:0", "minecraft:stick:2:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:spruce_fence:3:0",
				new String[] { "minecraft:planks:4:1", "minecraft:stick:2:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:birch_fence:3:0",
				new String[] { "minecraft:planks:4:2", "minecraft:stick:2:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:jungle_fence:3:0",
				new String[] { "minecraft:planks:4:3", "minecraft:stick:2:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:acacia_fence:3:0",
				new String[] { "minecraft:planks:4:4", "minecraft:stick:2:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:dark_oak_fence:3:0",
				new String[] { "minecraft:planks:4:5", "minecraft:stick:2:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:nether_brick_fence:1:0",
				new String[] { "minecraft:nether_brick:1:0", }));
		/* Bâton */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:stick:2:0", new String[] { "minecraft:planks:1:0", }));
		/* Planche en bûche */
		for (int i = 0; i < 4; i++) {
			jsonRecipesList
					.add(new JsonRecyclingRecipe("minecraft:planks:4:" + i, new String[] { "minecraft:log:1:" + i, }));
		}
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:planks:4:4", new String[] { "minecraft:log2:1:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:planks:4:5", new String[] { "minecraft:log2:1:1", }));
		/* Trappe */
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:trapdoor:1:0", new String[] { "minecraft:planks:3:0", }));
		jsonRecipesList.add(
				new JsonRecyclingRecipe("minecraft:iron_trapdoor:1:0", new String[] { "minecraft:iron_ingot:2:0", }));
		/* Tapis */
		for (int i = 0; i < 16; i++) {
			jsonRecipesList
					.add(new JsonRecyclingRecipe("minecraft:carpet:3:" + i, new String[] { "minecraft:wool:2:" + i, }));
		}
		/* Coffre */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:chest:1:0", new String[] { "minecraft:planks:8:0", }));
		/* Etabli */
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:crafting_table:1:0", new String[] { "minecraft:planks:4:0", }));
		/* Fourneau */
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:furnace:1:0", new String[] { "minecraft:cobblestone:8:0", }));
		/* Enclume */
		/** TODO damaged anvil */
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:anvil:1:0", new String[] { "minecraft:iron_ingot:31:0", }));
		/* Table d'enchantement */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:enchanting_table:1:0",
				new String[] { "minecraft:diamond:2:0", "minecraft:obsidian:4:0", "minecraft:book:1:0", }));
		/* Alambic */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:brewing_stand:1:0",
				new String[] { "minecraft:cobblestone:3:0", "minecraft:blaze_rod:1:0", }));
		/* Pancarte */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:sign:3:0",
				new String[] { "minecraft:planks:6:0", "minecraft:stick:1:0", }));
		/* Laine en Ficelle (plus de couleur) */
		for (int i = 0; i < 16; i++) {
			jsonRecipesList
					.add(new JsonRecyclingRecipe("minecraft:wool:1:" + i, new String[] { "minecraft:string:4:0", }));
		}
		/* Torche */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:torch:4:0",
				new String[] { "minecraft:coal:1:0", "minecraft:stick:1:0", }));
		/* Torche redstone */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:redstone_torch:1:0",
				new String[] { "minecraft:redstone:1:0", "minecraft:stick:1:0", }));
		/* Boussole */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:compass:1:0",
				new String[] { "minecraft:redstone:1:0", "minecraft:iron_ingot:4:0", }));
		/* Carte vierge */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:map:1:0",
				new String[] { "minecraft:compass:1:0", "minecraft:paper:8:0", }));
		/* Entonnoir */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:hopper:1:0",
				new String[] { "minecraft:iron_ingot:5:0", "minecraft:chest:1:0", }));
		/* Canne à pêche */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:fishing_rod:1:0",
				new String[] { "minecraft:string:2:0", "minecraft:stick:3:0", }));
		/* Carotte sur un bâton */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:carrot_on_a_stick:1:0",
				new String[] { "minecraft:carrot:1:0", "minecraft:fishing_rod:1:0", }));
		/* Seau vide */
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:bucket:1:0", new String[] { "minecraft:iron_ingot:3:0", }));
		/* Chaudron */
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:cauldron:1:0", new String[] { "minecraft:iron_ingot:7:0", }));
		/* Echelle */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:ladder:3:0", new String[] { "minecraft:stick:7:0", }));
		/* Montre */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:clock:1:0",
				new String[] { "minecraft:gold_ingot:4:0", "minecraft:redstone:1:0", }));
		/* Dropper */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:dropper:1:0",
				new String[] { "minecraft:cobblestone:7:0", "minecraft:redstone:1:0", }));
		/* Porte armure */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:armor_stand:1:0",
				new String[] { "minecraft:stick:8:0", "minecraft:stone_slab:1:0", }));
		/* Distributeur */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:dispenser:1:0",
				new String[] { "minecraft:cobblestone:7:0", "minecraft:redstone:1:0", "minecraft:bow:1:0", }));
		/* Lanterne aquatique */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:sea_lantern:1:0",
				new String[] { "minecraft:prismarine_crystals:5:0", "minecraft:prismarine_shard:4:0", }));
		/* Citrouille lanterne */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:lit_pumpkin:1:0",
				new String[] { "minecraft:pumpkin:1:0", "minecraft:torch:1:0", }));
		/* Lit */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:bed:1:0",
				new String[] { "minecraft:wool:3:0", "minecraft:planks:3:0", }));
		/* Boutons */
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:wooden_button:1:0", new String[] { "minecraft:planks:1:0", }));
		jsonRecipesList
				.add(new JsonRecyclingRecipe("minecraft:stone_button:1:0", new String[] { "minecraft:stone:1:0", }));
		/* Bâteau */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:boat:1:0", new String[] { "minecraft:planks:5:0", }));
		/* Jukebox */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:jukebox:1:0",
				new String[] { "minecraft:planks:8:0", "minecraft:diamond:1:0", }));
		/* TNT */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:tnt:1:0",
				new String[] { "minecraft:sandstone:4:0", "minecraft:gunpowder:5:0", }));
		/* Bol */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:bowl:1:0", new String[] { "minecraft:planks:3:0", }));
		/* Tableau */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:painting:1:0",
				new String[] { "minecraft:stick:8:0", "minecraft:wool:1:0", }));
		/* Crochet */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:tripwire_hook:2:0",
				new String[] { "minecraft:stick:1:0", "minecraft:planks:1:0", "minecraft:iron_ingot:1:0", }));
		/* Levier */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:lever:1:0",
				new String[] { "minecraft:stick:1:0", "minecraft:cobblestone:1:0", }));
		/* Pistons */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:sticky_piston:1:0",
				new String[] { "minecraft:slime_ball:1:0", "minecraft:piston:1:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:piston:1:0", new String[] { "minecraft:iron_ingot:1:0",
				"minecraft:cobblestone:4:0", "minecraft:redstone:1:0", "minecraft:planks:3:0", }));
		/* Rails */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:rail:16:0",
				new String[] { "minecraft:iron_ingot:6:0", "minecraft:stick:1:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:detector_rail:6:0", new String[] {
				"minecraft:iron_ingot:6:0", "minecraft:redstone:1:0", "minecraft:stone_pressure_plate:1:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:golden_rail:6:0",
				new String[] { "minecraft:gold_ingot:6:0", "minecraft:redstone:1:0", "minecraft:stick:1:0", }));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:activator_rail:6:0",
				new String[] { "minecraft:iron_ingot:6:0", "minecraft:redstone:1:0", "minecraft:stick:2:0", }));
		/* Pioche */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:wooden_pickaxe:1:0",
				new String[] { "minecraft:planks:3:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:stone_pickaxe:1:0",
				new String[] { "minecraft:cobblestone:3:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:iron_pickaxe:1:0",
				new String[] { "minecraft:cobblestone:3:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:golden_pickaxe:1:0",
				new String[] { "minecraft:gold_ingot:3:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:diamond_pickaxe:1:0",
				new String[] { "minecraft:diamond:3:0", "minecraft:stick:2:0", }, true));
		/* Hache */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:wooden_axe:1:0",
				new String[] { "minecraft:planks:3:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:stone_axe:1:0",
				new String[] { "minecraft:cobblestone:3:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:iron_axe:1:0",
				new String[] { "minecraft:cobblestone:3:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:golden_axe:1:0",
				new String[] { "minecraft:gold_ingot:3:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:diamond_axe:1:0",
				new String[] { "minecraft:diamond:3:0", "minecraft:stick:2:0", }, true));
		/* Pelle */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:wooden_shovel:1:0",
				new String[] { "minecraft:planks:1:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:stone_shovel:1:0",
				new String[] { "minecraft:cobblestone:1:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:iron_shovel:1:0",
				new String[] { "minecraft:cobblestone:1:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:golden_shovel:1:0",
				new String[] { "minecraft:gold_ingot:1:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:diamond_shovel:1:0",
				new String[] { "minecraft:diamond:1:0", "minecraft:stick:2:0", }, true));
		/* Epee */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:wooden_sword:1:0",
				new String[] { "minecraft:planks:2:0", "minecraft:stick:1:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:stone_sword:1:0",
				new String[] { "minecraft:cobblestone:2:0", "minecraft:stick:1:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:iron_sword:1:0",
				new String[] { "minecraft:cobblestone:2:0", "minecraft:stick:1:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:golden_sword:1:0",
				new String[] { "minecraft:gold_ingot:2:0", "minecraft:stick:1:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:diamond_sword:1:0",
				new String[] { "minecraft:diamond:2:0", "minecraft:stick:1:0", }, true));
		/* Houe */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:wooden_hoe:1:0",
				new String[] { "minecraft:planks:2:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:stone_hoe:1:0",
				new String[] { "minecraft:cobblestone:2:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:iron_hoe:1:0",
				new String[] { "minecraft:cobblestone:2:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:golden_hoe:1:0",
				new String[] { "minecraft:gold_ingot:2:0", "minecraft:stick:2:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:diamond_hoe:1:0",
				new String[] { "minecraft:diamond:2:0", "minecraft:stick:2:0", }, true));
		/* Arc */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:bow:1:0",
				new String[] { "minecraft:string:3:0", "minecraft:stick:3:0", }, true));
		/* Flèche */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:arrow:1:0",
				new String[] { "minecraft:feather:1:0", "minecraft:stick:1:0", "minecraft:flint:1:0", }));
		/* Armure */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:leather_boots:1:0",
				new String[] { "minecraft:leather:4:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:leather_helmet:1:0",
				new String[] { "minecraft:leather:5:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:leather_chestplate:1:0",
				new String[] { "minecraft:leather:8:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:leather_leggings:1:0",
				new String[] { "minecraft:leather:7:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:iron_boots:1:0",
				new String[] { "minecraft:iron_ingot:4:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:iron_helmet:1:0",
				new String[] { "minecraft:iron_ingot:5:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:iron_chestplate:1:0",
				new String[] { "minecraft:iron_ingot:8:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:iron_leggings:1:0",
				new String[] { "minecraft:iron_ingot:7:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:golden_boots:1:0",
				new String[] { "minecraft:golden_ingot:4:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:golden_helmet:1:0",
				new String[] { "minecraft:golden_ingot:5:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:golden_chestplate:1:0",
				new String[] { "minecraft:golden_ingot:8:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:golden_leggings:1:0",
				new String[] { "minecraft:golden_ingot:7:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:diamond_boots:1:0",
				new String[] { "minecraft:diamond:4:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:diamond_helmet:1:0",
				new String[] { "minecraft:diamond:5:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:diamond_chestplate:1:0",
				new String[] { "minecraft:diamond:8:0", }, true));
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:diamond_leggings:1:0",
				new String[] { "minecraft:diamond:7:0", }, true));
		if (ConfigurationHandler.craftChainmail) {
			jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:chainmail_boots:1:0",
					new String[] { "minecraft:iron_ingot:2:0", "minecraft:iron_bars:2:0", }, true));
			jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:chainmail_helmet:1:0",
					new String[] { "minecraft:iron_ingot:3:0", "minecraft:iron_bars:2:0", }, true));
			jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:chainmail_chestplate:1:0",
					new String[] { "minecraft:iron_ingot:6:0", "minecraft:iron_bars:2:0", }, true));
			jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:chainmail_leggings:1:0",
					new String[] { "minecraft:iron_ingot:3:0", "minecraft:iron_bars:4:0", }, true));
		}
		/* Bouclier */
		jsonRecipesList.add(new JsonRecyclingRecipe("minecraft:shield:1:0",
				new String[] { "minecraft:iron_ingot:1:0", "minecraft:planks:6:0", }, true));
		/* Recycleur */
		if (ConfigurationHandler.recyclerRecycled) {
			jsonRecipesList.add(new JsonRecyclingRecipe(Main.MOD_ID + ":recycler:1:0",
					new String[] { "minecraft:cobblestone:6:0", "minecraft:iron_ingot:3:0", }));
		}
		return jsonRecipesList;
	}

	private RecyclingManager() {
	}

	public static void loadJsonRecipes(List<JsonRecyclingRecipe> jsonRecipes) {
		for (int i = 0; i < jsonRecipes.size(); i++) {
			RecyclingRecipe recipe = convertJsonRecipe(jsonRecipes.get(i));
			if (recipe.getItemRecipe() != null && recipe.getCount() > 0) {
				instance.recipes.add(recipe);
			}
		}
	}

	public static RecyclingRecipe convertJsonRecipe(JsonRecyclingRecipe jRecipe) {
		ItemStack inputItem = StringToItemStack(jRecipe.inputItem);
		RecyclingRecipe recipe = new RecyclingRecipe(inputItem);
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
