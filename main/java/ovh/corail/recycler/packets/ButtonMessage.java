package ovh.corail.recycler.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import ovh.corail.recycler.tileentity.TileEntityRecycler;

public class ButtonMessage implements IMessage, IMessageHandler<ButtonMessage, IMessage> {
	BlockPos currentPos;
	int id;

	public ButtonMessage() {
	}

	public ButtonMessage(int id, BlockPos currentPos) {
		this.id = id;
		this.currentPos = currentPos;
	}

	@Override
	public IMessage onMessage(final ButtonMessage message, final MessageContext ctx) {
		IThreadListener mainThread = (IThreadListener) ctx.getServerHandler().playerEntity.worldObj;
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				TileEntityRecycler tile = (TileEntityRecycler) ctx.getServerHandler().playerEntity.worldObj
						.getTileEntity(currentPos);
				switch (message.id) {
				case 0: // Recycle
					tile.recycle();
					break;
				case 1: // Auto-recycle
					tile.switchWorking();
					break;
				}
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.currentPos = BlockPos.fromLong(buf.readLong());
		this.id = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(currentPos.toLong());
		buf.writeInt(this.id);
	}
}
