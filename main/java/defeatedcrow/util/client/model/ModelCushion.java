package defeatedcrow.util.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCushion extends ModelBase {

	ModelRenderer cushion;

	public ModelCushion(boolean baked) {
		super();

		textureWidth = 64;
		textureHeight = 32;

		cushion = new ModelRenderer(this, 0, 0);
		cushion.addBox(-8F, -8F, -8F, 16, 8, 16);
		cushion.setRotationPoint(0F, 0F, 0F);
		cushion.setTextureSize(64, 32);
		cushion.mirror = true;
		setRotation(cushion, 0.1396263F, 0F, 0F);
	}

	public void render(float scale, Entity entity) {
		render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, scale);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {
		this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
		cushion.render(scale);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scaleFactor, Entity entityIn) {
		super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
	}

}
