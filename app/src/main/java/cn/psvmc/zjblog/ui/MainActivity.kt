package cn.psvmc.zjblog.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast

import java.io.IOException
import java.util.ArrayList

import cn.psvmc.zjblog.R
import cn.psvmc.zjblog.adapter.ZJArticleListAdapter
import cn.psvmc.zjblog.db.ZJArticleDb
import cn.psvmc.zjblog.model.ZJArticle
import cn.psvmc.zjblog.model.ZJResult
import cn.psvmc.zjblog.service.ZJRetrofit
import cn.psvmc.zjblog.utils.recyclerview.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_main.*
import rx.Observable
import rx.Observer
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers

class MainActivity :
        BaseActivity(),
        SwipeRefreshLayout.OnRefreshListener,
        ZJArticleListAdapter.OnItemClickListener {
    private val TAG = "MainActivity"
    internal var mcontext: Context? = null

    internal var page = 0

    private val mListData = ArrayList<ZJArticle>()
    private var mListAdapter: ZJArticleListAdapter? = null

    internal var isLoadFromWeb = false

    private var exitTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mcontext = this
        toolbar!!.title = "剑行者的博客"
        initRecycleView()
        initRefresh()
        fab.setOnClickListener {
            loadDataFromWeb()
        }
    }

    private fun initRecycleView() {
        mListAdapter = ZJArticleListAdapter(mcontext, mListData)
        recyclerView!!.adapter = mListAdapter
        val manager = LinearLayoutManager(mcontext, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = manager
        recyclerView!!.addItemDecoration(RecycleViewDivider(mcontext, LinearLayoutManager.VERTICAL, false))
        // 设置item动画
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        mListAdapter!!.setOnItemClickListener(this)
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val lastVisiblePosition = manager.findLastVisibleItemPosition()
                    if (lastVisiblePosition >= manager.itemCount - 1) {
                        loadMoreDataFromDb()
                    }
                }
            }
        })
    }

    private fun initRefresh() {
        //下拉刷新
        id_swipe_ly!!.setOnRefreshListener(this)

        id_swipe_ly!!.setProgressBackgroundColorSchemeColor(
                ContextCompat.getColor(mContext,android.R.color.background_light)
        )
        id_swipe_ly!!.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        )
        Handler().postDelayed({ id_swipe_ly!!.autoRefresh() }, 800)
    }

    internal fun loadDataFromWeb() {
        id_swipe_ly!!.isRefreshing = true
        Observable
                .create(Observable.OnSubscribe<ZJResult<List<ZJArticle>>> { subscriber ->
                    var result: ZJResult<List<ZJArticle>>?
                    try {
                        result = ZJRetrofit.getInstance()
                                .articleService
                                .listData.execute().body()
                        subscriber.onNext(result)
                        subscriber.onCompleted()
                    } catch (e: IOException) {
                        subscriber.onError(e)
                    }
                })
                .doOnNext { listZJResult ->
                    ZJArticleDb.getInstance().deleteAll()
                    ZJArticleDb.getInstance().insertArticles(listZJResult.obj)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ZJResult<List<ZJArticle>>> {
                    override fun onCompleted() {
                        Log.w(TAG, "onCompleted: ")
                        isLoadFromWeb = true
                    }

                    override fun onError(e: Throwable) {
                        Log.w(TAG, "onError: " + e.localizedMessage)
                    }

                    override fun onNext(zjArticleZJResult: ZJResult<List<ZJArticle>>) {
                        Snackbar.make(window.decorView, "数据同步成功！", Snackbar.LENGTH_LONG).show()
                        loadDataFromDb()
                    }
                })
    }

    internal fun loadDataFromDb() {
        page = 0
        mListAdapter!!.mLastPosition = -1
        Observable
                .create(Observable.OnSubscribe<List<ZJArticle>> { subscriber ->
                    val articles = ZJArticleDb.getInstance().queryByPage(page)
                    subscriber.onNext(articles)
                    subscriber.onCompleted()
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<ZJArticle>> {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        Log.w(TAG, "onError: " + e.localizedMessage)
                    }

                    override fun onNext(articles: List<ZJArticle>) {
                        if (articles.size == 0) {
                            if (!isLoadFromWeb) {
                                loadDataFromWeb()
                            }
                        } else {
                            mListData.removeAll(mListData)
                            mListData.addAll(articles)
                            mListAdapter!!.notifyDataSetChanged()
                            id_swipe_ly!!.isRefreshing = false
                        }

                    }
                })

    }

    internal fun loadMoreDataFromDb() {
        page += 1
        id_swipe_ly!!.isRefreshing = true
        Observable
                .create(Observable.OnSubscribe<List<ZJArticle>> { subscriber ->
                    val articles = ZJArticleDb.getInstance().queryByPage(page)
                    subscriber.onNext(articles)
                    subscriber.onCompleted()
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<ZJArticle>> {
                    override fun onCompleted() {}

                    override fun onError(e: Throwable) {
                        Log.w(TAG, "onError: " + e.localizedMessage)
                    }

                    override fun onNext(articles: List<ZJArticle>) {
                        mListData.addAll(articles)
                        mListAdapter!!.notifyDataSetChanged()
                        id_swipe_ly!!.isRefreshing = false
                    }
                })

    }

    override fun onRefresh() {
        loadDataFromDb()
    }

    override fun onListItemClick(view: View, position: Int) {
        val article = mListData[position]
        Log.w(TAG, "onListItemClick: $position")
        val myIntent = Intent()
        myIntent.putExtra("title", article.title)
        myIntent.putExtra("url", article.url)
        myIntent.setClass(mcontext, MarkdownActivity::class.java)
        startActivity(myIntent)
        overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(applicationContext, "再按一次退出程序", Toast.LENGTH_SHORT).show()
                exitTime = System.currentTimeMillis()
            } else {
                finish()
                System.exit(0)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
