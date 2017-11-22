package com.repitch.mywishes.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.repitch.mywishes.R;
import com.repitch.mywishes.db.entity.Wish;
import com.repitch.mywishes.model.WishRepository;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements WishesAdapter.WishClickListener {

    private static final int REQUEST_CODE_ADD_WISH = 1;
    private WishesAdapter wishesAdapter;
    private WishRepository wishRepository = new WishRepository();
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerWishes = findViewById(R.id.recycler_wishes);
        emptyView = findViewById(R.id.empty_wishes);
        wishesAdapter = new WishesAdapter(this);
        recyclerWishes.setAdapter(wishesAdapter);
        recyclerWishes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        refreshWishes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_wish:
                addWish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_WISH && resultCode == RESULT_OK) {
            refreshWishes();
        }
    }

    private void refreshWishes() {
        Single.fromCallable(() -> wishRepository.getWishes())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(wishes -> {
                    emptyView.setVisibility(wishes.isEmpty() ? View.VISIBLE : View.GONE);
                    wishesAdapter.setWishes(wishes);
                }, Timber::e);
    }

    private void addWish() {
        startActivityForResult(EditWishActivity.createIntent(this), REQUEST_CODE_ADD_WISH);
    }

    @Override
    public void onWishClicked(Wish wish) {
        startActivityForResult(WishDetailActivity.createIntent(this, wish.getId()), REQUEST_CODE_ADD_WISH);
    }
}
