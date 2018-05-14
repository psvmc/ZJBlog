package cn.psvmc.zjblog.adapter.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by PSVMC on 16/7/6.
 */
public abstract class ZJCommonAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected Context mcontext;

    /**
     * 向指定位置添加元素
     *
     * @param position
     * @param value
     */
    public void add(int position, T value) {
        if (position > mDatas.size()) {
            position = mDatas.size();
        }
        if (position < 0) {
            position = 0;
        }
        mDatas.add(position, value);
        /**
         * 使用notifyItemInserted/notifyItemRemoved会有动画效果
         * 而使用notifyDataSetChanged()则没有
         */
        notifyItemInserted(position);
    }

    /**
     * 移除指定位置元素
     *
     * @param position
     * @return
     */
    public T remove(int position) {
        if (position > mDatas.size() - 1) {
            return null;
        }
        T value = mDatas.remove(position);
        notifyItemRemoved(position);
        return value;
    }

    /**
     * 更新单行内容item
     *
     * @param position 不包含头部和尾部
     */
    public void update(int position) {
        if (position > mDatas.size() - 1) {
            return;
        }
        notifyItemChanged(position);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return zjCreateContentViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        zjBindContentViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        if (null == mDatas) {
            return 0;
        } else {
            return mDatas.size();
        }
    }


    /**
     * 创建Content
     *
     * @param parent
     * @return
     */
    public abstract RecyclerView.ViewHolder zjCreateContentViewHolder(ViewGroup parent);


    /**
     * 设置Content事件或数据
     *
     * @param holder
     * @param position 不包含头尾
     */
    public abstract void zjBindContentViewHolder(RecyclerView.ViewHolder holder, int position);
}
