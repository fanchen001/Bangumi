package com.fanchen.imovie.view.dropdown;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fanchen.imovie.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义的下拉布局
 * <p>
 * 1、一定要使用init方法进行数据初始化，默认是单列，需要多列时请在布局文件中使用cols属性进行设置，同时init的listData参数的集合个数需要与cols一致
 * 2、可以调用setOnDropdownListChecked方法监听下拉列表的条目的选中情况，以此可以做到级联
 */
public class DropdownLayout extends FrameLayout {

    //与自定义属性对应的gravity值
    public static final int GRAVITY_CENTER = 0;
    public static final int GRAVITY_LEFT = 1;
    public static final int GRAVITY_RIGHT = 2;

    private Context mContext;

    /*------------------ 自定义属性 begin  ------------------*/
    private boolean animationEnabled = true;
    private boolean onlyShowOne = false;
    private int cols = 1;//下拉菜单列数，默认是1

    /**
     * 设置列数
     */
    public void setCols(int cols) {
        this.cols = cols;
    }

    /**
     * 得到列数
     */
    public int getCols() {
        return cols;
    }

    //下拉按钮
    private String topBtnTextPrefix = null;
    private String topBtnTextSuffix = null;
    private int topBg = Color.WHITE;
    private int topHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, getResources().getDisplayMetrics());
    private int topSplitLineColor = Color.LTGRAY;
    private int topSplitLineWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics());
    private int topSplitLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18f, getResources().getDisplayMetrics());
    //---默认由DropdownButton去控制
    private int topTextSize = -1;
    private int topTextNormalColor = -1;
    private int topTextSelectedColor = -1;
    private int topSelectedDrawableResId = -1;
    private int topNormalDrawableResId = -1;
    private int topBottomLineWidth = -1;
    private int topBottomLineHeight = -1;
    private int topBottomLineColor = -1;
    //分割线
    private int splitLineColor = Color.LTGRAY;
    private int splitLineWidth = LayoutParams.MATCH_PARENT;
    private int splitLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, getResources().getDisplayMetrics());
    //遮盖层
    private int maskBg = Color.parseColor("#80000000");
    private int listMaxHeight = -1;
    //---默认由DropdownListItemView去控制
    private int itemPaddingLeft = -1;
    private int itemPaddingRight = -1;
    private int itemHeight = -1;
    private int itemTextSize = -1;
    private int itemTextNormalColor = -1;
    private int itemTextSelectedColor = -1;
    private int itemNormalBg = -1;
    private int itemSelectedBg = -1;
    private int itemNormalDrawable = -1;
    private int itemSelectedDrawable = -1;
    private int itemTextGravity = -1;
    private int itemBottomLineHeight = -1;
    private int itemBottomLineMarginLeft = -1;
    private int itemBottomLineMarginRight = -1;
    private int itemBottomLineColor = -1;
    /*------------------ 自定义属性 end  ------------------*/

    //该组件中最根本的三部分
    private LinearLayout mRoot;//下拉按钮区
    private View mVLine;//分割线
    private FrameLayout mFlContent;//内容及下拉列表区

    private View mask;

    //下拉按钮集合
    private List<DropdownButton> mDropdownButtonList = new ArrayList<>();
    //下拉列表集合
    private List<DropdownListView> mDropdownListViewList = new ArrayList<>();
    //下拉列表中所有列的的key和value的集合
    private List<Map<String, String>> mDropdownListStringData;
    //下拉列表所有列的DropdownItemObject集合的集合
    private List<List<DropdownItemObject>> mAllDropdownListData = new ArrayList<>();

    private Animation dropdown_in, dropdown_out, dropdown_mask_out;
    public DropdownButtonsController dropdownButtonsController = new DropdownButtonsController();

    private int i = 0;

    public DropdownLayout(Context context) {
        super(context);
        mContext = context;
    }

    public DropdownLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DropdownLayout);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.DropdownLayout_ddl_only_show_one) {
                onlyShowOne = typedArray.getBoolean(attr, false);

            } else if (attr == R.styleable.DropdownLayout_ddl_cols) {
                cols = typedArray.getInt(attr, 1);

                //下拉按钮
            } else if (attr == R.styleable.DropdownLayout_ddl_top_btn_text_prefix) {
                topBtnTextPrefix = typedArray.getString(attr);
            } else if (attr == R.styleable.DropdownLayout_ddl_top_btn_text_suffix) {
                topBtnTextSuffix = typedArray.getString(attr);

            } else if (attr == R.styleable.DropdownLayout_ddl_top_bg) {
                topBg = typedArray.getColor(attr, Color.WHITE);

            } else if (attr == R.styleable.DropdownLayout_ddl_top_height) {
                topHeight = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_top_split_line_color) {
                topSplitLineColor = typedArray.getColor(attr, Color.LTGRAY);

            } else if (attr == R.styleable.DropdownLayout_ddl_top_split_line_width) {
                topSplitLineWidth = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_top_split_line_height) {
                topSplitLineHeight = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_top_btn_text_size) {
                topTextSize = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_top_btn_text_normal_color) {
                topTextNormalColor = typedArray.getColor(attr, Color.BLACK);

            } else if (attr == R.styleable.DropdownLayout_ddl_top_btn_text_selected_color) {
                topTextSelectedColor = typedArray.getColor(attr, Color.BLACK);

            } else if (attr == R.styleable.DropdownLayout_ddl_top_btn_noraml_drawable) {
                topNormalDrawableResId = typedArray.getResourceId(attr, -1);

            } else if (attr == R.styleable.DropdownLayout_ddl_top_btn_selected_drawable) {
                topSelectedDrawableResId = typedArray.getResourceId(attr, -1);

            } else if (attr == R.styleable.DropdownLayout_ddl_top_bottom_line_color) {
                topBottomLineColor = typedArray.getColor(attr, Color.LTGRAY);

            } else if (attr == R.styleable.DropdownLayout_ddl_top_bottom_line_width) {
                topBottomLineWidth = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_top_bottom_line_height) {
                topBottomLineHeight = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));


                //分割线
            } else if (attr == R.styleable.DropdownLayout_ddl_split_line_color) {
                splitLineColor = typedArray.getColor(attr, Color.LTGRAY);

            } else if (attr == R.styleable.DropdownLayout_ddl_split_line_width) {
                splitLineWidth = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_split_line_height) {
                splitLineHeight = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));


                //遮盖层
            } else if (attr == R.styleable.DropdownLayout_ddl_mask_bg) {
                maskBg = typedArray.getColor(attr, Color.GRAY);

                //下拉列表最大高度
            } else if (attr == R.styleable.DropdownLayout_ddl_list_max_height) {
                listMaxHeight = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

                //item
            } else if (attr == R.styleable.DropdownLayout_ddl_item_padding_left) {
                itemPaddingLeft = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_item_padding_right) {
                itemPaddingRight = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_item_height) {
                itemHeight = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_item_text_size) {
                itemTextSize = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_item_text_normal_color) {
                itemTextNormalColor = typedArray.getColor(attr, Color.BLACK);

            } else if (attr == R.styleable.DropdownLayout_ddl_item_text_selected_color) {
                itemTextSelectedColor = typedArray.getColor(attr, Color.BLACK);

            } else if (attr == R.styleable.DropdownLayout_ddl_item_normal_bg) {
                itemNormalBg = typedArray.getColor(attr, Color.WHITE);

            } else if (attr == R.styleable.DropdownLayout_ddl_item_selected_bg) {
                itemSelectedBg = typedArray.getColor(attr, Color.GRAY);

            } else if (attr == R.styleable.DropdownLayout_ddl_item_normal_drawable) {
                itemNormalDrawable = typedArray.getResourceId(attr, -1);

            } else if (attr == R.styleable.DropdownLayout_ddl_item_selected__drawable) {
                itemSelectedDrawable = typedArray.getResourceId(attr, -1);

            } else if (attr == R.styleable.DropdownLayout_ddl_item_text_gravity) {
                itemTextGravity = typedArray.getInt(attr, -1);

                //item下划线
            } else if (attr == R.styleable.DropdownLayout_ddl_item_bottom_line_height) {
                itemBottomLineHeight = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_item_bottom_line_margin_left) {
                itemBottomLineMarginLeft = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_item_bottom_line_margin_right) {
                itemBottomLineMarginRight = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, attr, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.DropdownLayout_ddl_item_bottom_line_color) {
                itemBottomLineColor = typedArray.getColor(attr, Color.GRAY);

            }
        }

    }

    /**
     *
     * @param keys
     * @param values
     */
    public void setDropdownList(String[][] keys,String[][] values) {
        if (keys == null || values == null || keys.length != values.length) {
            return;
        }
        List<Map<String, String>> listData = new ArrayList<>();
        for (int i = 0 ; i < values.length ;  i++){
            String[] key = keys[i];
            String[] value = values[i];
            if(key.length == value.length){
                //这里使用LinkedHashMap是为了让下拉列表的条目有序
                Map<String, String> map = new LinkedHashMap<>();
                for (int j = 0 ; j < value.length ;  j++) {
                    map.put(key[j],value[j]);
                }
                listData.add(map);
            }
        }
        init(listData);
    }

    /**
     *
     * @param listData
     */
    public void init(List<Map<String, String>> listData){
        init(null, listData);
    }

    /**
     * 初始化整个布局
     *
     * @param contentView 内容区（可以是一个布局，也可以是一个控件）
     * @param listData    存放多个下拉列表的数据，可指定某一列数据变化（联动时，只能修改不能重新new）
     */
    public void init(View contentView, List<Map<String, String>> listData) {
        if (listData == null) {
            throw new IllegalArgumentException("下拉列表数据不能为空");
        }
        if (cols != listData.size()) {
            throw new IllegalArgumentException("下拉列表的列数与数据个数不一致，列数为" + cols + ",数据个数为" + listData.size());
        }
        this.mDropdownListStringData = listData;
        //初始化所有动画
        dropdown_in = AnimationUtils.loadAnimation(mContext, R.anim.dropdown_in);
        dropdown_out = AnimationUtils.loadAnimation(mContext, R.anim.dropdown_out);
        dropdown_mask_out = AnimationUtils.loadAnimation(mContext, R.anim.dropdown_mask_out);

        //根布局（LinearLayout）
        mRoot = new LinearLayout(mContext);
        mRoot.setOrientation(LinearLayout.VERTICAL);
        mRoot.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mRoot.setGravity(Gravity.CENTER);

        //1、下拉按钮区（LinearLayout）
        LinearLayout llTop = new LinearLayout(mContext);
        llTop.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, topHeight));
        llTop.setBackgroundColor(topBg);
        llTop.setGravity(Gravity.CENTER);
        //根据下拉按钮个数创建下拉按钮
        for (i = 0; i < cols; i++) {
            DropdownButton dropdownButton = new DropdownButton(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            dropdownButton.setLayoutParams(params);
            llTop.addView(dropdownButton);
            //设置按钮样式
            setDropdownButtonStyle(dropdownButton);
            mDropdownButtonList.add(dropdownButton);
            if (i != cols - 1) {
                View view = new View(mContext);
                view.setLayoutParams(new LinearLayout.LayoutParams(topSplitLineWidth, topSplitLineHeight));
                view.setBackgroundColor(topSplitLineColor);
                llTop.addView(view);
            }
        }

        //2、分割线（View）
        mVLine = new View(mContext);
        mVLine.setLayoutParams(new LinearLayout.LayoutParams(splitLineWidth, splitLineHeight));
        mVLine.setBackgroundColor(splitLineColor);

        //3、下拉列表区（FrameLayout）
        mFlContent = new FrameLayout(mContext);
        mFlContent.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //将内容区放置最底
        if (contentView != null) {
            mFlContent.addView(contentView);
        }
        //创建遮盖层
        mask = new View(mContext);
        mask.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mask.setBackgroundColor(maskBg);
        //遮盖层点击事件
        mask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                maskClick(v);
            }
        });
        mFlContent.addView(mask);
        //根据下拉按钮个数创建对应个数的下拉列表
        for (i = 0; i < cols; i++) {
            DropdownListView dropdownListView = new DropdownListView(mContext);
            dropdownListView.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            //设置下拉列表和item样式
            setDropdownListAndItemViewStyle(dropdownListView);

            mFlContent.addView(dropdownListView);
            mDropdownListViewList.add(dropdownListView);
        }

        mRoot.addView(llTop);
        mRoot.addView(mVLine);
        mRoot.addView(mFlContent);
        this.addView(mRoot);

        dropdownButtonsController.init();
    }

    /**
     * 遮盖层的点击回调
     */
    private void maskClick(View view) {
        dropdownButtonsController.hide();
    }

    /**
     * 根据自定义属性设置下拉按钮的样式
     */
    private void setDropdownButtonStyle(DropdownButton dropdownButton) {
        if (topTextSize != -1)
            dropdownButton.setTextSize(topTextSize);
        if (topTextNormalColor != -1)
            dropdownButton.setTextNormalColor(topTextNormalColor);
        if (topTextSelectedColor != -1)
            dropdownButton.setTextSelectedColor(topTextSelectedColor);
        if (topNormalDrawableResId != -1)
            dropdownButton.setNormalDrawableResId(topNormalDrawableResId);
        if (topSelectedDrawableResId != -1)
            dropdownButton.setSelectedDrawableResId(topSelectedDrawableResId);
        if (topBottomLineColor != -1)
            dropdownButton.setBottomLineColor(topBottomLineColor);
        if (topBottomLineWidth != -1)
            dropdownButton.setBottomLineWidth(topBottomLineWidth);
        if (topBottomLineHeight != -1)
            dropdownButton.setBottomLineHeight(topBottomLineHeight);
    }

    /**
     * 设置下拉列表和item样式
     */
    private void setDropdownListAndItemViewStyle(DropdownListView dropdownListView) {
        dropdownListView.setOnlyShowOne(onlyShowOne);
        if (topBtnTextPrefix != null)
            dropdownListView.setTopBtnTextPrefix(topBtnTextPrefix);
        if (topBtnTextSuffix != null)
            dropdownListView.setTopBtnTextSuffix(topBtnTextSuffix);
        if (listMaxHeight != -1)
            dropdownListView.setListMaxHeight(listMaxHeight);
        if (itemPaddingLeft != -1)
            dropdownListView.setItemPaddingLeft(itemPaddingLeft);
        if (itemPaddingRight != -1)
            dropdownListView.setItemPaddingRight(itemPaddingRight);
        if (itemHeight != -1)
            dropdownListView.setItemHeight(itemHeight);
        if (itemTextSize != -1)
            dropdownListView.setItemTextSize(itemTextSize);
        if (itemTextNormalColor != -1)
            dropdownListView.setItemTextNormalColor(itemTextNormalColor);
        if (itemTextSelectedColor != -1)
            dropdownListView.setItemTextSelectedColor(itemTextSelectedColor);
        if (itemNormalBg != -1)
            dropdownListView.setItemNormalBg(itemNormalBg);
        if (itemSelectedBg != -1)
            dropdownListView.setItemSelectedBg(itemSelectedBg);
        if (itemNormalDrawable != -1)
            dropdownListView.setItemNormalDrawableResId(itemNormalDrawable);
        if (itemSelectedDrawable != -1)
            dropdownListView.setItemSelectedDrawableResId(itemSelectedDrawable);
        if (itemTextGravity != -1) {
            switch (itemTextGravity) {
                case 0://居中
                    itemTextGravity = Gravity.CENTER;
                    break;
                case 1://左
                    itemTextGravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                    break;
                case 2://右
                    itemTextGravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                    break;
            }
            dropdownListView.setItemTextGravity(itemTextGravity);
        }
        if (itemBottomLineHeight != -1)
            dropdownListView.setItemBottomLineHeight(itemBottomLineHeight);
        if (itemBottomLineMarginLeft != -1)
            dropdownListView.setItemBottomLineMarginLeft(itemBottomLineMarginLeft);
        if (itemBottomLineMarginRight != -1)
            dropdownListView.setItemBottomLineMarginRight(itemBottomLineMarginRight);
        if (itemBottomLineColor != -1) dropdownListView.setItemBottomLineColor(itemBottomLineColor);
    }


    /**
     * 下拉按钮与下拉列表的核心控制器
     */
    public class DropdownButtonsController implements DropdownListView.Container {
        private DropdownListView currentDropdownList;

        @Override
        public void show(DropdownListView view) {
            if (currentDropdownList != null) {
                currentDropdownList.clearAnimation();
                if (animationEnabled)
                    currentDropdownList.startAnimation(dropdown_out);
                currentDropdownList.setVisibility(View.GONE);
                currentDropdownList.button.setChecked(false);
            }
            currentDropdownList = view;
            mask.clearAnimation();
            mask.setVisibility(View.VISIBLE);
            currentDropdownList.clearAnimation();
            if (animationEnabled)
                currentDropdownList.startAnimation(dropdown_in);
            currentDropdownList.setVisibility(View.VISIBLE);
            currentDropdownList.button.setChecked(true);

            if (mOnDropdownListListener != null) {
                mOnDropdownListListener.onDropdownListOpen();
            }
        }

        @Override
        public void hide() {
            if (currentDropdownList != null) {
                currentDropdownList.clearAnimation();
                if (animationEnabled)
                    currentDropdownList.startAnimation(dropdown_out);
                currentDropdownList.button.setChecked(false);
                mask.clearAnimation();
                if (animationEnabled)
                    mask.startAnimation(dropdown_mask_out);
            }
            currentDropdownList = null;
            if (!animationEnabled)
                reset();

            if (mOnDropdownListListener != null) {
                mOnDropdownListListener.onDropdownListClosed();
            }
        }

        @Override
        public void onSelectionChanged(DropdownListView view) {
            if (mOnDropdownListListener != null) {
                int indexOf = mDropdownListViewList.indexOf(view);
                DropdownItemObject current = view.current;
                mOnDropdownListListener.OnDropdownListSelected(indexOf, current.id, current.text, current.value);
            }
        }

        /**
         * 重置所有下拉按钮的样式
         */
        void reset() {
            for (DropdownButton button : mDropdownButtonList) {
                button.setChecked(false);
            }
            for (DropdownListView listView : mDropdownListViewList) {
                listView.setVisibility(View.GONE);
                listView.clearAnimation();
            }
            mask.setVisibility(View.GONE);
            mask.clearAnimation();
        }

        void init() {

            reset();

            transToItemObjectList();

            dropdown_mask_out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (currentDropdownList == null) {
                        reset();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        /**
         * 将数据转换为DropdownListView专用的ItemObjec集合数据
         */
        public void transToItemObjectList() {
            mAllDropdownListData.clear();
            //根据下拉列表的数据，创建下拉列表专用的数据类型集合的数据
            for (Map<String, String> map : mDropdownListStringData) {
                List<DropdownItemObject> dataSet = new ArrayList<>();
                int i = 0;
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    dataSet.add(new DropdownItemObject(entry.getKey(), i++, entry.getValue()));
                }
                mAllDropdownListData.add(dataSet);
            }
            //根据下拉按钮的个数，给每列下拉列表绑定数据
            for (int i = 0; i < cols; i++) {
                //默认选中第一个
                mDropdownListViewList.get(i).bind(mAllDropdownListData.get(i), mDropdownButtonList.get(i), this, mDropdownListViewList.get(i).getCurrentSelectedId());
                mDropdownListViewList.get(i).flush();
            }
        }
    }

    /**
     * 更新数据（用于联动）
     */
    public void notifyDataSetChanged() {
        dropdownButtonsController.transToItemObjectList();
    }

    /*================== 下拉列表item的点击回调 begin ==================*/
    private OnDropdownListListener mOnDropdownListListener;

    public void setOnDropdownListListener(OnDropdownListListener onDropdownListListener) {
        this.mOnDropdownListListener = onDropdownListListener;
    }

    public interface OnDropdownListListener {

        /**
         * 下拉列表item的点击回调
         *
         * @param indexOfButton 下拉按钮的下标
         * @param indexOfList   下拉列表item的下标
         * @param textOfList    下拉列表对应的键
         * @param valueOfList   下拉列表对应的值
         */
        void OnDropdownListSelected(int indexOfButton, int indexOfList, String textOfList, String valueOfList);

        void onDropdownListOpen();

        void onDropdownListClosed();
    }
     /*================== 下拉列表item的点击回调 end ==================*/

    /*================== 自定义属性的getter和setter方法 begin ==================*/

    public boolean isAnimationEnabled() {
        return animationEnabled;
    }

    public void setAnimationEnabled(boolean animationEnabled) {
        this.animationEnabled = animationEnabled;
    }

    public boolean isOnlyShowOne() {
        return onlyShowOne;
    }

    public void setOnlyShowOne(boolean onlyShowOne) {
        this.onlyShowOne = onlyShowOne;
    }

    public String getTopBtnTextPrefix() {
        return topBtnTextPrefix;
    }

    public void setTopBtnTextPrefix(String topBtnTextPrefix) {
        this.topBtnTextPrefix = topBtnTextPrefix;
    }

    public String getTopBtnTextSuffix() {
        return topBtnTextSuffix;
    }

    public void setTopBtnTextSuffix(String topBtnTextSuffix) {
        this.topBtnTextSuffix = topBtnTextSuffix;
    }

    public int getTopBg() {
        return topBg;
    }

    public void setTopBg(int topBg) {
        this.topBg = topBg;
    }

    public int getTopHeight() {
        return topHeight;
    }

    public void setTopHeight(int topHeight) {
        this.topHeight = topHeight;
    }

    public int getTopSplitLineColor() {
        return topSplitLineColor;
    }

    public void setTopSplitLineColor(int topSplitLineColor) {
        this.topSplitLineColor = topSplitLineColor;
    }

    public int getTopSplitLineWidth() {
        return topSplitLineWidth;
    }

    public void setTopSplitLineWidth(int topSplitLineWidth) {
        this.topSplitLineWidth = topSplitLineWidth;
    }

    public int getTopSplitLineHeight() {
        return topSplitLineHeight;
    }

    public void setTopSplitLineHeight(int topSplitLineHeight) {
        this.topSplitLineHeight = topSplitLineHeight;
    }

    public int getTopTextSize() {
        return topTextSize;
    }

    public void setTopTextSize(int topTextSize) {
        this.topTextSize = topTextSize;
    }

    public int getTopTextNormalColor() {
        return topTextNormalColor;
    }

    public void setTopTextNormalColor(int topTextNormalColor) {
        this.topTextNormalColor = topTextNormalColor;
    }

    public int getTopTextSelectedColor() {
        return topTextSelectedColor;
    }

    public void setTopTextSelectedColor(int topTextSelectedColor) {
        this.topTextSelectedColor = topTextSelectedColor;
    }

    public int getTopSelectedDrawableResId() {
        return topSelectedDrawableResId;
    }

    public void setTopSelectedDrawableResId(int topSelectedDrawableResId) {
        this.topSelectedDrawableResId = topSelectedDrawableResId;
    }

    public int getTopNormalDrawableResId() {
        return topNormalDrawableResId;
    }

    public void setTopNormalDrawableResId(int topNormalDrawableResId) {
        this.topNormalDrawableResId = topNormalDrawableResId;
    }

    public int getTopBottomLineWidth() {
        return topBottomLineWidth;
    }

    public void setTopBottomLineWidth(int topBottomLineWidth) {
        this.topBottomLineWidth = topBottomLineWidth;
    }

    public int getTopBottomLineHeight() {
        return topBottomLineHeight;
    }

    public void setTopBottomLineHeight(int topBottomLineHeight) {
        this.topBottomLineHeight = topBottomLineHeight;
    }

    public int getTopBottomLineColor() {
        return topBottomLineColor;
    }

    public void setTopBottomLineColor(int topBottomLineColor) {
        this.topBottomLineColor = topBottomLineColor;
    }

    public int getSplitLineColor() {
        return splitLineColor;
    }

    public void setSplitLineColor(int splitLineColor) {
        this.splitLineColor = splitLineColor;
    }

    public int getSplitLineWidth() {
        return splitLineWidth;
    }

    public void setSplitLineWidth(int splitLineWidth) {
        this.splitLineWidth = splitLineWidth;
    }

    public int getSplitLineHeight() {
        return splitLineHeight;
    }

    public void setSplitLineHeight(int splitLineHeight) {
        this.splitLineHeight = splitLineHeight;
    }

    public int getMaskBg() {
        return maskBg;
    }

    public void setMaskBg(int maskBg) {
        this.maskBg = maskBg;
    }

    public int getListMaxHeight() {
        return listMaxHeight;
    }

    public void setListMaxHeight(int listMaxHeight) {
        this.listMaxHeight = listMaxHeight;
    }

    public int getItemPaddingLeft() {
        return itemPaddingLeft;
    }

    public void setItemPaddingLeft(int itemPaddingLeft) {
        this.itemPaddingLeft = itemPaddingLeft;
    }

    public int getItemPaddingRight() {
        return itemPaddingRight;
    }

    public void setItemPaddingRight(int itemPaddingRight) {
        this.itemPaddingRight = itemPaddingRight;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public int getItemTextSize() {
        return itemTextSize;
    }

    public void setItemTextSize(int itemTextSize) {
        this.itemTextSize = itemTextSize;
    }

    public int getItemTextNormalColor() {
        return itemTextNormalColor;
    }

    public void setItemTextNormalColor(int itemTextNormalColor) {
        this.itemTextNormalColor = itemTextNormalColor;
    }

    public int getItemTextSelectedColor() {
        return itemTextSelectedColor;
    }

    public void setItemTextSelectedColor(int itemTextSelectedColor) {
        this.itemTextSelectedColor = itemTextSelectedColor;
    }

    public int getItemNormalBg() {
        return itemNormalBg;
    }

    public void setItemNormalBg(int itemNormalBg) {
        this.itemNormalBg = itemNormalBg;
    }

    public int getItemSelectedBg() {
        return itemSelectedBg;
    }

    public void setItemSelectedBg(int itemSelectedBg) {
        this.itemSelectedBg = itemSelectedBg;
    }

    public int getItemNormalDrawable() {
        return itemNormalDrawable;
    }

    public void setItemNormalDrawable(int itemNormalDrawable) {
        this.itemNormalDrawable = itemNormalDrawable;
    }

    public int getItemSelectedDrawable() {
        return itemSelectedDrawable;
    }

    public void setItemSelectedDrawable(int itemSelectedDrawable) {
        this.itemSelectedDrawable = itemSelectedDrawable;
    }

    public int getItemTextGravity() {
        return itemTextGravity;
    }

    public void setItemTextGravity(int itemTextGravity) {
        this.itemTextGravity = itemTextGravity;
    }

    public int getItemBottomLineHeight() {
        return itemBottomLineHeight;
    }

    public void setItemBottomLineHeight(int itemBottomLineHeight) {
        this.itemBottomLineHeight = itemBottomLineHeight;
    }

    public int getItemBottomLineMarginLeft() {
        return itemBottomLineMarginLeft;
    }

    public void setItemBottomLineMarginLeft(int itemBottomLineMarginLeft) {
        this.itemBottomLineMarginLeft = itemBottomLineMarginLeft;
    }

    public int getItemBottomLineMarginRight() {
        return itemBottomLineMarginRight;
    }

    public void setItemBottomLineMarginRight(int itemBottomLineMarginRight) {
        this.itemBottomLineMarginRight = itemBottomLineMarginRight;
    }

    public int getItemBottomLineColor() {
        return itemBottomLineColor;
    }

    public void setItemBottomLineColor(int itemBottomLineColor) {
        this.itemBottomLineColor = itemBottomLineColor;
    }
    /*================== 自定义属性的getter和setter方法 end ==================*/
}
