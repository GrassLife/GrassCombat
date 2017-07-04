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
public class WeaponRewriteListener implements Listener {
    @EventHandler
    public void onRewriteWeapon(ItemRewriteEvent event) {
        if(!event.getType().equals(RewriteType.WEAPON) || !event.getJson().hasItemTag("Weapon")) return;
        List<String> lore = event.getLore();
        lore.add("");
        GrassJson json = event.getJson();
        if(json.hasItemTag("Sword")) {
            lore.add(ChatColor.BLUE + "物理攻撃力: " + json.getDynamicValue("AttackDamage").getAsMaskedDouble().orElse(0.0));
            lore.add(ChatColor.BLUE + "リーチ: " + json.getDynamicValue("AttackReach").getAsMaskedDouble().orElse(0.0));
            lore.add(ChatColor.BLUE + "攻撃間隔: " + json.getDynamicValue("AttackInterval").getAsMaskedInteger().orElse(20));
            lore.add(ChatColor.BLUE + "吹き飛ばし: " + json.getDynamicValue("KnockBackPower").getAsMaskedDouble().orElse(0.0));
            lore.add(ChatColor.BLUE + "巻き込み: " + json.getDynamicValue("AttackWidth").getAsMaskedInteger().orElse(0) + "%");
        }
        if(json.hasItemTag("Bow")) {
            lore.add(ChatColor.BLUE + "物理攻撃力: " + json.getDynamicValue("AttackDamage").getAsMaskedDouble().orElse(0.0));
            lore.add(ChatColor.BLUE + "射程: " + json.getDynamicValue("AttackRange").getAsMaskedInteger().orElse(20));
        }
        event.setLore(lore);
        event.setShowable(true);
    }
}
