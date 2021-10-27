/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photo_album_project;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Date;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;

/**
 *
 * @author 44280
 */
public class Post {

    private byte[] photo;
    private String caption;
    private Date date;
    private ImageView imageView;
    private int id;

    public Post(byte[] photo, String caption, Date date) {
        this.photo = photo;
        this.caption = caption;
        this.date = date;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(photo);
            BufferedImage bImage = ImageIO.read(bais);
            Image image = SwingFXUtils.toFXImage(bImage, null);
            imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Post(byte[] photo, String caption, Date date, int id) {
        this.photo = photo;
        this.caption = caption;
        this.date = date;
        this.id = id;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(photo);
            BufferedImage bImage = ImageIO.read(bais);
            Image image = SwingFXUtils.toFXImage(bImage, null);
            imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ImageView getImageView() {
        return imageView;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    public int getId() {
        return id;
    }

}
