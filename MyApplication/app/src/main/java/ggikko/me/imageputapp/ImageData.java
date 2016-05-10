package ggikko.me.imageputapp;

/**
 * Created by admin on 16. 5. 10..
 */
public class ImageData {

    private String file;

    public String getImageData() {
        return file;
    }

    public void setImageData(String imageData) {
        this.file = imageData;
    }

    public ImageData (String imageData){
        this.file = imageData;
    }
}
