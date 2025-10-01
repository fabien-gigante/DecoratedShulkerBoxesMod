package com.fabien_gigante;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;

public class DecoratedShulkerBoxBlockModel extends ShulkerBoxBlockEntityRenderer.ShulkerBoxBlockModel {
    private ModelPart lid, base;
    public DecoratedShulkerBoxBlockModel(ModelPart root) { 
        super(root); 
        this.lid = root.getChild("lid");
        this.base = root.getChild("base");
    }
    public void setAngles(Float float_) {
        if (float_.isNaN()) { this.lid.hidden = true; this.base.hidden = false; }
        else { this.lid.hidden = false; this.base.hidden = true; super.setAngles(float_); }
    }
}
