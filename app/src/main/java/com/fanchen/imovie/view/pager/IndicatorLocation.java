package com.fanchen.imovie.view.pager;

/**
 * 指示器位置
 * Left左
 * center中
 * Right右
 *
 * @author Edwin.Wu
 * @version 2016/12/1 18:59
 * @since JDK1.8
 */
public enum IndicatorLocation {
    Left(1),
    Center(0),
    Right(2);

    private int value;

    IndicatorLocation(int idx) {
        this.value = idx;
    }

    public int getValue() {
        return value;
    }
}
