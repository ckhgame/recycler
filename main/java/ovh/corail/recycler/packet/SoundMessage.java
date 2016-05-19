package ovh.corail.recycler.packet;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.handler.SoundHandler;

public class SoundMessage implements IMessage {
	BlockPos currentPos;
	/** numSound 0=recycle, 1=working */
	int numSound;

	public SoundMessage() {	
	}
	
	public SoundMessage(BlockPos currentPos, int numSound) {	
		this.currentPos = currentPos;
		this.numSound = numSound;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.currentPos = BlockPos.fromLong(buf.readLong());
		this.numSound = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.currentPos.toLong());
		buf.writeInt(this.numSound);
	}

	public static class Handler implements IMessageHandler<SoundMessage, IMessage> {
		@Override
		public IMessage onMessage(final SoundMessage message, final MessageContext ctx) {
			IThreadListener mainThread = Minecraft.getMinecraft();
			mainThread.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					WorldClient worldIn = Minecraft.getMinecraft().theWorld;
					worldIn.playSound(message.currentPos, (message.numSound==0?SoundHandler.recycler:SoundHandler.recycler_working), SoundCategory.NEUTRAL, 1.0f, 1.0f, true);
				}
			});
			return null;
		}
	}
}
