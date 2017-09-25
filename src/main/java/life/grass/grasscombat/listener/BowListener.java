package life.grass.grasscombat.listener;

import life.grass.grasscombat.datatype.DamageType;
import life.grass.grasscombat.entity.DressedEntity;
import life.grass.grasscombat.datatype.ArmorDataType;
import life.grass.grasscombat.datatype.WeaponDataType;
import life.grass.grasscombat.entity.Victim;
import life.grass.grasscombat.utils.DamageUtil;
import life.grass.grassitem.GrassJson;
import life.grass.grassitem.ItemData.WeaponSkill;
import life.grass.grassitem.JsonHandler;
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
            double damage = de.getWeaponData(WeaponDataType.ATTACK_DAMAGE);
            if(e.getEntity() instanceof Player) {
                arrow.setVelocity(arrow.getVelocity().multiply(Math.pow(de.getWeaponData(WeaponDataType.ATTACK_RANGE), 0.5) / 5.0));
                GrassJson grassJson = de.getGrassItemInMainHand();
                if(grassJson.hasDynamicValue("WeaponSkill/Type")) {
                    WeaponSkill skill = WeaponSkill.getSkill(grassJson.getDynamicValue("WeaponSkill/Type").getAsMaskedString().orElse(""));
                    double bonus = skill.apply((Player) e.getEntity());
                    System.out.println("Bonus" + bonus);
                    damage += bonus;
                }
            }
            arrow.addScoreboardTag(
                    (damage * (de.getGrassItemInMainHand() != null ? de.getGrassItemInMainHand().getJsonReader().getEffectRate() : 1.0))
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
            if(e.getEntity() instanceof LivingEntity) {
                e.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0.0);
                e.setDamage(0);
                LivingEntity le = (LivingEntity) e.getEntity();
                Victim victim = new Victim(le);
                victim.causeDamage(damage, DamageType.BASIC_DAMAGE);
            }

            e.setDamage(damage);
        }
    }
}
