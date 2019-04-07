package defeatedcrow.util.material;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import defeatedcrow.util.inventory.ContainerHopperFilter;
import defeatedcrow.util.inventory.DCInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileHopperFilter extends DCLockableTE implements IHopper, ISidedInventory {

	protected DCInventory inv = new DCInventory(5);
	private int cooldown = -1;
	private int lastCount = 0;

	public int getCoolTime() {
		return 4;
	}

	public boolean isFilterd() {
		return true;
	}

	@Override
	public void onServerUpdate() {
		if (cooldown <= 0) {
			cooldown = getCoolTime();
			if (isActive()) {
				extractItem();
				if (!suctionItem()) {
					suctionDrop();
				}
			}
		} else {
			cooldown--;
		}
	}

	@Nullable
	protected EnumFacing getCurrentFacing() {
		if (this.world != null && this.pos != null) {
			IBlockState state = this.world.getBlockState(pos);
			if (state != null && state.getBlock() instanceof BlockHopperFilter) {
				EnumSide side = DCStateBase.getSide(state, DCStateBase.SIDE);
				return side != null ? side.getFacing() : EnumFacing.DOWN;
			}
		}
		return EnumFacing.DOWN;
	}

	@Nullable
	protected EnumFacing getInsertSide() {
		if (this.world != null && this.pos != null) {
			IBlockState state = this.world.getBlockState(pos);
			if (state != null && state.getBlock() instanceof BlockHopperFilter) {
				EnumSide side = DCStateBase.getSide(state, DCStateBase.SIDE);
				return side == EnumSide.UP ? EnumFacing.DOWN : EnumFacing.UP;
			}
		}
		return EnumFacing.UP;
	}

	private boolean isActive() {
		IBlockState state = this.world.getBlockState(pos);
		if (state != null && state.getBlock() instanceof BlockHopperFilter) {
			boolean flag = DCStateBase.getBool(state, DCStateBase.POWERED);
			return flag;
		}
		return true;
	}

	protected boolean extractItem() {
		EnumFacing face = getCurrentFacing();
		if (face != null) {
			TileEntity tile = world.getTileEntity(pos.offset(face));
			if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite())) {
				IItemHandler target = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
						face.getOpposite());
				if (target != null) {
					boolean b = false;
					for (int i = 0; i < this.getSizeInventory(); i++) {
						ItemStack item = inv.getStackInSlot(i);
						int min = isFilterd() ? 1 : 0;
						if (!item.isEmpty()) {
							if (item.getItem().getItemStackLimit(item) == 1) {
								min = 0;
							}
							if (item.getCount() > min) {
								ItemStack ins = item.copy();
								ins.setCount(1);
								for (int j = 0; j < target.getSlots(); j++) {
									ItemStack ret = target.insertItem(j, ins, true);
									if (ret.isEmpty()) {
										target.insertItem(j, ins, false);
										this.decrStackSize(i, 1);
										this.markDirty();
										tile.markDirty();
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	protected boolean suctionItem() {
		EnumFacing face = getCurrentFacing() == EnumFacing.UP ? EnumFacing.DOWN : EnumFacing.UP;
		TileEntity tile = world.getTileEntity(pos.offset(face));
		if (tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN)) {
			IItemHandler target = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
			if (target != null) {
				boolean b = false;
				for (int i = 0; i < target.getSlots(); i++) {
					ItemStack item = target.extractItem(i, 1, true);
					if (!item.isEmpty()) {
						for (int j = 0; j < this.getSizeInventory(); j++) {
							ItemStack cur = this.getStackInSlot(j);
							if (this.isItemStackable(item, cur) > 0) {
								this.incrStackInSlot(j, item);
								target.extractItem(i, 1, false);
								this.markDirty();
								tile.markDirty();
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	protected boolean suctionDrop() {
		double x1 = getPos().getX() - 0D;
		double x2 = getPos().getX() + 1D;
		double y1 = getPos().getY() + 0.5D;
		double y2 = getPos().getY() + 2D;
		double z1 = getPos().getZ() - 0D;
		double z2 = getPos().getZ() + 1D;
		if (getCurrentFacing() == EnumFacing.UP) {
			y1 = getPos().getY() - 2D;
			y2 = getPos().getY() + 0.5D;
		}
		List list = this.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(x1, y1, z1, x2, y2, z2));
		if (list == null || list.isEmpty())
			return false;

		for (int i = 0; i < list.size(); ++i) {
			Entity entity = (Entity) list.get(i);
			if (entity != null) {
				if (entity instanceof EntityItem) {
					EntityItem drop = (EntityItem) entity;
					if (!drop.getItem().isEmpty()) {
						ItemStack ins = drop.getItem().copy();
						for (int j = 0; j < this.getSizeInventory(); j++) {
							ItemStack cur = this.getStackInSlot(j);
							int count = this.isItemStackable(ins, cur);
							if (count > 0) {
								ins.setCount(count);
								this.incrStackInSlot(j, ins);
								drop.getItem().splitStack(count);
								this.markDirty();
								if (drop.getItem().isEmpty()) {
									drop.setDead();
								}
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/* === 追加メソッド === */

	public static int isItemStackable(ItemStack target, ItemStack current) {
		if (DCInventory.isSameItem(target, current, true)) {
			int i = current.getCount() + target.getCount();
			if (i > current.getMaxStackSize()) {
				i = current.getMaxStackSize() - current.getCount();
				return i;
			}
			return target.getCount();
		}

		return 0;
	}

	public void incrStackInSlot(int i, ItemStack input) {
		inv.incrStackInSlot(i, input);
	}

	/* === IInventory === */

	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inv.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int num) {
		return inv.decrStackSize(i, num);
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		return inv.removeStackFromSlot(i);
	}

	// インベントリ内のスロットにアイテムを入れる
	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		inv.setInventorySlotContents(i, stack);
		this.markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (getWorld().getTileEntity(this.pos) != this || player == null)
			return false;
		else
			return Math.sqrt(player.getDistanceSq(pos)) < 256D;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {

		return 0;
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public String getName() {
		return "dcs.gui.device.hopper.filter";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerHopperFilter(this, playerIn);
	}

	@Override
	public String getGuiID() {
		return "dcs.gui.device.hopper.filter";
	}

	@Override
	public void invalidate() {
		super.invalidate();
		this.updateContainingBlockInfo();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	/* === Hopper === */

	@Override
	public double getXPos() {
		return this.pos.getX() + 0.5D;
	}

	@Override
	public double getYPos() {
		return this.pos.getY() + 0.5D;
	}

	@Override
	public double getZPos() {
		return this.pos.getZ() + 0.5D;
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	/* === SidedInventory === */

	protected int[] slotsSides() {
		return new int[] {
				0,
				1,
				2,
				3,
				4
		};
	};

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return slotsSides();
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStack, EnumFacing dir) {
		return this.isItemValidForSlot(index, itemStack);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing dir) {
		if (!stack.isEmpty()) {
			return !isFilterd() || stack.getCount() > 1 || stack.getItem().getItemStackLimit(stack) == 1;
		}
		return false;
	}

	IItemHandler handler = new HopperInvWrapper(this, getCurrentFacing());
	IItemHandler handler2 = new HopperInvWrapper(this, getCurrentFacing()) {
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return stack;
		}
	};

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == getCurrentFacing() || facing == getInsertSide()) {
				return (T) handler2;
			} else {
				return (T) handler;
			}
		}
		return null;
	}

	/* Packet,NBT */

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		inv.readFromNBT(tag);

		this.cooldown = tag.getInteger("Cooldown");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		// 燃焼時間や調理時間などの書き込み
		tag.setInteger("Cooldown", this.cooldown);

		// アイテムの書き込み
		inv.writeToNBT(tag);
		return tag;
	}

	@Override
	public NBTTagCompound getNBT(NBTTagCompound tag) {
		super.getNBT(tag);
		// 燃焼時間や調理時間などの書き込み
		tag.setInteger("Cooldown", this.cooldown);

		// アイテムの書き込み
		inv.writeToNBT(tag);

		return tag;
	}

	@Override
	public void setNBT(NBTTagCompound tag) {
		super.setNBT(tag);

		inv.readFromNBT(tag);

		this.cooldown = tag.getInteger("Cooldown");
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

	@Override
	public void markDirty() {}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	private class HopperInvWrapper extends SidedInvWrapper {
		private HopperInvWrapper(TileHopperFilter tile, EnumFacing side) {
			super(tile, side);
		}

		@Override
		@Nonnull
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (amount <= 0)
				return ItemStack.EMPTY;

			int slot1 = getSlot(inv, slot, side);

			if (slot1 == -1)
				return ItemStack.EMPTY;

			ItemStack stackInSlot = inv.getStackInSlot(slot1);

			if (stackInSlot.isEmpty())
				return ItemStack.EMPTY;

			if (!inv.canExtractItem(slot1, stackInSlot, side))
				return ItemStack.EMPTY;

			ItemStack copy2 = stackInSlot.copy();
			int count = copy2.getCount();
			if (count > amount) {
				count = amount;
			} else {
				if (stackInSlot.getItem().getItemStackLimit(copy2) == 1) {
					count = 1;
				} else if (isFilterd()) {
					count--;
				}
			}

			if (count <= 0) {
				return ItemStack.EMPTY;
			}

			if (simulate) {
				if (stackInSlot.getCount() < count) {
					return stackInSlot.copy();
				} else {
					ItemStack copy = stackInSlot.copy();
					copy.setCount(count);
					return copy;
				}
			} else {
				int m = Math.min(stackInSlot.getCount(), count);
				ItemStack ret = inv.decrStackSize(slot1, m);
				inv.markDirty();
				return ret;
			}
		}
	}

}
