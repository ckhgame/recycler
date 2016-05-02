package ovh.corail.recycler.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class ProgressMessage implements IMessage, IMessageHandler<ProgressMessage, IMessage> {
	int x, y, z, progress;
	boolean isWorking;

	public ProgressMessage() {
	}

	public ProgressMessage(int x, int y, int z, int progress, boolean isWorking) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.progress = progress;
		this.isWorking = isWorking;
	}

	@Override
	public IMessage onMessage(final ProgressMessage message, final MessageContext ctx) {
		IThreadListener mainThread = Minecraft.getMinecraft();;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				try {
					TileEntityRecycler tile = (TileEntityRecycler) Minecraft.getMinecraft().theWorld
						.getTileEntity(new BlockPos(message.x, message.y, message.z));
					if (tile == null)
						return;
					tile.refreshProgress(message.progress, message.isWorking);
					return;
				} catch (NullPointerException e) {
					return;
				}
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.progress = buf.readInt();
		this.isWorking = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		buf.writeInt(this.progress);
		buf.writeBoolean(this.isWorking);
	}
}
