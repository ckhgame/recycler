package ovh.corail.recycler.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.block.BlockRecycler;
import ovh.corail.recycler.handler.PacketHandler;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class ServerProgressMessage implements IMessage {
	BlockPos currentPos; 
	int progress;
	boolean isWorking, isReset;

	public ServerProgressMessage() {
	}

	public ServerProgressMessage(BlockPos currentPos, int progress, boolean isWorking, boolean isReset) {
		this.currentPos = currentPos;
		this.progress = progress;
		this.isWorking = isWorking;
		this.isReset = isReset;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.currentPos = BlockPos.fromLong(buf.readLong());
		this.progress = buf.readInt();
		this.isWorking = buf.readBoolean();
		this.isReset = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.currentPos.toLong());
		buf.writeInt(this.progress);
		buf.writeBoolean(this.isWorking);
		buf.writeBoolean(this.isReset);
	}
	
	public static class Handler implements IMessageHandler<ServerProgressMessage, IMessage> {
		@Override
		public IMessage onMessage(final ServerProgressMessage message, final MessageContext ctx) {
			IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					World worldIn = ctx.getServerHandler().playerEntity.worldObj;
					TileEntity tile = worldIn.getTileEntity(message.currentPos);
					if (tile == null || !(tile instanceof TileEntityRecycler)) { return ; }
					TileEntityRecycler recycler = (TileEntityRecycler) worldIn.getTileEntity(message.currentPos);
   					recycler.setProgress(message.progress, message.isWorking, message.isReset);
   					PacketHandler.INSTANCE.sendToAllAround(new ClientProgressMessage(message.currentPos, message.progress, message.isWorking, message.isReset),
   							new TargetPoint(worldIn.provider.getDimension(), message.currentPos.getX(), message.currentPos.getY(), message.currentPos.getZ(), 12));
				}
			});
			return null;
		}
	}
}
