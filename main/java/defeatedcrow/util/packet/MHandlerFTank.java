package defeatedcrow.util.packet;

import defeatedcrow.util.material.IFluidTile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MHandlerFTank implements IMessageHandler<MessageFTank, IMessage> {

	@Override
	// IMessageHandlerのメソッド
	public IMessage onMessage(MessageFTank message, MessageContext ctx) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (player != null) {
			int x = message.x;
			int y = message.y;
			int z = message.z;
			int id1 = message.id1;
			int amo1 = message.amo1;
			BlockPos pos = new BlockPos(x, y, z);
			TileEntity tile = player.world.getTileEntity(pos);
			if (tile instanceof IFluidTile) {
				IFluidTile te = (IFluidTile) tile;
				Fluid f1 = FluidIDRegisterDC.getFluid(id1);
				if (f1 != null) {
					FluidStack stack1 = new FluidStack(f1, amo1);
					te.setFluid(stack1);
				} else {
					te.setFluid(null);
				}
			}
		}
		return null;
	}
}
