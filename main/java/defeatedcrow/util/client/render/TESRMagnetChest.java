package defeatedcrow.util.client.render;

import defeatedcrow.util.client.model.DCTileModelBase;
import defeatedcrow.util.client.model.ModelMetalChest;
import defeatedcrow.util.material.DCLockableTE;
import defeatedcrow.util.material.TileLowChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

public class TESRMagnetChest extends TileEntitySpecialRenderer<DCLockableTE> {

	private final ModelMetalChest model = new ModelMetalChest();

	@Override
	public void render(DCLockableTE te, double x, double y, double z, float partialTicks, int destroyStage, float a) {
		int type = 0;
		int face = 0;
		float f = 0.0F;
		boolean open = false;
		if (te instanceof TileLowChest && ((TileLowChest) te).isOpen) {
			open = true;
		}

		if (te.hasWorld()) {
			int meta = te.getBlockMetadata();

			type = meta & 3;
			face = 5 - (meta >> 2);
			if (face == 2) {
				f = 180F;
			}
			if (face == 3) {
				f = 0F;
			}
			if (face == 4) {
				f = 90F;
			}
			if (face == 5) {
				f = -90F;
			}
		}

		DCTileModelBase model = this.getModel(type);

		this.bindTexture(new ResourceLocation(getTexPass(type)));

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);

		GlStateManager.rotate(f, 0.0F, 1.0F, 0.0F);

		if (open) {
			model.render(-75.0F);
		} else {
			model.render(0.0F);
		}
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}

	protected String getTexPass(int i) {
		return "dcs_util:textures/tiles/metalchest_magnet.png";
	}

	protected DCTileModelBase getModel(int i) {
		return model;
	}

}
