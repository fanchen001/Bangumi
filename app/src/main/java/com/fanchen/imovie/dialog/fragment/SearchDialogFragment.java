package com.fanchen.imovie.dialog.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanchen.imovie.R;
import com.fanchen.imovie.activity.CaptureActivity;
import com.fanchen.imovie.adapter.SearchHistoryAdapter;
import com.fanchen.imovie.base.BaseAdapter;
import com.fanchen.imovie.db.SearchHistoryHelper;
import com.fanchen.imovie.dialog.anim.CircularRevealAnim;
import com.fanchen.imovie.entity.face.ISearchWord;
import com.fanchen.imovie.entity.baidu.SearchHitRoot;
import com.fanchen.imovie.retrofit.RetrofitManager;
import com.fanchen.imovie.retrofit.callback.RetrofitCallback;
import com.fanchen.imovie.retrofit.service.BaiduService;
import com.fanchen.imovie.util.KeyBoardUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索弹窗
 * <p/>
 * Created by fanchen on 2017/1/13.
 */
public class SearchDialogFragment extends DialogFragment {

    public static final String TAG = "SearchFragment";
    private ImageView ivSearchBack;
    private EditText etSearchKeyword;
    private ImageView ivSearchSearch;
    private RecyclerView rvSearchHistory;
    private View searchUnderline;
    private TextView tvSearchClean;
    private View viewSearchOutside;
    private View viewScan;

    private long millis = System.currentTimeMillis();
    private View view;
    //动画
    private CircularRevealAnim mCircularRevealAnim;
    //历史搜索记录
    private ArrayList<ISearchWord> allHistorys = new ArrayList<>();
    private ArrayList<ISearchWord> historys = new ArrayList<>();
    //适配器
    private SearchHistoryAdapter searchHistoryAdapter;
    private InterfaceImpl impl = new InterfaceImpl();
    //数据库
    private SearchHistoryHelper searchHistoryDB;

    private OnSearchClickListener iOnSearchClickListener;
    private RetrofitManager mRetrofitManager;

    /**
     * @return
     */
    public static SearchDialogFragment newInstance() {
        return new SearchDialogFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRetrofitManager = RetrofitManager.with(getContext());
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        initDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_search, container, false);
        init();//实例化
        return view;
    }

    private void init() {
        ivSearchBack = (ImageView) view.findViewById(R.id.iv_search_back);
        etSearchKeyword = (EditText) view.findViewById(R.id.et_search_keyword);
        ivSearchSearch = (ImageView) view.findViewById(R.id.iv_search_search);
        rvSearchHistory = (RecyclerView) view.findViewById(R.id.rv_search_history);
        searchUnderline = (View) view.findViewById(R.id.search_underline);
        tvSearchClean = (TextView) view.findViewById(R.id.tv_search_clean);
        viewSearchOutside = (View) view.findViewById(R.id.view_search_outside);
        viewScan = view.findViewById(R.id.iv_search_scan);
        //实例化动画效果
        mCircularRevealAnim = new CircularRevealAnim();
        //监听动画
        mCircularRevealAnim.setAnimListener(impl);

        getDialog().setOnKeyListener(impl);//键盘按键监听
        ivSearchSearch.getViewTreeObserver().addOnPreDrawListener(impl);//绘制监听

        //实例化数据库
        searchHistoryDB = new SearchHistoryHelper(getContext(), SearchHistoryHelper.DB_NAME, null, 1);

        allHistorys = searchHistoryDB.queryAllHistory();

        setAllHistorys();

        //初始化recyclerView
        rvSearchHistory.setLayoutManager(new BaseAdapter.LinearLayoutManagerWrapper(getContext()));//list类型
        searchHistoryAdapter = new SearchHistoryAdapter(getContext());
        searchHistoryAdapter.addAll(historys);
        rvSearchHistory.setAdapter(searchHistoryAdapter);

        //设置删除单个记录的监听
        searchHistoryAdapter.setOnItemDeleteListener(impl);
        searchHistoryAdapter.setOnItemClickListener(impl);
        //监听编辑框文字改变
        etSearchKeyword.addTextChangedListener(new TextWatcherImpl());
        //监听点击
        ivSearchBack.setOnClickListener(impl);
        viewSearchOutside.setOnClickListener(impl);
        ivSearchSearch.setOnClickListener(impl);
        tvSearchClean.setOnClickListener(impl);
        viewScan.setOnClickListener(impl);
    }

    /**
     * 初始化SearchFragment
     */
    private void initDialog() {
        Window window = getDialog().getWindow();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels * 0.98); //DialogSearch的宽
        window.setLayout(width, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.TOP);
        window.setWindowAnimations(R.style.DialogEmptyAnimation);//取消过渡动画 , 使DialogSearch的出现更加平滑
    }


    private class InterfaceImpl implements DialogInterface.OnKeyListener, ViewTreeObserver.OnPreDrawListener,
            CircularRevealAnim.AnimListener, View.OnClickListener,
            SearchHistoryAdapter.OnItemClickListener, SearchHistoryAdapter.OnItemDeleteListener {

        /**
         * 监听键盘按键
         */
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                hideAnim();
            } else if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                search();
            }
            return false;
        }

        /**
         * 监听搜索键绘制时
         */
        @Override
        public boolean onPreDraw() {
            if(mCircularRevealAnim != null && ivSearchSearch != null && view != null){
                ivSearchSearch.getViewTreeObserver().removeOnPreDrawListener(this);
                mCircularRevealAnim.show(ivSearchSearch, view);
            }
            return true;
        }

        /**
         * 搜索框动画隐藏完毕时调用
         */
        @Override
        public void onHideAnimationEnd() {
            if(etSearchKeyword != null){
                etSearchKeyword.setText("");
            }
            dismiss();
        }

        /**
         * 搜索框动画显示完毕时调用
         */
        @Override
        public void onShowAnimationEnd() {
            if (isVisible() && etSearchKeyword != null) {
                KeyBoardUtils.openKeyboard(getContext(), etSearchKeyword);
            }
        }

        @Override
        public void OnItemDelete(List<?> datas, int position) {
            if (datas == null || datas.size() <= position || position < 0 || !(datas.get(position) instanceof ISearchWord))
                return;
            String keyword = ((ISearchWord) datas.get(position)).getWord();
            if(searchHistoryDB != null){
                searchHistoryDB.deleteHistory(keyword);
            }
            if(historys != null){
                historys.remove(keyword);
            }
            checkHistorySize();
            if(searchHistoryAdapter != null){
                searchHistoryAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.iv_search_back || view.getId() == R.id.view_search_outside) {
                hideAnim();
            } else if (view.getId() == R.id.iv_search_search) {
                search();
            } else if (view.getId() == R.id.tv_search_clean && searchHistoryDB != null
            && historys != null && searchUnderline != null && searchHistoryAdapter != null) {
                searchHistoryDB.deleteAllHistory();
                historys.clear();
                searchUnderline.setVisibility(View.GONE);
                searchHistoryAdapter.notifyDataSetChanged();
            } else if (view.getId() == R.id.iv_search_scan) {
                if (isAdded() && !isDetached()) {
                    CaptureActivity.startActivity(getActivity());
                }
                hideAnim();
            }
        }

        @Override
        public void onItemClick(List<?> datas, View v, int position) {
            if (datas.get(position) == null) return;
            if (!(datas.get(position) instanceof ISearchWord)) return;
            ISearchWord keyword = (ISearchWord) datas.get(position);
            if (iOnSearchClickListener != null)
                iOnSearchClickListener.onSearchClick(keyword);
            hideAnim();
        }

    }

    private void hideAnim() {
        if(etSearchKeyword != null  && mCircularRevealAnim != null && ivSearchSearch != null && view != null){
            KeyBoardUtils.closeKeyboard(getContext(), etSearchKeyword);
            mCircularRevealAnim.hide(ivSearchSearch, view);
        }
    }

    private void search() {
        if(etSearchKeyword != null && searchHistoryDB != null){
            final String searchKey = etSearchKeyword.getText().toString();
            if (TextUtils.isEmpty(searchKey.trim())) {
                Toast.makeText(getContext(), "请输入关键字", Toast.LENGTH_SHORT).show();
            } else {
                if (iOnSearchClickListener != null)
                    iOnSearchClickListener.onSearchClick(new ISearchWord() {
                        @Override
                        public int getViewType() {
                            return TYPE_NORMAL;
                        }

                        @Override
                        public int getType() {
                            return ISearchWord.TYPE_WORD;
                        }

                        @Override
                        public String getWord() {
                            return searchKey;
                        }
                    });//接口回调
                searchHistoryDB.insertHistory(searchKey);//插入到数据库
                hideAnim();
            }
        }
    }

    private void checkHistorySize() {
        if (historys != null && historys.size() < 1 && searchUnderline != null) {
            searchUnderline.setVisibility(View.GONE);
        } else if(searchUnderline != null){
            searchUnderline.setVisibility(View.VISIBLE);
        }
    }

    private void setAllHistorys() {
        if (historys != null &&  allHistorys != null) {
            historys.clear();
            historys.addAll(allHistorys);
            checkHistorySize();
        }
    }

    private void setKeyWordHistorys(String keyword) {
        historys.clear();
        for (ISearchWord string : allHistorys) {
            if (string.getWord().contains(keyword)) {
                historys.add(string);
            }
        }
        searchHistoryAdapter.notifyDataSetChanged();
        checkHistorySize();
    }


    public void setOnSearchClickListener(OnSearchClickListener iOnSearchClickListener) {
        this.iOnSearchClickListener = iOnSearchClickListener;
    }

    /**
     * @param activity
     */
    public void show(FragmentActivity activity) {
        if (isAdded()) return;
        show(activity.getSupportFragmentManager(), TAG);
    }

    /**
     * @param activity
     */
    public void show(FragmentManager activity) {
        if (isAdded()) return;
        show(activity, TAG);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        //防止  Fragment already added:  异常
        long currentTime = System.currentTimeMillis();
        if (currentTime - millis < 1000) {
            return;
        }
        millis = currentTime;
        try {
            Field mAddedField = manager.getClass().getDeclaredField("mAdded");
            mAddedField.setAccessible(true);
            List<?> mAdded = (List<?>) mAddedField.get(manager);
            if (mAdded != null && mAdded.contains(this)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.show(manager, tag);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        //防止  Fragment already added:  异常
        long currentTime = System.currentTimeMillis();
        if (currentTime - millis < 1000) {
            return -1;
        }
        millis = currentTime;
        try {
            Field mManagerField = transaction.getClass().getDeclaredField("mManager");
            mManagerField.setAccessible(true);
            FragmentManager mManager = (FragmentManager) mManagerField.get(transaction);
            if (mManager != null) {
                Field mAddedField = mManager.getClass().getDeclaredField("mAdded");
                mAddedField.setAccessible(true);
                List<?> mAdded = (List<?>) mAddedField.get(mManager);
                if (mAdded != null && mAdded.contains(this)) {
                    return -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.show(transaction, tag);
    }

    /**
     * 监听编辑框文字改变
     */
    private class TextWatcherImpl implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String keyword = charSequence.toString().trim();
            if (!TextUtils.isEmpty(keyword) && mRetrofitManager != null) {
                setKeyWordHistorys(keyword);
                mRetrofitManager.enqueue(BaiduService.class, apkCallback, "searchHit", keyword);
            } else {
                setAllHistorys();
                searchHistoryAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    public interface OnSearchClickListener {
        void onSearchClick(ISearchWord word);
    }

    private RetrofitCallback<SearchHitRoot> apkCallback = new RetrofitCallback<SearchHitRoot>() {

        @Override
        public void onSuccess(int enqueueKey, SearchHitRoot response) {
            if (searchHistoryAdapter != null) {
                if (response != null && response.getG() != null) {
                    searchHistoryAdapter.setList(response.getG());
                }
            }
        }

    };

}
