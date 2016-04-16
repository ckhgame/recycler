package ovh.corail.recycler.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ovh.corail.recycler.handler.PacketHandler;
import ovh.corail.recycler.packets.VisualMessage;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class ContainerRecycler extends Container {
	public BlockPos currentPos;
	public TileEntityRecycler inventory;
	public IInventory visual;

	public ContainerRecycler(EntityPlayer playerIn, World worldIn, int x, int y, int z, TileEntityRecycler inventory) {
		this.inventory = inventory;
		this.visual = inventory.visual;
		this.currentPos = new BlockPos(x, y, z);
		this.addSlotToContainer(new SlotRecycler(inventory, 0, 9, 6));
		this.addSlotToContainer(new SlotRecycler(inventory, 1, 9, 24));
		for (int i = inventory.firstOutput; i <= 10; i++) {
			this.addSlotToContainer(new SlotRecycler(inventory, i, 9 + (i - 2) * 18, 61));
			this.addSlotToContainer(new SlotRecycler(inventory, i + 9, 9 + (i - 2) * 18, 43));
		}
		for (int i = 0; i < 3; i++) {
			this.addSlotToContainer(new SlotVisual(inventory.visual, i, 117 + (i * 18), 5));
			this.addSlotToContainer(new SlotVisual(inventory.visual, i + 3, 117 + (i * 18), 23));
		}
		/** TODO VISUAL MESSAGE */
		PacketHandler.INSTANCE.sendToServer(new VisualMessage(inventory.getPos()));
		inventory.refreshVisual(inventory.getStackInSlot(0));
		bindPlayerInventory(playerIn.inventory);
	}

	@Override
	public ItemStack func_184996_a(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
		ItemStack stack = super.func_184996_a(slotId, dragType, clickType, player);
		if (slotId == 0) {
			/** TODO VISUAL MESSAGE */
			PacketHandler.INSTANCE.sendToServer(new VisualMessage(inventory.getPos()));
			inventory.refreshVisual(inventory.getStackInSlot(0));
		}
		return stack;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		int i, j;
		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inventoryPlayer, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return inventory.isUseableByPlayer(playerIn);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < 9) {
				if (!this.mergeItemStack(itemstack1, 10, 45, true)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(playerIn, itemstack1);
		}

		return itemstack;
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
	}
}
