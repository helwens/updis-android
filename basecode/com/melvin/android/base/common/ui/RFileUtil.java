
package com.melvin.android.base.common.ui;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * 用于处理R文件的反射值
 *
 * @author wangbx
 */
public class RFileUtil {

    @SuppressWarnings("unused")
    private String packageName = null;

    @SuppressWarnings("rawtypes")
    private Class drawableClass = null;

    private Class<?> idClass = null;

    private Class<?> layoutClass = null;

    private Class<?> stringClass = null;

    private Class<?> styleableClass = null;

    private Class<?> rawClass = null;

    private Class<?> colorClass = null;

    /**
     * 实例对象
     */
    private static RFileUtil rFileUtil = null;

    /**
     * 缓存数据
     */
    private HashMap<String, Integer> cacheData = null;

    private HashMap<String, int[]> cacheDatas = null;

    /**
     * 反射res属性
     */
    public final static String ID = "id";

    public final static String COLOR = "color";

    public final static String DIMEN = "dimen";

    public final static String INTEGER = "integer";

    public final static String BOOL = "bool";

    public final static String STYLEABLE = "styleable";

    public final static String STYLE = "style";

    public final static String ATTRS = "attrs";

    public final static String LAYOUT = "layout";

    public final static String STRING = "string";

    /**
     * @param packageName 包名
     */
    public RFileUtil(String packageName) {
        this.packageName = packageName;
        try {
            drawableClass = Class.forName(packageName + ".R$drawable");
            idClass = Class.forName(packageName + ".R$id");
            layoutClass = Class.forName(packageName + ".R$layout");
            stringClass = Class.forName(packageName + ".R$string");
            styleableClass = Class.forName(packageName + ".R$styleable");
            rawClass = Class.forName(packageName + ".R$raw");
            cacheData = new HashMap<String, Integer>();
            cacheDatas = new HashMap<String, int[]>();
            colorClass = Class.forName(packageName + ".R$color");
        } catch (ClassNotFoundException e) {
        }
    }

    public static RFileUtil getInstance(String packageName) {
        if (rFileUtil == null) {
            rFileUtil = new RFileUtil(packageName);
        }
        return rFileUtil;
    }

    /**
     * 取得Drawable的值,R.drawable
     *
     * @param key
     * @return
     */
    public int getDrawableValue(String key) {
        return getValue("drawable", key);
    }

    /**
     * 取得ID的值,R.id
     *
     * @param key
     * @return
     */
    public int getIdValue(String key) {
        return getValue("id", key);
    }

    /**
     * 取得布局的值,R.layout
     *
     * @param key
     * @return
     */
    public int getLayoutValue(String key) {
        return getValue("layout", key);
    }

    /**
     * 取得字符串的值,R.string
     *
     * @param key
     * @return
     */
    public int getStringValue(String key) {
        return getValue("string", key);
    }

    public int getRawValue(String key) {
        return getValue("raw", key);
    }

    /**
     * 取得ID的值,R.id
     *
     * @param key
     * @return
     */
    public int[] getStyleableValue(String key) {
        return getValues("styleable", key);
    }

    /**
     * 取得int值
     *
     * @param type
     * @param key
     * @return
     */
    private int getValue(String type, String key) {
        Field ff = null;
        try {
            if (cacheData != null && cacheData.containsKey(type + key)) {
                return cacheData.get(type + key);
            }

            if ("id".equals(type)) {
                if (idClass != null)
                    ff = idClass.getField(key);
            } else if ("drawable".equals(type)) {
                if (drawableClass != null)
                    ff = drawableClass.getField(key);
            } else if ("layout".equals(type)) {
                if (layoutClass != null)
                    ff = layoutClass.getField(key);
            } else if ("string".equals(type)) {
                if (stringClass != null)
                    ff = stringClass.getField(key);
            } else if ("styleable".equals(type)) {
                if (styleableClass != null)
                    ff = styleableClass.getField(key);
            } else if ("raw".equals(type)) {
                if (rawClass != null) {
                    ff = rawClass.getField(key);
                }
            } else if ("color".equals(type)) {
                if (colorClass != null) {
                    ff = colorClass.getField(key);
                }
            }
            if (ff == null)
                return -1;
            int val = ff.getInt(null);
            if (cacheData != null) {
                cacheData.put(type + key, val);
            }
            return val;
        } catch (Exception e) {
        }
        return -1;
    }

    /**
     * 取得int[]值
     *
     * @param type
     * @param key
     * @return
     */
    private int[] getValues(String type, String key) {
        Field ff = null;
        try {
            if (cacheDatas != null && cacheDatas.containsKey(type + key)) {
                return cacheDatas.get(type + key);
            }
            if ("styleable".equals(type)) {
                if (styleableClass != null)
                    ff = styleableClass.getField(key);
            }
            if (ff == null)
                return null;
            int[] val = (int[]) (ff.get(null));
            if (cacheDatas != null) {
                cacheDatas.put(type + key, val);
            }
            return val;
        } catch (Exception e) {
        }
        return null;
    }

    public int getColor(String key) {
        return getValue("color", key);
    }
}
