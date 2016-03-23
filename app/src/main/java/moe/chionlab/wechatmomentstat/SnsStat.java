package moe.chionlab.wechatmomentstat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import moe.chionlab.wechatmomentstat.Model.SnsInfo;
import moe.chionlab.wechatmomentstat.Model.UserSnsInfo;

/**
 * Created by chiontang on 3/21/16.
 */
public class SnsStat {
    public String currentUserId = "";
    public String currentUserName = "";
    public ArrayList<SnsInfo> snsList = null;
    public ArrayList<UserSnsInfo> userSnsList = new ArrayList<UserSnsInfo>();
    public ArrayList<UserSnsInfo> momentRank = null;
    public ArrayList<UserSnsInfo> likeRank = null;
    public ArrayList<UserSnsInfo> likedRank = null;
    public ArrayList<UserSnsInfo> sentCommentRank = null;
    public ArrayList<UserSnsInfo> receivedCommentRank = null;
    public ArrayList<UserSnsInfo> photoRank = null;
    public ArrayList<UserSnsInfo> heatRank = null;
    public ArrayList<UserSnsInfo> coldRank = null;
    public long earliestTimestamp = 0;

    public SnsStat(ArrayList<SnsInfo> snsList) {
        this.snsList = snsList;
        this.generateUserSnsList();
        this.generateRanks();
    }

    protected void generateUserSnsList() {
        for (int i=0;i<snsList.size();i++) {
            SnsInfo snsInfo = snsList.get(i);
            if (snsInfo.isCurrentUser) {
                this.currentUserId = snsInfo.authorId;
                this.currentUserName = snsInfo.authorName;
            }
            if (earliestTimestamp == 0 || snsInfo.timestamp < earliestTimestamp) {
                earliestTimestamp = snsInfo.timestamp;
            }
            UserSnsInfo userSnsInfo = getUserSnsInfo(snsInfo.authorId);
            userSnsInfo.userName = snsInfo.authorName;
            userSnsInfo.snsList.add(snsInfo);
            userSnsInfo.likedCount += snsInfo.likes.size();
            userSnsInfo.receivedCommentCount += snsInfo.comments.size();
            userSnsInfo.photoNumbers += snsInfo.mediaList.size();
            for (int commentId=0;commentId<snsInfo.comments.size();commentId++) {
                SnsInfo.Comment comment = snsInfo.comments.get(commentId);
                UserSnsInfo commentSender = getUserSnsInfo(comment.authorId);
                commentSender.sentCommentCount++;
                if (comment.toUserId != null) {
                    UserSnsInfo replyToUser = getUserSnsInfo(comment.toUserId);
                    replyToUser.repliedCommentCount++;
                }
            }
            for (int likeId=0;likeId<snsInfo.likes.size();likeId++) {
                SnsInfo.Like like = snsInfo.likes.get(likeId);
                UserSnsInfo liker = getUserSnsInfo(like.userId);
                liker.likeCount++;
            }
            if (userSnsInfo.sentCommentCount > 0) {
                userSnsInfo.heatRate = ((double)userSnsInfo.repliedCommentCount)/((double)userSnsInfo.sentCommentCount);
            }
        }
    }

    protected void generateRanks() {
        momentRank = new ArrayList<UserSnsInfo>(userSnsList);
        likeRank = new ArrayList<UserSnsInfo>(userSnsList);
        likedRank = new ArrayList<UserSnsInfo>(userSnsList);
        sentCommentRank = new ArrayList<UserSnsInfo>(userSnsList);
        receivedCommentRank = new ArrayList<UserSnsInfo>(userSnsList);
        photoRank = new ArrayList<UserSnsInfo>(userSnsList);
        heatRank = new ArrayList<UserSnsInfo>(userSnsList);
        coldRank = new ArrayList<UserSnsInfo>();
        for (int i=0;i<userSnsList.size();i++) {
            UserSnsInfo userSnsInfo = userSnsList.get(i);
            if (userSnsInfo.sentCommentCount >= 15) {
                coldRank.add(userSnsInfo);
            }
        }
        Collections.sort(momentRank, new Comparator<UserSnsInfo>() {
            @Override
            public int compare(UserSnsInfo lhs, UserSnsInfo rhs) {
                return rhs.snsList.size() - lhs.snsList.size();
            }
        });
        Collections.sort(likeRank, new Comparator<UserSnsInfo>() {
            @Override
            public int compare(UserSnsInfo lhs, UserSnsInfo rhs) {
                return rhs.likeCount - lhs.likeCount;
            }
        });
        Collections.sort(likedRank, new Comparator<UserSnsInfo>() {
            @Override
            public int compare(UserSnsInfo lhs, UserSnsInfo rhs) {
                return rhs.likedCount - lhs.likedCount;
            }
        });
        Collections.sort(sentCommentRank, new Comparator<UserSnsInfo>() {
            @Override
            public int compare(UserSnsInfo lhs, UserSnsInfo rhs) {
                return rhs.sentCommentCount - lhs.sentCommentCount;
            }
        });
        Collections.sort(receivedCommentRank, new Comparator<UserSnsInfo>() {
            @Override
            public int compare(UserSnsInfo lhs, UserSnsInfo rhs) {
                return rhs.receivedCommentCount - lhs.receivedCommentCount;
            }
        });
        Collections.sort(photoRank, new Comparator<UserSnsInfo>() {
            @Override
            public int compare(UserSnsInfo lhs, UserSnsInfo rhs) {
                return rhs.photoNumbers - lhs.photoNumbers;
            }
        });
        Collections.sort(heatRank, new Comparator<UserSnsInfo>() {
            @Override
            public int compare(UserSnsInfo lhs, UserSnsInfo rhs) {
                return (rhs.heatRate - lhs.heatRate)>0?1:-1;
            }
        });
        Collections.sort(coldRank, new Comparator<UserSnsInfo>() {
            @Override
            public int compare(UserSnsInfo lhs, UserSnsInfo rhs) {
                return (lhs.heatRate - rhs.heatRate)>0?1:-1;
            }
        });
    }

    public UserSnsInfo getUserSnsInfo(String userId) {
        for (int i=0;i<userSnsList.size();i++) {
            if (userSnsList.get(i).userId.equals(userId)) {
                return userSnsList.get(i);
            }
        }
        UserSnsInfo userSnsInfo = new UserSnsInfo();
        userSnsInfo.userId = userId;
        userSnsList.add(userSnsInfo);
        return userSnsInfo;
    }

}
