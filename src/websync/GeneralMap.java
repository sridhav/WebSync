/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websync;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sridhar
 */
public class GeneralMap {
    
    String MD5;
    long time;
    File file;
    boolean isThere=false;
    public GeneralMap(File file){
        this.file=file;
        this.MD5=generateMD5();
        this.time=file.lastModified();
    }
    
    public long getTime(){
        return time;
    }
    
    public String getMD5(){
        return MD5;
    }
    
    private String generateMD5(){
        String MD5att=file.getAbsolutePath();
        String x=null;
        try {
            byte[] b=MD5att.getBytes("UTF-8");
            MessageDigest md=MessageDigest.getInstance("MD5");
            byte[] dig=md.digest(b);
            x=new String(dig, "UTF-8");

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GeneralMap.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(GeneralMap.class.getName()).log(Level.SEVERE, null, ex);
        }
        return x;
    }
    
    public String stripAndAdd(String winPath, String servPath){
        String name=file.getAbsolutePath();
        name=name.replace(winPath, servPath);
        return name;
    }
}
