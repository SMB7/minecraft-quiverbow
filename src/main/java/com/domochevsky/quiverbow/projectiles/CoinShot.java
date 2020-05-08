package com.domochevsky.quiverbow.projectiles;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class CoinShot extends _ProjectileBase
{
	private boolean shouldDrop;
	
	public CoinShot(World world) { super(world); }

	public CoinShot(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	
	public CoinShot(World world, Entity entity, float speed, float accHor, float AccVert) 
    {
        super(world);
        this.doSetup(entity, speed, accHor, AccVert, entity.rotationYaw, entity.rotationPitch);
    }
	
	
	public void setDrop(boolean set) { this.shouldDrop = set; }

	
	@Override
	public void onImpact(MovingObjectPosition hitPos)	// Server-side
	{
		if (hitPos.entityHit != null) 
    	{
    		// Firing
    		hitPos.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) this.damage);	// Damage gets applied here
    		
    		hitPos.entityHit.hurtResistantTime = 0;
        }
        else 
        {        	
        	Block block = this.worldObj.getBlockState(hitPos.getBlockPos()).getBlock(); //unused?
        	
        	// Glass breaking
            Helper.tryBlockBreak(this.worldObj, this, hitPos, 1);
            
            if (this.shootingEntity != null && this.shootingEntity instanceof EntityPlayer)
            {
            	EntityPlayer player = (EntityPlayer) this.shootingEntity;
            	
            	if (this.shouldDrop && !player.capabilities.isCreativeMode)
            	{            		
    	        	ItemStack nuggetStack = new ItemStack(Items.gold_nugget);
    	        	EntityItem entityitem = new EntityItem(this.worldObj,
    	        										   hitPos.getBlockPos().getX(),
    	        										   hitPos.getBlockPos().getY() + (double)0.5F,
    	        										   hitPos.getBlockPos().getZ(), nuggetStack);
    	            entityitem.setDefaultPickupDelay(); //reminder: default delay is 10 anyway, which is what this originally set it to
    	            
    	            if (captureDrops) { capturedDrops.add(entityitem); }
    	            else { this.worldObj.spawnEntityInWorld(entityitem); }
            	}
            	// else, they're in creative mode, so no dropping nuggets
            }
            // else, either we don't have a shooter or they're not a player
        }
    	
    	// SFX
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 13, (byte) 1);
        this.worldObj.playSoundAtEntity(this, "random.break", 1.0F, 3.0F);
        
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()	// Called by the renderer. Expects a 3 item byte array
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, icon
		type[1] = 1;	// Length, (mis)used as indicator for the icon. Gold nugget
		type[2] = 0;	// Width
		
		return type; // Fallback, 0 0 0
	}
	
	
	@Override
	public String getEntityTexturePath() { return null; }
}
