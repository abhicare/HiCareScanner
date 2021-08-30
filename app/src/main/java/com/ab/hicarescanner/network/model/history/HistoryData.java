package com.ab.hicarescanner.network.model.history;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Arjun Bhatt on 1/30/2020.
 */
public class HistoryData extends RealmObject {
    private String CodeType;
    private String Time;
    private String CodeText;
    private String CodeFormat;


    public String getCodeType() {
        return CodeType;
    }

    public void setCodeType(String codeType) {
        CodeType = codeType;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getCodeText() {
        return CodeText;
    }

    public void setCodeText(String codeText) {
        CodeText = codeText;
    }

    public String getCodeFormat() {
        return CodeFormat;
    }

    public void setCodeFormat(String codeFormat) {
        CodeFormat = codeFormat;
    }
}
