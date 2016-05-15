package ovh.corail.recycler.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class VisualMessage implements IMessage {
	BlockPos currentPos;

	public VisualMessage() {
	}

	public VisualMessage(BlockPos currentPos) {
		this.currentPos = currentPos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.currentPos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.currentPos.toLong());
	}
		
	public static class Handler implements IMessageHandler<VisualMessage, IMessage> {
		@Override
		public IMessage onMessage(final VisualMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World worldIn = ctx.getServerHandler().playerEntity.worldObj;
					TileEntity tile = worldIn.getTileEntity(message.currentPos);
					if (tile == null || !(tile instanceof TileEntityRecycler)) { return ; }
					TileEntityRecycler recycler = (TileEntityRecycler) worldIn.getTileEntity(message.currentPos);
					recycler.refreshVisual(recycler.getStackInSlot(0));
				}
			});
			return null;
		}
	}
}
