package life.grass.grasscombat.entity;

import life.grass.grasscombat.GrassCombat;
import life.grass.grasscombat.datatype.DamageType;
import life.grass.grasscombat.datatype.WeaponDataType;
import life.grass.grasscombat.utils.Vector3D;
import life.grass.grasscombat.utils.VectorUtil;
import life.grass.grassitem.JsonHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ecila on 2017/07/04.
 */
public class Damager extends DressedEntity {
    private LivingEntity livingEntity;
    private boolean isPlayer;

    public Damager(LivingEntity entity) {
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

        Victim victim = new Victim(target);
        return victim.canDamageBy(livingEntity);
    }

    public void damage(LivingEntity target) {
        double knockback = getWeaponData(WeaponDataType.KNOCKBACK_POWER);
        double damage = getWeaponData(WeaponDataType.ATTACK_DAMAGE);
        int attackInterval = (int) getWeaponData(WeaponDataType.ATTACK_INTERVAL);

        if(!canDamage(target)) return;

        target.damage(0);
        if(isPlayer && ((Player)livingEntity).isSprinting()) knockback += 2.0;

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

        Victim victim = new Victim(target);
        victim.causeKnockBackFrom(livingEntity.getLocation(), knockback);
        victim.causeDamage(damage, livingEntity, DamageType.BASIC_DAMAGE);
        victim.setTimeStamp(livingEntity, attackInterval);
    }

    public void attack() {
        double reach = getWeaponData(WeaponDataType.ATTACK_REACH);
        double expanding = getWeaponData(WeaponDataType.ATTACK_WIDTH);

        for(LivingEntity target: getEyeLineEntities(reach, expanding)) {
            damage(target);
        }
    }

    public void castFireBolt(Location point, Location target) {
        Location location = point.add(0, 1.7, 0).clone();
        Vector vec = target.add(0, 1.0, 0).toVector().subtract(point.toVector()).normalize();
        World world = point.getWorld();
//        Vector vec = target.toVector().add(point.toVector()).normalize();
        new BukkitRunnable() {
            double space = 0;
            @Override
            public void run() {
                double x = space * vec.getX();
                double y = space * vec.getY() - space * space / 5.0;
                double z = space * vec.getZ();
                location.add(x, y, z);
                if (15.0 < space
                        || location.getBlock().getType() != Material.AIR
                        || world.getNearbyEntities(location, 0.7, 0.7, 0.7).stream().filter(entity -> entity instanceof LivingEntity).toArray().length != 0) {
                    world.getNearbyEntities(location, 0.7, 0.7, 0.7).stream()
                            .filter(entity -> entity instanceof LivingEntity)
                            .forEach(player -> {
                                ((LivingEntity) player).damage(4.0);
                            });
                    world.spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
                    world.spawnParticle(Particle.FIREWORKS_SPARK, location, 16, 0.1, 0.1, 0.1, 0.05);
                    world.spawnParticle(Particle.LAVA, location, 6, 0, 0, 0, 0.2);
                    cancel();
                }
                world.spawnParticle(Particle.FLAME, location, 9, 0.1, 0.05, 0.1, 0.02);
                world.spawnParticle(Particle.SMOKE_NORMAL, location, 2, 0, 0, 0, 0);
                space += 0.2;
            }
        }.runTaskTimer(GrassCombat.getInstance(), 0, 1);
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
