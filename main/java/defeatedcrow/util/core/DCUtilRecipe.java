package defeatedcrow.util.core;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class DCUtilRecipe {

	public static void addOreDic() {
		OreDictionary.registerOre("itemLeather", new ItemStack(Items.LEATHER, 1, 0));
		OreDictionary.registerOre("bucketEmpty", new ItemStack(Items.BUCKET, 1, 0));
		OreDictionary.registerOre("blockWool", new ItemStack(Blocks.WOOL, 1, 32767));
	}

	public static void addAnother() {
		OreDictionary.registerOre("gearSteel", new ItemStack(DCUtilInit.steelGear, 1, 0));
		OreDictionary.registerOre("ingotSteel", new ItemStack(DCUtilInit.steelGear, 1, 1));

		ShapedOreRecipe ret = new ShapedOreRecipe(new ResourceLocation(DCUtilCore.MOD_ID, "steel_gear_0"),
				new ItemStack(DCUtilInit.steelGear), new Object[] {
						" X ",
						"XYX",
						" X ",
						'X',
						"ingotSteel",
						'Y',
						"ingotIron"
				});
		ret.setRegistryName(new ResourceLocation(DCUtilCore.MOD_ID, "steel_gear_0"));
		ForgeRegistries.RECIPES.register(ret);

		GameRegistry.addSmelting(new ItemStack(Items.IRON_INGOT), new ItemStack(DCUtilInit.steelGear, 1, 1), 0.3F);
	}

}
