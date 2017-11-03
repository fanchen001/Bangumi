package com.fanchen.imovie.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import com.fanchen.imovie.R;

/**
 * Created by fanchen on 2017/11/3.
 */
public class PermissionDialog extends AlertDialog implements View.OnClickListener {
    private View dialogView;
    private View.OnClickListener clickListener;

    public PermissionDialog(Context context) {
        super(context);
        init(context);
    }

    public PermissionDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected PermissionDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        dialogView = View.inflate(context, R.layout.dialog_permission_guide, null);
        setView(dialogView, 0, 0, 0, 0);
        dialogView.findViewById(R.id.tv_open).setOnClickListener(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public PermissionDialog setOnClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (clickListener != null) {
            clickListener.onClick(v);
        }
    }
}
