package life.grass.grasscombat.utils;

/**
 * Created by ecila on 2017/07/04.
 */
public class DamageUtil {
    public static double getDefencedDamage(double base, double defence, double protection) {
        return Math.max(Math.max(base - defence / 10.0, base / 10.0), 0.0) * 20.0 / (protection + 20.0);
    }
}
