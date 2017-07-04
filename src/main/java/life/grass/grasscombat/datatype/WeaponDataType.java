package life.grass.grasscombat.datatype;

/**
 * Created by ecila on 2017/07/04.
 */
public enum WeaponDataType {
    ATTACK_DAMAGE("AttackDamage", 1.0),
    ATTACK_RANGE("AttackRange", 20.0),
    ATTACK_REACH("AttackReach", 4.0),
    ATTACK_WIDTH("AttackWidth", 100.0),
    ATTACK_INTERVAL("AttackInterval", 20.0),
    KNOCKBACK_POWER("KnockBackPower", 1.0);

    private String key;
    private double defaultData;

    WeaponDataType(String key, double defaultData) {
        this.key = key;
        this.defaultData = defaultData;
    }

    public String getKey() {
        return this.key;
    }

    public double getDefaultData() {
        return this.defaultData;
    }
}
