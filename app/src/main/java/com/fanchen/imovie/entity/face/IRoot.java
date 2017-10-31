package com.fanchen.imovie.entity.face;

/**
 * Created by fanchen on 2017/9/19.
 */
public interface IRoot {
    /**
     * 是否有数据
     * @return
     */
    boolean isSuccess();

    /**
     * 错误信息
     * @return
     */
    String getMessage();
}
