package life.grass.grasscombat.entity;

import life.grass.grasscombat.GrassCombat;
import life.grass.grasscombat.datatype.ArmorDataType;
import life.grass.grasscombat.datatype.DamageType;
import life.grass.grasscombat.utils.DamageUtil;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by ecila on 2017/07/18.
 */
public class Victim extends DressedEntity {
    private LivingEntity livingEntity;
    private boolean isPlayer;

    private static String LAST_DAMAGE_TICK = "LastDamageTick";
    private static String TOTAL_DAMAGE_BY_PLAYER = "TotalDamageByPlayer";

    public Victim(LivingEntity entity) {
        super(entity);
        this.livingEntity = entity;
        this.isPlayer = entity instanceof Player;
    }

    public void causeRawDamage(double amount) {
        double totalDamage = 0.0;
        if(livingEntity.hasMetadata(TOTAL_DAMAGE_BY_PLAYER)) {
            totalDamage += livingEntity.getMetadata(TOTAL_DAMAGE_BY_PLAYER).get(0).asDouble();
        }

        if(amount < livingEntity.getHealth()) {
            livingEntity.setMetadata(TOTAL_DAMAGE_BY_PLAYER, new FixedMetadataValue(GrassCombat.getInstance(), totalDamage + amount));
            livingEntity.setHealth(livingEntity.getHealth() - amount);
        } else {
            livingEntity.setMetadata(TOTAL_DAMAGE_BY_PLAYER, new FixedMetadataValue(GrassCombat.getInstance(), totalDamage + livingEntity.getHealth()));
            livingEntity.setHealth(0);
        }
    }

    public void causeDamage(double amount, LivingEntity damager, DamageType type) {
        double damage = 0.0;
        if(type.equals(DamageType.BASIC_DAMAGE)) {
            damage = DamageUtil.getDefencedDamage(amount, getArmorData(ArmorDataType.DEFENCE), getArmorData(ArmorDataType.PROTECTION));
        } else if(type.equals(DamageType.MAGIC_DAMAGE)) {
            damage = DamageUtil.getDefencedDamage(amount, getArmorData(ArmorDataType.MAGIC_DEFENCE), getArmorData(ArmorDataType.PROTECTION));
        } else {
            damage = amount;
        }
        causeRawDamage(damage);
    }

    public void setTimeStamp(LivingEntity damager, int attackInterval) {
        long ldt = livingEntity.getWorld().getFullTime() + (long) attackInterval;
        livingEntity.setMetadata(damager.getUniqueId() + LAST_DAMAGE_TICK, new FixedMetadataValue(GrassCombat.getInstance(), ldt));
    }

    public boolean canDamageBy(LivingEntity damager) {
        if(livingEntity.hasMetadata(damager.getUniqueId() + LAST_DAMAGE_TICK)) {
            List<MetadataValue> meta = livingEntity.getMetadata(damager.getUniqueId() + LAST_DAMAGE_TICK);
            if(meta.size() > 0  && meta.get(0).asLong() > livingEntity.getWorld().getFullTime()
                    && Math.abs(livingEntity.getMetadata(damager.getUniqueId() + LAST_DAMAGE_TICK).get(0).asLong() - livingEntity.getWorld().getFullTime()) < 1000L)
                return false;
        }
        return true;
    }

    public void causeKnockBackFrom(Location location, double power) {
        Vector vector = livingEntity.getLocation().toVector().subtract(location.toVector()).normalize();
        livingEntity.setVelocity(vector.multiply(0.2 * power).setY(0.25));
    }

    public void causeRawKnockBackFrom(Location location, double power) {
        Vector vector = livingEntity.getLocation().toVector().subtract(location.toVector()).normalize();
        livingEntity.setVelocity(vector.multiply(0.2 * power));
    }

    public boolean isDroppable() {
        if(livingEntity.hasMetadata(TOTAL_DAMAGE_BY_PLAYER)) {
            double damage = livingEntity.getMetadata(TOTAL_DAMAGE_BY_PLAYER).get(0).asDouble();
            return damage >= (livingEntity.getMaxHealth() / 2.0);
        } else {
            return false;
        }
    }
}
