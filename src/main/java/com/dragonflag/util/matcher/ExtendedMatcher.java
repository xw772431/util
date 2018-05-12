package com.dragonflag.util.matcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串匹配扩展类
 *
 * @author xuwei
 */
public abstract class ExtendedMatcher implements StringMatcher {
    /**
     * 正向匹配
     *
     * @param str      文本
     * @param ptr      模式文本
     * @param callback 结果回调
     */
    public abstract void match(String str, String ptr, MatchCallback<?> callback);

    /**
     * 反向匹配
     *
     * @param str      文本
     * @param ptr      模式文本
     * @param callback 结果回调
     */
    public abstract void matchReversely(String str, String ptr, MatchCallback<?> callback);

    /**
     * 索引结果列表
     */
    public static class ListIndexCallback implements MatchCallback<List<Integer>> {
        private List<Integer> value = new ArrayList<>();

        @Override
        public List<Integer> get() {
            return value;
        }

        @Override
        public boolean set(int index) {
            value.add(index);
            return true;
        }
    }

    /**
     * 单个索引结果
     */
    public static class SingleIndexCallback implements MatchCallback<Integer> {
        private int value = -1;

        @Override
        public Integer get() {
            return value;
        }

        @Override
        public boolean set(int index) {
            this.value = index;
            return false;
        }
    }
}
