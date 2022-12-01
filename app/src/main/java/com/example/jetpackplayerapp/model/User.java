package com.example.jetpackplayerapp.model;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id && userId == user.userId && likeCount == user.likeCount && topCommentCount == user.topCommentCount && followCount == user.followCount && followerCount == user.followerCount && expires_time == user.expires_time && score == user.score && historyCount == user.historyCount && commentCount == user.commentCount && favoriteCount == user.favoriteCount && feedCount == user.feedCount && hasFollow == user.hasFollow && name.equals(user.name) && avatar.equals(user.avatar) && description.equals(user.description) && qqOpenId.equals(user.qqOpenId);
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
