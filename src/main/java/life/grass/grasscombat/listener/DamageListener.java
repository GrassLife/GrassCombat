package life.grass.grasscombat.listener;

import life.grass.grasscombat.GrassCombat;
import life.grass.grasscombat.utils.Vector3D;
import life.grass.grasscombat.utils.VectorUtil;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by ecila on 2017/07/03.
 */
public class DamageListener implements Listener {
    private static String LAST_DAMAGE_TICK = "LastDamageTick";

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            damageEntity(e.getPlayer());
        }
    }

    @EventHandler
    public void onTest(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player) {
            if(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) && e.getDamager() instanceof LivingEntity) {
                damageEntity((Player) e.getDamager());
                e.setCancelled(true);
            }
        }
    }

    public void damageEntity(Player damager) {
        double reach = 10.0;
        double expanding = 1.0;
        double knockback = 1.0;
        double damage = 5.0;
        int attackInterval = 20;

        Location observerPos = damager.getEyeLocation();
        Vector3D observerDir = new Vector3D(observerPos.getDirection());

        Vector3D observerStart = new Vector3D(observerPos);
        Vector3D observerEnd = observerStart.add(observerDir.multiply(50.0));

        for(Entity entity: damager.getNearbyEntities(reach, reach, reach)) {
            if(entity instanceof LivingEntity) {
                Vector3D targetPos = new Vector3D(entity.getLocation());
                Vector3D minimum = targetPos.add(entity.getWidth() / -2.0 * expanding, 0, entity.getWidth() / -2.0 * expanding);
                Vector3D maximum = targetPos.add(entity.getWidth() / 2.0 * expanding, entity.getHeight() * expanding, entity.getWidth() / 2.0 * expanding);

                if(VectorUtil.hasIntersection(observerStart, observerEnd, minimum, maximum) && damager.hasLineOfSight(entity)) {
                    LivingEntity le = (LivingEntity) entity;
                    if(le.isDead() || le.isInvulnerable()) return;

                    if(le.hasMetadata(damager.getUniqueId() + LAST_DAMAGE_TICK)) {
                        if(le.getMetadata(damager.getUniqueId() + LAST_DAMAGE_TICK).get(0).asLong() > le.getWorld().getFullTime()
                                && Math.abs(le.getMetadata(damager.getUniqueId() + LAST_DAMAGE_TICK).get(0).asLong() - le.getWorld().getFullTime()) < 1000L)
                            return;
                    }

                    le.damage(0);
                    entity.setVelocity(observerPos.getDirection().multiply(0.2 * knockback).setY(0.3));

                    if(damage < le.getHealth()) {
                        le.setHealth(le.getHealth() - damage);
                    } else {
                        le.setHealth(0);
                    }
                    long ldt = le.getWorld().getFullTime() + (long) attackInterval;
                    le.setMetadata(damager.getUniqueId() + LAST_DAMAGE_TICK, new FixedMetadataValue(GrassCombat.getInstance(), ldt));
                }
            }
        }
    }
}
