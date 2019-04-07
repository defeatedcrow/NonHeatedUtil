package defeatedcrow.util.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabUtil extends CreativeTabs {

	// クリエイティブタブのアイコン画像や名称の登録クラス
	public CreativeTabUtil(String type) {
		super(type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return "NonHeatedUtil";
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(DCUtilInit.steelGear);
	}

}
