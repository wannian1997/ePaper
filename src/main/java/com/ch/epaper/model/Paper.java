package com.ch.epaper.model;

/**
 * @author songwannian
 */
public class Paper {
    private String caseId;
    private String causeOfAction;
    private String paperName;
    private String court;
    private String paperType;
    private String prosecution;
    private String indictmentId;
    private String timeOfCase;
    private String date;
    private String defendants;
    private String text;
    private String textIndex;

    public String getCaseId(){
        return caseId;
    }
    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getCauseOfAction() {
        return causeOfAction;
    }
    public void setCauseOfAction(String causeOfAction) {
        this.causeOfAction = causeOfAction;
    }

    public String getPaperName() {
        return paperName;
    }
    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }
    public String getCourt() {
        return court;
    }
    public void setCourt(String court) {
        this.court = court;
    }

    public String getPaperType() {
        return paperType;
    }
    public void setPaperType(String paperType) {
        this.paperType = paperType;
    }

    public String getProsecution() {
        return prosecution;
    }
    public void setProsecution(String prosecution) {
        this.prosecution = prosecution;
    }
    public String getIndictmentId() {
        return indictmentId;
    }
    public void setIndictmentId(String indictmentId) {
        this.indictmentId = indictmentId;
    }

    public String getTimeOfCase() {
        return timeOfCase;
    }
    public void setTimeOfCase(String timeOfCase) {
        this.timeOfCase = timeOfCase;
    }
    public String getDate(){
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getDefendants() {
        return defendants;
    }
    public void setDefendants(String defendants) {
        this.defendants = defendants;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    // 不映射部分
    public String getTextIndex() {
        return textIndex;
    }
    public void setTextIndex(String textIndex) {
        this.textIndex = textIndex;
    }

    @Override
    public String toString() {
        String out = caseId + "\n";
        out += causeOfAction + "\n";
        return out;
    }
}
