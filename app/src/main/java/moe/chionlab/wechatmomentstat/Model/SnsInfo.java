package moe.chionlab.wechatmomentstat.Model;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by chiontang on 2/8/16.
 */
public class SnsInfo {
    public String id = "";
    public String authorName = "";
    public String content = "";
    public String authorId = "";
    public ArrayList<Like> likes = new ArrayList<Like>();
    public ArrayList<Comment> comments = new ArrayList<Comment>();
    public ArrayList<String> mediaList = new ArrayList<String>();
    public String rawXML = "";
    public long timestamp = 0;
    public boolean ready = false;
    public boolean isCurrentUser = false;
    public boolean selected = true;

    public void print() {
        Log.d("wechatmomentstat", "================================");
        Log.d("wechatmomentstat", "id: " + this.id);
        Log.d("wechatmomentstat", "Author: " + this.authorName);
        Log.d("wechatmomentstat", "Content: " + this.content);
        Log.d("wechatmomentstat", "Likes:");
        for (int i=0; i<likes.size();i++) {
            Log.d("wechatmomentstat", likes.get(i).userName);
        }
        Log.d("wechatmomentstat", "Comments:");
        for (int i=0; i<comments.size();i++) {
            Comment comment = comments.get(i);
            Log.d("wechatmomentstat", "CommentAuthor: " + comment.authorName + "; CommentContent: " + comment.content + "; ToUser: " + comment.toUser);
        }
        Log.d("wechatmomentstat", "Media List:");
        for (int i=0;i<mediaList.size();i++) {
            Log.d("wechatmomentstat", mediaList.get(i));
        }
    }

    public SnsInfo clone() {
        SnsInfo newSns = new SnsInfo();
        newSns.id = this.id;
        newSns.authorName = this.authorName;
        newSns.content = this.content;
        newSns.authorId = this.authorId;
        newSns.likes = new ArrayList<Like>(this.likes);
        newSns.comments = new ArrayList<Comment>(this.comments);
        newSns.mediaList = new ArrayList<String>(this.mediaList);
        newSns.rawXML = this.rawXML;
        newSns.timestamp = this.timestamp;
        return newSns;
    }

    public void clear() {
        id = "";
        authorName = "";
        content = "";
        authorId = "";
        likes.clear();
        comments.clear();
        mediaList.clear();
        rawXML = "";
    }

    static public class Like {
        public String userName;
        public String userId;
        public boolean isCurrentUser = false;
    }

    static public class Comment {
        public String authorName;
        public String content;
        public String toUser;
        public String authorId;
        public String toUserId;
        public boolean isCurrentUser = false;
    }
}
