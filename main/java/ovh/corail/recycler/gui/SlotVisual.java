package ovh.corail.recycler.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotVisual extends Slot {
	private int id;

	public SlotVisual(InventoryBasic visual, int id, int xPos, int yPos) {
		super(visual, id, xPos, yPos);
		this.id = id;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer playerIn) {
		return false;
	}
}
