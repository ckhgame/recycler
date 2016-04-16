package ovh.corail.recycler.gui;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class SlotRecycler extends Slot {
	private TileEntityRecycler inventory;
	private int id;

	public SlotRecycler(TileEntityRecycler inventory, int id, int xPos, int yPos) {
		super(inventory, id, xPos, yPos);
		this.inventory=inventory;
		this.id = id;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return inventory.isItemValidForSlot(id, stack);
	}

	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return true;
	}

}
