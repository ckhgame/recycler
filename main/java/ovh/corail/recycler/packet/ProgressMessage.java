package ovh.corail.recycler.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class ProgressMessage implements IMessage {
	BlockPos currentPos; 
	int progress;
	boolean isWorking;

	public ProgressMessage() {
	}

	public ProgressMessage(BlockPos currentPos, int progress, boolean isWorking) {
		this.currentPos = currentPos;
		this.progress = progress;
		this.isWorking = isWorking;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.currentPos = BlockPos.fromLong(buf.readLong());
		this.progress = buf.readInt();
		this.isWorking = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.currentPos.toLong());
		buf.writeInt(this.progress);
		buf.writeBoolean(this.isWorking);
	}
	
	public static class Handler implements IMessageHandler<ProgressMessage, IMessage> {
		@Override
		public IMessage onMessage(final ProgressMessage message, final MessageContext ctx) {
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					WorldClient worldIn = Minecraft.getMinecraft().theWorld;
					TileEntity tile = worldIn.getTileEntity(message.currentPos);
					if (tile == null || !(tile instanceof TileEntityRecycler)) { return ; }
					TileEntityRecycler recycler = (TileEntityRecycler) worldIn.getTileEntity(message.currentPos);
					recycler.refreshProgress(message.progress, message.isWorking);	
				}
			});
			return null;
		}
	}
}
