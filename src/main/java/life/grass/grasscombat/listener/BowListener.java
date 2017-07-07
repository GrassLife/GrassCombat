package life.grass.grasscombat.listener;

import life.grass.grasscombat.DressedEntity;
import life.grass.grasscombat.datatype.ArmorDataType;
import life.grass.grasscombat.datatype.WeaponDataType;
import life.grass.grasscombat.utils.DamageUtil;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;

/**
 * Created by ecila on 2017/07/04.
 */
public class BowListener implements Listener{
    @EventHandler
    public void onShotBowByPlayer(EntityShootBowEvent e) {
        if(e.getProjectile() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getProjectile();
            DressedEntity de = new DressedEntity(e.getEntity());
            if(e.getEntity() instanceof Player) arrow.setVelocity(arrow.getVelocity().multiply(Math.pow(de.getWeaponData(WeaponDataType.ATTACK_RANGE), 0.5) / 5.0));
            arrow.addScoreboardTag(
                    (de.getWeaponData(WeaponDataType.ATTACK_DAMAGE) * (de.getGrassItemInMainHand() != null ? de.getGrassItemInMainHand().getJsonReader().getEffectRate() : 1.0))
                            + "," + e.getForce());
        }
    }

    @EventHandler
    public void onDamageByBow(EntityDamageByEntityEvent e) {
        if(e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            String[] arrowData = e.getDamager().getScoreboardTags().stream().findFirst().orElse("0,0").split(",");
            double damage = Double.parseDouble(arrowData[0]) * Double.parseDouble(arrowData[1]);
            if(e.getEntity() instanceof LivingEntity) {
                DressedEntity de = new DressedEntity((LivingEntity) e.getEntity());
                damage = DamageUtil.getDefencedDamage(damage, de.getArmorData(ArmorDataType.DEFENCE), de.getArmorData(ArmorDataType.PROTECTION));
            }
            e.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0);
            e.setDamage(damage);
        }
    }
}
