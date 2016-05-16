package ovh.corail.recycler.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.core.Helper;
import ovh.corail.recycler.handler.PacketHandler;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class SwitchWorkingMessage implements IMessage {
	BlockPos currentPos;

	public SwitchWorkingMessage() {
	}

	public SwitchWorkingMessage(BlockPos currentPos) {
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
	
	public static class Handler implements IMessageHandler<SwitchWorkingMessage, IMessage> {
		 
        @Override
        public IMessage onMessage(final SwitchWorkingMessage message, final MessageContext ctx) {
    		IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
    		mainThread.addScheduledTask(new Runnable() {
    			@Override
    			public void run() {
    				World worldIn = ctx.getServerHandler().playerEntity.worldObj;
    				TileEntity tile = worldIn.getTileEntity(message.currentPos);
    				if (tile != null && tile instanceof TileEntityRecycler) {
    					TileEntityRecycler recycler = (TileEntityRecycler) tile;
    					recycler.resetProgress();
    					recycler.switchWorking();
    					PacketHandler.INSTANCE.sendToAllAround(new ProgressMessage(message.currentPos, 0, recycler.isWorking()),
    							new TargetPoint(worldIn.provider.getDimension(), message.currentPos.getX(), message.currentPos.getY(), message.currentPos.getZ(), 12));
    				}
    			}
    		});
    		return null;
        }
 
    }
}
