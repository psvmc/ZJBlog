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

import java.util.ArrayList;
import java.util.List;

import cn.psvmc.zjblog.R;
import cn.psvmc.zjblog.adapter.common.ZJCommonAdapter;
import cn.psvmc.zjblog.model.ZJArticle;
import me.next.tagview.TagCloudView;

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
        ArrayList<String> tagList = new ArrayList<>();
        String keywords = itemData.keywords;
        String[] keyArr = keywords.split(" ");
        for (String key:keyArr
             ) {
            tagList.add(key);
        }
        contentViewHolder.tag_cloud_view.setTags(tagList);
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
        LinearLayout a_outer_ly;
        public TextView a_title;
        public TextView a_content;
        public TagCloudView tag_cloud_view;
        public TextView a_date;

        public ContentViewHolder(View itemView) {
            super(itemView);

            a_outer_ly = (LinearLayout) itemView.findViewById(R.id.a_outer_ly);
            a_title = (TextView) itemView.findViewById(R.id.a_title);
            a_content = (TextView) itemView.findViewById(R.id.a_content);
            tag_cloud_view = (TagCloudView) itemView.findViewById(R.id.tag_cloud_view);
            a_date = (TextView) itemView.findViewById(R.id.a_date);
        }
    }
}
