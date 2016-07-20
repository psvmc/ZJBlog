package cn.psvmc.zjblog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.psvmc.zjblog.R;
import cn.psvmc.zjblog.adapter.common.ZJCommonAdapter;
import cn.psvmc.zjblog.model.ZJArticle;

/**
 * Created by PSVMC on 16/7/6.
 */
public class ZJArticleListAdapter extends ZJCommonAdapter<ZJArticle> {

    private static final int DELAY = 0;
    public int mLastPosition = -1;

    private OnItemClickListener onItemClickListener;

    public ZJArticleListAdapter(Context context, List<ZJArticle> mDatas) {
        this.mDatas = mDatas;
        this.mInflater = LayoutInflater.from(context);
        this.mcontext = context;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.onItemClickListener = mOnItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder zjCreateContentViewHolder(ViewGroup parent) {
        RecyclerView.ViewHolder holder = new ContentViewHolder(mInflater.inflate(R.layout.article_list_item, parent, false));
        return holder;
    }

    @Override
    public void zjBindContentViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
        ZJArticle itemData = mDatas.get(position);
        contentViewHolder.a_title.setText(itemData.title);
        contentViewHolder.a_content.setText(itemData.description);
        contentViewHolder.a_keywords.setText(itemData.keywords);
        contentViewHolder.a_date.setText(itemData.date);
        //showItemAnim(contentViewHolder.a_outer_ly,position);
        contentViewHolder.a_outer_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onListItemClick(v, position);
            }
        });
    }

    public void showItemAnim(final View view, final int position) {
        final Context context = view.getContext();
        if (position > mLastPosition) {
            view.setAlpha(0);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            view.setAlpha(1);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    view.startAnimation(animation);
                }
            }, DELAY * position);
            mLastPosition = position;
        }
    }

    /**
     * 处理item的点击事件和长按事件
     */
    public interface OnItemClickListener {
        void onListItemClick(View view, int position);

    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.a_outer_ly)
        LinearLayout a_outer_ly;

        @Bind(R.id.a_title)
        public TextView a_title;

        @Bind(R.id.a_content)
        public TextView a_content;

        @Bind(R.id.a_keywords)
        public TextView a_keywords;

        @Bind(R.id.a_date)
        public TextView a_date;

        public ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
