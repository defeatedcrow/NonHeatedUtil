package defeatedcrow.util.material;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class DCItemBlockBase extends ItemBlock {

	public DCItemBlockBase(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
		this.setRegistryName(block.getRegistryName());
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}
}
