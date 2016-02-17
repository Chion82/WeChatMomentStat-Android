package moe.chionlab.wechatmomentstat.Model;

import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;

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

    public void print() {
        XposedBridge.log("================================");
        XposedBridge.log("id: " + this.id);
        XposedBridge.log("Author: " + this.authorName);
        XposedBridge.log("Content: " + this.content);
        XposedBridge.log("Likes:");
        for (int i=0; i<likes.size();i++) {
            XposedBridge.log(likes.get(i).userName);
        }
        XposedBridge.log("Comments:");
        for (int i=0; i<comments.size();i++) {
            Comment comment = comments.get(i);
            XposedBridge.log("CommentAuthor: " + comment.authorName + "; CommentContent: " + comment.content + "; ToUser: " + comment.toUser);
        }
        XposedBridge.log("Media List:");
        for (int i=0;i<mediaList.size();i++) {
            XposedBridge.log(mediaList.get(i));
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
