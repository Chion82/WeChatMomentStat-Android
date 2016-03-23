package moe.chionlab.wechatmomentstat.Model;

import java.util.ArrayList;

/**
 * Created by chiontang on 3/21/16.
 */
public class UserSnsInfo {
    public String userId;
    public String userName;
    public ArrayList<SnsInfo> snsList = new ArrayList<SnsInfo>();
    public int likeCount = 0;
    public int likedCount = 0;
    public int sentCommentCount = 0;
    public int receivedCommentCount = 0;
    public int repliedCommentCount = 0;
    public int photoNumbers = 0;
    public double heatRate = 0;
}
