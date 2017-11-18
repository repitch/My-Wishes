package com.repitch.mywishes.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.repitch.mywishes.R;
import com.repitch.mywishes.db.entity.Wish;
import com.repitch.mywishes.model.WishRepository;
import com.repitch.mywishes.ui.dialog.EnterTextDialog;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by repitch on 18.11.2017.
 */
public class AddWishActivity extends AppCompatActivity {

    private static final String DIALOG_IMAGE_URL = "image_url";
    private static final String EXTRA_WISH_ID = "wish_id";

    private WishRepository wishRepository;
    private Wish wish;

    private ImageView wishImage;
    private View insertWishImageBtn;
    private EditText wishName;
    private EditText wishCost;
    private EditText wishLink;

    public static Intent createIntent(Context context, int wishId) {
        return new Intent(context, AddWishActivity.class)
                .putExtra(EXTRA_WISH_ID, wishId);
    }

    public static Intent createIntent(Context context) {
        return createIntent(context, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wishRepository = new WishRepository();

        setContentView(R.layout.activity_add_wish);
        wishImage = findViewById(R.id.wish_image);
        insertWishImageBtn = findViewById(R.id.btn_insert_image);
        wishName = findViewById(R.id.edit_name);
        wishCost = findViewById(R.id.edit_cost);
        wishLink = findViewById(R.id.edit_link);

        insertWishImageBtn.setOnClickListener(v -> {
            final EnterTextDialog editImageUrlDialog = EnterTextDialog.newInstance("Вставьте ссылку на изображение",
                    "Ссылка", wish.getImageUrl(), 512);

            editImageUrlDialog.setListener(new EnterTextDialog.OnEnterTextListener() {
                @Override
                public boolean onTextEntered(String text) {
                    wish.setImageUrl(text);
                    if (!TextUtils.isEmpty(wish.getImageUrl())) {
                        Glide.with(AddWishActivity.this)
                                .load(wish.getImageUrl())
                                .into(wishImage);
                    }
                    return true;
                }
            });
            editImageUrlDialog.show(getSupportFragmentManager(), DIALOG_IMAGE_URL);
        });

        int wishId = getIntent().getIntExtra(EXTRA_WISH_ID, 0);
        if (wishId == 0) {
            setWish(new Wish());
        } else {
            loadWish(wishId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_add_wish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_wish:
                saveWish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveWish() {
        Completable.fromCallable(() -> {
            wish.setName(wishName.getText().toString());
            wish.setCost(Long.parseLong(wishCost.getText().toString()));
            wish.setLink(wishLink.getText().toString());
            wishRepository.updateWish(wish);
            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Toast.makeText(AddWishActivity.this, "Желание сохранено!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }, Timber::e);
    }

    private void loadWish(int wishId) {
        Single.fromCallable(() -> wishRepository.getWishById(wishId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setWish, Timber::e);
    }

    private void setWish(Wish wish) {
        this.wish = wish;
        if (!TextUtils.isEmpty(wish.getImageUrl())) {
            Glide.with(this)
                    .load(wish.getImageUrl())
                    .into(wishImage);
        }
        wishName.setText(wish.getName());
        wishCost.setText(Long.toString(wish.getCost()));
        wishLink.setText(wish.getLink());
    }
}
