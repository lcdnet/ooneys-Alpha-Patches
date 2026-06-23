package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderPlayer extends RenderLiving {
	private ModelBiped modelBipedMain = (ModelBiped)this.mainModel;
	private ModelBiped modelArmorChestplate = new ModelBiped(1.0F);
	private ModelBiped modelArmor = new ModelBiped(0.5F);
	private static final String[] armorFilenamePrefix = new String[]{"cloth", "chain", "iron", "diamond", "gold"};

	public RenderPlayer() {
		super(new ModelBiped(0.0F), 0.5F);
	}

	protected boolean setArmorModel(EntityPlayer var1, int var2) {
		ItemStack var3 = var1.inventory.armorItemInSlot(3 - var2);
		if(var3 != null) {
			Item var4 = var3.getItem();
			if(var4 instanceof ItemArmor) {
				ItemArmor var5 = (ItemArmor)var4;
				this.loadTexture("/armor/" + armorFilenamePrefix[var5.renderIndex] + "_" + (var2 == 2 ? 2 : 1) + ".png");
				ModelBiped var6 = var2 == 2 ? this.modelArmor : this.modelArmorChestplate;
				var6.bipedHead.showModel = var2 == 0;
				var6.bipedHeadwear.showModel = var2 == 0;
				var6.bipedBody.showModel = var2 == 1 || var2 == 2;
				var6.bipedRightArm.showModel = var2 == 1;
				var6.bipedLeftArm.showModel = var2 == 1;
				var6.bipedRightLeg.showModel = var2 == 2 || var2 == 3;
				var6.bipedLeftLeg.showModel = var2 == 2 || var2 == 3;
				this.setRenderPassModel(var6);
				return true;
			}
		}

		return false;
	}

	public void renderPlayer(EntityPlayer var1, double var2, double var4, double var6, float var8, float var9) {
		ItemStack var10 = var1.inventory.getCurrentItem();
		this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = var10 != null;
		this.modelArmorChestplate.isSneak = this.modelArmor.isSneak;
	    this.modelBipedMain.isSneak = var1.isSneaking();
		super.doRenderLiving(var1, var2, var4 - (double)var1.yOffset, var6, var8, var9);
		this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
		this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = false;
		FontRenderer var11 = this.getFontRendererFromRenderManager();
		float var12 = 1.6F;
		float var13 = (float)(1.0D / 60.0D) * var12;
		GL11.glPushMatrix();
		GL11.glTranslatef((float)var2 + 0.0F, (float)var4 + 2.3F, (float)var6);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		float var14 = var1.getDistanceToEntity(this.renderManager.player);
		var13 = (float)((double)var13 * (Math.sqrt((double)var14) / 2.0D));
		GL11.glScalef(-var13, -var13, var13);
		String var15 = var1.username;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator var16 = Tessellator.instance;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		var16.startDrawingQuads();
		int var17 = var11.getStringWidth(var15) / 2;
		var16.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
		var16.addVertex((double)(-var17 - 1), -1.0D, 0.0D);
		var16.addVertex((double)(-var17 - 1), 8.0D, 0.0D);
		var16.addVertex((double)(var17 + 1), 8.0D, 0.0D);
		var16.addVertex((double)(var17 + 1), -1.0D, 0.0D);
		var16.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		var11.drawString(var15, -var11.getStringWidth(var15) / 2, 0, 553648127);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		var11.drawString(var15, -var11.getStringWidth(var15) / 2, 0, -1);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}

	protected void renderSpecials(EntityPlayer var1, float var2) {

		float var5;
		if(var1.username.equals("deadmau5") && this.loadDownloadableImageTexture(var1.skinUrl, (String)null)) {
			for(int var19 = 0; var19 < 2; ++var19) {
				var5 = var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var2 - (var1.prevRenderYawOffset + (var1.renderYawOffset - var1.prevRenderYawOffset) * var2);
				float var6 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var2;
				GL11.glPushMatrix();
				GL11.glRotatef(var5, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(var6, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(6.0F / 16.0F * (float)(var19 * 2 - 1), 0.0F, 0.0F);
				GL11.glTranslatef(0.0F, -(6.0F / 16.0F), 0.0F);
				GL11.glRotatef(-var6, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(-var5, 0.0F, 1.0F, 0.0F);
				float var7 = 4.0F / 3.0F;
				GL11.glScalef(var7, var7, var7);
				this.modelBipedMain.renderEars(1.0F / 16.0F);
				GL11.glPopMatrix();
			}
		}

		boolean var21 = false;
		if (var1.playerCloakUrl != null && var1.playerCloakUrl.startsWith("/")) {
			this.loadTexture(var1.playerCloakUrl);
			var21 = true;
		} else {
			var21 = this.loadDownloadableImageTexture(var1.playerCloakUrl, (String)null);
		}

		if(var21) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, 2.0F / 16.0F);
			double var20 = var1.field_20066_r + (var1.field_20063_u - var1.field_20066_r) * (double)var2 - (var1.prevPosX + (var1.posX - var1.prevPosX) * (double)var2);
			double var22 = var1.field_20065_s + (var1.field_20062_v - var1.field_20065_s) * (double)var2 - (var1.prevPosY + (var1.posY - var1.prevPosY) * (double)var2);
			double var8 = var1.field_20064_t + (var1.field_20061_w - var1.field_20064_t) * (double)var2 - (var1.prevPosZ + (var1.posZ - var1.prevPosZ) * (double)var2);
			float var10 = var1.prevRenderYawOffset + (var1.renderYawOffset - var1.prevRenderYawOffset) * var2;
			double var11 = (double)MathHelper.sin(var10 * (float)Math.PI / 180.0F);
			double var13 = (double)(-MathHelper.cos(var10 * (float)Math.PI / 180.0F));
			float var15 = (float)var22 * 10.0F;
			if(var15 < -6.0F) {
				var15 = -6.0F;
			}

			if(var15 > 32.0F) {
				var15 = 32.0F;
			}

			float var16 = (float)(var20 * var11 + var8 * var13) * 100.0F;
			float var17 = (float)(var20 * var13 - var8 * var11) * 100.0F;
			if(var16 < 0.0F) {
				var16 = 0.0F;
			}

			float var18 = var1.prevCameraYaw + (var1.cameraYaw - var1.prevCameraYaw) * var2;
			var15 += MathHelper.sin((var1.prevDistanceWalkedModified + (var1.distanceWalkedModified - var1.prevDistanceWalkedModified) * var2) * 6.0F) * 32.0F * var18;
			if(var1.isSneaking()) {
				var15 += 25.0F;
			}

			GL11.glRotatef(6.0F + var16 / 2.0F + var15, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(var17 / 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-var17 / 2.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			this.modelBipedMain.renderCloak(1.0F / 16.0F);
			GL11.glPopMatrix();
		}


		ItemStack var3 = var1.inventory.getCurrentItem();
		if(var3 != null) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedRightArm.renderWithRotation(1.0F / 16.0F);
			GL11.glTranslatef(-(1.0F / 16.0F), 7.0F / 16.0F, 1.0F / 16.0F);


			float var4;
			if(var3.itemID < 256 && RenderBlocks.renderItemIn3d(Block.blocksList[var3.itemID].getRenderType())) {
				var4 = 0.5F;
				GL11.glTranslatef(0.0F, 3.0F / 16.0F, -(5.0F / 16.0F));
				var4 *= 12.0F / 16.0F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(var4, -var4, var4);
			} else if(Item.itemsList[var3.itemID].isFull3D()) {
				var4 = 10.0F / 16.0F;
				GL11.glTranslatef(0.0F, 3.0F / 16.0F, 0.0F);
				GL11.glScalef(var4, -var4, var4);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				var4 = 6.0F / 16.0F;
				GL11.glTranslatef(0.25F, 3.0F / 16.0F, -(3.0F / 16.0F));
				GL11.glScalef(var4, var4, var4);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			this.renderManager.itemRenderer.renderItem(var3);
			GL11.glPopMatrix();
		}

	}

	protected void scalePlayer(EntityPlayer var1, float var2) {
		float var3 = 15.0F / 16.0F;
		GL11.glScalef(var3, var3, var3);
	}

	public void drawFirstPersonHand() {
		this.modelBipedMain.swingProgress = 0.0F;
		this.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / 16.0F);
		this.modelBipedMain.bipedRightArm.render(1.0F / 16.0F);
	}

	protected void preRenderCallback(EntityLiving var1, float var2) {
		this.scalePlayer((EntityPlayer)var1, var2);
	}

	protected boolean shouldRenderPass(EntityLiving var1, int var2) {
		return this.setArmorModel((EntityPlayer)var1, var2);
	}

	protected void renderEquippedItems(EntityLiving var1, float var2) {
		this.renderSpecials((EntityPlayer)var1, var2);
	}

	public void doRenderLiving(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
		this.renderPlayer((EntityPlayer)var1, var2, var4, var6, var8, var9);
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.renderPlayer((EntityPlayer)var1, var2, var4, var6, var8, var9);
	}
}