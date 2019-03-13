package com.fanchen.imovie.dialog;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.VideoDetailsActivity;
import com.fanchen.imovie.base.BaseActivity;
import com.fanchen.imovie.entity.bmob.DialogBanner;
import com.fanchen.imovie.picasso.PicassoWrap;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * MainBannerDialog
 * Created by fanchen on 2018/8/6.
 */
public class MainBannerDialog extends MaterialDialog implements OnButtonClickListener {

    private DialogBanner banner;
    private BaseActivity activity;

    public MainBannerDialog(BaseActivity context, DialogBanner banner) {
        super(context, R.layout.dialog_main_banner);
        this.activity = context;
        this.banner = banner;
        btnText("知道了", "马上去看");
        setButtonClickListener(this);
    }

    @Override
    public void setUiBeforShow() {
        super.setUiBeforShow();
        TextView textView1 = (TextView) view.findViewById(R.id.tv_banner_title);
        TextView textView2 = (TextView) view.findViewById(R.id.tv_banner_info);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_banner_image);
        textView2.setText(banner.getIntroduce());
        textView1.setText(banner.getTitle());
        tv_title.setTextSize(18);
        if(activity != null) new PicassoWrap(activity.getPicasso()).loadHorizontal(banner.getCover(), imageView);
    }

    @Override
    public View onCreateView() {
        return super.onCreateView();
    }

    @Override
    public void onButtonClick(BaseAlertDialog<?> dialog, int btn) {
        if (btn == RIGHT) VideoDetailsActivity.Companion.startActivity(context, banner);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("banner_int", banner.getBannerInt()).apply();
        dismiss();
    }

    public static void showMainBanner(final BaseActivity context) {
        BmobQuery<DialogBanner> dialogBannerBmobQuery = new BmobQuery<>();
        dialogBannerBmobQuery.findObjects(context.getApplication(), new OnFindListener(context));
    }

    private static class OnFindListener extends FindListener<DialogBanner> {
        private BaseActivity context;

        private OnFindListener(BaseActivity context) {
            this.context = context;
        }

        @Override
        public void onSuccess(List<DialogBanner> list) {
            if (list == null || list.size() == 0) return;
            int banner_int = PreferenceManager.getDefaultSharedPreferences(context).getInt("banner_int", 0);
            for (DialogBanner banner : list) {
                if (banner.getBannerInt() > banner_int) {
                    MainBannerDialog dialog = new MainBannerDialog(context, banner);
                    dialog.title("新片推荐~~");
                    dialog.show();
                    return;
                }
            }
        }

        @Override
        public void onError(int i, String s) {
        }
    }
}
