package com.simopuve.model;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by aldorangel on 3/30/17.
 */

public class PDVSurvey extends RealmObject {
    private PDVHeader header;
    private RealmList<PDVRow> rows;

    public PDVSurvey() {
        rows= new RealmList<PDVRow>();
        header = new PDVHeader();
    }

    public PDVSurvey(PDVHeader header, RealmList rows) {
        this.header = header;
        this.rows = rows;
    }

    public PDVHeader getHeader() {
        return header;
    }

    public void setHeader(PDVHeader header) {
        this.header = header;
    }

    public RealmList getRows() {
        return rows;
    }

    public void setRows(RealmList<PDVRow> rows) {
        this.rows = rows;
    }

}