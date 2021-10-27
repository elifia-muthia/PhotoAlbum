/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photo_album_project;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 44280
 */
public class Album {

    Connection conn = null;
    Statement stat = null;
    ResultSet res = null;

    public Album() {
        try {
            connectDB();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectDB() throws ClassNotFoundException {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/photo_album", "root", "");
            stat = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean addPost(Post post) {
        boolean success = true;
        ByteArrayInputStream bais = new ByteArrayInputStream(post.getPhoto());
        try {
            String sql = "INSERT INTO post(photo, caption, date) VALUES "
                    + "(?, \'" + post.getCaption() + "\', ?)";
            PreparedStatement pstat = conn.prepareStatement(sql);
            pstat.setBlob(1, bais);
            pstat.setDate(2, new java.sql.Date(post.getDate().getTime()));
            pstat.execute();
            return success;
        } catch (Exception e) {
            return !success;
        }
    }
    
    public void updatePosts(List<Post> posts) {
        try {
            for (int i = 0; i < posts.size(); i++) {
                // check sql
                String sql = "UPDATE post SET caption = \'" + posts.get(i).getCaption() 
                        + "\', date = ? WHERE id = " + posts.get(i).getId();
                PreparedStatement pstat = conn.prepareStatement(sql);
                pstat.setDate(1, new java.sql.Date(posts.get(i).getDate().getTime()));
                pstat.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePost(Post post) {
        try {
            stat.executeUpdate("DELETE FROM post WHERE id = " + post.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Post> getPosts() {
        ArrayList<Post> allPosts = new ArrayList<Post>();
        try {
            res = stat.executeQuery("select * from post order by date desc");
            while (res.next()) {
                Blob blobImage = res.getBlob("photo");
                byte[] photo = null;
                if (blobImage != null) {
                    photo = blobImage.getBytes(1, (int) blobImage.length());
                }
                String caption = res.getString("caption");
                Date date = new java.util.Date(res.getDate("date").getTime());
                int id = res.getInt("id");
                allPosts.add(new Post(photo, caption, date, id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allPosts;
    }

}
