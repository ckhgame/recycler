package ovh.corail.recycler.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ovh.corail.recycler.common.tileentity.RecyclerTile;

public class SlotVisual extends Slot {
	private int id;
	public RecyclerTile invent;

	public SlotVisual(RecyclerTile inventory, InventoryBasic visual, int index, int xPos, int yPos) {
		super(visual, index, xPos, yPos);
		this.id = index;
		this.invent=inventory;
	}
	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}
	@Override
	public void onSlotChange(ItemStack item1, ItemStack item2) {
		super.onSlotChange(item1, item2);
	}
	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
		super.onPickupFromSlot(player, stack);
		
	}
	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
    }
}
