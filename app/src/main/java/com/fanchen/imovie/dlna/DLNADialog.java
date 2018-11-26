package com.fanchen.imovie.dlna;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.fanchen.imovie.dialog.MaterialListDialog;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;

/**
 * DLNADialog
 * Created by fanchen on 2018/10/13.
 */
public class DLNADialog extends MaterialListDialog implements DLNAManager.DeviceRefreshListener {

    private View nullView = null;
    private DLNAManager dlnaManager = null;
    private ControlPoint mControlPoint;
    private OnSelectDLNAListener onSelectDLNAListener = null;

    public DLNADialog(Context context) {
        super(context, new ArrayList<DLNAManager.DeviceWarp>());
        dlnaManager = DLNAManager.getInstance(context);
        ArrayAdapter adapter = getAdapter();
        if (adapter != null) adapter.addAll(dlnaManager.getDeviceList());
        dlnaManager.setOnDeviceRefreshListener(this);
        setButtonVisble(View.GONE);
        setTitleVisble(View.VISIBLE);
    }

    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();
        dlnaManager.search();
    }

    @Override
    public View onCreateView() {
        View createView = super.onCreateView();
        title("扫描DLNA设备");
        ArrayAdapter<?> adapter = getAdapter();
        if (adapter != null && adapter.getCount() == 0 && progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            nullView = new View(context);
            nullView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(60)));
            ll_container.addView(nullView);
        }
        return createView;
    }

    @Override
    public void onDeviceRefresh() {
        if (mControlPoint == null) mControlPoint = dlnaManager.getControlPoint();
        ArrayAdapter adapter = getAdapter();
        if (adapter == null) return;
        adapter.clear();
        adapter.addAll(dlnaManager.getDeviceList());
        if (progressBar != null && adapter.getCount() > 0) {
            progressBar.setVisibility(View.GONE);
            if (nullView != null && nullView.getParent() != null)
                ((ViewGroup) nullView.getParent()).removeView(nullView);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 0 || parent.getCount() <= position) return;
        DLNAManager.DeviceWarp deviceWarp = (DLNAManager.DeviceWarp) parent.getItemAtPosition(position);
        if (mControlPoint == null) mControlPoint = dlnaManager.getControlPoint();
        if (onSelectDLNAListener != null && deviceWarp.device != null && deviceWarp.device.getDetails() != null && mControlPoint != null)
            onSelectDLNAListener.onSelectDLNA(deviceWarp.device, mControlPoint);
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (nullView != null && nullView.getParent() != null)
            ((ViewGroup) nullView.getParent()).removeView(nullView);
        dlnaManager.setOnDeviceRefreshListener(null);
    }

    public void setOnSelectDLNAListener(OnSelectDLNAListener onSelectDLNAListener) {
        this.onSelectDLNAListener = onSelectDLNAListener;
    }

    /**
     * OnSelectDLNAListener
     */
    public interface OnSelectDLNAListener {
        /**
         * 选中设备
         *
         * @param device
         * @param controlPoint
         */
        void onSelectDLNA(Device device, ControlPoint controlPoint);
    }
}
