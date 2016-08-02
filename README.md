# PagingRecyclerView

[![](https://jitpack.io/v/dss886/PagingRecyclerView.svg)](https://jitpack.io/#dss886/PagingRecyclerView)

A RecyclerView provides page loading and error retry with strong scalability of header and footer.

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
	compile 'com.github.dss886:PagingRecyclerView:v0.1.0'
}
~~~

## Usage

1\. Using PagingRecyclerView to replace your origin RecyclerView:

~~~xml
<com.dss886.pagingrecyclerviewdemo.PagingRecyclerView
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

4\. Update state of paging item when you finish your work or some exceptions occured:

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
