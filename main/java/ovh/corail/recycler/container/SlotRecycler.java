package ovh.corail.recycler.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ovh.corail.recycler.tileentity.TileEntityRecycler;


public class SlotRecycler extends Slot {
	private TileEntityRecycler invent;
	private int id;

	public SlotRecycler(TileEntityRecycler inventory, int index, int xPos, int yPos) {
		super(inventory, index, xPos, yPos);
		this.id = index;
		this.invent = inventory;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return invent.isItemValidForSlot(id, stack);
	}

	@Override
	public void onSlotChange(ItemStack item1, ItemStack item2) {
		super.onSlotChange(item1, item2);
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return true;
	}
	
	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
	}
}
