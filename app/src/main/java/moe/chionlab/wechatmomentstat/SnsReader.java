package moe.chionlab.wechatmomentstat;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
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
        Task.saveToJSONFile(this.snsList, Config.EXT_DIR + "/all_sns.json", false);
    }

    public ArrayList<SnsInfo> getSnsList() {
        return this.snsList;
    }

    protected void queryDatabase() throws Throwable {
        String dbPath = Config.EXT_DIR + "/SnsMicroMsg.db";
        if (!new File(dbPath).exists()) {
            Log.e("wechatmomentstat", "DB file not found");
            throw new Exception("DB file not found");
        }
        snsList.clear();
        SQLiteDatabase database = SQLiteDatabase.openDatabase(dbPath, null, 0);
        getCurrentUserIdFromDatabase(database);
        Cursor cursor = database.query("SnsInfo", new String[]{"SnsId", "userName", "createTime", "content", "attrBuf"} ,"", new String[]{},"","","createTime DESC","");
        while (cursor.moveToNext()) {
            addSnsInfoFromCursor(cursor);
        }
        cursor.close();
        database.close();
    }

    protected void getCurrentUserIdFromDatabase(SQLiteDatabase database) throws Throwable {
        Cursor cursor = database.query("snsExtInfo3", new String[]{"userName"}, "ROWID=?", new String[]{"1"}, "", "", "", "1");
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
        //newSns.print();
    }



}
