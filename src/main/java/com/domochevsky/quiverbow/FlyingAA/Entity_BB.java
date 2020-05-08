package com.domochevsky.quiverbow.FlyingAA;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.AI.AI_RandomEquip;
import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;

//new imports
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;


public class Entity_BB extends Entity_AA
{
	public double movementSpeed = 0.5d;	// Half speed. They're QUITE fast
	
	public float ringRotationAngle = 0;	// For the model. Gets updated constantly
	
	public Entity_BB(World world)
	{
		super(world);
		this.renderDistanceWeight = 10.0d;
		
		this.height = 0.5f;
		this.setEntityBoundingBox(AxisAlignedBB.fromBounds(-0.5d, 1.0d, -0.5d, 0.5d, 1.0d + this.height, 0.5d));
		
		this.setCanPickUpLoot(false);
		
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.movementSpeed);
		
		//if (!world.isRemote) { AI_RandomEquip.setupGear(this); }	// Hand me my gear! 
		
		this.canFly = true;
		this.hasMobilityUpgrade = true;
		//this.getNavigator().setAvoidsWater(true);	// Airborne, not sea borne
	}	//setAvoidsWater no longer exists? This isn't the biggest deal so I'll just disable it for now
	
	
	public Entity_BB(World world, EntityPlayer player) 
	{
		super(world);
		
		this.renderDistanceWeight = 10.0d;
		
		if (player != null)
		{
			this.ownerName = player.getDisplayName().toString();
			
			this.setPositionAndRotation(player.posX, player.posY, player.posZ, player.cameraYaw, player.cameraPitch);
			this.worldObj.playSoundAtEntity(this, "random.anvil_land", 0.7f, 1.5f);
		}
		else
		{
			AI_RandomEquip.setupGear(this);	// Hand me my gear!
		}
		
		this.height = 0.5f;
		this.setEntityBoundingBox(AxisAlignedBB.fromBounds(-0.5d, 1.0d, -0.5d, 0.5d, 1.0d + this.height, 0.5d));
		
		this.setCanPickUpLoot(false);
		
		this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(this.movementSpeed);
		
		this.canFly = true;
		this.hasMobilityUpgrade = true;
		
		//this.getNavigator().setAvoidsWater(true);	// Airborne, not sea borne
	}	//see above
	

	@Override
	public void fall(float distance, float damageMultiplier) {  }	// No fall damage
	//fall is now public and cannot be protected here, and damageMultiplier was added.
	
	@Override
    public void onLivingUpdate()
    {
		super.onLivingUpdate();
		
		if (this.motionY < 0 && this.waypointY > this.posY)	// Let's try this... no falling down for you, unless you WANT to go down
		{
			this.motionY = 0;
			//this.getJumpHelper().doJump();
		}
		else
		{
			this.motionY *= 0.5;	// Half fall
		}
		
		stayOffGround(1);
		stayOffGround(2);
    }
	
	
	private void stayOffGround(int distance)
	{				//world.getBlockState(target.getBlockPos()).getBlock()
		Block block = this.worldObj.getBlockState(new BlockPos(MathHelper.ceiling_double_int(this.posX), MathHelper.ceiling_double_int(this.posY - distance), MathHelper.ceiling_double_int(this.posZ))).getBlock();
		
		if (block == null) // We're above the void? Go up!
		{
			this.motionY += 0.2;
		}
		else
		{
			if (block.getMaterial() == Material.air)
			{
				//return;	// High enough up
			}
			else
			{
				this.motionY += 0.2;	// There's ground below us, so go up
			}
		}
	}
}
