# PagingRecyclerView

[![](https://jitpack.io/v/dss886/PagingRecyclerView.svg)](https://jitpack.io/#dss886/PagingRecyclerView)

[简体中文版说明 >>>](/README_CN.md)

A RecyclerView provides page loading and error retry with scalability of header and footer.

![Demo](/1.gif)

![Demo](/2.gif)

![Demo](/3.gif)

## Download

Add it in your build.gradle at the end of repositories:

~~~
allprojects {
	repositories {
		jcenter()
		maven { url "https://jitpack.io" }
	}
}
~~~

Add the dependency in the form:

~~~
dependencies {
	compile 'com.github.dss886:PagingRecyclerView:$(latest-version)'
}
~~~

## Usage

1\. Using PagingRecyclerView to replace your origin RecyclerView:

~~~xml
<com.dss886.pagingrecyclerview.PagingRecyclerView
    android:id="@+id/recycler_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
~~~

2\. Set OnPagingListener to specify actions when paging:

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

4\. Update state of paging item when you finished your work or some exceptions occured:

~~~java
recyclerView.onPaging(PagingRecyclerView.FOOT);
recyclerView.onFailure(PagingRecyclerView.FOOT);
recyclerView.onNoMoreData(PagingRecyclerView.FOOT);
~~~

## Header and Footer

By default the head paging is disable. If you want to change the pageablity of header and footer, using:

~~~java
recyclerView.setPageEnable(boolean header, boolean footer);
~~~

If want to custom Header and Footer, inherit PagingRecyclerView and override the `createPagingViewHolder` method:

~~~java
/**
 * Override this method to custom your own paging item
 */
protected AbsPagingViewHolder createPagingViewHolder(LayoutInflater inflater, ViewGroup parent, int direction, PagingRecyclerView.OnPagingListener pagingListener) {
    View view = inflater.inflate(R.layout.paging_recycler_view_default_item, parent, false);
    return new DefaultPagingViewHolder(view, direction, this, pagingListener);
}
~~~

## Custom LayoutManager

This project support all three default LayoutManager(Linear, Grid and Staggered). But if you are using a custom LayoutManager, PagingRecyclerView will have no idea how the manager layout its children and can't detect when scolled to he top or the bottom. You need to impement your own OnScrollListener add add it to the PagingRecyclerView.

See more detail at: [com/dss886/pagingrecyclerview/PagingScrollListener.java](https://github.com/dss886/PagingRecyclerView/blob/master/library/src/main/java/com/dss886/pagingrecyclerview/PagingScrollListener.java)

## Notice

1\. As PagingRecyclerView using a decorator to wrap your adapter, `adapter.notifyDataSetChanged()` may not act as expected, you can use `PagingRecyclerView.notifyDataSetChanged()` instead.

2\. When header is showing, the return value of `ViewHolder.getAdapterPosition()` and `ViewHolder.getLayoutPosition()` has counted the header, use `PagingRecyclerView.getFixedPostion(int position)` to get its true position in inner dataset.

## Thanks

1\. This project is largely an external version of [nicolasjafelle's PagingListView](https://github.com/nicolasjafelle/PagingListView), which is no longer be maintained.

2\. [cundong's HeaderAndFooterRecyclerView](https://github.com/cundong/HeaderAndFooterRecyclerView) also gives a lot of inspiration to this project.

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
