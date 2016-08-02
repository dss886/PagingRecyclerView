# PagingRecyclerView

[![](https://jitpack.io/v/dss886/PagingRecyclerView.svg)](https://jitpack.io/#dss886/PagingRecyclerView)

[For English Version, Click Me >>>](/README.md)

一个支持分页加载和错误重试的RecyclerView，提供可扩展的header和footer。

![Demo](/1.gif)

## 下载

在project的build.gradle中加入以下语句：

~~~
allprojects {
	repositories {
		jcenter()
		maven { url "https://jitpack.io" }
	}
}
~~~

在module的build.gradle中加入以下语句：

~~~
dependencies {
	compile 'com.github.dss886:PagingRecyclerView:v0.1.1'
}
~~~

## 使用方法

1\. 使用PagingRecyclerView替换原生的RecyclerView:

~~~xml
<com.dss886.pagingrecyclerviewdemo.PagingRecyclerView
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
~~~

2\. 在设置适配器前使用PagingAdapterDecorator包装你的adapter：

~~~java
recyclerView = (PagingRecyclerView) findViewById(R.id.recycler_view);
adapter = new YourRecyclerAdapter(this);
recyclerView.setLayoutManager(new LinearLayoutManager(this));
recyclerView.setAdapter(new PagingAdapterDecorator(this, adapter));
~~~

3\. 添加OnPagingListener来设置分页加载时要执行的动作：

~~~java
recyclerView.setOnPagingListener(new PagingRecyclerView.OnPagingListener() {
    @Override
    public void onPaging(PagingRecyclerView view, int direction) {
        if (direction == PagingRecyclerView.DIRECTION_FOOT) {
            // do your paging work
        }
    }
});
~~~

4\. 当加载完成或发生错误时更新header或footer的状态：

~~~java
recyclerView.updateFooter(PagingItem.STATE_FINISH);
recyclerView.updateFooter(PagingItem.STATE_ERROR);
~~~

## 自定义 PagingItem

1\. 定义你PagingItem的ViewHolder：

~~~java
private class FooterHolder extends RecyclerView.ViewHolder {
    public View progress;
    public View error;
    public TextView text;
    public FooterHolder(View itemView) {
        super(itemView);
        this.progress = itemView.findViewById(R.id.progress);
        this.error = itemView.findViewById(R.id.error);
        this.text = (TextView) itemView.findViewById(R.id.text);
    }
}

~~~

2\. 实现PagingItem.Pageable接口来在不同状态时显示不同的样式：

~~~java
private class FooterPageable implements PagingItem.Pageable {
    @Override
    public void onPaging(PagingItem item) {
        FooterHolder holder = (FooterHolder) item.holder;
        holder.progress.setVisibility(View.VISIBLE);
        holder.error.setVisibility(View.GONE);
        holder.text.setText("Loading...");
        holder.itemView.setOnClickListener(null);
    }

    @Override
    public void onError(final PagingItem item) {
        final FooterHolder holder = (FooterHolder) item.holder;
        holder.progress.setVisibility(View.GONE);
        holder.error.setVisibility(View.VISIBLE);
        holder.text.setText("Tap to retry");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.updateFooter(PagingItem.STATE_PAGING);
            }
        });
    }
}
~~~

3\. 实例化PagingItem然后设置进PagingRecyclerView：

~~~java
View footer = LayoutInflater.from(this).inflate(R.layout.item_paging, recyclerView, false);
recyclerView.setFooter(new PagingItem(new FooterHolder(footer), new FooterPageable()));
~~~

## Header and Footer

默认情况下顶部的分页加载是关闭的，如果你希望启用它，使用：

~~~java
recyclerView.setPageEnable(boolean header, boolean footer);
~~~

## 注意事项

1\. 因为PagingRecyclerView用了一个装饰类来包装你的adapter，所以你的`adapter.notifyDataSetChanged()`可能会没有效果，需要用`PagingRecyclerView.notifyDataSetChanged()`方法来代替。

2\. 当顶部加载的item正在显示时，`ViewHolder.getAdapterPosition()`和`ViewHolder.getLayoutPosition()`的返回值是计算header了的，如果这不是你想要的，使用`PagingRecyclerView.getFixedPostion(int position)`来得到这个item在你的adatper中的真正位置。

## Thanks

1\. 本项目的基本思路源自于[nicolasjafelle 的 PagingListView](https://github.com/nicolasjafelle/PagingListView)，但是很遗憾的是作者不再维护了，也没有支持RecyclerView的打算，所以才有了这个项目。

2\. [cundong 的 HeaderAndFooterRecyclerView](https://github.com/cundong/HeaderAndFooterRecyclerView)在接口设计和实现上也给了本项目很大的灵感。

## License

~~~
Copyright 2016 dss886

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
~~~
