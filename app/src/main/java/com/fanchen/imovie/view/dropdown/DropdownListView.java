package com.fanchen.imovie.view.dropdown;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.fanchen.imovie.R;

import java.util.LinkedList;
import java.util.List;

/**
 * 下拉列表
 */
public class DropdownListView extends ScrollView {

    private Context mContext;

    public LinearLayout linearLayout;

    public DropdownItemObject current;

    List<? extends DropdownItemObject> list;

    public DropdownButton button;

    /**
     * 得到当前被选中的itemId
     */
    public int getCurrentSelectedId() {
        if (list != null && current != null)
            return list.indexOf(current);
        return 0;
    }

    public DropdownListView(Context context) {
        this(context, null);
    }

    public DropdownListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropdownListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (listMaxHeight != -1) {
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            if (heightMode == MeasureSpec.EXACTLY) {
                heightSize = heightSize <= listMaxHeight ? heightSize
                        : listMaxHeight;
            }

            if (heightMode == MeasureSpec.UNSPECIFIED) {
                heightSize = heightSize <= listMaxHeight ? heightSize
                        : listMaxHeight;
            }
            if (heightMode == MeasureSpec.AT_MOST) {
                heightSize = heightSize <= listMaxHeight ? heightSize
                        : listMaxHeight;
            }
            int maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
            super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_dropdown_list, this, true);
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
    }


    public void flush() {
        for (int i = 0, n = linearLayout.getChildCount(); i < n; i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof DropdownListItemView) {
                DropdownListItemView itemView = (DropdownListItemView) view;
                setDropdownListItemViewStyle(itemView);
                DropdownItemObject data = (DropdownItemObject) itemView.getTag();
                if (data == null) {
                    return;
                }
                boolean checked = data == current;
                String suffix = data.getSuffix();
                itemView.bind(TextUtils.isEmpty(suffix) ? data.text : data.text + suffix, checked);

                if (checked) {
                    //如果设置了下拉按钮与下拉列表中只显示一个
                    if (onlyShowOne) {
                        //则隐藏下拉列表中的item
                        itemView.setVisibility(View.GONE);
                    }
                    String text = data.text;
                    if (!TextUtils.isEmpty(topBtnTextPrefix)) {
                        text = topBtnTextPrefix + text;
                    }
                    if (!TextUtils.isEmpty(topBtnTextSuffix)) {
                        text = text + topBtnTextSuffix;
                    }
                    button.setText(text);
                } else {
                    itemView.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    public void bind(List<? extends DropdownItemObject> list,
                     DropdownButton button,
                     final Container container,
                     int selectedId
    ) {
        current = null;
        this.list = list;
        this.button = button;
        LinkedList<View> cachedDividers = new LinkedList<>();
        LinkedList<DropdownListItemView> cachedViews = new LinkedList<>();
        for (int i = 0, n = linearLayout.getChildCount(); i < n; i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof DropdownListItemView) {
                cachedViews.add((DropdownListItemView) view);
            } else {
                cachedDividers.add(view);
            }
        }

        linearLayout.removeAllViews();
//        LayoutInflater inflater = LayoutInflater.from(getContext());
        boolean isFirst = true;
        for (DropdownItemObject item : list) {
            if (isFirst) {
                isFirst = false;
            } else {
                View divider = cachedDividers.poll();
                if (divider == null) {
                    divider = genItemDivider();
                }
                linearLayout.addView(divider);
            }
            DropdownListItemView view = cachedViews.poll();
            if (view == null) {

                //创建下拉列表中的item
                view = genDropdownListItemView();
            }
            view.setTag(item);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    DropdownItemObject data = (DropdownItemObject) v.getTag();
                    if (data == null) return;
                    DropdownItemObject oldOne = current;
                    current = data;
                    flush();
                    container.hide();
                    if (oldOne != current) {
                        container.onSelectionChanged(DropdownListView.this);
                    }
                }
            });
            linearLayout.addView(view);
            if (item.id == selectedId && current == null) {
                current = item;
            }
        }

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getVisibility() == VISIBLE) {
                    container.hide();
                } else {
                    container.show(DropdownListView.this);
                }
            }
        });

        if (current == null && list.size() > 0) {
            current = list.get(0);
        }
        flush();
    }

    /**
     * 创建下拉列表的item下划线
     */
    @NonNull
    private View genItemDivider() {
        View divider;
        divider = new View(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemBottomLineHeight);
        params.leftMargin = itemBottomLineMarginLeft;
        params.rightMargin = itemBottomLineMarginRight;
        divider.setLayoutParams(params);
        divider.setBackgroundColor(itemBottomLineColor);
        return divider;
    }

    /**
     * 创建下拉列表中的item
     */
    @NonNull
    private DropdownListItemView genDropdownListItemView() {
        DropdownListItemView view;
        view = new DropdownListItemView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
        view.setLayoutParams(params);
        view.setCompoundDrawablePadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        view.setPadding(itemPaddingLeft, 0, itemPaddingRight, 0);
        view.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        view.setMaxLines(1);
        view.setSingleLine(true);
        view.setTextColor(Color.parseColor("#ff666666"));
        view.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
        return view;
    }


    public static interface Container {
        void show(DropdownListView listView);

        void hide();

        void onSelectionChanged(DropdownListView view);
    }

    /*================== DropdownButton的属性 begin ==================*/
    private boolean onlyShowOne = false;
    private String topBtnTextPrefix = null;
    private String topBtnTextSuffix = null;

    public void setOnlyShowOne(boolean onlyShowOne) {
        this.onlyShowOne = onlyShowOne;
    }

    public void setTopBtnTextPrefix(String topBtnTextPrefix) {
        this.topBtnTextPrefix = topBtnTextPrefix;
    }

    public void setTopBtnTextSuffix(String topBtnTextSuffix) {
        this.topBtnTextSuffix = topBtnTextSuffix;
    }

    /*================== DropdownButton的属性 begin ==================*/
    /*================== DropdownList的属性 begin ==================*/
    private int listMaxHeight = -1;

    public void setListMaxHeight(int listMaxHeight) {
        this.listMaxHeight = listMaxHeight;
    }
    /*================== DropdownList的属性 end ==================*/


    /*================== DropdownListItem的属性 begin ==================*/
    private int itemPaddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
    private int itemPaddingRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
    private int itemHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
    private int itemBottomLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
    private int itemBottomLineMarginLeft = 0;
    private int itemBottomLineMarginRight = 0;
    private int itemBottomLineColor = Color.parseColor("#ffe4e4e4");
    private int itemTextSize = -1;
    private int itemTextNormalColor = -1;
    private int itemTextSelectedColor = -1;
    private int itemNormalBg = -1;
    private int itemSelectedBg = -1;
    private int itemNormalDrawableResId = -1;
    private int itemSelectedDrawableResId = -1;
    private int itemTextGravity = -1;

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
    }

    public void setItemPaddingRight(int itemPaddingRight) {
        this.itemPaddingRight = itemPaddingRight;
    }

    public void setItemPaddingLeft(int itemPaddingLeft) {
        this.itemPaddingLeft = itemPaddingLeft;
    }

    public void setItemBottomLineHeight(int itemBottomLineHeight) {
        this.itemBottomLineHeight = itemBottomLineHeight;
    }

    public void setItemBottomLineMarginLeft(int itemBottomLineMarginLeft) {
        this.itemBottomLineMarginLeft = itemBottomLineMarginLeft;
    }

    public void setItemBottomLineMarginRight(int itemBottomLineMarginRight) {
        this.itemBottomLineMarginRight = itemBottomLineMarginRight;
    }

    public void setItemBottomLineColor(int itemBottomLineColor) {
        this.itemBottomLineColor = itemBottomLineColor;
    }

    public void setItemTextSize(int itemTextSize) {
        this.itemTextSize = itemTextSize;
    }

    public void setItemTextNormalColor(int itemTextNormalColor) {
        this.itemTextNormalColor = itemTextNormalColor;
    }

    public void setItemTextSelectedColor(int itemTextSelectedColor) {
        this.itemTextSelectedColor = itemTextSelectedColor;
    }

    public void setItemNormalBg(int itemNormalBg) {
        this.itemNormalBg = itemNormalBg;
    }

    public void setItemSelectedBg(int itemSelectedBg) {
        this.itemSelectedBg = itemSelectedBg;
    }

    public void setItemNormalDrawableResId(int itemNormalDrawableResId) {
        this.itemNormalDrawableResId = itemNormalDrawableResId;
    }

    public void setItemSelectedDrawableResId(int itemSelectedDrawableResId) {
        this.itemSelectedDrawableResId = itemSelectedDrawableResId;
    }

    public void setItemTextGravity(int itemTextGravity) {
        this.itemTextGravity = itemTextGravity;
    }

    private void setDropdownListItemViewStyle(DropdownListItemView itemView) {
        if (itemTextSize != -1)
            itemView.setItemTextSize(itemTextSize);
        if (itemTextNormalColor != -1)
            itemView.setItemTextNormalColor(itemTextNormalColor);
        if (itemTextSelectedColor != -1)
            itemView.setItemTextSelectedColor(itemTextSelectedColor);
        if (itemNormalBg != -1)
            itemView.setItemNormalBg(itemNormalBg);
        if (itemSelectedBg != -1)
            itemView.setItemSelectedBg(itemSelectedBg);
        if (itemNormalDrawableResId != -1)
            itemView.setItemNormalDrawableResId(itemNormalDrawableResId);
        if (itemSelectedDrawableResId != -1)
            itemView.setItemSelectedDrawableResId(itemSelectedDrawableResId);
        if (itemTextGravity != -1)
            itemView.setItemTextGravity(itemTextGravity);
    }
    /*================== DropdownListItem的属性 end ==================*/

}
