package moe.chionlab.wechatmomentstat;

import android.os.Environment;

/**
 * Created by chiontang on 2/4/16.
 */
public class Config {

    static boolean ready = false;

    final static public String EXT_DIR = Environment.getExternalStorageDirectory() + "/WeChatMomentStat";
    final static public String DATA_DIR = Environment.getDataDirectory() + "/data/moe.chionlab.wechatmomentstat";

    final static public String WECHAT_PACKAGE = "com.tencent.mm";

    final static String[] VERSIONS = {"6.3.13.49_r4080b63", "6.3.13.64_r4488992"};
    final static String[] PROTOCAL_SNS_DETAIL_CLASSES = {"com.tencent.mm.protocal.b.atp", "com.tencent.mm.protocal.b.atp"};
    final static String[] PROTOCAL_SNS_DETAIL_METHODS = {"a", "a"};
    final static String[] SNS_XML_GENERATOR_CLASSES = {"com.tencent.mm.plugin.sns.f.i", "com.tencent.mm.plugin.sns.f.i"};
    final static String[] SNS_XML_GENERATOR_METHODS = {"a", "a"};
    final static String[] PROTOCAL_SNS_OBJECT_CLASSES = {"com.tencent.mm.protocal.b.aqi", "com.tencent.mm.protocal.b.aqi"};
    final static String[] PROTOCAL_SNS_OBJECT_METHODS = {"a", "a"};
    final static String[] PROTOCAL_SNS_OBJECT_USERID_FIELDS = {"iYA", "iYA"};
    final static String[] PROTOCAL_SNS_OBJECT_NICKNAME_FIELDS = {"jyd", "jyd"};
    final static String[] PROTOCAL_SNS_OBJECT_TIMESTAMP_FIELDS = {"fpL", "fpL"};
    final static String[] PROTOCAL_SNS_OBJECT_COMMENTS_FIELDS = {"jJX", "jJX"};
    final static String[] PROTOCAL_SNS_OBJECT_LIKES_FIELDS = {"jJU", "jJU"};
    final static String[] SNS_OBJECT_EXT_AUTHOR_NAME_FIELDS = {"jyd", "jyd"};
    final static String[] SNS_OBJECT_EXT_REPLY_TO_FIELDS = {"jJM", "jJM"};
    final static String[] SNS_OBJECT_EXT_COMMENT_FIELDS = {"fsI", "fsI"};
    final static String[] SNS_OBJECT_EXT_AUTHOR_ID_FIELDS = {"iYA", "iYA"};
    final static String[] SNS_DETAIL_FROM_BIN_METHODS = {"am", "am"};
    final static String[] SNS_OBJECT_FROM_BIN_METHODS = {"am", "am"};

    static String PROTOCAL_SNS_DETAIL_CLASS;
    static String PROTOCAL_SNS_DETAIL_METHOD;
    static String SNS_XML_GENERATOR_CLASS;
    static String SNS_XML_GENERATOR_METHOD;
    static String PROTOCAL_SNS_OBJECT_CLASS;
    static String PROTOCAL_SNS_OBJECT_METHOD;
    static String PROTOCAL_SNS_OBJECT_USERID_FIELD;
    static String PROTOCAL_SNS_OBJECT_NICKNAME_FIELD;
    static String PROTOCAL_SNS_OBJECT_TIMESTAMP_FIELD;
    static String PROTOCAL_SNS_OBJECT_COMMENTS_FIELD;
    static String PROTOCAL_SNS_OBJECT_LIKES_FIELD;
    static String SNS_OBJECT_EXT_AUTHOR_NAME_FIELD;
    static String SNS_OBJECT_EXT_REPLY_TO_FIELD;
    static String SNS_OBJECT_EXT_COMMENT_FIELD;
    static String SNS_OBJECT_EXT_AUTHOR_ID_FIELD;
    static String SNS_DETAIL_FROM_BIN_METHOD;
    static String SNS_OBJECT_FROM_BIN_METHOD;

    static public void initWeChatVersion(String version) {
        for (int i=0;i<VERSIONS.length;i++) {
            if (VERSIONS[i].equals(version)) {
                Config.setConstants(i);
                Config.ready = true;
                return;
            }
        }
        Config.ready = false;
    }

    static private void setConstants(int index) {
        PROTOCAL_SNS_DETAIL_CLASS = PROTOCAL_SNS_DETAIL_CLASSES[index];
        PROTOCAL_SNS_DETAIL_METHOD = PROTOCAL_SNS_DETAIL_METHODS[index];
        SNS_XML_GENERATOR_CLASS = SNS_XML_GENERATOR_CLASSES[index];
        SNS_XML_GENERATOR_METHOD = SNS_XML_GENERATOR_METHODS[index];
        PROTOCAL_SNS_OBJECT_CLASS = PROTOCAL_SNS_OBJECT_CLASSES[index];
        PROTOCAL_SNS_OBJECT_METHOD = PROTOCAL_SNS_OBJECT_METHODS[index];
        PROTOCAL_SNS_OBJECT_USERID_FIELD = PROTOCAL_SNS_OBJECT_USERID_FIELDS[index];
        PROTOCAL_SNS_OBJECT_NICKNAME_FIELD = PROTOCAL_SNS_OBJECT_NICKNAME_FIELDS[index];
        PROTOCAL_SNS_OBJECT_TIMESTAMP_FIELD = PROTOCAL_SNS_OBJECT_TIMESTAMP_FIELDS[index];
        PROTOCAL_SNS_OBJECT_COMMENTS_FIELD = PROTOCAL_SNS_OBJECT_COMMENTS_FIELDS[index];
        PROTOCAL_SNS_OBJECT_LIKES_FIELD = PROTOCAL_SNS_OBJECT_LIKES_FIELDS[index];
        SNS_OBJECT_EXT_AUTHOR_NAME_FIELD = SNS_OBJECT_EXT_AUTHOR_NAME_FIELDS[index];
        SNS_OBJECT_EXT_REPLY_TO_FIELD = SNS_OBJECT_EXT_REPLY_TO_FIELDS[index];
        SNS_OBJECT_EXT_COMMENT_FIELD = SNS_OBJECT_EXT_COMMENT_FIELDS[index];
        SNS_OBJECT_EXT_AUTHOR_ID_FIELD = SNS_OBJECT_EXT_AUTHOR_ID_FIELDS[index];
        SNS_DETAIL_FROM_BIN_METHOD = SNS_DETAIL_FROM_BIN_METHODS[index];
        SNS_OBJECT_FROM_BIN_METHOD = SNS_OBJECT_FROM_BIN_METHODS[index];
    }

}
