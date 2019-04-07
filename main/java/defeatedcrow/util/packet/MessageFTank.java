package defeatedcrow.util.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageFTank implements IMessage {

	public int x;
	public int y;
	public int z;
	public int amo1;
	public int id1;

	public MessageFTank() {}

	public MessageFTank(BlockPos pos, int fluid1, int a1) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		id1 = fluid1;
		amo1 = a1;
	}

	// read
	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		amo1 = buf.readInt();
		id1 = buf.readInt();
	}

	// write
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(amo1);
		buf.writeInt(id1);
	}
}
