package life.grass.grasscombat.listener;

import life.grass.grasscombat.Caster;
import life.grass.grasscombat.DressedEntity;
import life.grass.grasscombat.GrassCombat;
import life.grass.grasscombat.datatype.ArmorDataType;
import life.grass.grasscombat.utils.DamageUtil;
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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by ecila on 2017/07/03.
 */
public class DamageListener implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            damageEntity(e.getPlayer());
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player) {
            if(e.getCause().equals(DamageCause.ENTITY_ATTACK) && e.getDamager() instanceof LivingEntity) {
                damageEntity((Player) e.getDamager());
                e.setCancelled(true);
            }
        } else if(e.getEntity() instanceof LivingEntity) {
            double damage = e.getDamage();
            DressedEntity de = new DressedEntity((LivingEntity) e.getEntity());
            if(e.getCause().equals(DamageCause.MAGIC) || e.getCause().equals(DamageCause.POISON)) {
                damage = DamageUtil.getDefencedDamage(damage, de.getArmorData(ArmorDataType.MAGIC_DEFENCE), de.getArmorData(ArmorDataType.PROTECTION));
            } else if(e.getCause().equals(DamageCause.ENTITY_ATTACK) || e.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
                damage = DamageUtil.getDefencedDamage(damage, de.getArmorData(ArmorDataType.DEFENCE), de.getArmorData(ArmorDataType.PROTECTION));
            }
            e.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0);
            e.setDamage(damage);
        }
    }

    public void damageEntity(LivingEntity damager) {
        Caster caster = new Caster(damager);
        caster.attack();
    }

    @EventHandler
    public void onDamageEntityByEntity(EntityDamageByEntityEvent e) {

    }
}
