package ovh.corail.recycler.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ovh.corail.recycler.core.Main;
import ovh.corail.recycler.handler.PacketHandler;
import ovh.corail.recycler.packet.ServerProgressMessage;
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
		this.addSlotToContainer(new SlotRecycler(inventory, 0, 27, 9));
		this.addSlotToContainer(new SlotRecycler(inventory, 1, 27, 27));
		for (int i = inventory.firstOutput; i <= 10; i++) {
			this.addSlotToContainer(new SlotRecycler(inventory, i, ((i - inventory.firstOutput) * 18) + 8, 54));
			this.addSlotToContainer(new SlotRecycler(inventory, i + 9, ((i - inventory.firstOutput) * 18) + 8, 72));
		}
		for (int i = 0 ; i < 3 ; i++) {
			for (int j = 0 ; j < 3 ; j++) {
				this.addSlotToContainer(new SlotVisual(inventory, inventory.visual, (i*3) + j, (j * 16) + 118, i*16 + 2));
			}
		}
		PacketHandler.INSTANCE.sendToServer(new VisualMessage(inventory.getPos()));
		inventory.refreshVisual(inventory.getStackInSlot(0));
		bindPlayerInventory(player.inventory);
	}

	@Override
	public ItemStack func_184996_a(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
		ItemStack stack = super.func_184996_a(slotId, dragType, clickType, player);
		if (slotId == 0) {
			PacketHandler.INSTANCE.sendToServer(new VisualMessage(inventory.getPos()));
			inventory.refreshVisual(inventory.getStackInSlot(0));
		}
		if ((slotId == 0 || slotId == 1) && inventory.isWorking()) {
			PacketHandler.INSTANCE.sendToServer(new ServerProgressMessage(inventory.getPos(), 0, inventory.isWorking(), true));
			//inventory.resetProgress();
		}
		return stack;
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		int i;
		int j;
		for (i = 0; i < 3; i++) {
			for (j = 0; j < 9; j++) {
				this.addSlotToContainer(new Slot(inventoryPlayer, ((i + 1) * 9) + j, (j * 18) + 8, (i * 18) + 106));
			}
		}
		for (j = 0; j < 9; j++) {
			this.addSlotToContainer(new Slot(inventoryPlayer, j, (j * 18) +8, 160));
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
	}
}
