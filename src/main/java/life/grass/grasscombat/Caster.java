package life.grass.grasscombat;

import life.grass.grasscombat.datatype.ArmorDataType;
import life.grass.grasscombat.datatype.WeaponDataType;
import life.grass.grasscombat.utils.DamageUtil;
import life.grass.grasscombat.utils.Vector3D;
import life.grass.grasscombat.utils.VectorUtil;
import life.grass.grassitem.JsonHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ecila on 2017/07/04.
 */
public class Caster extends DressedEntity{
    private LivingEntity livingEntity;
    private boolean isPlayer;

    private static String LAST_DAMAGE_TICK = "LastDamageTick";

    public Caster(LivingEntity entity) {
        super(entity);
        this.livingEntity = entity;
        this.isPlayer = entity instanceof Player;
    }

    public List<LivingEntity> getEyeLineEntities(double range, double expanding) {
        List<LivingEntity> list = new ArrayList<>();
        Location observerPos = livingEntity.getEyeLocation();
        Vector3D observerDir = new Vector3D(observerPos.getDirection());

        Vector3D observerStart = new Vector3D(observerPos);
        Vector3D observerEnd = observerStart.add(observerDir.multiply(50.0));

        for(Entity entity: livingEntity.getNearbyEntities(range, range, range)) {
            if(entity instanceof LivingEntity) {
                Vector3D targetPos = new Vector3D(entity.getLocation());
                Vector3D minimum = targetPos.add(entity.getWidth() / -2.0 * expanding, 0, entity.getWidth() / -2.0 * expanding);
                Vector3D maximum = targetPos.add(entity.getWidth() / 2.0 * expanding, entity.getHeight() * expanding, entity.getWidth() / 2.0 * expanding);

                if(VectorUtil.hasIntersection(observerStart, observerEnd, minimum, maximum) && livingEntity.hasLineOfSight(entity)) {
                    list.add((LivingEntity) entity);
                }
            }
        }
        return list;
    }

    public boolean canDamage(LivingEntity target) {
        if(target.isDead() || target.isInvulnerable()) return false;
        if(!target.getWorld().getPVP() && isPlayer && target instanceof Player) return false;

        if(target.hasMetadata(livingEntity.getUniqueId() + LAST_DAMAGE_TICK)) {
            List<MetadataValue> meta = target.getMetadata(livingEntity.getUniqueId() + LAST_DAMAGE_TICK);
            if(meta.size() > 0  && meta.get(0).asLong() > target.getWorld().getFullTime()
                    && Math.abs(target.getMetadata(livingEntity.getUniqueId() + LAST_DAMAGE_TICK).get(0).asLong() - target.getWorld().getFullTime()) < 1000L)
                return false;
        }
        return true;
    }

    public void damage(LivingEntity target) {
        double knockback = getWeaponData(WeaponDataType.KNOCKBACK_POWER);
        double damage = getWeaponData(WeaponDataType.ATTACK_DAMAGE);
        int attackInterval = (int) getWeaponData(WeaponDataType.ATTACK_INTERVAL);

        if(!canDamage(target)) return;

        target.damage(0);
        if(isPlayer && ((Player)livingEntity).isSprinting()) knockback += 2.0;
        target.setVelocity(livingEntity.getEyeLocation().getDirection().multiply(0.2 * knockback).setY(0.3));

        DressedEntity de = new DressedEntity(target);

        if(isPlayer && getGrassItemInMainHand() != null) {
            damage *= getGrassItemInMainHand().getJsonReader().getEffectRate();
            Player player = (Player) livingEntity;
            ItemStack item = player.getInventory().getItemInMainHand();
            if(item != null || !item.getData().getItemType().equals(Material.AIR)) {
                item = JsonHandler.damageItem(item);
                player.getInventory().setItemInMainHand(item);
                player.updateInventory();
            }
        }
        damage = DamageUtil.getDefencedDamage(damage, de.getArmorData(ArmorDataType.DEFENCE), de.getArmorData(ArmorDataType.PROTECTION) );

        if(damage < target.getHealth()) {
            target.setHealth(target.getHealth() - damage);
        } else {
            target.setHealth(0);
        }
        long ldt = target.getWorld().getFullTime() + (long) attackInterval;
        target.setMetadata(livingEntity.getUniqueId() + LAST_DAMAGE_TICK, new FixedMetadataValue(GrassCombat.getInstance(), ldt));
    }

    public void attack() {
        double reach = getWeaponData(WeaponDataType.ATTACK_REACH);
        double expanding = getWeaponData(WeaponDataType.ATTACK_WIDTH);

        for(LivingEntity target: getEyeLineEntities(reach, expanding)) {
            damage(target);
        }
    }

    public LivingEntity getLivingEntity() {
        return livingEntity;
    }

    public void setLivingEntity(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    public boolean isPlayer() {
        return isPlayer;
    }
}
