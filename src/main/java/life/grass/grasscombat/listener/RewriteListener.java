package life.grass.grasscombat.listener;

import life.grass.grassitem.GrassJson;
import life.grass.grassitem.events.ItemRewriteEvent;
import life.grass.grassitem.events.RewriteType;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ecila on 2017/07/04.
 */
public class RewriteListener implements Listener {
    @EventHandler
    public void onRewriteWeapon(ItemRewriteEvent event) {
        if(!event.getType().equals(RewriteType.WEAPON)) return;
        List<String> lore = event.getLore();
        lore.add("");
        GrassJson json = event.getJson();
        if(json.hasDynamicValue("AttackDamage")) lore.add(ChatColor.BLUE + "物理攻撃力: " + formatDouble(json.getDynamicValue("AttackDamage").getAsMaskedDouble().orElse(0.0)));
        if(json.hasDynamicValue("AttackReach")) lore.add(ChatColor.BLUE + "リーチ: " + formatDouble(json.getDynamicValue("AttackReach").getAsMaskedDouble().orElse(0.0)));
        if(json.hasDynamicValue("AttackInterval")) lore.add(ChatColor.BLUE + "攻撃間隔: " + json.getDynamicValue("AttackInterval").getAsMaskedInteger().orElse(20));
        if(json.hasDynamicValue("KnockBackPower")) lore.add(ChatColor.BLUE + "吹き飛ばし: " + formatDouble(json.getDynamicValue("KnockBackPower").getAsMaskedDouble().orElse(0.0) * 100) + "%");
        if(json.hasDynamicValue("AttackWidth")) lore.add(ChatColor.BLUE + "巻き込み: " + formatDouble(json.getDynamicValue("AttackWidth").getAsMaskedInteger().orElse(0) * 100) + "%");
        if(json.hasDynamicValue("AttackRange")) lore.add(ChatColor.BLUE + "射程: " + json.getDynamicValue("AttackRange").getAsMaskedInteger().orElse(20));
        event.setLore(lore);
        event.setShowable(true);
    }

    @EventHandler
    public void onRewriteArmor(ItemRewriteEvent event) {
        if(!event.getType().equals(RewriteType.ARMOR)) return;
        List<String> lore = event.getLore();
        lore.add("");
        GrassJson json = event.getJson();
        if(json.hasDynamicValue("Defence")) lore.add(ChatColor.BLUE + "防御: " + json.getDynamicValue("Defence").getAsOriginalInteger().orElse(0));
        if(json.hasDynamicValue("Protection")) lore.add(ChatColor.BLUE + "保護: " + json.getDynamicValue("Protection").getAsOriginalInteger().orElse(0));
        if(json.hasDynamicValue("MagicDefence")) lore.add(ChatColor.BLUE + "魔法防御: "+ json.getDynamicValue("MagicDefence").getAsOriginalInteger().orElse(0));
        if(json.hasDynamicValue("BonusAttackDamage")) lore.add(ChatColor.BLUE + "追加攻撃力: "+ json.getDynamicValue("BonusAttackDamage").getAsOriginalInteger().orElse(0));
        event.setLore(lore);
        event.setShowable(true);
    }

    private static String formatDouble(double value) {
        return String.format("%.2f", value);
    }
}
