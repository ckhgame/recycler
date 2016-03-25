package ovh.corail.recycler.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Recipe {
	private ItemStack itemRecipe;
	private boolean canBeRepaired=false;
	private List<ItemStack> itemsList=new ArrayList<ItemStack>();
	public Recipe(Item item, int count, int meta) {
		this.itemRecipe=new ItemStack(item, count, meta);
	}
	public Recipe(ItemStack itemStack) {
		this.itemRecipe=itemStack;
	}
	public Recipe(ItemStack stackIn, Object... recipeComponents) {
		this.itemRecipe=stackIn.copy();
		for (Object object : recipeComponents) {
			if (object instanceof ItemStack) {
				itemsList.add(((ItemStack) object).copy());
			} 
		}
	}
	public Recipe(ItemStack stackIn, ItemStack stackOut) {
		this.itemRecipe=stackIn.copy();
		itemsList.add(stackOut.copy());
	}
	public String getName() {
		return itemRecipe.getItem().getRegistryName();
	}
	public ItemStack getItemRecipe() {
		return itemRecipe;
	}
	public void setCanBeRepaired(boolean state) {
		canBeRepaired=state;
	}
	public boolean canBeRepaired() {
		return canBeRepaired;
	}
	public int getMeta() {
		return itemRecipe.getItemDamage();
	}
	public Integer getCount() {
		return itemsList.size();
	}
	public void addStack(Item item, int count, int meta) {
		itemsList.add(new ItemStack(item, count, meta));
	}
	public void addStack(Block block, int count, int meta) {
		addStack(Item.getItemFromBlock(block), count, meta);
	}
	public void addStack(ItemStack stack) {
		itemsList.add(stack);
	}
	public ItemStack getStack(int index) {
		return itemsList.get(index);
	}
	public void setStack(int index, ItemStack stack) {
		itemsList.set(index, stack);
	}
}
