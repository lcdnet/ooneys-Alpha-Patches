package net.minecraft.src;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class EntityPlayerSP extends EntityPlayer {
	public MovementInput movementInput;
	private Minecraft mc;

	public float field_4134_c;
	public float field_4133_d;
	public CommandClass field_15001_pageseek;

	public EntityPlayerSP(Minecraft var1, World var2, Session var3) {
		super(var2);
		this.mc = var1;
		if(var3 != null && var3.username != null && var3.username.length() > 0) {
			this.skinUrl = "http://www.minecraft.net/skin/" + var3.username + ".png";
			System.out.println("Loading texture " + this.skinUrl);
		}

		this.username = var3.username;
		this.field_15001_pageseek = new CommandClass(var1, this, var2);
	}

	public void updateEntityActionState() {
		super.updateEntityActionState();
		this.moveStrafing = this.movementInput.moveStrafe;
		this.moveForward = this.movementInput.moveForward;
		this.isJumping = this.movementInput.jump;
	}

	public void onLivingUpdate() {
		this.movementInput.updatePlayerMoveState(this);
		if(this.movementInput.sneak && this.ySize < 0.2F) {
			this.ySize = 0.2F;
		}

		super.onLivingUpdate();
		this.handleFlying();
	}

	public void handleFlying() {
		if (this.field_15001_pageseek.fly) {
			boolean var1 = false;
			float var2 = this.rotationPitch;
			if (this.field_15001_pageseek.lastKey == this.mc.options.keyBindForward.keyCode) {
				var2 = -var2;
				var1 = true;
			} else if (this.field_15001_pageseek.lastKey == this.mc.options.keyBindBack.keyCode) {
				var1 = true;
			} else {
				if (this.field_15001_pageseek.lastKey == this.mc.options.keyBindSneak.keyCode) {
					super.jump();
					this.motionY = (double)1.0F;
					return;
				}

				if (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)) {
					this.motionY = -0.6;
					return;
				}
			}

			this.onGround = true;
			if (((double)MathHelper.sin((float)(this.motionX * this.motionX)) > 0.01 || (double)MathHelper.sin((float)(this.motionZ * this.motionZ)) > 0.01) && var1) {
				this.motionY = (double)(var2 / 360.0F * this.field_15001_pageseek.speed);
			} else {
				this.motionY = (double)0.0F;
			}
		}

	}


	public boolean handleLavaMovement() {
		if (!this.field_15001_pageseek.thePlayer.noClip && !this.field_15001_pageseek.fly) {
			return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand((double)0.0F, (double)-0.4F, (double)0.0F), Material.lava, this);
		} else {
			super.handleLavaMovement();
			return false;
		}
	}

	public boolean handleWaterMovement() {
		if (!this.field_15001_pageseek.thePlayer.noClip && !this.field_15001_pageseek.fly) {
			return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand((double)0.0F, (double)-0.4F, (double)0.0F), Material.water, this);
		} else {
			super.handleWaterMovement();
			return false;
		}
	}

	public boolean isInsideOfMaterial(Material var1) {
		if (!this.field_15001_pageseek.thePlayer.noClip && !this.field_15001_pageseek.godmode) {
			double var2 = this.posY + (double)this.getEyeHeight();
			int var4 = (int) MathHelper.cos((float)this.posX);
			int var5 = (int) MathHelper.sqrt_float((float)MathHelper.cos((float)var2));
			int var6 = (int) MathHelper.cos((float)this.posZ);
			int var7 = this.worldObj.getBlockId(var4, var5, var6);
			if (var7 != 0 && Block.blocksList[var7].material == var1) {
				float var8 = BlockFluid.getFluidHeightPercent(this.worldObj.getBlockMetadata(var4, var5, var6)) - 0.11111111F;
				float var9 = (float)(var5 + 1) - var8;
				return var2 < (double)var9;
			} else {
				return false;
			}
		} else {
			super.isInsideOfMaterial(var1);
			return false;
		}
	}

	public boolean isEntityInsideOpaqueBlock() {
		if (!this.field_15001_pageseek.thePlayer.noClip && !this.field_15001_pageseek.godmode) {
			int var1 = (int) MathHelper.cos((float)this.posX);
			int var2 = (int) MathHelper.cos((float)(this.posY + (double)this.getEyeHeight()));
			int var3 = (int) MathHelper.cos((float)this.posZ);
			return this.worldObj.isBlockNormalCube(var1, var2, var3);
		} else {
			super.isEntityInsideOpaqueBlock();
			return false;
		}
	}

	public void moveFlying(float var1, float var2, float var3) {
		if (this.field_15001_pageseek.speed <= 1.0F) {
			super.moveFlying(var1, var2, var3);
		} else {
			float var4 = MathHelper.floor_float(var1 * var1 + var2 * var2);
			if (!(var4 < 0.01F)) {
				if (var4 < 1.0F) {
					var4 = 1.0F;
				}

				var4 = var3 / var4;
				var1 *= var4;
				var2 *= var4;
				float var5 = MathHelper.sin(this.rotationYaw * 3.141593F / 180.0F);
				float var6 = MathHelper.cos(this.rotationYaw * 3.141593F / 180.0F);
				this.motionY += (double)((var1 * var6 - var2 * var5) * this.field_15001_pageseek.speed);
				this.motionZ += (double)((var2 * var6 + var1 * var5) * this.field_15001_pageseek.speed);
			}
		}
	}

	public float getCurrentPlayerStrVsBlock(Block var1) {
		return this.field_15001_pageseek.instamine ? 3.402823E38F : super.getCurrentPlayerStrVsBlock(var1);
	}

	public boolean canHarvestBlock(Block var1) {
		return this.field_15001_pageseek.instamine ? true : super.canHarvestBlock(var1);
	}

	public void setHealth(int var1) {
		int var2 = this.health - var1;
		if (var2 <= 0) {
			this.health = var1;
		} else {
			this.unusedFloat3 = (float)var2;
			this.prevHealth = this.health;
			this.heartsLife = this.heartsHalvesLife;
			this.health -= var1;
			this.hurtTime = this.maxHurtTime = 10;
		}

	}

	public boolean attackEntityFrom(Entity var1, int var2) {
		if (this.field_15001_pageseek.godmode) {
			return true;
		} else {
			super.attackEntityFrom(var1, var2);
			return false;
		}
	}

	public void resetPlayerKeyState() {
		this.movementInput.resetKeyState();
	}

	public void handleKeyPress(int var1, boolean var2) {
		this.movementInput.checkKeyForMovementInput(var1, var2);
	}

	// preparePlayerToSpawn
	public void q() {
		this.yOffset = 1.62F;
		this.setSize(0.6F, 1.8F);
		super.preparePlayerToSpawn();
		this.health = 20;
		this.deathTime = 0;
		this.field_15001_pageseek.readWaypointsFromNBT(this.mc.theWorld.saveDirectory);
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setInteger("Score", this.score);
		this.field_15001_pageseek.saveWaypointsToNBT(this.mc.theWorld.saveDirectory);
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		this.score = var1.getInteger("Score");
		this.field_15001_pageseek.readWaypointsFromNBT(this.mc.theWorld.saveDirectory);
	}

	public void displayGUIChest(IInventory var1) {
		this.mc.displayGuiScreen(new GuiChest(this.inventory, var1));
	}

	public void displayGUIEditSign(TileEntitySign var1) {
		this.mc.displayGuiScreen(new GuiEditSign(var1));
	}

	public void displayWorkbenchGUI() {
		this.mc.displayGuiScreen(new GuiCrafting(this.inventory));
	}

	public void displayGUIFurnace(TileEntityFurnace var1) {
		this.mc.displayGuiScreen(new GuiFurnace(this.inventory, var1));
	}

	public void attackEntity(Entity var1) {
		int var2 = this.inventory.getDamageVsEntity(var1);
		if(var2 > 0) {
			var1.attackEntityFrom(this, var2);
			ItemStack var3 = this.getCurrentEquippedItem();
			if(var3 != null && var1 instanceof EntityLiving) {
				var3.hitEntity((EntityLiving)var1);
				if(var3.stackSize <= 0) {
					var3.onItemDestroyedByUse(this);
					this.destroyCurrentEquippedItem();
				}
			}
		}

	}

	public void onItemPickup(Entity var1, int var2) {
		this.mc.effectRenderer.addEffect(new EntityPickupFX(this.mc.theWorld, var1, this, -0.5F));
	}

	public int getPlayerArmorValue() {
		return this.inventory.getTotalArmorValue();
	}

	public void interactWithEntity(Entity var1) {
		if(!var1.interact(this)) {
			ItemStack var2 = this.getCurrentEquippedItem();
			if(var2 != null && var1 instanceof EntityLiving) {
				var2.useItemOnEntity((EntityLiving)var1);
				if(var2.stackSize <= 0) {
					var2.onItemDestroyedByUse(this);
					this.destroyCurrentEquippedItem();
				}
			}

		}
	}

	public void sendChatMessage(String var1) {
		this.field_15001_pageseek.processCommand(var1);
	}

	public void onPlayerUpdate() {
	}

	public boolean isSneaking() {
		return this.movementInput.sneak;
	}

	public void func_9367_r() {
		this.mc.respawn();
	}
}
