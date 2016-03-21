package moe.chionlab.wechatmomentstat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import moe.chionlab.wechatmomentstat.Model.SnsInfo;

/**
 * Created by chiontang on 2/12/16.
 */
public class SnsReader {

    Class SnsDetail = null;
    Class SnsDetailParser = null;
    Class SnsObject = null;
    Parser parser = null;
    ArrayList<SnsInfo> snsList = new ArrayList<SnsInfo>();
    String currentUserId = "";

    public SnsReader(Class SnsDetail, Class SnsDetailParser, Class SnsObject){
        this.SnsDetail = SnsDetail;
        this.SnsDetailParser = SnsDetailParser;
        this.SnsObject = SnsObject;
        this.parser = new Parser(SnsDetail, SnsDetailParser, SnsObject);
    }

    public void run() throws Throwable {
        Log.d("wechatmomentstat", "Querying Sns database.");
        queryDatabase();
        saveToJSONFile();
    }

    protected void queryDatabase() throws Throwable {
        String dbPath = Config.EXT_DIR + "/SnsMicroMsg.db";
        if (!new File(dbPath).exists()) {
            Log.e("wechatmomentstat", "DB file not found");
            return;
        }
        SQLiteDatabase database = SQLiteDatabase.openDatabase(dbPath, null, 0);
        getCurrentUserIdFromDatabase(database);
        Cursor cursor = database.query("SnsInfo", new String[]{"SnsId", "userName", "createTime", "content", "attrBuf"} ,"", new String[]{},"","","","");
        while (cursor.moveToNext()) {
            addSnsInfoFromCursor(cursor);
        }
        cursor.close();
        database.close();
    }

    protected void getCurrentUserIdFromDatabase(SQLiteDatabase database) throws Throwable {
        Cursor cursor = database.query("snsExtInfo2", new String[]{"userName"}, "ROWID=?" , new String[]{"1"}, "", "", "", "1");
        if (cursor.moveToNext()) {
            this.currentUserId = cursor.getString(cursor.getColumnIndex("userName"));
        }
        cursor.close();
        Log.d("wechatmomentstat", "Current userID=" + this.currentUserId);
    }

    protected void addSnsInfoFromCursor(Cursor cursor) throws Throwable {
        byte[] snsDetailBin = cursor.getBlob(cursor.getColumnIndex("content"));
        byte[] snsObjectBin = cursor.getBlob(cursor.getColumnIndex("attrBuf"));
        SnsInfo newSns = parser.parseSnsAllFromBin(snsDetailBin, snsObjectBin);

        for (int i=0;i<snsList.size();i++) {
            if (snsList.get(i).id.equals(newSns.id)) {
                return;
            }
        }

        if (newSns.authorId.equals(this.currentUserId)) {
            newSns.isCurrentUser = true;
        }

        for (int i=0;i<newSns.comments.size();i++) {
            if (newSns.comments.get(i).authorId.equals(this.currentUserId)) {
                newSns.comments.get(i).isCurrentUser = true;
            }
        }

        for (int i=0;i<newSns.likes.size();i++) {
            if (newSns.likes.get(i).userId.equals(this.currentUserId)) {
                newSns.likes.get(i).isCurrentUser = true;
            }
        }

        snsList.add(newSns);
        newSns.print();
    }

    protected void saveToJSONFile() {
        JSONArray snsListJSON = new JSONArray();

        for (int snsIndex=0; snsIndex<snsList.size(); snsIndex++) {
            SnsInfo currentSns = snsList.get(snsIndex);
            if (!currentSns.ready) {
                continue;
            }
            JSONObject snsJSON = new JSONObject();
            JSONArray commentsJSON = new JSONArray();
            JSONArray likesJSON = new JSONArray();
            JSONArray mediaListJSON = new JSONArray();
            try {
                snsJSON.put("isCurrentUser", currentSns.isCurrentUser);
                snsJSON.put("snsId", currentSns.id);
                snsJSON.put("authorName", currentSns.authorName);
                snsJSON.put("authorId", currentSns.authorId);
                snsJSON.put("content", currentSns.content);
                for (int i = 0; i < currentSns.comments.size(); i++) {
                    JSONObject commentJSON = new JSONObject();
                    commentJSON.put("isCurrentUser", currentSns.comments.get(i).isCurrentUser);
                    commentJSON.put("authorName", currentSns.comments.get(i).authorName);
                    commentJSON.put("authorId", currentSns.comments.get(i).authorId);
                    commentJSON.put("content", currentSns.comments.get(i).content);
                    commentJSON.put("toUserName", currentSns.comments.get(i).toUser);
                    commentJSON.put("toUserId", currentSns.comments.get(i).toUserId);
                    commentsJSON.put(commentJSON);
                }
                snsJSON.put("comments", commentsJSON);
                for (int i = 0; i < currentSns.likes.size(); i++) {
                    JSONObject likeJSON = new JSONObject();
                    likeJSON.put("isCurrentUser", currentSns.likes.get(i).isCurrentUser);
                    likeJSON.put("userName", currentSns.likes.get(i).userName);
                    likeJSON.put("userId", currentSns.likes.get(i).userId);
                    likesJSON.put(likeJSON);
                }
                snsJSON.put("likes", likesJSON);
                for (int i = 0; i < currentSns.mediaList.size(); i++) {
                    mediaListJSON.put(currentSns.mediaList.get(i));
                }
                snsJSON.put("mediaList", mediaListJSON);
                snsJSON.put("rawXML", currentSns.rawXML);
                snsJSON.put("timestamp", currentSns.timestamp);

                snsListJSON.put(snsJSON);

            } catch (Exception exception) {
                Log.e("wechatmomentstat", "exception", exception);
            }
        }

        File jsonFile = new File(Config.EXT_DIR + "/all_sns.json");
        if (!jsonFile.exists()) {
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                Log.e("wechatmomentstat", "exception", e);
            }
        }

        try {
            FileWriter fw = new FileWriter(jsonFile.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(snsListJSON.toString());
            bw.close();
        } catch (IOException e) {
            Log.e("wechatmomentstat", "exception", e);
        }
    }

}
