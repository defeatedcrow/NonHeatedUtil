package defeatedcrow.util.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class DCInventory implements IInventory {

	private final int size;
	public final NonNullList<ItemStack> inv;

	public DCInventory(int i) {
		size = i;
		inv = NonNullList.<ItemStack>withSize(size, ItemStack.EMPTY);
	}

	public List<ItemStack> getInputs(int from, int to) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		for (int i = from; i <= to; i++) {
			if (getStackInSlot(i).isEmpty()) {
				ret.add(ItemStack.EMPTY);
			} else {
				ret.add(getStackInSlot(i));
			}
		}
		return ret;
	}

	public List<ItemStack> getOutputs(int from, int to) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		for (int i = from; i <= to; i++) {
			if (getStackInSlot(i).isEmpty()) {
				ret.add(ItemStack.EMPTY);
			} else {
				ret.add(getStackInSlot(i));
			}
		}
		return ret;
	}

	// スロット数
	@Override
	public int getSizeInventory() {
		return size;
	}

	// インベントリ内の任意のスロットにあるアイテムを取得
	@Override
	public ItemStack getStackInSlot(int i) {
		if (i >= 0 && i < getSizeInventory()) {
			return inv.get(i);
		} else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int i, int num) {
		if (i < 0 || i >= this.getSizeInventory())
			return ItemStack.EMPTY;
		if (!getStackInSlot(i).isEmpty()) {
			ItemStack item = getStackInSlot(i).splitStack(num);
			if (item.getCount() <= 0) {
				item = ItemStack.EMPTY;
			}
			return item;
		} else
			return ItemStack.EMPTY;
	}

	// インベントリ内のスロットにアイテムを入れる
	@Override
	public void setInventorySlotContents(int i, ItemStack stack) {
		if (i < 0 || i >= this.getSizeInventory()) {
			return;
		} else {
			if (stack == null) {
				stack = ItemStack.EMPTY;
			}

			inv.set(i, stack);

			if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
				stack.setCount(this.getInventoryStackLimit());
			}

			this.markDirty();
		}
	}

	// インベントリの名前
	@Override
	public String getName() {
		return "dcs.gui.device.normal_inv";
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
	public void markDirty() {}

	// par1EntityPlayerがTileEntityを使えるかどうか
	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		// Tile側で定義する
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack stack) {
		return true;
	}

	// 追加メソッド
	public static int isItemStackable(ItemStack target, ItemStack current) {
		if (isSameItem(target, current, false)) {
			int i1 = current.getCount() + target.getCount();
			if (!current.isEmpty() && i1 > current.getMaxStackSize()) {
				return current.getMaxStackSize() - current.getCount();
			} else {
				return target.getCount();
			}
		}
		return 0;
	}

	public int canIncr(int i, ItemStack get) {
		if (i < 0 || i >= this.getSizeInventory() || get.isEmpty())
			return 0;
		else if (getStackInSlot(i).isEmpty())
			return get.getCount();
		else {
			return isItemStackable(get, getStackInSlot(i));
		}
	}

	public int incrStackInSlot(int i, ItemStack input) {
		if (i >= 0 || i < this.getSizeInventory() && !input.isEmpty()) {
			if (!getStackInSlot(i).isEmpty()) {
				int add = isItemStackable(input, getStackInSlot(i));
				addStackSize(getStackInSlot(i), add);
				return add;
			} else {
				this.setInventorySlotContents(i, input);
				return input.getCount();
			}
		}
		return 0;
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		if (i < 0 || i >= this.getSizeInventory())
			return ItemStack.EMPTY;
		else {
			if (!getStackInSlot(i).isEmpty()) {
				ItemStack itemstack = this.getStackInSlot(i);
				inv.set(i, ItemStack.EMPTY);
				return itemstack;
			}
		}
		return ItemStack.EMPTY;
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
		for (int i = 0; i < this.getSizeInventory(); ++i) {
			inv.set(i, ItemStack.EMPTY);
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(this.getName());
	}

	/* Packet,NBT */

	public void readFromNBT(NBTTagCompound tag) {

		NBTTagList nbttaglist = tag.getTagList("InvItems", 10);

		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound tag1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = tag1.getByte("Slot");

			if (b0 >= 0 && b0 < this.getSizeInventory()) {
				inv.set(b0, new ItemStack(tag1));
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {

		// アイテムの書き込み
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.getSizeInventory(); ++i) {
			if (!getStackInSlot(i).isEmpty()) {
				NBTTagCompound tag1 = new NBTTagCompound();
				tag1.setByte("Slot", (byte) i);
				getStackInSlot(i).writeToNBT(tag1);
				nbttaglist.appendTag(tag1);
			}
		}
		tag.setTag("InvItems", nbttaglist);
		return tag;
	}

	@Override
	public boolean isEmpty() {
		boolean flag = true;
		for (ItemStack item : inv) {
			if (!item.isEmpty())
				flag = false;
		}
		return flag;
	}

	public static boolean isSameItem(ItemStack i1, ItemStack i2, boolean nullable) {
		if (i1.isEmpty() || i2.isEmpty()) {
			return nullable;
		} else {
			if (i1.getItem() == i2.getItem() && i1.getItemDamage() == i2.getItemDamage()) {
				NBTTagCompound t1 = i1.getTagCompound();
				NBTTagCompound t2 = i2.getTagCompound();
				if (t1 == null && t2 == null) {
					return true;
				} else if (t1 == null || t2 == null) {
					return false;
				} else {
					return t1.equals(t2);
				}
			}
			return false;
		}
	}

	public static int addStackSize(ItemStack item, int i) {
		if (!item.isEmpty()) {
			int ret = item.getMaxStackSize() - item.getCount();
			ret = Math.min(i, ret);
			item.grow(ret);
			return ret;
		}
		return 0;
	}

	public static boolean isStackable(ItemStack i1, ItemStack i2) {
		if (isSameItem(i1, i2, false)) {
			return i1.getCount() <= (i2.getMaxStackSize() - i2.getCount());
		}
		return false;
	}

	public static boolean canInsert(ItemStack i1, ItemStack i2) {
		if (i1.isEmpty() && i2.isEmpty()) {
			return true;
		} else if (isSameItem(i1, i2, false)) {
			return i1.getCount() <= (i2.getMaxStackSize() - i2.getCount());
		}
		return false;
	}

}
