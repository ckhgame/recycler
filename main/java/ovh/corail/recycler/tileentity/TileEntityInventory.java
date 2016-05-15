package ovh.corail.recycler.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import ovh.corail.recycler.core.Main;

public class TileEntityInventory extends TileEntity implements ISidedInventory {
	public final int count = 20;
	public int firstOutput = 2;
	protected ItemStack[] inventory;

	public TileEntityInventory() {
		super();
		this.inventory = new ItemStack[count];
	}
	
	public int getEmptySlot() {
		for (int i = firstOutput; i < getSizeInventory(); i++) {
			ItemStack item = getStackInSlot(i);
			if (item == null) {
				return i;
			}
		}
		return -1;
	}
	
	public int hasEmptySlot() {
		int count = 0;
		for (int i = firstOutput; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) == null) {
				count++;
			}
		}
		return count;
	}
	
	@Override
	public int getSizeInventory() {
		return count;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if (index < 0 || index >= this.getSizeInventory()) {
			return null;
		} else {
			return this.inventory[index];
		}
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = getStackInSlot(index);
		if (stack != null) {
			if (stack.stackSize <= count) {
				setInventorySlotContents(index, null);
				this.markDirty();
			} else {
				stack = stack.splitStack(count);
				if (stack.stackSize == 0) {
					setInventorySlotContents(index, null);
					this.markDirty();
				}
			}
		}
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack stack;
		if (index >= 0 && index < count) {
			stack = inventory[index];
		} else {
			stack = null;
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		if (index >= 0 && index < count) {
			inventory[index] = stack;
		} else {
			inventory[index] = null;
		}
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		super.markDirty();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		pos = this.getPos();
		return worldObj.getTileEntity(pos) == this && player.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			this.setInventorySlotContents(i, null);
		}
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		switch (side) {
		/** insert */
		case SOUTH:
			int[] slotsInsert = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
			return slotsInsert;
		/** extract */
		default: 
			int[] slotsExtract = {0, 1};
			return slotsExtract;
		}
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		//Helper.addChatMessage("Index : "+index + "Direction : "+direction.name(), Minecraft.getMinecraft().thePlayer, false);
		return direction == EnumFacing.SOUTH && index > 1 && index < count;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
		return direction != EnumFacing.SOUTH && ((index == 1 && stack.getItem() == Main.diamond_disk) || index == 0);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inventory.length; i++) {
			ItemStack stack = inventory[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		compound.setTag("inventory", itemList);
		super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList tagList = compound.getTagList("inventory", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inventory.length) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
	}
}
