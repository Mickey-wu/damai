package com.yztc.damai.view;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.yztc.core.image.ImageLoader;
import com.yztc.damai.R;
import com.yztc.damai.ui.recommend.ClassifyBean;
import com.yztc.damai.ui.recommend.HeadLineBean;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wanggang on 2016/12/15.
 */

public class ClassifyView extends RelativeLayout {

    private static final int AUTO_SCROLL_TIME=3000;

    @BindView(R.id.classify_grid)
    RecyclerView classifyGrid;
    @BindView(R.id.classify_hot)
    TextSwitcher classifyHot;
    private ArrayList<ClassifyBean> classifys=new ArrayList<>();

    private ClassifyAdapter adapter;
    private ArrayList<HeadLineBean> headLines;

    private Timer timer=new Timer();
    private Handler handler=new Handler();

    public ClassifyView(Context context) {
        super(context);
        init();
    }

    public ClassifyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext())
                .inflate(R.layout.view_classify, this, true);
        ButterKnife.bind(this,this);
        GridLayoutManager mgr=new GridLayoutManager(getContext(),4);
        classifyGrid.setLayoutManager(mgr);
         adapter = new ClassifyAdapter(getContext(), classifys);
        classifyGrid.setAdapter(adapter);

        //动画  文字切换的效果
        Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_in);
        Animation out = AnimationUtils.loadAnimation(getContext(), R.anim.push_up_out);
        classifyHot.setInAnimation(in);
        classifyHot.setOutAnimation(out);
        //文字显示的效果
        classifyHot.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView text=new TextView(getContext());
                text.setTextColor(getResources().getColor(R.color.text_title));
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
                text.setGravity(Gravity.CENTER_VERTICAL);
                return text;
            }
        });

    }

    public void setClassifys(ArrayList<ClassifyBean> classifys) {
        this.classifys.clear();
        this.classifys.addAll(classifys);
        adapter.notifyDataSetChanged();

    }

    private int posotion;
    public void setHeadLines(final ArrayList<HeadLineBean> headLines) {
        this.headLines = headLines;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        HeadLineBean bean = headLines.get(posotion++ % headLines.size());
                        //显示的文字
                        classifyHot.setText(bean.getText());
                    }
                });
            }
        },0,AUTO_SCROLL_TIME);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(timer!=null){
            timer.cancel();
            timer=null;
        }
    }


    static class ClassifyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private LayoutInflater inflater;
        private ArrayList<ClassifyBean> classifys;

        public ClassifyAdapter(Context context, ArrayList<ClassifyBean> classifys) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.classifys = classifys;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_classify, null);
            return new ClassifyAdapter.ClassifyHolder(view);

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ClassifyAdapter.ClassifyHolder) {
                ((ClassifyAdapter.ClassifyHolder) holder).classifyIcon.setImageResource(R.mipmap.ic_launcher);
                ImageLoader.getInstance().loadImages(((ClassifyAdapter.ClassifyHolder) holder).classifyIcon, classifys.get(position).getImg(), false);
                ((ClassifyAdapter.ClassifyHolder) holder).classifyTitle.setText(classifys.get(position).getName());
            }
        }

        @Override
        public int getItemCount() {
            return classifys != null ? classifys.size() : 0;
        }

        static class ClassifyHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.classify_icon)
            ImageView classifyIcon;

            @BindView(R.id.classify_title)
            TextView classifyTitle;

            ClassifyHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

}
