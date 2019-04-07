package defeatedcrow.util.client;

import defeatedcrow.util.client.render.RenderEntityBigCushion;
import defeatedcrow.util.client.render.RenderEntityFlowerPot;
import defeatedcrow.util.client.render.TESRHopperFluid;
import defeatedcrow.util.client.render.TESRIBC;
import defeatedcrow.util.client.render.TESRLargeClock;
import defeatedcrow.util.client.render.TESRMCClock;
import defeatedcrow.util.core.DCUtilCommon;
import defeatedcrow.util.core.DCUtilCore;
import defeatedcrow.util.core.DCUtilInit;
import defeatedcrow.util.material.EntityBigCushion;
import defeatedcrow.util.material.EntityFlowerPot;
import defeatedcrow.util.material.TileHopperFluid;
import defeatedcrow.util.material.TileIBC;
import defeatedcrow.util.material.TileMCClock_L;
import defeatedcrow.util.material.TileRealtimeClock_L;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class DCUtilClient extends DCUtilCommon {
	@Override
	public void loadMaterial() {
		super.loadMaterial();
		JsonHelperDC.INSTANCE.regSimpleItem(DCUtilInit.steelGear, DCUtilCore.MOD_ID, "dcutil_steel", 1);
		JsonHelperDC.INSTANCE.regSimpleItem(DCUtilInit.cushionGray, DCUtilCore.MOD_ID, "dcutil_cushion", 0);
		JsonHelperDC.INSTANCE.regSimpleItem(DCUtilInit.flowerPot, DCUtilCore.MOD_ID, "dcutil_flowerpot", 0);

		JsonHelperDC.INSTANCE.regBlockJson(DCUtilInit.fenceLadderSteel, DCUtilCore.MOD_ID, "dcutil_ladder_steel");
		JsonHelperDC.INSTANCE.regTEBlock2(DCUtilInit.realtimeClock_L, DCUtilCore.MOD_ID, "dcutil_realtimeclock_l");
		JsonHelperDC.INSTANCE.regTEBlock2(DCUtilInit.mcClock_L, DCUtilCore.MOD_ID, "dcutil_mcclock_l");
		JsonHelperDC.INSTANCE.regBlockJson(DCUtilInit.faucet, DCUtilCore.MOD_ID, "dcutil_faucet");
		JsonHelperDC.INSTANCE.regBlockJson(DCUtilInit.faucet_adv, DCUtilCore.MOD_ID, "dcutil_faucet_adv");
		JsonHelperDC.INSTANCE.regTEBlock(DCUtilInit.IBC, DCUtilCore.MOD_ID, "dcutil_ibc");
		JsonHelperDC.INSTANCE.regBlockJson(DCUtilInit.hopperFilter, DCUtilCore.MOD_ID, "dcutil_hopper_filter");
		JsonHelperDC.INSTANCE.regTEBlock3(DCUtilInit.hopperFluid, DCUtilCore.MOD_ID, "dcutil_hopper_fluid");
		JsonHelperDC.INSTANCE.regBlockJson(DCUtilInit.monitorRS, DCUtilCore.MOD_ID, "dcutil_monitor_rs");
		JsonHelperDC.INSTANCE.regBlockJson(DCUtilInit.monitorCM, DCUtilCore.MOD_ID, "dcutil_monitor_cm");
	}

	@Override
	public void loadEntity() {
		super.loadEntity();
		registRender(EntityBigCushion.class, RenderEntityBigCushion.class);
		registRender(EntityFlowerPot.class, RenderEntityFlowerPot.class);
	}

	@Override
	public void loadInit() {
		super.loadInit();
	}

	@Override
	public void loadTE() {
		super.loadTE();
		ClientRegistry.bindTileEntitySpecialRenderer(TileRealtimeClock_L.class, new TESRLargeClock());
		ClientRegistry.bindTileEntitySpecialRenderer(TileMCClock_L.class, new TESRMCClock());
		ClientRegistry.bindTileEntitySpecialRenderer(TileIBC.class, new TESRIBC());
		ClientRegistry.bindTileEntitySpecialRenderer(TileHopperFluid.class, new TESRHopperFluid());
	}

	// ruby氏に無限に感謝
	public static void registRender(Class<? extends Entity> cls, final Class<? extends Render> render) {
		RenderingRegistry.registerEntityRenderingHandler(cls, new IRenderFactory() {
			@Override
			public Render createRenderFor(RenderManager manager) {
				try {
					return render.getConstructor(manager.getClass()).newInstance(manager);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
}
