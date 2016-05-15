package ovh.corail.recycler.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.core.Helper;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class TakeAllMessage implements IMessage {
	BlockPos currentPos;

	public TakeAllMessage() {
		}

	public TakeAllMessage(BlockPos currentPos) {
			this.currentPos = currentPos;
		}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.currentPos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(currentPos.toLong());
	}

	public static class Handler implements IMessageHandler<TakeAllMessage, IMessage> {
		@Override
		public IMessage onMessage(final TakeAllMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayer player = ctx.getServerHandler().playerEntity;
					World worldIn = ctx.getServerHandler().playerEntity.worldObj;
					TileEntity tile = worldIn.getTileEntity(message.currentPos);
					if (tile == null || !(tile instanceof TileEntityRecycler)) { return ; }
					TileEntityRecycler recycler = (TileEntityRecycler) worldIn.getTileEntity(message.currentPos);
					for (int i = recycler.firstOutput; i < recycler.getSizeInventory(); i++) {
						if (recycler.getStackInSlot(i) != null) {
							ItemStack stack = recycler.getStackInSlot(i);
							recycler.setInventorySlotContents(i, Helper.addToInventoryWithLeftover(stack, player.inventory, false));
						}
					}
					player.openContainer.detectAndSendChanges();
				}
			});
			return null;
		}
	}
}
