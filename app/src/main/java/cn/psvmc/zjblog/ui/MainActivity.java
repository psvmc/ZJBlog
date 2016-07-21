package cn.psvmc.zjblog.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.psvmc.swiperefreshlayout.AutoSwipeRefreshLayout;
import cn.psvmc.zjblog.R;
import cn.psvmc.zjblog.adapter.ZJArticleListAdapter;
import cn.psvmc.zjblog.db.ZJArticleDb;
import cn.psvmc.zjblog.model.ZJArticle;
import cn.psvmc.zjblog.model.ZJResult;
import cn.psvmc.zjblog.service.ZJRetrofit;
import cn.psvmc.zjblog.utils.recyclerview.RecycleViewDivider;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ZJArticleListAdapter.OnItemClickListener {
    private String TAG = "MainActivity";
    Context mcontext;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.id_swipe_ly)
    AutoSwipeRefreshLayout mSwipeLayout;

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    int page = 0;

    private List<ZJArticle> mListData = new ArrayList<>();
    private ZJArticleListAdapter mListAdapter;

    boolean isLoadFromWeb = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mcontext = this;
        toolbar.setTitle("剑行者的博客");
        initRecycleView();
        initRefresh();

    }

    private void initRecycleView() {
        mListAdapter = new ZJArticleListAdapter(mcontext, mListData);
        mRecyclerView.setAdapter(mListAdapter);
        final LinearLayoutManager manager = new LinearLayoutManager(mcontext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mcontext, LinearLayoutManager.VERTICAL, false));
        // 设置item动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mListAdapter.setOnItemClickListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisiblePosition = manager.findLastVisibleItemPosition();
                    if (lastVisiblePosition >= manager.getItemCount() - 1) {
                        loadMoreDataFromDb();
                    }
                }
            }
        });
    }

    private void initRefresh() {
        //下拉刷新
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(android.R.color.background_light));
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mSwipeLayout.autoRefresh();
            }
        }, 800);
    }

    @OnClick(R.id.fab)
    public void onFab(View v) {
        loadDataFromWeb();
    }

    void loadDataFromWeb() {
        mSwipeLayout.setRefreshing(true);
        Observable
                .create(new Observable.OnSubscribe<ZJResult<List<ZJArticle>>>() {
                    @Override
                    public void call(Subscriber<? super ZJResult<List<ZJArticle>>> subscriber) {
                        ZJResult<List<ZJArticle>> result = null;
                        try {
                            result = ZJRetrofit.getInstance()
                                    .getArticleService()
                                    .getListData().execute().body();
                            subscriber.onNext(result);
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }

                    }
                })
                .doOnNext(new Action1<ZJResult<List<ZJArticle>>>() {
                    @Override
                    public void call(ZJResult<List<ZJArticle>> listZJResult) {
                        ZJArticleDb.getInstance().deleteAll();
                        ZJArticleDb.getInstance().insertArticles(listZJResult.getObj());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZJResult<List<ZJArticle>>>() {
                    @Override
                    public void onCompleted() {
                        Log.w(TAG, "onCompleted: ");
                        isLoadFromWeb = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(TAG, "onError: " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(ZJResult<List<ZJArticle>> zjArticleZJResult) {
                        Snackbar.make(getWindow().getDecorView(), "数据同步成功！", Snackbar.LENGTH_LONG).show();
                        loadDataFromDb();
                    }
                });
    }

    void loadDataFromDb() {
        page = 0;
        mListAdapter.mLastPosition = -1;
        Observable
                .create(new Observable.OnSubscribe<List<ZJArticle>>() {
                    @Override
                    public void call(Subscriber<? super List<ZJArticle>> subscriber) {
                        List<ZJArticle> articles = ZJArticleDb.getInstance().queryByPage(page);
                        subscriber.onNext(articles);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ZJArticle>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(TAG, "onError: " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(List<ZJArticle> articles) {
                        if (articles.size() == 0) {
                            if (!isLoadFromWeb) {
                                loadDataFromWeb();
                            }
                        } else {
                            mListData.removeAll(mListData);
                            mListData.addAll(articles);
                            mListAdapter.notifyDataSetChanged();
                            mSwipeLayout.setRefreshing(false);
                        }

                    }
                });

    }

    void loadMoreDataFromDb() {
        page += 1;
        mSwipeLayout.setRefreshing(true);
        Observable
                .create(new Observable.OnSubscribe<List<ZJArticle>>() {
                    @Override
                    public void call(Subscriber<? super List<ZJArticle>> subscriber) {
                        List<ZJArticle> articles = ZJArticleDb.getInstance().queryByPage(page);
                        subscriber.onNext(articles);
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ZJArticle>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w(TAG, "onError: " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onNext(List<ZJArticle> articles) {
                        mListData.addAll(articles);
                        mListAdapter.notifyDataSetChanged();
                        mSwipeLayout.setRefreshing(false);
                    }
                });

    }

    @Override
    public void onRefresh() {
        loadDataFromDb();
    }

    @Override
    public void onListItemClick(View view, int position) {
        ZJArticle article = mListData.get(position);
        Log.w(TAG, "onListItemClick: " + position);
        Intent myIntent = new Intent();
        myIntent.putExtra("title", article.title);
        myIntent.putExtra("url", article.url);
        myIntent.setClass(mcontext, MarkdownActivity.class);
        startActivity(myIntent);
        overridePendingTransition(R.anim.open_enter, R.anim.open_exit);
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
