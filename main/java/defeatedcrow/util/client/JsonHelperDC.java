package defeatedcrow.util.client;

import defeatedcrow.util.material.DCStateBase;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class JsonHelperDC {

	public static final JsonHelperDC INSTANCE = new JsonHelperDC();

	public void regSimpleItem(Item item, String domein, String name, int max) {
		if (item == null)
			return;
		int m = 0;
		while (m < max + 1) {
			ModelLoader.setCustomModelResourceLocation(item, m, new ModelResourceLocation(domein + ":" + name + m,
					"inventory"));
			m++;
		}
	}

	/**
	 * 汎用Tile使用メソッド
	 * 外見は仮のJsonファイルに紐付け、TESRで描画する
	 */
	public void regTEBlock(Block block, String domein, String name) {
		if (block == null)
			return;
		ModelLoader.setCustomStateMapper(block, (new StateMap.Builder()).ignore(DCStateBase.FACING,
				DCStateBase.TYPE4).build());
		ModelBakery.registerItemVariants(Item.getItemFromBlock(block), new ModelResourceLocation(
				domein + ":" + "basetile"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(
				domein + ":" + name, "inventory"));
	}

	public void regTEBlock2(Block block, String domein, String name) {
		if (block == null)
			return;
		ModelLoader.setCustomStateMapper(block, (new StateMap.Builder()).ignore(DCStateBase.FACING).build());
		ModelBakery.registerItemVariants(Item.getItemFromBlock(block), new ModelResourceLocation(
				domein + ":" + "basetile"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(
				domein + ":" + name, "inventory"));
	}

	public void regTEBlock3(Block block, String domein, String name) {
		if (block == null)
			return;
		ModelLoader.setCustomStateMapper(block, (new StateMap.Builder()).ignore(DCStateBase.SIDE,
				DCStateBase.POWERED).build());
		ModelBakery.registerItemVariants(Item.getItemFromBlock(block), new ModelResourceLocation(
				domein + ":" + "basetile"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(
				domein + ":" + name, "inventory"));
	}

	public void regBlockJson(Block block, String domein, String name) {
		if (block == null)
			return;
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(
				domein + ":" + name, "inventory"));
	}

}
