package cn.dengx.sample.Bean;

import cn.dengx.cousyncdb.annotations.Ignore;
import cn.dengx.cousyncdb.annotations.PrimaryKey;
import cn.dengx.cousyncdb.annotations.Table;

/**
 * Current project:CouSyncDb.
 * Created by dengx on 16/1/19,17:02.
 */

@Table(Name = "bean1")
public class Bean1 {
    private byte aByte;
    private boolean aBoolean;
    private short aShort;

    @PrimaryKey(keyName = "anInt")
    private int anInt;
    private long aLong;
    private float aFloat;
    private double aDouble;
    private String aString;

    @Ignore
    private int haha;

    public byte getaByte() {
        return aByte;
    }

    public void setaByte(byte aByte) {
        this.aByte = aByte;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public short getaShort() {
        return aShort;
    }

    public void setaShort(short aShort) {
        this.aShort = aShort;
    }

    public int getAnInt() {
        return anInt;
    }

    public void setAnInt(int anInt) {
        this.anInt = anInt;
    }

    public long getaLong() {
        return aLong;
    }

    public void setaLong(long aLong) {
        this.aLong = aLong;
    }

    public float getaFloat() {
        return aFloat;
    }

    public void setaFloat(float aFloat) {
        this.aFloat = aFloat;
    }

    public double getaDouble() {
        return aDouble;
    }

    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }

    public String getaString() {
        return aString;
    }

    public void setaString(String aString) {
        this.aString = aString;
    }

    public int getHaha() {
        return haha;
    }

    public void setHaha(int haha) {
        this.haha = haha;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("aByte=").append(aByte).append("\r\n");
        builder.append("aBoolean=").append(aBoolean).append("\r\n");
        builder.append("aShort=").append(aShort).append("\r\n");
        builder.append("anInt=").append(anInt).append("\r\n");
        builder.append("aLong=").append(aLong).append("\r\n");
        builder.append("aFloat=").append(aFloat).append("\r\n");
        builder.append("aDouble=").append(aDouble).append("\r\n");
        builder.append("aString=").append(aString).append("\r\n");
        builder.append("haha=").append(haha).append("\r\n");
        return builder.toString();
    }
}
