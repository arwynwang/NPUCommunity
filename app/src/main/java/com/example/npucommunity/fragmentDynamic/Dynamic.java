package com.example.npucommunity.fragmentDynamic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.npucommunity.R;
import com.example.npucommunity.fragmentPeer.PeerList;
import com.qmuiteam.qmui.widget.QMUICollapsingTopBarLayout;

import java.util.ArrayList;
import java.util.List;

public class Dynamic extends Fragment {

    private QMUICollapsingTopBarLayout qmuiCollapsingTopBarLayout;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private Context context;
    private ArrayList<DynamicContent> dynamicContentList;
    private static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        qmuiCollapsingTopBarLayout = view.findViewById(R.id.collapsing_topbar_layout);
        imageView = view.findViewById(R.id.top_image);
        recyclerView = view.findViewById(R.id.recyclerView);
        context = getContext();

        //iniDynamic();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        DynamicAdapter dynamicAdapter = new DynamicAdapter(dynamicContentList);
        recyclerView.setAdapter(dynamicAdapter);
        qmuiCollapsingTopBarLayout.setTitle("西北工业大学");
        imageView.setImageResource(R.drawable.npu);
        return view;
    }

    /*
    private void iniDynamic() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }
        }
        ArrayList<Bitmap> imageArrayList = new ArrayList<Bitmap>();
        imageArrayList.add(R.drawable.icon664);
        DynamicContent dynamicContent = new DynamicContent(PeerList.me, "埃克设计的可就是开发了的说服力的数据", imageArrayList);
    }*/

    public static int dp2px(float value, Context context) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) (value * (scale / 160) + 0.5f);

    }

    public class SquareImageView extends android.support.v7.widget.AppCompatImageView {
        public SquareImageView(Context context) {
            super(context);
        }

        public SquareImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        }
    }



    public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.ViewHolder> {
        private List<DynamicContent> dynamicList;

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView avatar;
            TextView name;
            TextView signature;
            TextView textView;
            GridLayout gridLayout;

            public ViewHolder(View view) {
                super(view);
                avatar = view.findViewById(R.id.avatar);
                name = view.findViewById(R.id.name);
                signature = view.findViewById(R.id.signature);
                textView = view.findViewById(R.id.dynamic_text);
                gridLayout = view.findViewById(R.id.dynamic_image);
            }
        }

        public DynamicAdapter(List<DynamicContent> list) {
            dynamicList = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            DynamicContent dynamicContent = dynamicList.get(position);
            viewHolder.avatar.setImageResource(dynamicContent.getImageId());
            viewHolder.name.setText(dynamicContent.getName());
            viewHolder.signature.setText(dynamicContent.getSignature());
            viewHolder.textView.setText(dynamicContent.getText());
            updateViewGroup(viewHolder.gridLayout, dynamicContent.getImageList());
        }

        @Override
        public int getItemCount() {
            return dynamicList.size();
        }

        public void updateViewGroup(GridLayout gridLayout, ArrayList<Bitmap> imageModels) {
            gridLayout.removeAllViews();//清空子视图 防止原有的子视图影响
            int columnCount=gridLayout.getColumnCount();//得到列数
            int marginSize = dp2px(4, context);//得到经过dp转化的margin值
            //遍历集合 动态添加
            for (int i = 0, size = imageModels.size(); i < size; i++) {
                GridLayout.Spec rowSpec = GridLayout.spec(i / columnCount);//行数
                GridLayout.Spec columnSpec = GridLayout.spec(i % columnCount, 1.0f);//列数 列宽的比例 weight=1
                ImageView imageView = new SquareImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                //由于宽（即列）已经定义权重比例 宽设置为0 保证均分
                GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(new ViewGroup.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutParams.rowSpec=rowSpec;
                layoutParams.columnSpec=columnSpec;

                layoutParams.setMargins(marginSize, marginSize, marginSize, marginSize);

                gridLayout.addView(imageView, layoutParams);
            }
        }
    }

}
