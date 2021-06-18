package defeatedcrow.util.material;

import javax.annotation.Nullable;

import defeatedcrow.util.inventory.ContainerLowChest;
import defeatedcrow.util.inventory.DCInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class TileLowChest extends DCLockableTE implements IInventory {

	public TileLowChest() {
		super();
	}

	/* open count */

	public boolean isOpen = false;
	public int numPlayersUsing = 0;

	@Override
	public void updateTile() {
		int x = this.pos.getX();
		int y = this.pos.getY();
		int z = this.pos.getZ();

		if (!world.isRemote && this.numPlayersUsing != 0) {
			this.numPlayersUsing = 0;

			for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(
					x - 5.0F, y - 5.0F, z - 5.0F, x + 1 + 5.0F, y + 1 + 5.0F, z + 1 + 5.0F))) {
				if (entityplayer.openContainer instanceof ContainerLowChest) {
					IInventory iinventory = ((ContainerLowChest) entityplayer.openContainer).tile;

					if (iinventory == this) {
						++this.numPlayersUsing;
					}
				}
			}
		}
	}

	@Override
	public void onTickUpdate() {
		int x = this.pos.getX();
		int y = this.pos.getY();
		int z = this.pos.getZ();
		if (this.numPlayersUsing > 0 && !isOpen) {
			isOpen = true;
			// DCLogger.debugLog("open");
			this.world.playSound((EntityPlayer) null, x + 0.5D, y + 0.5D, z + 0.5D, getOpenSound(),
					SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (this.numPlayersUsing == 0 && isOpen) {
			isOpen = false;
			// DCLogger.debugLog("close");
			this.world.playSound((EntityPlayer) null, x + 0.5D, y + 0.5D, z + 0.5D, getCloseSound(),
					SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
		}
	}

	protected SoundEvent getOpenSound() {
		return SoundEvents.BLOCK_PISTON_EXTEND;
	}

	protected SoundEvent getCloseSound() {
		return SoundEvents.BLOCK_PISTON_CONTRACT;
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.numPlayersUsing = type;
			return true;
		} else
			return super.receiveClientEvent(id, type);
	}

	@Override
	public void openInventory(EntityPlayer player) {
		if (!player.isSpectator()) {
			if (this.numPlayersUsing < 0) {
				this.numPlayersUsing = 0;
			}

			++this.numPlayersUsing;
			this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
			this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		if (!player.isSpectator() && this.getBlockType() instanceof BlockLowChest) {
			--this.numPlayersUsing;
			this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
			this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
		}
	}

	/* ========== NBT ========== */

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		inv.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		// アイテムの書き込み
		inv.writeToNBT(tag);
		return tag;
	}

	@Override
	public NBTTagCompound getNBT(NBTTagCompound tag) {
		super.getNBT(tag);
		// アイテムの書き込み
		inv.writeToNBT(tag);
		return tag;
	}

	@Override
	public void setNBT(NBTTagCompound tag) {
		super.setNBT(tag);

		inv.readFromNBT(tag);
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

	/* ========== 以下、IInventoryのメソッド ========== */

	public DCInventory inv = new DCInventory(this.getSizeInventory());

	// スロット数
	@Override
	public int getSizeInventory() {
		return 54;
	}

	// インベントリ内の任意のスロットにあるアイテムを取得
	@Override
	public ItemStack getStackInSlot(int i) {
		return inv.getStackInSlot(i);
	}

	@Override
	public ItemStack decrStackSize(int i, int num) {
		return inv.decrStackSize(i, num);
	}

	// インベントリ内のスロットにアイテムを入れる
	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		inv.setInventorySlotContents(i, stack);
	}

	// インベントリの名前
	@Override
	public String getName() {
		return "dcutil.gui.chest";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	// インベントリ内のスタック限界値
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		super.markDirty();
	}

	// par1EntityPlayerがTileEntityを使えるかどうか
	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (getWorld().getTileEntity(this.pos) != this || player == null)
			return false;
		else
			return Math.sqrt(player.getDistanceSq(pos)) < 256D;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		if (!stack.isEmpty()) {
			if (stack.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null))
				return false;
			else if (stack.hasTagCompound() && stack.getTagCompound().hasKey("InvItems"))
				return false;
		}
		return true;
	}

	// 追加メソッド
	public static int isItemStackable(ItemStack target, ItemStack current) {
		return DCInventory.isItemStackable(target, current);
	}

	public void incrStackInSlot(int i, ItemStack input) {
		inv.incrStackInSlot(i, input);
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		return inv.removeStackFromSlot(i);
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	IItemHandler handler = new InvWrapper(this);

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) handler;
		return super.getCapability(capability, facing);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		this.updateContainingBlockInfo();
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerLowChest(this, playerIn);
	}

	@Override
	public String getGuiID() {
		return "dcutil:low_chest";
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

}
