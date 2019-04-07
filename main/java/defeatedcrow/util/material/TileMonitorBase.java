package defeatedcrow.util.material;

import javax.annotation.Nullable;

import defeatedcrow.util.packet.DCUtilPacket;
import defeatedcrow.util.packet.MessageMonitor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileMonitorBase extends DCTileEntity implements ITagGetter {

	// pair
	protected BlockPos pairPos = null;
	protected int dim = 0;
	protected int side = 0;

	public float amount = 0F;
	public float amountMax = 0F;
	public float last = 0F;

	public String unit() {
		return "";
	}

	public String amountString(float amo) {
		return amo == 0 ? "---.-" : String.format("%1$.1f", amo);
	}

	public BlockPos getPairPos() {
		return pairPos;
	}

	public void setPairPos(BlockPos pos) {
		pairPos = pos;
	}

	public int getDim() {
		return dim;
	}

	public void setDim(int i) {
		dim = i;
	}

	public EnumSide getSide() {
		return EnumSide.fromIndex(side);
	}

	public void setSide(EnumSide side) {
		dim = side.index;
	}

	public float getCurrentAmount() {
		return amount;
	}

	public float getMaxAmount() {
		return amountMax;
	}

	public float getGauge() {
		return amountMax < 1F ? 0 : amount / amountMax;
	}

	boolean isActive() {
		int currentDim = world.provider.getDimension();
		return getPairPos() != null && currentDim == getDim();
	}

	TileEntity targetTile() {
		if (pairPos != null && world.isBlockLoaded(pairPos)) {
			return world.getTileEntity(pairPos);
		}
		return null;
	}

	@Override
	protected int getMaxCool() {
		return 10;
	}

	boolean next = false;

	@Override
	public void updateTile() {
		super.updateTile();
		if (!world.isRemote && isActive()) {
			last = amount;

			if (!updateAmount()) {
				amount = 0F;
				amountMax = 0F;
			}

			DCUtilPacket.INSTANCE.sendToAll(new MessageMonitor(pos, amount, amountMax, last));

			if (next) {
				world.notifyNeighborsOfStateChange(pos, getBlockType(), true);
			} else if (last != amount) {
				next = true;
			}

			boolean on = amount > 0;
			IBlockState state = world.getBlockState(getPos());
			boolean b = DCStateBase.getBool(state, DCStateBase.POWERED);
			if (on != b) {
				IBlockState newState = state.withProperty(DCStateBase.POWERED, on);
				world.setBlockState(getPos(), newState, 3);
			}
		}
	}

	protected boolean updateAmount() {
		return false;
	}

	// NBT

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		setNBT(tag);
		amount = tag.getFloat("gauge");
		amountMax = tag.getFloat("maxgauge");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		getNBT(tag);
		tag.setFloat("gauge", amount);
		tag.setFloat("maxgauge", amountMax);
		return tag;
	}

	@Override
	public NBTTagCompound getNBT(NBTTagCompound tag) {
		super.getNBT(tag);
		if (pairPos != null) {
			tag.setInteger("card.dim", dim);
			tag.setInteger("card.X", pairPos.getX());
			tag.setInteger("card.Y", pairPos.getY());
			tag.setInteger("card.Z", pairPos.getZ());
			tag.setInteger("card.facing", side);

		}
		return tag;
	}

	@Override
	public void setNBT(NBTTagCompound tag) {
		super.setNBT(tag);
		if (tag.hasKey("card.dim")) {
			dim = tag.getInteger("card.dim");
			int px = tag.getInteger("card.X");
			int py = tag.getInteger("card.Y");
			int pz = tag.getInteger("card.Z");
			side = tag.getInteger("card.facing");

			BlockPos pos = new BlockPos(px, py, pz);
			pairPos = pos;

		}
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
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return (oldState.getBlock() != newSate.getBlock());
	}

	@Override
	public void invalidate() {
		super.invalidate();
		this.updateContainingBlockInfo();
	}

	/* inventory handler */

}