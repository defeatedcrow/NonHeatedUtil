package defeatedcrow.util.material;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public class ItemLowChest extends DCItemBlockBase {

	public ItemLowChest(Block block) {
		super(block);
		this.setMaxStackSize(3);
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		if (!stack.isEmpty() && stack.getItem() == this) {
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagList nbttaglist = new NBTTagList();
			tag.setTag("InvItems", nbttaglist);
			stack.setTagCompound(tag);
		}
	}

}
