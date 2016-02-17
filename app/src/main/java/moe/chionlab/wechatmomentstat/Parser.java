package moe.chionlab.wechatmomentstat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moe.chionlab.wechatmomentstat.Model.SnsInfo;

/**
 * Created by chiontang on 2/11/16.
 */
public class Parser {

    protected Class SnsDetailParser = null;
    protected Class SnsDetail = null;
    protected Class SnsObject = null;

    public Parser(Class SnsDetail, Class SnsDetailParser, Class SnsObject) {
        this.SnsDetailParser = SnsDetailParser;
        this.SnsDetail = SnsDetail;
        this.SnsObject = SnsObject;
    }

    public SnsInfo parseSnsAllFromBin(byte[] snsDetailBin, byte[] snsObjectBin) throws Throwable {
        Object snsDetail = parseSnsDetailFromBin(snsDetailBin);
        Object snsObject = parseSnsObjectFromBin(snsObjectBin);
        SnsInfo snsInfo = parseSnsDetail(snsDetail);
        parseSnsObject(snsObject, snsInfo);
        return snsInfo;
    }

    public Object parseSnsDetailFromBin(byte[] bin) throws Throwable {
        Object snsDetail = SnsDetail.newInstance();
        Method fromBinMethod = SnsDetail.getMethod(Config.SNS_DETAIL_FROM_BIN_METHOD, byte[].class);
        fromBinMethod.invoke(snsDetail, bin);
        return snsDetail;
    }

    public SnsInfo parseSnsDetail(Object snsDetail) throws Throwable {
        Method snsDetailParserMethod = SnsDetailParser.getMethod(Config.SNS_XML_GENERATOR_METHOD, SnsDetail);
        String xmlResult = (String)snsDetailParserMethod.invoke(this, snsDetail);
        return parseTimelineXML(xmlResult);
    }

    public Object parseSnsObjectFromBin(byte[] bin) throws Throwable {
        Object snsObject = SnsObject.newInstance();
        Method fromBinMethod = SnsObject.getMethod(Config.SNS_OBJECT_FROM_BIN_METHOD, byte[].class);
        fromBinMethod.invoke(snsObject, bin);
        return snsObject;
    }

    static public SnsInfo parseTimelineXML(String xmlResult) throws Throwable {
        SnsInfo currentSns = new SnsInfo();
        Pattern userIdPattern = Pattern.compile("<username><!\\[CDATA\\[(.+?)\\]\\]></username>");
        Pattern contentPattern = Pattern.compile("<contentDesc><!\\[CDATA\\[(.+?)\\]\\]></contentDesc>", Pattern.DOTALL);
        Pattern mediaPattern = Pattern.compile("<media>.*?<url.*?><!\\[CDATA\\[(.+?)\\]\\]></url>.*?</media>");
        Pattern timestampPattern = Pattern.compile("<createTime><!\\[CDATA\\[(.+?)\\]\\]></createTime>");

        Matcher userIdMatcher = userIdPattern.matcher(xmlResult);
        Matcher contentMatcher = contentPattern.matcher(xmlResult);
        Matcher mediaMatcher = mediaPattern.matcher(xmlResult);
        Matcher timestampMatcher = timestampPattern.matcher(xmlResult);

        currentSns.id = getTimelineId(xmlResult);

        currentSns.rawXML = xmlResult;

        if (timestampMatcher.find()) {
            currentSns.timestamp = Integer.parseInt(timestampMatcher.group(1));
        }

        if (userIdMatcher.find()) {
            currentSns.authorId = userIdMatcher.group(1);
        }

        if (contentMatcher.find()) {
            currentSns.content = contentMatcher.group(1);
        }

        while (mediaMatcher.find()) {
            boolean flag = true;
            for (int i=0;i<currentSns.mediaList.size();i++) {
                if (currentSns.mediaList.get(i).equals(mediaMatcher.group(1))) {
                    flag = false;
                    break;
                }
            }
            if (flag)
                currentSns.mediaList.add(mediaMatcher.group(1));
        }

        return currentSns;
    }

    static public void parseSnsObject(Object aqiObject, SnsInfo matchSns) throws Throwable{
        Field field = null;
        Object userId=null, nickname=null;

        field = aqiObject.getClass().getField(Config.PROTOCAL_SNS_OBJECT_USERID_FIELD);
        userId = field.get(aqiObject);

        field = aqiObject.getClass().getField(Config.PROTOCAL_SNS_OBJECT_NICKNAME_FIELD);
        nickname = field.get(aqiObject);

        field = aqiObject.getClass().getField(Config.PROTOCAL_SNS_OBJECT_TIMESTAMP_FIELD);
        long snsTimestamp = ((Integer) field.get(aqiObject)).longValue();

        if (userId == null || nickname == null) {
            return;
        }

        matchSns.ready = true;
        matchSns.authorName = (String)nickname;
        field = aqiObject.getClass().getField(Config.PROTOCAL_SNS_OBJECT_COMMENTS_FIELD);
        LinkedList list = (LinkedList)field.get(aqiObject);
        for (int i=0;i<list.size();i++) {
            Object childObject = list.get(i);
            parseSnsObjectExt(childObject, true, matchSns);
        }

        field = aqiObject.getClass().getField(Config.PROTOCAL_SNS_OBJECT_LIKES_FIELD);
        LinkedList likeList = (LinkedList)field.get(aqiObject);
        for (int i=0;i<likeList.size();i++) {
            Object likeObject = likeList.get(i);
            parseSnsObjectExt(likeObject, false, matchSns);
        }
    }

    static public void parseSnsObjectExt(Object apzObject, boolean isComment, SnsInfo matchSns) throws Throwable {
        if (isComment) {
            Field field = apzObject.getClass().getField(Config.SNS_OBJECT_EXT_AUTHOR_NAME_FIELD);
            Object authorName = field.get(apzObject);

            field = apzObject.getClass().getField(Config.SNS_OBJECT_EXT_REPLY_TO_FIELD);
            Object replyToUserId = field.get(apzObject);

            field = apzObject.getClass().getField(Config.SNS_OBJECT_EXT_COMMENT_FIELD);
            Object commentContent = field.get(apzObject);

            field = apzObject.getClass().getField(Config.SNS_OBJECT_EXT_AUTHOR_ID_FIELD);
            Object authorId = field.get(apzObject);

            if (authorId == null || commentContent == null || authorName == null) {
                return;
            }

            for (int i = 0; i < matchSns.comments.size(); i++) {
                SnsInfo.Comment loadedComment = matchSns.comments.get(i);
                if (loadedComment.authorId.equals((String) authorId) && loadedComment.content.equals((String) commentContent)) {
                    return;
                }
            }

            SnsInfo.Comment newComment = new SnsInfo.Comment();
            newComment.authorName = (String) authorName;
            newComment.content = (String) commentContent;
            newComment.authorId = (String) authorId;
            newComment.toUserId = (String) replyToUserId;

            for (int i = 0; i < matchSns.comments.size(); i++) {
                SnsInfo.Comment loadedComment = matchSns.comments.get(i);
                if (replyToUserId != null && loadedComment.authorId.equals((String) replyToUserId)) {
                    newComment.toUser = loadedComment.authorName;
                    break;
                }
            }

            matchSns.comments.add(newComment);
        } else {
            Field field = apzObject.getClass().getField(Config.SNS_OBJECT_EXT_AUTHOR_NAME_FIELD);
            Object nickname = field.get(apzObject);
            field = apzObject.getClass().getField(Config.SNS_OBJECT_EXT_AUTHOR_ID_FIELD);
            Object userId = field.get(apzObject);
            if (nickname == null || userId == null) {
                return;
            }

            if (((String)userId).equals("")) {
                return;
            }
            for (int i = 0; i < matchSns.likes.size(); i++) {
                if (matchSns.likes.get(i).userId.equals((String)userId)) {
                    return;
                }
            }
            SnsInfo.Like newLike = new SnsInfo.Like();
            newLike.userId = (String)userId;
            newLike.userName = (String)nickname;
            matchSns.likes.add(newLike);
        }
    }

    static public String getTimelineId(String xmlResult) {
        Pattern idPattern = Pattern.compile("<id><!\\[CDATA\\[(.+?)\\]\\]></id>");
        Matcher idMatcher = idPattern.matcher(xmlResult);
        if (idMatcher.find()) {
            return idMatcher.group(1);
        } else {
            return "";
        }
    }
}
