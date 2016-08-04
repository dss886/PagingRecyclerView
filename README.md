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
	compile 'com.github.dss886:PagingRecyclerView:v0.1.3'
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

2\. Using PagingAdapterDecorator to wrap your adapter before being set:

~~~java
recyclerView = (PagingRecyclerView) findViewById(R.id.recycler_view);
adapter = new YourRecyclerAdapter(this);
recyclerView.setLayoutManager(new LinearLayoutManager(this));
recyclerView.setAdapter(new PagingAdapterDecorator(this, adapter));
~~~

3\. Set OnPagingListener to specify actions when paging:

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

4\. Update state of paging item when you finished your work or some exceptions occured:

~~~java
recyclerView.updateFooter(PagingItem.STATE_FINISH);
recyclerView.updateFooter(PagingItem.STATE_ERROR);
~~~

## Custom Paging Items

1\. Define the ViewHolder of your paging item:

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

2\. Implement PagingItem.Pageable to specify the appearance of items under different states:

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

3\. Create the PagingItem and set it to PagingRecyclerView:

~~~java
View footer = LayoutInflater.from(this).inflate(R.layout.item_paging, recyclerView, false);
recyclerView.setFooter(new PagingItem(new FooterHolder(footer), new FooterPageable()));
~~~

## Header and Footer

By default the head paging is disable. If you want to change the pageablity of header and footer, using:

~~~java
recyclerView.setPageEnable(boolean header, boolean footer);
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
