package com.fanchen.imovie.view.pager;

/**
 * Loop style
 * 默认empty
 * 深度depth
 * 缩小zoo
 *
 * @author Edwin.Wu
 * @version 2016/11/2 00:41
 * @since JDK1.8
 */
public enum LoopStyle {
    Empty(-1),
    Depth(1),
    Zoom(2);

    private int value;

    LoopStyle(int idx) {
        this.value = idx;
    }

    public int getValue() {
        return value;
    }

}
