package ovh.corail.recycler.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ovh.corail.recycler.handler.PacketHandler;
import ovh.corail.recycler.packet.VisualMessage;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class ContainerRecycler extends Container {
	public int i = 0;
	public int j = 0;
	public int k = 0;
	public TileEntityRecycler inventory;
	public IInventory visual;

	public ContainerRecycler(EntityPlayer player, World world, int x, int y, int z, TileEntityRecycler inventory) {
		this.inventory = inventory;
		this.visual = inventory.visual;
		this.i = x;
		this.j = y;
		this.k = z;
		this.addSlotToContainer(new SlotRecycler(inventory, 0, 9, 6));
		this.addSlotToContainer(new SlotRecycler(inventory, 1, 9, 24));
		for (int i = inventory.firstOutput; i <= 10; i++) {
			this.addSlotToContainer(new SlotRecycler(inventory, i, 9 + (i - 2) * 18, 61));
			this.addSlotToContainer(new SlotRecycler(inventory, i + 9, 9 + (i - 2) * 18, 43));
		}
		for (int i = 0; i < 3; i++) {
			this.addSlotToContainer(new SlotVisual(inventory, inventory.visual, i, 117 + (i * 18), 5));
			this.addSlotToContainer(new SlotVisual(inventory, inventory.visual, i + 3, 117 + (i * 18), 23));
		}
		PacketHandler.INSTANCE.sendToServer(
				new VisualMessage(inventory.getPos().getX(), inventory.getPos().getY(), inventory.getPos().getZ()));
		inventory.refreshVisual(inventory.getStackInSlot(0));
		bindPlayerInventory(player.inventory);
	}

	@Override
	public ItemStack func_184996_a(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
		ItemStack stack = super.func_184996_a(slotId, dragType, clickType, player);
		if (slotId == 0) {
			PacketHandler.INSTANCE.sendToServer(
					new VisualMessage(inventory.getPos().getX(), inventory.getPos().getY(), inventory.getPos().getZ()));
			inventory.refreshVisual(inventory.getStackInSlot(0));
		}
		return stack;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		int i;
		int j;
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
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
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

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		/*
		 * if (!this.world.isRemote) for (int i = 0; i <
		 * content.getSizeInventory(); ++i) { ItemStack itemstack =
		 * content.getStackInSlot(i); if (itemstack != null)
		 * player.dropPlayerItemWithRandomChoice(itemstack, false); }
		 */

	}
}
