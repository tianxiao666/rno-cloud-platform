package com.hgicreate.rno.util;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * @author tao.xj
 */
public class ZipFileHandler {
    private static final int BUFFER = 1024;
 
    private static void createDirectory(String directory, String subDirectory) {
        String[] dir;
        File fl = new File(directory);
        try {
            if ("".equals(subDirectory) && !fl.exists() ) {
                fl.mkdir();
            } else if (!("".equals(subDirectory))) {
                dir = subDirectory.replace('\\', '/').split("/");
                for (int i = 0; i < dir.length; i++) {
                    File subFile = new File(directory + File.separator + dir[i]);
                    if (!subFile.exists()) {
                        subFile.mkdir();
                    }
                    directory += File.separator + dir[i];
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
 
    @SuppressWarnings("unchecked")
    public static boolean unZip(String zipFilePath, String outputDirectory) {
        boolean flag = false;
        try {
            ZipFile zipFile = new ZipFile(zipFilePath,"gbk");
            Enumeration e = zipFile.getEntries();
            ZipEntry zipEntry;
            createDirectory(outputDirectory, "");
            while (e.hasMoreElements()) {
                zipEntry = (ZipEntry) e.nextElement();
                if (zipEntry.isDirectory()) {
                    String name = new String(zipEntry.getName().getBytes("gbk"),"utf-8").trim();
                    name = name.substring(0, name.length() - 1);
                    File f = new File(outputDirectory + File.separator + name);
                    if (!f.exists()) {
                        f.mkdir();
                    }
 
                } else {
                    String fileName = new String(zipEntry.getName());
                    fileName = fileName.replace('\\', '/');
                    if (fileName.indexOf("/") != -1) {
                        createDirectory(outputDirectory, fileName.substring(0,
                                fileName.lastIndexOf("/")));
                    }
                    File f = new File(outputDirectory + File.separator
                            + zipEntry.getName());
                    f.createNewFile();
                    InputStream in = zipFile.getInputStream(zipEntry);
                    FileOutputStream out = new FileOutputStream(f);
                    byte[] by = new byte[BUFFER];
                    int c;
                    while ((c = in.read(by)) != -1) {
                        out.write(by, 0, c);
                    }
                    in.close();
                    out.close();
                }
            }
            flag = true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }       
        return flag;
    }
 
    
    
    public static boolean zip(String srcDirName,String excludes, String zipFilePath) {
        boolean flag;
        try {
            File srcdir = new File(srcDirName);
            if (!srcdir.exists()){
                throw new RuntimeException(srcDirName + " is not exist!");
            }
 
            Project prj = new Project();
            Zip zip = new Zip();
            zip.setProject(prj);
            zip.setDestFile(new File(zipFilePath));
 
            FileSet fileSet = new FileSet();
            fileSet.setProject(prj);
            fileSet.setDir(srcdir);
            fileSet.setExcludes(excludes);
            zip.addFileset(fileSet);
 
            zip.execute();
            flag = true;
        } catch (Exception ex) {
        	ex.printStackTrace();
        	flag=false;
        }
        return flag;
    }
}
