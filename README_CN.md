# PagingRecyclerView

[![](https://jitpack.io/v/dss886/PagingRecyclerView.svg)](https://jitpack.io/#dss886/PagingRecyclerView)

[For English Version, Click Me >>>](/README.md)

一个支持分页加载和错误重试的RecyclerView，提供可扩展的header和footer。

![Demo](/1.gif)

![Demo](/2.gif)

![Demo](/3.gif)

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
	compile 'com.github.dss886:PagingRecyclerView:$(latest-version)'
}
~~~

## 使用方法

1\. 使用PagingRecyclerView替换原生的RecyclerView:

~~~xml
<com.dss886.pagingrecyclerview.PagingRecyclerView
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
~~~

2\. 添加OnPagingListener来设置分页加载时要执行的动作：

~~~java
recyclerView.setOnPagingListener(new PagingRecyclerView.OnPagingListener() {
    @Override
    public void onPaging(PagingRecyclerView view, int direction) {
        if (direction == PagingRecyclerView.FOOT) {
            // do your paging work
        }
    }
});
~~~

4\. 当加载完成或发生错误时更新header或footer的状态：

~~~java
recyclerView.onPaging(PagingRecyclerView.FOOT);
recyclerView.onFailure(PagingRecyclerView.FOOT);
recyclerView.onNoMoreData(PagingRecyclerView.FOOT);
~~~

## Header and Footer

默认情况下顶部的分页加载是关闭的，如果你希望启用它，使用：

~~~java
recyclerView.setPageEnable(boolean header, boolean footer);
~~~

如果想要自定义Header and Footer，继承PagingRecyclerView并覆写`createPagingViewHolder`方法：

~~~java
/**
 * Override this method to custom your own paging item
 */
protected AbsPagingViewHolder createPagingViewHolder(LayoutInflater inflater, ViewGroup parent, int direction, PagingRecyclerView.OnPagingListener pagingListener) {
    View view = inflater.inflate(R.layout.paging_recycler_view_default_item, parent, false);
    return new DefaultPagingViewHolder(view, direction, this, pagingListener);
}
~~~

## 使用了自定义的LayoutManager

本项目支持三种默认的LayoutManager（Linear、Gird和Staggered）。但如果你使用了自定义的LayoutManager，默认的OnScrollListener就无法自动检测到是否滑到了顶部或底部，你需要自己实现OnScrollListener然后添加到PagingRecyclerView中。

更多信息请参考：[com/dss886/pagingrecyclerview/PagingScrollListener.java](https://github.com/dss886/PagingRecyclerView/blob/master/library/src/main/java/com/dss886/pagingrecyclerview/PagingScrollListener.java)

## 注意事项

1\. 因为PagingRecyclerView使用了一个装饰类来包装你的adapter，所以`adapter.notifyDataSetChanged()`可能会没有效果，需要用`PagingRecyclerView.notifyDataSetChanged()`方法来代替。

2\. 当顶部加载的item正在显示时，`ViewHolder.getAdapterPosition()`和`ViewHolder.getLayoutPosition()`的返回值是计算header了的，如果这不是你想要的，可以使用`PagingRecyclerView.getFixedPostion(int position)`来得到这个item在你的adatper中的真正位置。

## Thanks

1\. 本项目的基本思路源自于[nicolasjafelle的PagingListView](https://github.com/nicolasjafelle/PagingListView)，但是遗憾的是作者不再进行维护了，也没有支持RecyclerView的打算，所以才有了这个项目。

2\. [cundong的HeaderAndFooterRecyclerView](https://github.com/cundong/HeaderAndFooterRecyclerView)在接口设计和实现上也给了本项目很大的灵感。

## License

~~~
Copyright 2018 dss886

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
