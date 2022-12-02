package com.example.jetpackplayerapp.model;

import android.text.TextUtils;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.jetpackplayerapp.BR;

import java.io.Serializable;
import java.util.Objects;

public class User extends BaseObservable implements Serializable {

    /**
     * id : 962
     * userId : 3223400206308231
     * name : 二师弟请随我来
     * avatar :
     * description :
     * likeCount : 0
     * topCommentCount : 0
     * followCount : 0
     * followerCount : 0
     * qqOpenId : null
     * expires_time : 0
     * score : 0
     * historyCount : 0
     * commentCount : 0
     * favoriteCount : 0
     * feedCount : 0
     * hasFollow : false
     */

    public int id;
    public long userId;
    public String name;
    public String avatar;
    public String description;
    public int likeCount;
    public int topCommentCount;
    public int followCount;
    public int followerCount;
    public String qqOpenId;
    public long expires_time;
    public int score;
    public int historyCount;
    public int commentCount;
    public int favoriteCount;
    public int feedCount;
    public boolean hasFollow;

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof User))
            return false;
        User newUser = (User) obj;
        return TextUtils.equals(name, newUser.name)
                && TextUtils.equals(avatar, newUser.avatar)
                && TextUtils.equals(description, newUser.description)
                && likeCount == newUser.likeCount
                && topCommentCount == newUser.topCommentCount
                && followCount == newUser.followCount
                && followerCount == newUser.followerCount
                && qqOpenId == newUser.qqOpenId
                && expires_time == newUser.expires_time
                && score == newUser.score
                && historyCount == newUser.historyCount
                && commentCount == newUser.commentCount
                && favoriteCount == newUser.favoriteCount
                && feedCount == newUser.feedCount
                && hasFollow == newUser.hasFollow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, avatar, description, likeCount, topCommentCount, followCount, followerCount, qqOpenId, expires_time, score, historyCount, commentCount, favoriteCount, feedCount, hasFollow);
    }

    @Bindable
    public boolean isHasFollow() {
        return hasFollow;
    }

    public void setHasFollow(boolean hasFollow) {
        notifyPropertyChanged(BR._all);
    }
}
