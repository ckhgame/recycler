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

public class ResetProgressMessage implements IMessage {
	BlockPos currentPos;

	public ResetProgressMessage() {
	}

	public ResetProgressMessage(BlockPos currentPos) {
		this.currentPos = currentPos;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		currentPos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(currentPos.toLong());
	}
	
	public static class Handler implements IMessageHandler<ResetProgressMessage, IMessage> {
		 
        @Override
        public IMessage onMessage(final ResetProgressMessage message, final MessageContext ctx) {
    		IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
    		mainThread.addScheduledTask(new Runnable() {
    			@Override
    			public void run() {
    				World worldIn = ctx.getServerHandler().playerEntity.worldObj;
    				TileEntity tile = worldIn.getTileEntity(message.currentPos);
    				if (tile != null && tile instanceof TileEntityRecycler) {
    					TileEntityRecycler recycler = (TileEntityRecycler) tile;
    					recycler.resetProgress();
    				}
    			}
    		});
    		return null;
        }
 
    }
}
