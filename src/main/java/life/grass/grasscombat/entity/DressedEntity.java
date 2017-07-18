package life.grass.grasscombat.entity;

import life.grass.grasscombat.datatype.ArmorDataType;
import life.grass.grasscombat.datatype.WeaponDataType;
import life.grass.grassitem.GrassJson;
import life.grass.grassitem.GrassJsonDataValue;
import life.grass.grassitem.JsonHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ecila on 2017/07/04.
 */
public class DressedEntity {
    private LivingEntity entity;

    public DressedEntity(LivingEntity entity) {
        this.entity = entity;
    }

    private GrassJson getGrassJson(ItemStack item) {
        return JsonHandler.getGrassJson(item);
    }

    private GrassJsonDataValue getDynamicValue(ItemStack item, String key) {
        return getGrassJson(item) != null ? getGrassJson(item).getDynamicValue(key) : null;
    }

    public double getWeaponData(WeaponDataType type) {
        GrassJsonDataValue data = getDynamicValue(entity.getEquipment().getItemInMainHand(), type.getKey());
        double damage = data != null ? data.getAsMaskedDouble().orElse(type.getDefaultData()) : type.getDefaultData();
        damage += getArmorData(ArmorDataType.BONUS_ATTACK_DAMAGE);
        return damage;
    }

    public double getArmorData(ArmorDataType type) {
        double result = 0.0;
        List<ItemStack> items = new ArrayList<>();
        items.addAll(Arrays.asList(entity.getEquipment().getArmorContents()));
        items.add(entity.getEquipment().getItemInMainHand());
        items.add(entity.getEquipment().getItemInOffHand());
        for(ItemStack item: items) {
            GrassJsonDataValue data = getDynamicValue(item, type.getKey());
            result += (data != null ? data.getAsMaskedDouble().orElse(type.getDefaultData()) : type.getDefaultData()) * (getGrassJson(item) != null ? getGrassJson(item).getJsonReader().getEffectRate() : 1.0);
        }
        return result;
    }

    public GrassJson getGrassItemInMainHand() {
        return getGrassJson(entity.getEquipment().getItemInMainHand());
    }
}
