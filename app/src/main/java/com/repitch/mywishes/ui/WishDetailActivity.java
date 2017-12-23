package com.repitch.mywishes.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.repitch.mywishes.R;
import com.repitch.mywishes.db.entity.Wish;
import com.repitch.mywishes.model.WishRepository;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by repitch on 22.11.2017.
 */
public class WishDetailActivity extends AppCompatActivity {

    private static final String EXTRA_WISH_ID = "wish_id";
    private static final int REQUEST_CODE_EDIT = 1;

    private WishRepository wishRepository;
    private ImageView image;
    private TextView txtCost;
    private TextView txtLink;

    public static Intent createIntent(Context context, int wishId) {
        return new Intent(context, WishDetailActivity.class)
                .putExtra(EXTRA_WISH_ID, wishId);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wishRepository = new WishRepository();
        setContentView(R.layout.activity_wish_detail);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = findViewById(R.id.wish_image);
        txtCost = findViewById(R.id.txt_wish_cost);
        txtLink = findViewById(R.id.txt_wish_link);

        loadWish(getWishId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_wish_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_wish:
                showDeleteAlert();
                return true;
            case R.id.edit_wish:
                startActivityForResult(EditWishActivity.createIntent(this, getWishId()), REQUEST_CODE_EDIT);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.wish_delete_title)
                .setMessage(R.string.wish_delete_message)
                .setPositiveButton(R.string.wish_delete_ok_btn, (dialogInterface, i) -> deleteWish(getWishId()))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteWish(int wishId) {
        Completable.fromAction(() -> wishRepository.deleteWish(wishId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(this, R.string.wish_delete_success_message, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }, Timber::e);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_EDIT && resultCode == RESULT_OK) {
            loadWish(getWishId());
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private int getWishId() {
        return getIntent().getIntExtra(EXTRA_WISH_ID, 0);
    }

    private void loadWish(int wishId) {
        Single.fromCallable(() -> wishRepository.getWishById(wishId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(wish -> setWish(wish), Timber::e);
    }

    private void setWish(Wish wish) {
        getSupportActionBar().setTitle(wish.getName());
        Glide.with(this)
                .load(wish.getImageUrl())
                .into(image);
        txtCost.setText(Long.toString(wish.getCost()));
        txtLink.setText(wish.getLink());
        txtLink.setOnClickListener(v -> {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wish.getLink()));
                startActivity(browserIntent);
            } catch (ActivityNotFoundException e) {
                Timber.e(e);
                Toast.makeText(this, "Невалидная ссылка, не удается открыть", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
