package com.dragonflag.util;

import java.lang.reflect.*;
import java.util.*;

/**
 * 泛型工具类
 *
 * @author xuwei
 */
public class GenericsUtils {
    /**
     * 得到字段泛型参数类型信息列表
     *
     * @param field
     * @return
     */
    public static GenericInfo[] getFieldGenericInfos(Field field) {
        if (field == null)
            return null;
        return resolveType(field.getGenericType()).getParameters();
    }

    /**
     * 得到字段泛型参数信息
     *
     * @param field 字段反射实例
     * <br><br>
     * 如：字段f定义如下
     * <br><br>
     * <code>
     * f = Map&lt;String, List&lt;Integer&gt;&gt;
     * </code>
     * <br><br>
     * 以该字段作为参数调用，返回结果为：
     * <br><br>
     * <code>
     * [String, List&lt;Integer&gt;]
     * </code>
     * @return
     */
    public static GenericInfo getFieldGenericInfo(Field field) {
        GenericInfo[] infos = getFieldGenericInfos(field);
        if (infos != null && infos.length > 0)
            return infos[0];
        return null;
    }

    /**
     * 得到父类类或则接口的泛型参数列表
     *
     * @param clazz 需得到父类或则接口泛型列表的Class
     * @return 泛型信息数组
     * <br><br>
     * 如：类A, B定义如下
     * <br><br>
     * <code>
     * class A extends HashMap&lt;String, Integer&gt; implements Comparable&lt;Integer&gt;
     * <br><br>
     * class B extends A implements Map&lt;String, Integer&gt;, List&lt;String&gt;
     * </code>
     * <br><br>
     * 分别以A,B作为参数调用，返回结果分别为：
     * <br><br>
     * <code>
     * A:[HashMap&lt;String, Integer&gt;, Comparable&lt;Integer&gt;]
     * <br><br>
     * B:[HashMap&lt;String, Integer&gt;, Comparable&lt;Integer&gt;, List&lt;String&gt]
     * </code>
     **/

    public static GenericInfo[] getClassGenericInfos(Class<?> clazz) {
        if (clazz == null)
            return null;
        Set<GenericInfo> superSet = new LinkedHashSet<>();
        Set<GenericInfo> interSet = new LinkedHashSet<>();

        getClassGenericInfos(clazz, superSet, interSet);

        List<GenericInfo> list = new ArrayList<>(superSet.size() + interSet.size());
        list.addAll(superSet);
        list.addAll(interSet);

        return list.toArray(new GenericInfo[0]);
    }

    /**
     * 获取类反射信息
     *
     * @param clazz
     * @param superSet 父类反射信息列表
     * @param interSet 接口反射信息列表
     * @return
     */
    private static void getClassGenericInfos(Class<?> clazz, Set<GenericInfo> superSet, Set<GenericInfo> interSet) {
        if (clazz == Object.class)
            return;

        Type type = clazz.getGenericSuperclass();
        Type[] inters = clazz.getGenericInterfaces();

        if (isParameterized(type))
            superSet.add(resolveType(type));

        loop_inters:
        for (int i = 0; i < inters.length; ++i) {
            if (isParameterized(inters[i])) {
                GenericInfo info = resolveType(inters[i]);
                for (GenericInfo tmp : superSet)
                    if (info.getRawType().isAssignableFrom(tmp.getRawType()))
                        continue loop_inters;
                interSet.add(info);
            }
        }

        getClassGenericInfos(clazz.getSuperclass(), superSet, interSet);
    }

    /**
     * 得到类泛型参数
     *
     * @param clazz
     * @return
     */
    public static GenericInfo getClassGenericInfo(Class<?> clazz) {
        GenericInfo[] infos = getClassGenericInfos(clazz);
        if (infos != null && infos.length > 0)
            return infos[0];
        return null;
    }

    /**
     * 解析泛型参数
     *
     * @param type
     * @return
     */
    public static GenericInfo resolveType(Type type) {
        if (type == null)
            return null;

        Class<?> clazz = null;
        GenericInfo[] params = null;

        if (isParameterized(type)) {
            ParameterizedType pt = (ParameterizedType) type;
            clazz = (Class<?>) pt.getRawType();
            Type[] ts = pt.getActualTypeArguments();
            params = new GenericInfo[ts.length];
            for (int i = 0; i < ts.length; ++i)
                params[i] = resolveType(ts[i]);
        } else if (isClass(type)) { //类型为Class说明递归到最后一层
            clazz = (Class<?>) type;
        } else if (isWildcard(type)) { //通配符类型取其上限，super修饰的通配符上限为Object
            Type upper = ((WildcardType) type).getUpperBounds()[0];
            return resolveType(upper);
        } else if (isTypeVariable(type)) { //类型变量默认为Object，不继续递归解析
            clazz = Object.class;
        } else {
            return null;
        }
        return new GenericInfo(clazz, params);
    }

    /**
     * 是否为Class
     *
     * @param type
     * @return
     */
    public static boolean isClass(Type type) {
        return type == null ? false : Class.class == type.getClass();
    }

    /**
     * 是否为参数化类型
     *
     * @param type
     * @return
     */
    public static boolean isParameterized(Type type) {
        return type == null ? false : type instanceof ParameterizedType;
    }

    /**
     * 是否为通配符类型
     *
     * @param type
     * @return
     */
    public static boolean isWildcard(Type type) {
        return type == null ? false : type instanceof WildcardType;
    }

    /**
     * 是否为类型变量
     *
     * @param type
     * @return
     */
    public static boolean isTypeVariable(Type type) {
        return type == null ? false : type instanceof TypeVariable;
    }

    /**
     * 泛型信息
     */
    @SuppressWarnings("rawtypes")
    public static class GenericInfo {

        //原生类型
        private Class<?> rawType;
        //泛型参数信息（泛型嵌套）
        private GenericInfo[] params;

        public GenericInfo(Class<?> rawType) {
            this(rawType, null);
        }

        public GenericInfo(Class<?> rawType, GenericInfo[] args) {
            if (rawType == null) throw new IllegalArgumentException("the rawType cannot be null");
            this.rawType = rawType;
            this.params = args;
        }

        public Class<?> getRawType() {
            return rawType;
        }

        public GenericInfo[] getParameters() {
            return params;
        }

        public Class[] getParameterTypes() {
            if (params == null) return null;
            Class[] cs = new Class[params.length];
            for (int i = 0; i < params.length; ++i)
                cs[i] = params[i].getRawType();
            return cs;
        }

        public String format() {
            StringBuilder sb = new StringBuilder(rawType.getName());
            if (params != null) {
                sb.append("<");
                for (int i = 0; i < params.length; ++i) {
                    if (i > 0) sb.append(", ");
                    sb.append(params[i].format());
                }
                sb.append(">");
            }
            return sb.toString();
        }

        public String simpleFormat() {
            StringBuilder sb = new StringBuilder(rawType.getSimpleName());
            if (params != null) {
                sb.append("<");
                for (int i = 0; i < params.length; ++i) {
                    if (i > 0) sb.append(", ");
                    sb.append(params[i].simpleFormat());
                }
                sb.append(">");
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            return simpleFormat();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GenericInfo that = (GenericInfo) o;

            if (rawType != null ? !rawType.equals(that.rawType) : that.rawType != null) return false;
            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            return Arrays.equals(params, that.params);
        }

        @Override
        public int hashCode() {
            int result = rawType != null ? rawType.hashCode() : 0;
            result = 31 * result + Arrays.hashCode(params);
            return result;
        }
    }
}
