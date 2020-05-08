package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.RegularArrow;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Crossbow_Compact extends _WeaponBase
{
	public Crossbow_Compact() { super(1); }

	private String nameInternal = "Compact Crossbow";


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/Crossbow");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/Crossbow_Empty");
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) { return stack; }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return stack; }	// Is empty

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return stack;
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		if (this.getCooldown(stack) != 0) { return; }	// Hasn't cooled down yet

		// SFX
		entity.worldObj.playSoundAtEntity(entity, "random.bow", 1.0F, 0.5F);

		RegularArrow entityarrow = new RegularArrow(world, entity, (float) this.Speed);

		// Random Damage
		int dmg_range = this.DmgMax - this.DmgMin; 	// If max dmg is 20 and min is 10, then the range will be 10
		int dmg = world.rand.nextInt(dmg_range + 1);// Range will be between 0 and 10
		dmg += this.DmgMin;							// Adding the min dmg of 10 back on top, giving us the proper damage range (10-20)

		entityarrow.damage = dmg;
		entityarrow.knockbackStrength = this.Knockback;	// Comes with an inbuild knockback II

		world.spawnEntityInWorld(entityarrow);	// pew

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, 10);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		super.addInformation(stack, player, list, par4);

		if (player.capabilities.isCreativeMode)
		{
			list.add(EnumChatFormatting.BLUE + "Bolts: INFINITE / " + this.getMaxDamage());
		}
		else
		{
			int ammo = this.getMaxDamage() - this.getDamage(stack);
			list.add(EnumChatFormatting.BLUE + "Bolts: " + ammo + " / " + this.getMaxDamage());
		}

		list.add(EnumChatFormatting.BLUE + "Damage: " + this.DmgMin + " - " + this.DmgMax);
		list.add(EnumChatFormatting.GREEN + "Knockback " + this.Knockback + " on hit.");
		list.add(EnumChatFormatting.YELLOW + "Craft with 1 Arrow to reload.");
		list.add("A simple yet effective design.");
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);

		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();

		this.DmgMin = config.get(this.nameInternal, "What damage am I dealing, at least? (default 14)", 14).getInt();
		this.DmgMax = config.get(this.nameInternal, "What damage am I dealing, tops? (default 20)", 20).getInt();

		this.Speed = config.get(this.nameInternal, "How fast are my projectiles? (default 2.5 BPT (Blocks Per Tick))", 2.5).getDouble();

		this.Knockback = config.get(this.nameInternal, "How hard do I knock the target back when firing? (default 2)", 2).getInt();

		this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One compact crossbow (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "zxy", "xzy", "zxy",
					'x', Items.stick,
					'y', Items.string,
					'z', Blocks.planks
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		GameRegistry.addShapelessRecipe(new ItemStack(this),	// Fill the empty crossbow with one arrow
				Items.arrow,
				new ItemStack(this, 1 , this.getMaxDamage())
				);
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "Crossbow_empty"; }		// empty

		return "Crossbow";	// Regular
	}
}
