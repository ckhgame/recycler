package ovh.corail.recycler.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelRecycler extends ModelBase {
	public ModelRenderer Base01;
	public ModelRenderer Base02;
	public ModelRenderer Disque;

	public ModelRecycler() {
	        this.textureWidth = 64;
	        this.textureHeight = 32;
	        this.Base01 = new ModelRenderer(this, 0, 0);
	        this.Base01.setRotationPoint(-8.0F, 23.0F, -8.0F);
	        this.Base01.addBox(0.0F, 0.0F, 0.0F, 16, 1, 16, 0.0F);
	        this.Base02 = new ModelRenderer(this, 0, 0);
	        this.Base02.setRotationPoint(-6.0F, 18.0F, -6.0F);
	        this.Base02.addBox(0.0F, 0.0F, 0.0F, 12, 5, 12, 0.0F);
	        this.Disque = new ModelRenderer(this, 0, 0);
	        this.Disque.setRotationPoint(0.0F, 13.0F, -5.0F);
	        this.Disque.addBox(0.0F, 0.0F, 0.0F, 0, 5, 10, 0.0F);
	    }

	public void render() {
		this.Base01.render(1.0F);
		this.Base02.render(1.0F);
		this.Disque.render(1.0F);
	}

	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
