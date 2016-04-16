package ovh.corail.recycler.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class VisualMessage implements IMessage, IMessageHandler<VisualMessage, IMessage> {
		BlockPos currentPos;

		public VisualMessage() {
		}

		public VisualMessage(BlockPos currentPos) {
			this.currentPos = currentPos;
		}

		@Override
		public IMessage onMessage(final VisualMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					TileEntityRecycler tile = (TileEntityRecycler) ctx.getServerHandler().playerEntity.worldObj
							.getTileEntity(currentPos);
					tile.refreshVisual(tile.getStackInSlot(0));

				}
			});
			return null;
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			this.currentPos = BlockPos.fromLong(buf.readLong());
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeLong(currentPos.toLong());
		}
	}
