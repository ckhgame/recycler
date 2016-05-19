package ovh.corail.recycler.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import ovh.corail.recycler.block.BlockRecycler;
import ovh.corail.recycler.core.Helper;
import ovh.corail.recycler.core.Main;
import ovh.corail.recycler.core.RecyclingManager;
import ovh.corail.recycler.core.RecyclingRecipe;
import ovh.corail.recycler.handler.PacketHandler;
import ovh.corail.recycler.handler.SoundHandler;
import ovh.corail.recycler.packet.ClientProgressMessage;
import ovh.corail.recycler.packet.ServerProgressMessage;
import ovh.corail.recycler.packet.SoundMessage;

public class TileEntityRecycler extends TileEntityInventory implements ITickable {
	public InventoryBasic visual;
	private RecyclingManager recyclingManager;
	private int countTicks = 0;
	private final int maxTicks = 200;
	private boolean isWorking = false;
	private int progress = 0;
	private int cantRecycleTicks = 0;

	public TileEntityRecycler() {
		super();
		this.visual = new InventoryBasic("visual", true, 9);
		recyclingManager = RecyclingManager.getInstance();
	}

	public boolean canRecycle(EntityPlayer currentPlayer) {
		/** item input slot empty */
		if (getStackInSlot(0) == null) {
			Helper.addChatMessage("tile.recycler.message.emptySlot", currentPlayer, true);
			return false;
		}
		if (getStackInSlot(0).stackSize <= 0) {
			Helper.addChatMessage("tile.recycler.message.emptySlot", currentPlayer, true);
			setInventorySlotContents(0, null);
			return false;
		}
		/** disk input slot empty */
		ItemStack diskStack = getStackInSlot(1);
		if (diskStack == null) {
			Helper.addChatMessage("tile.recycler.message.noDisk", currentPlayer, true);
			return false;
		}
		if (diskStack.stackSize <= 0) {
			Helper.addChatMessage("tile.recycler.message.noDisk", currentPlayer, true);
			setInventorySlotContents(1, null);
			return false;
		}
		// TODO nécessaire?
		if (getStackInSlot(1).getItemDamage() >= getStackInSlot(1).getMaxDamage()) {
			setInventorySlotContents(1, null);
			Helper.addChatMessage("tile.recycler.message.noDisk", currentPlayer, true);
			return false;
		}
		return true;
	}

	public boolean transferSlotInput() {
		int emptySlot = this.getEmptySlot();
		if (emptySlot == -1 || getStackInSlot(0) == null) { return false; }
		ItemStack stack = getStackInSlot(0).copy();
		this.setInventorySlotContents(0, (ItemStack) null);
		this.setInventorySlotContents(emptySlot, stack);
		return true;
	}
	
	public boolean recycle(EntityPlayer currentPlayer) {
		if (!canRecycle(currentPlayer)) {
			return false;
		}
		ItemStack diskStack = getStackInSlot(1);
		/** corresponding recipe */
		int num_recipe = recyclingManager.hasRecipe(getStackInSlot(0));
		if (num_recipe < 0) {
			Helper.addChatMessage("tile.recycler.message.noRecipe", currentPlayer, true);
			transferSlotInput();
			return false;
		}
		RecyclingRecipe currentRecipe = recyclingManager.getRecipe(num_recipe);
		/** enough stacksize for the input slot */
		if (getStackInSlot(0).stackSize < currentRecipe.getItemRecipe().stackSize) {
			Helper.addChatMessage("tile.recycler.message.noEnoughInput", currentPlayer, true);
			return false;
		}
		int nb_input = (int) Math.floor((double) getStackInSlot(0).stackSize / (double) currentRecipe.getItemRecipe().stackSize);
		/** by unit in auto recycle */
		if (isWorking) { nb_input = 1; }
		/** max uses of the disk */
		int maxDiskUse = (int) Math.floor((double) (diskStack.getMaxDamage() - diskStack.getItemDamage()) / 10.0);
		if (maxDiskUse < nb_input) {
			nb_input = maxDiskUse;
		}
		/** calculation of the result */
		List<ItemStack> itemsList = recyclingManager.getResultStack(getStackInSlot(0), nb_input);
		// TODO calcul des stacksizes pour les slots libres à mettre plus bas
		int emptyCount = hasEmptySlot();
		if (emptyCount >= itemsList.size()) {
			/** fill the identical slots not fullstack */
			/** for each result of the recipe */
			for (int i = 0; i < itemsList.size(); i++) {
				/** if the slot isn't fullstack */
				if (itemsList.get(i).stackSize != itemsList.get(i).getMaxStackSize()) {
					/** for each slot */
					for (int j = firstOutput; j < this.count; j++) {
						/** same item */
						if (itemsList.get(i) != null && itemsList.get(i).isItemEqual(inventory[j])) {
							int sommeStackSize = inventory[j].stackSize + itemsList.get(i).stackSize;
							if (sommeStackSize > inventory[j].getMaxStackSize()) {
								inventory[j].stackSize = inventory[j].getMaxStackSize();
								ItemStack resteStack = itemsList.get(i).copy();
								resteStack.stackSize = sommeStackSize - inventory[j].getMaxStackSize();
								itemsList.set(i, resteStack);
							} else {
								inventory[j].stackSize = sommeStackSize;
								itemsList.set(i, null);
								// break;
							}
						}
					}
				}
			}
			/** fill the output slots left */
			for (int i = 0; i < itemsList.size(); i++) {
				if (itemsList.get(i) != null) {
					int emptySlot = getEmptySlot();
					setInventorySlotContents(emptySlot, itemsList.get(i).copy());
				}
			}

		} else {
			Helper.addChatMessage("tile.recycler.message.notEnoughOutputSlots", currentPlayer, true);
			return false;
		}
		/** empty the input slot */
		if (currentRecipe.getItemRecipe().stackSize * nb_input == getStackInSlot(0).stackSize) {
			setInventorySlotContents(0, null);
			emptyVisual();
		} else {
			ItemStack stack = getStackInSlot(0).copy();
			stack.stackSize = getStackInSlot(0).stackSize - (nb_input * currentRecipe.getItemRecipe().stackSize);
			setInventorySlotContents(0, stack);
		}
		/** damage the disk */
		diskStack.setItemDamage(diskStack.getItemDamage() + (10 * nb_input));
		if (diskStack.getItemDamage() >= diskStack.getMaxDamage()) {
			Helper.addChatMessage("tile.recycler.message.BrokenDisk", currentPlayer, true);
			this.setInventorySlotContents(1, null);
		} else {
			this.setInventorySlotContents(1, diskStack);
		}
		/** play sound */
		PacketHandler.INSTANCE.sendToAllAround(new SoundMessage(getPos(), 0),
			new TargetPoint(worldObj.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 20));
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setInteger("countTicks", countTicks);
		compound.setBoolean("isWorking", isWorking);
		compound.setInteger("progress", progress);
		compound.setInteger("cantRecycleTicks", cantRecycleTicks);
		super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		countTicks = compound.getInteger("countTicks");
		isWorking = compound.getBoolean("isWorking");
		progress = compound.getInteger("progress");
		cantRecycleTicks = compound.getInteger("cantRecycleTicks");
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		/** output slots */
		if (index > 1) {
			return false;
		}
		/** disk input slot */
		if (index == 1) {
			if (stack.getItem()==Main.diamond_disk) {
				return true;
			} else {
				return false;
			}
		}
		if (stack.getItem()==Main.diamond_disk) {
			return false;
		}
		/** item input slot */
		/*int currentRecipe = recyclingManager.hasRecipe(stack);
		if (currentRecipe < 0) {
			return false;
		}*/
		return true;
	}

	public void fillVisual(List<ItemStack> itemsList) {
		int num_slot = 0;
		for (int i = 0; i < itemsList.size(); i++) {
			if (num_slot < visual.getSizeInventory()) {
				visual.setInventorySlotContents(num_slot++, itemsList.get(i));
			}
		}
	}

	public void emptyVisual() {
		for (int i = 0; i < visual.getSizeInventory(); i++) {
			visual.setInventorySlotContents(i, null);
		}
	}

	public void refreshVisual(ItemStack stack) {
		emptyVisual();
		List<ItemStack> itemsList = recyclingManager.getResultStack(stack, 1);
		fillVisual(itemsList);
	}

	@Override
	public void update() {
		if (worldObj.isRemote || !isWorking) { return; }
		countTicks--;
		boolean hasChanged = false;
		
		/** each tick */
		if (!canRecycle((EntityPlayer) null)) {
			cantRecycleTicks++;
			countTicks = maxTicks;
		} else {
			/** corresponding recipe */
			int num_recipe = recyclingManager.hasRecipe(getStackInSlot(0));
			if (num_recipe >= 0) {
				int neededStacksize = recyclingManager.getRecipe(num_recipe).getItemRecipe().stackSize;
				/** enough stacksize for the input slot */
				if (getStackInSlot(0).stackSize < neededStacksize) {
					cantRecycleTicks++;
					countTicks = maxTicks;
				}
			/** no recipe */
			} else {
				cantRecycleTicks++;
				countTicks = maxTicks;
			}
		}
		
		/** can't recycle since 4 seconds */
		if (cantRecycleTicks > 40) {
			/** no input item or no disk */
			if (getStackInSlot(0) == null || getStackInSlot(1) == null) {
				isWorking = false;
				hasChanged = true;
			}
			/** no output slot */
			if (!transferSlotInput()) {
				isWorking = false;
				hasChanged = true;
			}
			cantRecycleTicks = 0;
			countTicks = maxTicks;
		}
		
		/** try to recycle */
		if (countTicks <= 0) {
			if (!recycle((EntityPlayer) null)) {
				cantRecycleTicks++;
			}
			countTicks = maxTicks;
		}

		progress = (int) Math.floor(((double) (maxTicks-countTicks) / (double) maxTicks) * 100.0);
		if (hasChanged && !worldObj.isRemote) {
			//worldObj.setBlockState(this.getPos(), worldObj.getBlockState(this.getPos()).withProperty(BlockRecycler.ENABLED, isWorking), 3);
		}
		PacketHandler.INSTANCE.sendToServer(new ServerProgressMessage(getPos(), progress, isWorking, false));
	}

	public int getPercentWorking() {
		return progress;
	}
	
	public boolean isWorking() {
		return isWorking;
	}
	
	public int getCountTicks() {
		return countTicks;
	}
	
	public void setProgress(int progress, boolean isWorking, boolean isReset) {
		this.progress = progress;
		this.isWorking = isWorking;
		if (isReset) {
			countTicks = maxTicks;
		}
	}
}
