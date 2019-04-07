package defeatedcrow.util.packet;

import defeatedcrow.util.material.TileMonitorBase;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MHandlerMonitor implements IMessageHandler<MessageMonitor, IMessage> {

	@Override
	// IMessageHandlerのメソッド
	public IMessage onMessage(MessageMonitor message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player != null) {
			int x = message.x;
			int y = message.y;
			int z = message.z;
			float amo = message.amo;
			float max = message.max;
			float last = message.last;
			BlockPos pos = new BlockPos(x, y, z);
			TileEntity tile = player.world.getTileEntity(pos);
			if (tile instanceof TileMonitorBase) {
				TileMonitorBase te = (TileMonitorBase) tile;
				te.amount = amo;
				te.amountMax = max;
			}
		}
		return null;
	}
}
