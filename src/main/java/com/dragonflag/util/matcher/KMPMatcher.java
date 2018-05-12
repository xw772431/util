package com.dragonflag.util.matcher;

import java.util.List;

/**
 * “看毛片”匹配算法实现-_-
 *
 * @author xuwei
 */
public class KMPMatcher extends ExtendedMatcher {
    @Override
    public void match(String str, String ptr, boolean reverse, MatchCallback<?> callback) {
        if (str == null || ptr == null || str.length() == 0 || ptr.length() == 0)
            return;

        if (reverse)
            matchReversely(str, ptr, callback);
        else
            match(str, ptr, callback);
    }

    @Override
    public List<Integer> match(String str, String ptr, boolean reverse) {
        ListIndexCallback callback = new ListIndexCallback();
        match(str, ptr, reverse, callback);
        return callback.get();
    }

    @Override
    public int matchFirst(String str, String ptr, boolean reverse) {
        SingleIndexCallback callback = new SingleIndexCallback();
        match(str, ptr, reverse, callback);
        return callback.get();
    }

    /**
     * 匹配
     *
     * @param str      文本
     * @param ptr      模式文本
     * @param callback 结果回调
     */
    @Override
    public void match(String str, String ptr, MatchCallback<?> callback) {

        int[] next = buildNext(ptr);

        int smax = str.length() - 1;
        int pmax = ptr.length() - 1;

        //文本指针
        int s = 0;
        //模式指针
        int p = 0;

        for (; ; ) {
            char pc = ptr.charAt(p);
            //如果当前位置字符相等
            if (str.charAt(s) == pc) {
                //如果当前模式指针指向尾字符，说明已完全匹配
                if (p == pmax) {
                    //设置文本当前匹配位置的首字符索引为匹配结果
                    //如果返回false，则终止匹配
                    if (!callback.set(s - pmax))
                        break;
                    //回溯
                    p = next[p];
                } else
                    ++p;
                if (s++ == smax)
                    break;
            } else {
                //如果模式指针处于模式字符串首字符，则文本移动一位
                if (p == 0) {
                    if (s++ == smax)
                        break;
                } else {
                    //回溯
                    p = next[p - 1];
                    //如果回溯值和当前值相等，则直接回溯到头部
                    if (p != 0 && ptr.charAt(p) == pc)
                        p = 0;
                }
            }
        }
    }

    /**
     * 反向匹配
     *
     * @param str      文本
     * @param ptr      模式文本
     * @param callback 结果回调
     */
    @Override
    public void matchReversely(String str, String ptr, MatchCallback<?> callback) {

        int[] next = buildNextReversely(ptr);

        int smax = str.length() - 1;
        int pmax = ptr.length() - 1;

        //文本指针
        int s = smax;
        //模式指针
        int p = pmax;

        for (; ; ) {
            char pc = ptr.charAt(p);
            //如果当前位置字符相等
            if (str.charAt(s) == pc) {
                //如果当前模式指针指向首字符，说明已完全匹配
                if (p == 0) {
                    //设置文本当前匹配位置的首字符索引为匹配结果
                    //如果返回false，则终止匹配
                    if (!callback.set(s)) {
                        break;
                    }
                    //回溯
                    p = next[p];
                } else
                    --p;
                if (s-- == 0)
                    break;
            } else {
                //如果指针处于模式字符串尾字符，则文本移动一位
                if (p == pmax) {
                    if (s-- == 0)
                        break;
                } else {
                    //回溯
                    p = next[p + 1];
                    //如果回溯值和当前值相等，则回溯到尾部
                    if (p != pmax && ptr.charAt(p) == pc)
                        p = pmax;
                }
            }
        }
    }

    /**
     * 构建next数组
     *
     * @param ptr 模式文本
     * @return next数组
     */
    private int[] buildNext(String ptr) {
        int[] next = new int[ptr.length()];

        //最大连续重叠索引
        int i = 0;
        //遍历指针
        int j = 1;

        while (j < next.length) {
            //如果重叠
            if (ptr.charAt(j) == ptr.charAt(i)) {
                //最大连续重叠索引+1，当前位置next值指向此位置
                next[j] = ++i;
            } else
                //回溯到首字符，消除重叠状态
                i = 0;
            ++j;
        }
        return next;
    }


    /**
     * 构建反向next数组
     *
     * @param ptr 模式文本
     * @return 反向next数组
     */
    private int[] buildNextReversely(String ptr) {
        int[] next = new int[ptr.length()];
        //归位索引，尾字符
        int base = next.length - 1;
        //最小连续重叠索引（从右往左）
        int i = base;
        //遍历指针
        int j = base - 1;
        //最高位必定归位
        next[base] = base;

        while (j >= 0) {
            if (ptr.charAt(j) == ptr.charAt(i)) {
                //最小连续重叠索引-1，当前位置next值指向此位置
                next[j] = --i;
            } else {
                //回溯到尾字符，消除重叠状态
                i = base;
                next[j] = base;
            }
            --j;
        }
        return next;
    }
//    /**
//     * 构建next数组
//     * @param ptr 模式字符串
//     * @param reverse 是否反向
//     * @return
//     */
//    private int[] buildNext(String ptr, boolean reverse) {
//        int len = ptr.length();
//        int[] next = new int[len];
//        if (reverse) {
//            char rc = ptr.charAt(len - 1);
//            for (int i = len - 1; i >= 0; i--)
//                next[i] = maxOverlapReversely(i, rc, ptr);
//        } else {
//            char lc = ptr.charAt(0);
//            for (int i = 0; i < len; i++)
//                next[i] = maxOverlap(i, lc, ptr);
//        }
//
//        return next;
//    }
//
//    /**
//     * 计算前后缀最大重叠个数
//     * @param i 当前字符索引
//     * @param lc 前缀首字符
//     * @param ptr 模式字符串
//     * @return
//     */
//    private int maxOverlap(int i, char lc, String ptr) {
//        //后缀尾字符
//        char rc = ptr.charAt(i);
//        //边界
//        int edge = -1;
//        //最大重叠字符索引
//        int m = edge;
//        //以前缀为基点，从倒数第二个字符开始往前遍历
//        for (int j=i-1; j>edge; j--) {
//            char c = ptr.charAt(j);
//            //如果前面没有连续重叠字符，且当前字符等于后缀尾字符，说明前后缀尾字符开始重叠，
//            //接下来要找出对应位置的后缀首字符(i-j，因为前后缀长度一定相等)，然后判断前后缀首字符是否重叠
//            if (m == edge && c == rc) {
//                //如果前后缀首字符没有重叠，则跳过
//                if (ptr.charAt(i - j) != lc)
//                    continue;
//                //如果前后缀首字符重叠，则（暂时）设置最大重叠字符索引为当前索引
//                m = j;
//            } else if (m != edge){
//                //如果前面已经有连续重叠字符，接下来应该判断对应位置的字符是否重叠，
//                //如果重叠，则跳过，继续判断下一个字符
//                if (c == ptr.charAt(i - (m - j)))
//                    continue;
//                //如果不重叠，清除已连续匹配状态
//                m = edge;
//            }
//        }
//        return m + 1;
//    }
//
//    /**
//     * 反向计算前后缀最大重叠个数
//     * @param i 当前字符索引
//     * @param rc 后缀尾字符
//     * @param ptr 模式字符串
//     * @return
//     */
//    private int maxOverlapReversely(int i, char rc, String ptr) {
//        //前缀首字符
//        char lc = ptr.charAt(i);
//        //边界
//        int edge = ptr.length();
//        //最大重叠字符索引
//        int m = edge;
//        //以后缀为基点，从第二个字符开始往后遍历
//        for (int j=i+1, end = edge - 1; j < edge; j++) {
//            char c = ptr.charAt(j);
//            //如果前面没有连续重叠字符，且当前字符等于前缀首字符，说明前后缀首字符开始重叠，
//            //接下来要找出对应位置的前缀尾字符(i-j，因为前后缀长度一定相等)，然后判断前后缀尾字符是否重叠
//            if (m == edge && c == lc) {
//                //如果前后缀尾字符没有重叠，则跳过
//                if (ptr.charAt(end - j) != rc)
//                    continue;
//                //如果前后缀尾字符重叠，则（暂时）设置最大重叠字符索引为当前索引
//                m = j;
//            } else if (m != edge){
//                //如果前面已经有连续重叠字符，接下来应该判断对应位置的字符是否重叠，
//                //如果重叠，则跳过，继续判断下一个字符
//                if (c == ptr.charAt(j - m))
//                    continue;
//                //如果不重叠，清除已连续匹配状态
//                m = edge;
//            }
//        }
//
//        return m - 1;
//    }
}
