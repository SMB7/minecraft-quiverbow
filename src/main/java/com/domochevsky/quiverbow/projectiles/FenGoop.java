package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

//new imports
import net.minecraft.util.BlockPos;


public class FenGoop extends _ProjectileBase
{	
	public FenGoop(World world) { super(world); }

	public FenGoop(World world, Entity entity, float speed)
    {
        super(world);
        this.doSetup(entity, speed);
    }
	
	public int lightTick;
	
	
	@Override
	public void onImpact(MovingObjectPosition target)
	{
		if (target.entityHit != null) // hit a entity
    	{
    		target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.shootingEntity), (float) 0);	// No dmg, but knockback
            target.entityHit.hurtResistantTime = 0;
            target.entityHit.setFire(fireDuration); 	// Some minor fire, for flavor
    	}
    	else // hit the terrain
        {        	
        	int plusX = 0;
    		int plusY = 0;
    		int plusZ = 0;
    		
    		int posiX = target.getBlockPos().getX();
    		int posiY = target.getBlockPos().getY();
    		int posiZ = target.getBlockPos().getZ();

    		//Block targetBlock = this.worldObj.getBlock(posiX, posiY, posiZ);
    		
    		// Is the attached block a valid material?
    		boolean canPlace = false;
    		if ( Helper.hasValidMaterial(this.worldObj, posiX, posiY, posiZ) ) { canPlace = true; }
    		
        	// Glass breaking
            if ( Helper.tryBlockBreak(this.worldObj, this, target, 0)) { canPlace = false; }
    		
    		if (target.sideHit == EnumFacing.valueOf("DOWN"))       { plusY = -1; } // Bottom		
    		else if (target.sideHit == EnumFacing.valueOf("UP"))    { plusY = 1; } 	// Top
    		else if (target.sideHit == EnumFacing.valueOf("EAST"))  { plusZ = -1; } // East
    		else if (target.sideHit == EnumFacing.valueOf("WEST"))  { plusZ = 1; } 	// West
    		else if (target.sideHit == EnumFacing.valueOf("NORTH")) { plusX = -1; } // North
    		else if (target.sideHit == EnumFacing.valueOf("SOUTH")) { plusX = 1; } 	// South
    		//In EnumFacing.class, the directions are ordered DOWN, UP, NORTH, SOUTH, WEST, EAST. If that's a problem these may need to be changed to match.
    		
    		// Is the space free?
    		BlockPos newBlock = new BlockPos((int)posiX + plusX, (int)posiY + plusY, (int)posiZ + plusZ);
    		if (this.worldObj.getBlockState(newBlock).getBlock().getMaterial() == Material.air       ||
    				this.worldObj.getBlockState(newBlock).getBlock().getMaterial() == Material.fire  ||
    				this.worldObj.getBlockState(newBlock).getBlock().getMaterial() == Material.grass ||
    				this.worldObj.getBlockState(newBlock).getBlock().getMaterial() == Material.snow  ||
    				this.worldObj.getBlockState(newBlock).getBlock().getMaterial() == Material.water)
        	{
    			// Putting light there (if we can)
    			if (canPlace)
    			{
	    			this.worldObj.setBlockState(newBlock, Main.fenLight.getDefaultState()); //setBlock(posiX + plusX, posiY + plusY, posiZ + plusZ, Main.fenLight, 0, 3)
	    			//this.worldObj.setBlockMetadataWithNotify(posiX + plusX, posiY + plusY, posiZ + plusZ, target.sideHit, 3); //this is probably important but I don't know what it does
	    			
	    			/* if (this.lightTick != 0) 
	    			{ 
	    				this.worldObj.scheduleBlockUpdate(posiX + plusX, posiY + plusY, posiZ + plusZ, Main.fenLight, this.lightTick); 
	    			} */ //don't know what this does either :(
	    			// else, stays on indefinitely
    			}
    			// else, can't place. The block isn't of a valid material
        	}
    		// else, none of the allowed materials
        }
    	
    	// SFX
    	for (int i = 0; i < 8; ++i) { this.worldObj.spawnParticle(EnumParticleTypes.valueOf("slime"), this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D); }
        this.worldObj.playSoundAtEntity(this, Block.soundTypeGlass.getBreakSound(), 1.0F, 1.0F);
        
        this.setDead();		// We've hit something, so begone with the projectile
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 3;	// Type 3, item
		type[1] = 5;	// Length, misused as item type. glowstone dust
		type[2] = 2;	// Width
		
		return type;
	}
}
