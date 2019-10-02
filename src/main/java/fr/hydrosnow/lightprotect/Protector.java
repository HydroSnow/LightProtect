package fr.hydrosnow.lightprotect;

import org.bukkit.Chunk;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

class Protector implements Listener {
	public boolean activated;

	public Protector() {
		activated = true;
	}

	private boolean block(final Chunk chunk) {
		return Area.getArea(chunk).get3() != null;
	}
	
	private boolean block(final Player player, final Chunk chunk) {
		final Area area = Area.getArea(chunk);
		return !((area.get3() == null) || player.hasPermission("lightprotect.bypass") || (area.permissionLevel(player) >= 1));
	}
	
	private boolean block(final Player player, final Chunk[] chunks) {
		for (final Chunk chunk : chunks)
			if (block(player, chunk))
				return true;
		return false;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void inventoryOpen(final InventoryOpenEvent e) {
		if (!activated)
			return;

		final Player player = (Player) e.getPlayer();

		if (e.getInventory().getHolder() instanceof DoubleChest) {
			final DoubleChest dc = (DoubleChest) e.getInventory().getHolder();

			if (block(player, new Chunk[] {
					((Chest) dc.getLeftSide()).getChunk(),
					((Chest) dc.getRightSide()).getChunk()
			}))
				e.setCancelled(true);
		} else if (block(player, e.getInventory().getLocation().getChunk()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void playerInteract(final PlayerInteractEvent e) {
		if (!activated)
			return;

		if (e.getClickedBlock() != null)
			if (block(e.getPlayer(), e.getClickedBlock().getChunk()))
				e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerShearEntity(final PlayerShearEntityEvent e) {
		if (!activated)
			return;

		if (block(e.getPlayer(), e.getEntity().getLocation().getChunk()))
			e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerLeashEntity(final PlayerLeashEntityEvent e) {
		if (!activated)
			return;

		if (block(e.getPlayer(), e.getEntity().getLocation().getChunk()))
			e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void entityTame(final EntityTameEvent e) {
		if (!activated)
			return;

		if (e.getOwner() instanceof Player)
			if (block((Player) e.getOwner(), e.getEntity().getLocation().getChunk()))
				e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void hangingBreakByEntity(final HangingBreakByEntityEvent e) {
		if (!activated)
			return;

		if (e.getRemover() instanceof Player)
			if (block((Player) e.getRemover(), e.getEntity().getLocation().getChunk()))
				e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerInteractAtEntity(final PlayerInteractAtEntityEvent e) {
		if (!activated)
			return;

		if (block(e.getPlayer(), e.getRightClicked().getLocation().getChunk()))
			e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerArmorStandManipulate(final PlayerArmorStandManipulateEvent e) {
		if (!activated)
			return;

		if (block(e.getPlayer(), e.getRightClicked().getLocation().getChunk()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void playerInteractEntity(final PlayerInteractEntityEvent e) {
		if (!activated)
			return;

		if (block(e.getPlayer(), e.getRightClicked().getLocation().getChunk()))
			e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void entityDamageByEntity(final EntityDamageByEntityEvent e) {
		if (!activated)
			return;

		if (e.getDamager() instanceof Player)
			if (block((Player) e.getDamager(), e.getEntity().getLocation().getChunk()))
				e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void entityExplode(final EntityExplodeEvent e) {
		if (!activated)
			return;

		if (block(e.getEntity().getLocation().getChunk()))
			e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void blockBreak(final BlockBreakEvent e) {
		if (!activated)
			return;

		if (block(e.getPlayer(), e.getBlock().getChunk()))
			e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void blockPlace(final BlockPlaceEvent e) {
		if (!activated)
			return;

		if (block(e.getPlayer(), e.getBlock().getChunk()))
			e.setCancelled(true);
	}
}
