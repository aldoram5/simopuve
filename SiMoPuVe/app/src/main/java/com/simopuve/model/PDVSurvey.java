package com.simopuve.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aldorangel on 3/30/17.
 */

public class PDVSurvey {
    private PDVHeader header;
    private List<PDVRow> rows;

    public PDVSurvey() {
        rows= new ArrayList<>();
        header = new PDVHeader();
    }

    public PDVSurvey(PDVHeader header, List<PDVRow> rows) {
        this.header = header;
        this.rows = rows;
    }

    public PDVHeader getHeader() {
        return header;
    }

    public void setHeader(PDVHeader header) {
        this.header = header;
    }

    public List<PDVRow> getRows() {
        return rows;
    }

    public void setRows(List<PDVRow> rows) {
        this.rows = rows;
    }

}