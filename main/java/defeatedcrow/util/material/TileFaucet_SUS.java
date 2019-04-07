package defeatedcrow.util.material;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileFaucet_SUS extends DCTileEntity {

	private int cooldown = 5;

	@Override
	protected void onServerUpdate() {
		super.onServerUpdate();
		if (!world.isRemote) {
			if (cooldown <= 0) {
				cooldown = 5;
				// 水が無限に出てくる
				IBlockState state = world.getBlockState(getPos());
				if (!DCStateBase.getBool(state, DCStateBase.POWERED))
					return;
				else {
					TileEntity toTE = world.getTileEntity(getPos().down());
					if (toTE != null && toTE.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
							EnumFacing.UP)) {
						IFluidHandler outtank = toTE.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
								EnumFacing.UP);
						if (outtank != null) {
							int limit = 1000; // 1000mBずつ
							// 引き出せる量
							FluidStack get = new FluidStack(FluidRegistry.WATER, limit);
							int ret = outtank.fill(get, false);
							if (ret > 0) {
								outtank.fill(get, true);
							}
						}
					}
				}
			} else {
				cooldown--;
			}
		}
	}

	@Override
	protected int getMaxCool() {
		return 5;
	}

	/* Packet,NBT */

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		return tag;
	}

	@Override
	public NBTTagCompound getNBT(NBTTagCompound tag) {
		super.getNBT(tag);
		return tag;
	}

	@Override
	public void setNBT(NBTTagCompound tag) {
		super.setNBT(tag);
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		this.writeToNBT(nbtTagCompound);
		return new SPacketUpdateTileEntity(pos, -50, nbtTagCompound);
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

}
