package defeatedcrow.util.inventory;

import javax.annotation.Nullable;

import defeatedcrow.util.material.ItemLowChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotLowChest extends Slot {

	public SlotLowChest(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	// inventory持ちのItemを入れることはできない
	@Override
	public boolean isItemValid(@Nullable ItemStack stack) {
		if (stack != null && !stack.isEmpty()) {
			if (stack.getItem() instanceof ItemLowChest) {
				return false;
			} else if (stack.hasTagCompound() && stack.getTagCompound().hasKey("InvItems")) {
				return false;
			}
		}
		return true;
	}

}
