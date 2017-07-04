package life.grass.grasscombat;

import life.grass.grasscombat.datatype.ArmorDataType;
import life.grass.grasscombat.datatype.WeaponDataType;
import life.grass.grassitem.GrassJson;
import life.grass.grassitem.GrassJsonDataValue;
import life.grass.grassitem.JsonHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

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
        // TODO 性能を反映
        GrassJsonDataValue data = getDynamicValue(entity.getEquipment().getItemInMainHand(), type.getKey());
        return data != null ? data.getAsMaskedDouble().orElse(type.getDefaultData()) : type.getDefaultData();
    }

    public double getArmorData(ArmorDataType type) {
        double result = 0.0;
        for(ItemStack item: entity.getEquipment().getArmorContents()) {
            GrassJsonDataValue data = getDynamicValue(item, type.getKey());
            result += data != null ? data.getAsMaskedDouble().orElse(type.getDefaultData()) : type.getDefaultData();
        }
        return result;
    }
}
