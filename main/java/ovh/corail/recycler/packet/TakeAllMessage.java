package ovh.corail.recycler.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.core.Helper;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class TakeAllMessage implements IMessage, IMessageHandler<TakeAllMessage, IMessage> {
	BlockPos pos;

	public TakeAllMessage() {
		}

	public TakeAllMessage(BlockPos pos) {
			this.pos = pos;
		}

	@Override
	public IMessage onMessage(final TakeAllMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				TileEntityRecycler tile = (TileEntityRecycler) player.worldObj.getTileEntity(message.pos);
				for (int i = tile.firstOutput; i < tile.getSizeInventory(); i++) {
					if (tile.getStackInSlot(i) != null) {
						ItemStack stack = tile.getStackInSlot(i);
						tile.setInventorySlotContents(i, Helper.addToInventoryWithLeftover(stack, player.inventory, false));
					}
				}
				player.openContainer.detectAndSendChanges();
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
	}

}
