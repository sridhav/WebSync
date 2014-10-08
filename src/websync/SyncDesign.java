/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package websync;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sridhar
 */
public class SyncDesign implements Runnable{
    ArrayList modTime;
    String _BASE="C:\\Users\\Sridhar\\";
    String _TO=null;
    public SyncDesign(String base, String to){
        modTime=new ArrayList<GeneralMap>();
        _BASE=base;
        _TO=to;
        File f=new File(_TO);
        f.mkdirs();
    }
    
    private void initiator(File base) {
        File[] f=base.listFiles();
        if(f!=null){
            for(File temp:f){
                if(temp==null){
                    continue;
                }
                if(temp.isFile()){
                    processFile(temp);
                }
                if(temp.isDirectory()){
                    initiator(temp);
                }
            }
        }
        Iterator<GeneralMap> it=modTime.iterator();
        while(it.hasNext()){
            GeneralMap m=it.next();
            if(!m.isThere){
                removeRemoteFile(m);
                it.remove();
            }
        }
    }
    
    private void processFile(File temp) {
        GeneralMap m=new GeneralMap(temp);
        if(!checkForMD5(m.getMD5())){
            modTime.add(m);
        }
    }
    
    private void iterativeCheck() {
        int count=0;
        while(true){
            for(int i=0;i<modTime.size();i++){
                GeneralMap m=(GeneralMap) modTime.get(i);
                String servP=m.stripAndAdd(_BASE, _TO);
                File servF=new File(servP);
                if(!servF.exists()){
                    String x=servF.getAbsolutePath();
                    x=x.substring(0, x.lastIndexOf("\\"));
                    File temp2=new File(x);
                    temp2.mkdirs();
                    try {
                        Files.copy(m.file.toPath(), servF.toPath());
                        m.isThere=true;
                    } catch (IOException ex) {
                        Logger.getLogger(SyncDesign.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else{
                    long lastModTime=m.getTime();
                    long nowModTime=m.file.lastModified();
                    if(lastModTime!=nowModTime){
                        try {
                            System.out.println("MODIFIED FILE" + m.file.toPath());
                            Files.copy(m.file.toPath(), servF.toPath());
                            m.time=m.file.lastModified();
                        } catch (IOException ex) {
                            // Logger.getLogger(SyncDesign.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
            System.out.println(_BASE+" SLEEP ######################################");
            try {
                Thread.sleep(1000);
                count++;
                if(count%5==0){
                    initiator(new File(_BASE));
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(SyncDesign.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private boolean checkForMD5(String mD5) {
        for(int i=0;i<modTime.size();i++){
            GeneralMap x=(GeneralMap) modTime.get(i);
            if(x.getMD5().equals(mD5)){
                x.isThere=true;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void run() {
        initiator(new File(_BASE));
        iterativeCheck();
    }

    private void removeRemoteFile(GeneralMap m) {
        String file=m.stripAndAdd(_BASE, _TO);
        File f=new File(file);
        f.delete();
        System.out.println(f.getAbsoluteFile()+"REMOTE FILE REMOVED");
    }
}
