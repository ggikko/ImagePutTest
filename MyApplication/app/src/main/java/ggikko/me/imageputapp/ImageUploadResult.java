package ggikko.me.imageputapp;

/**
 * Created by admin on 16. 5. 10..
 */
public class ImageUploadResult {

    public String errmsg;

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public ImageUploadResult(String errmsg) {
        this.errmsg = errmsg;
    }
}
