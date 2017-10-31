package com.fanchen.imovie.entity.apk;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanchen.imovie.dialog.ShowImagesDialog;
import com.fanchen.imovie.entity.face.IViewType;
import com.fanchen.imovie.util.DateUtil;

import java.util.List;

/**
 * Created by fanchen on 2017/3/22.
 */
public class ApkDetails implements Parcelable {

    public static final int HAS_VIDEO = 1;

    private String id;
    private String title;
    private String type;
    private String intro;
    private String size;
    private String recentchanges;
    private String description;
    private String translatedescription;
    private String transname;
    private String developeremail;
    private String developername;
    private String developerwebsite;
    private String currentversioncode;
    private String currentversionname;
    private String uploaddate;
    private String playavailablestatus;
    private String free;
    private String price;
    private String hasheaderimage;
    private String hasdata;
    private int hasvideo;
    private String videourl;
    private String privacypolicyurl;
    private String updatetime;
    private String clicksum;
    private String downloadsum;
    private String iconurl;
    private String headerurl;
    private String packageName;
    private List<ScreenShots> screen_shots;
    private List<Files> files;
    private List<String> article;
    private List<Marks> marks;

    public ApkDetails() {

    }

    public ApkDetails(Parcel in) {
        id = in.readString();
        title = in.readString();
        type = in.readString();
        intro = in.readString();
        recentchanges = in.readString();
        description = in.readString();
        translatedescription = in.readString();
        transname = in.readString();
        developeremail = in.readString();
        developername = in.readString();
        developerwebsite = in.readString();
        currentversioncode = in.readString();
        currentversionname = in.readString();
        uploaddate = in.readString();
        playavailablestatus = in.readString();
        free = in.readString();
        price = in.readString();
        hasheaderimage = in.readString();
        hasdata = in.readString();
        hasvideo = in.readInt();
        videourl = in.readString();
        privacypolicyurl = in.readString();
        updatetime = in.readString();
        clicksum = in.readString();
        downloadsum = in.readString();
        iconurl = in.readString();
        headerurl = in.readString();
        packageName = in.readString();
        article = in.createStringArrayList();
        size = in.readString();
    }

    public static final Creator<ApkDetails> CREATOR = new Creator<ApkDetails>() {
        @Override
        public ApkDetails createFromParcel(Parcel in) {
            return new ApkDetails(in);
        }

        @Override
        public ApkDetails[] newArray(int size) {
            return new ApkDetails[size];
        }
    };

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getIntro() {
        return intro;
    }

    public void setRecentchanges(String recentchanges) {
        this.recentchanges = recentchanges;
    }

    public String getRecentchanges() {
        return recentchanges;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setTranslatedescription(String translatedescription) {
        this.translatedescription = translatedescription;
    }

    public String getTranslatedescription() {
        return translatedescription;
    }

    public void setTransname(String transname) {
        this.transname = transname;
    }

    public String getTransname() {
        return transname;
    }

    public void setDeveloperemail(String developeremail) {
        this.developeremail = developeremail;
    }

    public String getDeveloperemail() {
        return developeremail;
    }

    public void setDevelopername(String developername) {
        this.developername = developername;
    }

    public String getDevelopername() {
        return developername;
    }

    public void setDeveloperwebsite(String developerwebsite) {
        this.developerwebsite = developerwebsite;
    }

    public String getDeveloperwebsite() {
        return developerwebsite;
    }

    public void setCurrentversioncode(String currentversioncode) {
        this.currentversioncode = currentversioncode;
    }

    public String getCurrentversioncode() {
        return currentversioncode;
    }

    public void setCurrentversionname(String currentversionname) {
        this.currentversionname = currentversionname;
    }

    public String getCurrentversionname() {
        return currentversionname;
    }

    public void setUploaddate(String uploaddate) {
        this.uploaddate = uploaddate;
    }

    public String getUploaddate() {
        return uploaddate;
    }

    public void setPlayavailablestatus(String playavailablestatus) {
        this.playavailablestatus = playavailablestatus;
    }

    public String getPlayavailablestatus() {
        return playavailablestatus;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public String getFree() {
        return free;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public void setHasheaderimage(String hasheaderimage) {
        this.hasheaderimage = hasheaderimage;
    }

    public String getHasheaderimage() {
        return hasheaderimage;
    }

    public void setHasdata(String hasdata) {
        this.hasdata = hasdata;
    }

    public String getHasdata() {
        return hasdata;
    }

    public void setHasvideo(int hasvideo) {
        this.hasvideo = hasvideo;
    }

    public int getHasvideo() {
        return hasvideo;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setPrivacypolicyurl(String privacypolicyurl) {
        this.privacypolicyurl = privacypolicyurl;
    }

    public String getPrivacypolicyurl() {
        return privacypolicyurl;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public String getUpdatetime() {
        try{
            String format = DateUtil.format(Long.valueOf(updatetime) * 1000, "yyyy-MM-dd");
            return format;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public void setClicksum(String clicksum) {
        this.clicksum = clicksum;
    }

    public String getClicksum() {
        return clicksum;
    }

    public void setDownloadsum(String downloadsum) {
        this.downloadsum = downloadsum;
    }

    public String getDownloadsum() {
        return downloadsum;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setHeaderurl(String headerurl) {
        this.headerurl = headerurl;
    }

    public String getHeaderurl() {
        return headerurl;
    }

    public void setPackagename(String packagename) {
        this.packageName = packagename;
    }

    public String getPackagename() {
        return packageName;
    }

    public void setScreenShots(List<ScreenShots> screenShots) {
        this.screen_shots = screenShots;
    }

    public List<ScreenShots> getScreenShots() {
        if (screen_shots != null){
            for (ScreenShots sc : screen_shots){
                sc.setPackageName(packageName);
            }
        }
        return screen_shots;
    }

    public void setFiles(List<Files> files) {
        this.files = files;
    }

    public List<Files> getFiles() {
        return files;
    }

    public void setArticle(List<String> article) {
        this.article = article;
    }

    public List<String> getArticle() {
        return article;
    }

    public void setMarks(List<Marks> marks) {
        this.marks = marks;
    }

    public List<Marks> getMarks() {
        return marks;
    }

    public String getSize() {
        return size;
    }

    public String getIco() {
        return String.format("http://cdn.moeapk.com/statics/apk/%s/%s.thumbnail?%s", packageName, packageName, currentversioncode);
    }

    public String getCover() {
        return String.format("http://cdn.moeapk.com/statics/apk/%s/header.image?%s", packageName, currentversioncode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(intro);
        dest.writeString(recentchanges);
        dest.writeString(description);
        dest.writeString(translatedescription);
        dest.writeString(transname);
        dest.writeString(developeremail);
        dest.writeString(developername);
        dest.writeString(developerwebsite);
        dest.writeString(currentversioncode);
        dest.writeString(currentversionname);
        dest.writeString(uploaddate);
        dest.writeString(playavailablestatus);
        dest.writeString(free);
        dest.writeString(price);
        dest.writeString(hasheaderimage);
        dest.writeString(hasdata);
        dest.writeInt(hasvideo);
        dest.writeString(videourl);
        dest.writeString(privacypolicyurl);
        dest.writeString(updatetime);
        dest.writeString(clicksum);
        dest.writeString(downloadsum);
        dest.writeString(iconurl);
        dest.writeString(headerurl);
        dest.writeString(packageName);
        dest.writeStringList(article);
        dest.writeString(size);
    }


    /**
     * Created by fanchen on 2017/3/22.
     */
    public static class ScreenShots implements ShowImagesDialog.IPhotoImage, IViewType {

        private String id;
        private String pid;
        private String filename;
        private String sourceurl;
        private String rank;
        private String auto;
        private String packageName;

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getPid() {
            return pid;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getFilename() {
            return filename;
        }

        public void setSourceurl(String sourceurl) {
            this.sourceurl = sourceurl;
        }

        public String getSourceurl() {
            return String.format("http://cdn.moeapk.com/statics/images/app/%s/%s.thumbnail",packageName,filename);
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getRank() {
            return rank;
        }

        public void setAuto(String auto) {
            this.auto = auto;
        }

        public String getAuto() {
            return auto;
        }

        @Override
        public String getCover() {
            return String.format("http://cdn.moeapk.com/statics/images/app/%s/%s.image", packageName, filename);
        }

        @Override
        public int getViewType() {
            return IViewType.TYPE_NORMAL;
        }
    }

    public static class Files {

        private String id;
        private String pid;
        private String filenamefix;
        private String title;
        private String versioncode;
        private String versionname;
        private String md5;
        private String uploadtime;
        private String size;
        private String packageName;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getPid() {
            return pid;
        }

        public void setFilenamefix(String filenamefix) {
            this.filenamefix = filenamefix;
        }

        public String getFilenamefix() {
            return filenamefix;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setVersioncode(String versioncode) {
            this.versioncode = versioncode;
        }

        public String getVersioncode() {
            return versioncode;
        }

        public void setVersionname(String versionname) {
            this.versionname = versionname;
        }

        public String getVersionname() {
            return versionname;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getMd5() {
            return md5;
        }

        public void setUploadtime(String uploadtime) {
            this.uploadtime = uploadtime;
        }

        public String getUploadtime() {
            return uploadtime;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getSize() {
            return size;
        }

        public void setPackagename(String packagename) {
            this.packageName = packagename;
        }

        public String getPackagename() {
            return packageName;
        }

    }

    public static class Marks {

        private String id;
        private String pid;
        private String mid;
        private String auto;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getPid() {
            return pid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getMid() {
            return mid;
        }

        public void setAuto(String auto) {
            this.auto = auto;
        }

        public String getAuto() {
            return auto;
        }

    }
}
