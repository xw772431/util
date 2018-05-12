package com.dragonflag.util.matcher;

import java.util.List;

/**
 * 字符串匹配接口
 *
 * @author xuwei
 */
public interface StringMatcher {
    /**
     * 自定义匹配结果
     *
     * @param str      文本
     * @param ptr      模式文本
     * @param reverse  是否反向查找
     * @param callback 结果回调
     */
    void match(String str, String ptr, boolean reverse, MatchCallback<?> callback);

    /**
     * 匹配所有结果
     *
     * @param str     文本
     * @param ptr     模式文本
     * @param reverse 是否反向查找
     * @return 首字符索引列表
     */
    List<Integer> match(String str, String ptr, boolean reverse);

    /**
     * 匹配第一个结果
     *
     * @param str     文本
     * @param ptr     模式文本
     * @param reverse 是否反向查找
     * @return 首字符索引
     */
    int matchFirst(String str, String ptr, boolean reverse);

    /**
     * 匹配结果封装类
     *
     * @param <T>
     */
    public static interface MatchCallback<T> {
        /**
         * 得到匹配结果
         *
         * @return
         */
        T get();

        /**
         * 设置当前匹配结果
         *
         * @param index 当前匹配的首字符索引
         * @return 是否继续匹配 true-继续 false-终止
         */
        boolean set(int index);
    }
}
