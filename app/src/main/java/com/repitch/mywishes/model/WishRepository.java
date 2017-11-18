package com.repitch.mywishes.model;

import com.repitch.mywishes.db.base.SingleDaoService;
import com.repitch.mywishes.db.entity.Wish;

import java.util.List;

/**
 * Created by repitch on 18.11.2017.
 */
public class WishRepository extends SingleDaoService<Wish, Integer> {
    public WishRepository() {
        super(Wish.class);
    }

    public Wish getWishById(int id) {
        return getDao().queryForId(id);
    }

    public List<Wish> getWishes() {
        return getDao().queryForAll();
    }

    public void updateWish(Wish wish) {
        getDao().createOrUpdate(wish);
    }
}
