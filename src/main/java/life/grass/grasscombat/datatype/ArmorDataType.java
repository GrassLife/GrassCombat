package life.grass.grasscombat.datatype;

/**
 * Created by ecila on 2017/07/04.
 */
public enum ArmorDataType {
    DEFENCE("Defence", 0.0),
    PROTECTION("Protection", 0.0),
    MAGIC_DEFENCE("MagicDefence", 0.0);

    private String key;
    private double defaultData;

    ArmorDataType(String key, double defaultData) {
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
