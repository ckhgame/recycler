package ovh.corail.recycler.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.common.tileentity.RecyclerTile;

public class VisualMessage implements IMessage, IMessageHandler<VisualMessage, IMessage> {
		int x, y, z;

		public VisualMessage() {
		}

		public VisualMessage(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public IMessage onMessage(final VisualMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					RecyclerTile tile = (RecyclerTile) ctx.getServerHandler().playerEntity.worldObj
							.getTileEntity(new BlockPos(message.x, message.y, message.z));
					tile.refreshVisual(tile.getStackInSlot(0));

				}
			});
			return null;
		}
		@Override
		public void fromBytes(ByteBuf buf) {
			this.x = buf.readInt();
			this.y = buf.readInt();
			this.z = buf.readInt();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(this.x);
			buf.writeInt(this.y);
			buf.writeInt(this.z);
		}
	}
