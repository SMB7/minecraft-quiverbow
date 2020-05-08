package com.domochevsky.quiverbow.ammo;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LargeNetherrackMagazine extends _AmmoBase
{
	public LargeNetherrackMagazine()
	{
		this.setMaxStackSize(1);	// No stacking, since we're filling these up
		
		this.setMaxDamage(200);		// Filled with gold nuggets (8 shots with 9 scatter, 24 with 3 scatter)
		this.setCreativeTab(CreativeTabs.tabCombat);	// On the combat tab by default, since this is amunition
		
		this.setHasSubtypes(true);
	}
	
	
	private Item rack = Item.getItemFromBlock(Blocks.netherrack);
	
	
	@SideOnly(Side.CLIENT)
	private IIcon Icon;
	@SideOnly(Side.CLIENT)
	private IIcon Icon_Empty;
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister) 
	{ 
		Icon = par1IconRegister.registerIcon("quiverchevsky:ammo/LargeNetherAmmo");
		Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:ammo/LargeNetherAmmo_Empty");
	}
	
	
	@Override
    public IIcon getIconFromDamage(int meta) 
    {
		if (meta == this.getMaxDamage()) { return Icon_Empty; }
		return Icon;
    }
	
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) 
    {  
		if (world.isRemote) { return stack; }				// Not doing this on client side
		if (stack.getItemDamage() == 0) { return stack; }	// Already fully loaded
		
		if (player.isSneaking())
		{
			this.fillEight(stack, world, player);
			return stack;
		}
		// else, not sneaking, so just filling one
		
		boolean doSFX = false;
		
		if (player.inventory.hasItem(rack))
		{
			int dmg = stack.getItemDamage() - 1;
			stack.setItemDamage(dmg);
			
			player.inventory.consumeInventoryItem(rack);	// We're just grabbing what we need from the inventory
			
			// SFX
			doSFX = true;
		}
		// else, doesn't have what it takes
		
		if (doSFX) { world.playSoundAtEntity(player, "random.wood_click", 0.5F, 0.3F); }
		
		return stack;
    }
	
	
	private void fillEight(ItemStack stack, World world, EntityPlayer player)
	{
		boolean doSFX = false;
		
		int counter = 0;
		
		while (counter < 8)
		{
			if (player.inventory.hasItem(rack))
			{
				int dmg = stack.getItemDamage() - 1;
				stack.setItemDamage(dmg);
				
				player.inventory.consumeInventoryItem(rack);	// We're just grabbing what we need from the inventory
				
				doSFX = true;
			}
			// else, doesn't have what it takes
			
			counter += 1;
		}
		
		if (doSFX) { world.playSoundAtEntity(player, "random.wood_click", 1.0F, 0.2F); }
	}
	
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean unknown) 
	{
		list.add(EnumChatFormatting.BLUE + "Netherrack: " + (this.getMaxDamage() - stack.getItemDamage()) + " / " + this.getMaxDamage());
		list.add(EnumChatFormatting.YELLOW + "Use magazine to fill it with Netherrack.");
		list.add(EnumChatFormatting.YELLOW + "Crouch-use to fill it with 8 Netherrack.");
		list.add("A loading helper, full of netherrack.");
		
		if (!player.inventory.hasItem(rack)) { list.add(EnumChatFormatting.RED + "You don't have Netherrack."); }
		if (player.capabilities.isCreativeMode) { list.add(EnumChatFormatting.RED + "Does not work in creative mode."); }
	}
	
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) { return "Large Netherrack Magazine"; }
	
	
	@Override
	public void addRecipes() 
	{
		GameRegistry.addRecipe(new ItemStack(this, 1, this.getMaxDamage()), "x x", "x x", "xgx",
		         'x', Blocks.nether_brick, 
		         'g', Items.iron_ingot
		 );
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) 	// getSubItems
	{
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack( item, 1, this.getMaxDamage() ));
	}
	
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) { return true; }	// Always showing this bar, since it acts as ammo display
}
