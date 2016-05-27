---
title: 从一个实例简单学习RecyclerView基础应用
date: 2016-05-22 17:19:22
tags:
- 安卓
categories:
- 技术
---
## 实例简介

* 每日对【听力特快】中空中英语教室和CNN学生新闻视频栏目的音频文件下载，同时包括对CNN学生新闻视频的字幕抓取
* RecyclerView+CardView进行布局展示
* AsyncTask和Jsoup进行异步网络下载音频和字幕抓取
* MediaPlay 进行判断本地是否有音频，若有则本地播放，若无则进行预加载播放
* AppCompatSeekBar 进度控制
## RcyclerView简述
### 基础代码

* LayoutManager: 管理RecyclerView的结构.
* Adapter: 处理每个Item的显示.
* ItemDecoration: 添加每个Item的装饰.
* ItemAnimator: 负责添加\移除\重排序时的动画效果.
```java
 mRecyclerView = findView(R.id.recyclerView);
//设置布局管理器
mRecyclerView.setLayoutManager(layout);
//设置adapter
mRecyclerView.setAdapter(adapter)
//设置Item增加、移除动画
mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//添加分割线
mRecyclerView.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
```
### 实例代码
```java
LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
recyclerView.setLayoutManager(llm);
recyclerView.setHasFixedSize(true);
recyclerView.setAdapter(adapter);
int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space);
recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels)); //设置分割线
```
### 布局管理

* RecyclerView提供这些内置的布局管理器：

* LinearLayoutManager 显示在垂直或水平滚动列表项。

* GridLayoutManager 显示在网格中的项目。

* StaggeredGridLayoutManager 显示了交错网格项目。

* 要创建自定义布局管理器，扩展RecyclerView.LayoutManager类。

### 自定义分割线
```java
 // 分隔间距 继承RecyclerView.ItemDecoration
    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        public SpaceItemDecoration(int space) {
            this.space = space;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = space;
            }
        }
    }
```
### 写好继承自RecyclerView.ItemDecoration的类，即可自定义间隔距离。
```java
int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space);
recyclerView.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
```
### Adapter

适配器, 处理RecyclerView的Item事务.
```java
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.InfosViewHolder> {
    private final String TAG = "RecyclerViewAdapter";
    private List<Infos> infos;
    private Context context;
    private String[] url = new String[2];
    private int progress;
    public RecyclerViewAdapter(List<Infos> infos, Context context) {
        this.infos = infos;
        this.context = context;
    }
    @Override
    public RecyclerViewAdapter.InfosViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.cardview_item, viewGroup, false);
        InfosViewHolder nvh = new InfosViewHolder(v);
        return nvh;
    }
    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.InfosViewHolder personViewHolder, int i) {
        final int j = i;
      personViewHolder.news_photo.setImageResource(infos.get(i).getPhotoId());
        personViewHolder.news_title.setText(infos.get(i).getTitle());
        personViewHolder.news_desc.setText(infos.get(i).getDesc());
        url[i] = infos.get(i).getUrl();
        //cardView arrowbutton设置点击事件
        personViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InfosActivity.class);
                intent.putExtra("Infos", infos.get(j));
                context.startActivity(intent);
                Log.d(TAG, "this is " + j);
            }
        });
      personViewHolder.arrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personViewHolder.arrowButton.startAnimating();
//               这里因为viewHolder是final 从第一栏加载 所以最后的数据是最好一栏的数据 解决方案就是将viewHolder需要做事件的控件传入
                DownloadTask downloadTask = new DownloadTask(context, infos.get(j).getTitle(), personViewHolder.arrowButton);
                downloadTask.execute(url[j]);
                Log.d(TAG, url[j]);
            }
        });
```
### 关键方法意义

> onCreateViewHolder创建ViewHolder.
> onBindViewHolder绑定每一项数据.

> getItemCount返回列表长度.

### RecyclerView强制使用ViewHolder.

```java
//自定义ViewHolder类 进行视图绑定
static class InfosViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.cardView)
    CardView cardView;
    @Bind(R.id.news_photo)
    ImageView news_photo;
    @Bind(R.id.news_title)
    TextView news_title;
    @Bind(R.id.news_desc)
    TextView news_desc;
    @Bind(R.id.arrow_button)
    ArrowDownloadButton arrowButton;
    public InfosViewHolder(final View itemView) {
        super(itemView);		
        ButterKnife.bind(this, itemView);
        //设置TextView背景为半透明
        news_title.setBackgroundColor(Color.argb(20, 0, 0, 0));
    }
}
```
> 在onCreateViewHolder方法, 创建类; 在onBindViewHolder方法, 绑定数据.

### 遇到的小问题

ViewHolder加载视图的顺序是从第一个Item到最后一个，而且该对象是final类型，故自己最初在DownloadTask exntend AsyncTask<> 该类写 ViewHolder.arrowButton.setProgress 只有最后一个Item才会有下载的动画显示（setProgress生效）,但下载是没有问题的。

解决方案则是将该Button在new DownloadTask的时候传入。

问题解决。

### 数据更新

RecyclerView的更新自己目前用到的还是adapter.notifyDataSetChanged

### 新手Tips

* implements Serializable 的范类才可以进行Intent传送
```java
Intent intent = getIntent();
Infos item = (Infos) intent.getSerializableExtra("Infos");
```
* 理解ViewHolder的加载模式

* Handler灵活应用