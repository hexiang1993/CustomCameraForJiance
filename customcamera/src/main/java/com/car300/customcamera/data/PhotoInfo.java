package com.car300.customcamera.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xhe on 2017/3/28.
 * 相机使用的photoinfo
 */

public class PhotoInfo implements Parcelable {
    long key_id;

    long task_id;
    int category_id;//类别ID
    String name;//类别名称
    String alias;//类别别名
    String thumbnail;//照片位置图
    String guide_image;//照片引导图
    String tips;//说明

    String path_qiniu;//上传七牛的路径
    String path_local;//本地路径

    boolean additional;//是否是附加照片

    //上传的时候，先看是否有七牛的路径，再看是否修改过
    boolean uploaded_qiniu;//是否上传七牛成功
    boolean modified;//是否修改过

    boolean canSystemPhoto;//是否可以从系统相册选取

    @Override
    public String toString() {
        return "PhotoInfo{" +
                "key_id=" + key_id +
                ", task_id=" + task_id +
                ", category_id=" + category_id +
                ", alias='" + alias + '\'' +
                ", path_qiniu='" + path_qiniu + '\'' +
                ", path_local='" + path_local + '\'' +
                ", modified=" + modified +
                '}';
    }

    public PhotoInfo() {
    }

    public PhotoInfo(long task_id, int category_id, String alias, String guide_image, String tips,
                     String path_qiniu, String path_local, boolean additional, boolean uploaded_qiniu, boolean modified, boolean canSystemPhoto) {
        this.task_id = task_id;
        this.category_id = category_id;
        this.alias = alias;
        this.guide_image = guide_image;
        this.tips = tips;
        this.path_qiniu = path_qiniu;
        this.path_local = path_local;
        this.additional = additional;
        this.uploaded_qiniu = uploaded_qiniu;
        this.modified = modified;
        this.canSystemPhoto = canSystemPhoto;
    }

    public long getKey_id() {
        return key_id;
    }

    public void setKey_id(long key_id) {
        this.key_id = key_id;
    }

    public long getTask_id() {
        return task_id;
    }

    public void setTask_id(long task_id) {
        this.task_id = task_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getGuide_image() {
        return guide_image;
    }

    public void setGuide_image(String guide_image) {
        this.guide_image = guide_image;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getPath_qiniu() {
        return path_qiniu;
    }

    public void setPath_qiniu(String path_qiniu) {
        this.path_qiniu = path_qiniu;
    }

    public String getPath_local() {
        return path_local;
    }

    public void setPath_local(String path_local) {
        this.path_local = path_local;
    }

    public boolean isAdditional() {
        return additional;
    }

    public void setAdditional(boolean additional) {
        this.additional = additional;
    }

    public boolean isUploaded_qiniu() {
        return uploaded_qiniu;
    }

    public void setUploaded_qiniu(boolean uploaded_qiniu) {
        this.uploaded_qiniu = uploaded_qiniu;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean isCanSystemPhoto() {
        return canSystemPhoto;
    }

    public void setCanSystemPhoto(boolean canSystemPhoto) {
        this.canSystemPhoto = canSystemPhoto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.key_id);
        dest.writeLong(this.task_id);
        dest.writeInt(this.category_id);
        dest.writeString(this.name);
        dest.writeString(this.alias);
        dest.writeString(this.thumbnail);
        dest.writeString(this.guide_image);
        dest.writeString(this.tips);
        dest.writeString(this.path_qiniu);
        dest.writeString(this.path_local);
        dest.writeByte(additional ? (byte) 1 : (byte) 0);
        dest.writeByte(uploaded_qiniu ? (byte) 1 : (byte) 0);
        dest.writeByte(modified ? (byte) 1 : (byte) 0);
        dest.writeByte(canSystemPhoto ? (byte) 1 : (byte) 0);
    }

    protected PhotoInfo(Parcel in) {
        this.key_id = in.readLong();
        this.task_id = in.readLong();
        this.category_id = in.readInt();
        this.name = in.readString();
        this.alias = in.readString();
        this.thumbnail = in.readString();
        this.guide_image = in.readString();
        this.tips = in.readString();
        this.path_qiniu = in.readString();
        this.path_local = in.readString();
        this.additional = in.readByte() != 0;
        this.uploaded_qiniu = in.readByte() != 0;
        this.modified = in.readByte() != 0;
        this.canSystemPhoto = in.readByte() != 0;
    }

    public static final Creator<PhotoInfo> CREATOR = new Creator<PhotoInfo>() {
        public PhotoInfo createFromParcel(Parcel source) {
            return new PhotoInfo(source);
        }

        public PhotoInfo[] newArray(int size) {
            return new PhotoInfo[size];
        }
    };
}
