package com.fabien_gigante;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;

public class DecoratedBoxModel extends ShulkerBoxRenderer.ShulkerBoxModel {
    private ModelPart lid, base;
    public DecoratedBoxModel(ModelPart root) { 
        super(root); 
        this.lid = root.getChild("lid");
        this.base = root.getChild("base");
    }
    public void setupAnim(Float angle) {
        if (angle.isNaN()) { this.lid.skipDraw = true; this.base.skipDraw = false; }
        else { this.lid.skipDraw = false; this.base.skipDraw = true; super.setupAnim(angle); }
    }
}
