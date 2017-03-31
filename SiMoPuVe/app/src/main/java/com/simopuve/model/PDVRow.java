package com.simopuve.model;

/**
 * Created by aldorangel on 3/31/17.
 */

public class PDVRow {
    private int rowNumber;
    private int personNumber;
    private String deviceBrand;
    private String deviceModel;
    private String contractType;
    private String deviceMode;
    private String additionalCharacteristics;
    private boolean boughtCard;
    private boolean boughtChip;
    private boolean boughtAccessory;
    private String planRating;
    private String deviceRating;
    private int expressRefillValue;
    private String portabilityChange;
    private String portabilityChangeReason;

    public PDVRow() {
    }

    public PDVRow(int rowNumber, int personNumber, String deviceBrand, String deviceModel, String contractType, String deviceMode, String additionalCharacteristics, boolean boughtCard, boolean boughtChip, boolean boughtAccessory, String planRating, String deviceRating, int expressRefillValue, String portabilityChange, String portabilityChangeReason) {
        this.rowNumber = rowNumber;
        this.personNumber = personNumber;
        this.deviceBrand = deviceBrand;
        this.deviceModel = deviceModel;
        this.contractType = contractType;
        this.deviceMode = deviceMode;
        this.additionalCharacteristics = additionalCharacteristics;
        this.boughtCard = boughtCard;
        this.boughtChip = boughtChip;
        this.boughtAccessory = boughtAccessory;
        this.planRating = planRating;
        this.deviceRating = deviceRating;
        this.expressRefillValue = expressRefillValue;
        this.portabilityChange = portabilityChange;
        this.portabilityChangeReason = portabilityChangeReason;
    }

    public String getPortabilityChangeReason() {
        return portabilityChangeReason;
    }

    public void setPortabilityChangeReason(String portabilityChangeReason) {
        this.portabilityChangeReason = portabilityChangeReason;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(int personNumber) {
        this.personNumber = personNumber;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getDeviceMode() {
        return deviceMode;
    }

    public void setDeviceMode(String deviceMode) {
        this.deviceMode = deviceMode;
    }

    public String getAdditionalCharacteristics() {
        return additionalCharacteristics;
    }

    public void setAdditionalCharacteristics(String additionalCharacteristics) {
        this.additionalCharacteristics = additionalCharacteristics;
    }

    public boolean isBoughtCard() {
        return boughtCard;
    }

    public void setBoughtCard(boolean boughtCard) {
        this.boughtCard = boughtCard;
    }

    public boolean isBoughtChip() {
        return boughtChip;
    }

    public void setBoughtChip(boolean boughtChip) {
        this.boughtChip = boughtChip;
    }

    public boolean isBoughtAccessory() {
        return boughtAccessory;
    }

    public void setBoughtAccessory(boolean boughtAccessory) {
        this.boughtAccessory = boughtAccessory;
    }

    public String getPlanRating() {
        return planRating;
    }

    public void setPlanRating(String planRating) {
        this.planRating = planRating;
    }

    public String getDeviceRating() {
        return deviceRating;
    }

    public void setDeviceRating(String deviceRating) {
        this.deviceRating = deviceRating;
    }

    public int getExpressRefillValue() {
        return expressRefillValue;
    }

    public void setExpressRefillValue(int expressRefillValue) {
        this.expressRefillValue = expressRefillValue;
    }

    public String getPortabilityChange() {
        return portabilityChange;
    }

    public void setPortabilityChange(String portabilityChange) {
        this.portabilityChange = portabilityChange;
    }

}