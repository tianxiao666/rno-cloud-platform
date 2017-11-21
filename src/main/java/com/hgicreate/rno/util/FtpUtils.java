package com.hgicreate.rno.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * FTP 工具类
 */
@Slf4j
public final class FtpUtils {
    /**
     * 把文件发送到FTP服务器
     *
     * @param moduleName      所属模块名
     * @param localFullPath   文件在本地的全路径
     * @param deleteAfterSent 发送完成后是否删除本地文件
     * @param env             Spring 环境变量，用于获取配置文件中的属性值
     * @return 返回通过FTP获取文件的全路径，包含FTP协议前缀和主机信息。如果处理失败，则返回 null
     */
    public static String sendToFtp(String moduleName, String localFullPath, Boolean deleteAfterSent, Environment env) {
        String host = env.getProperty("rno.ftp.host", "localhost");
        int port = Integer.parseInt(env.getProperty("rno.ftp.port", "21"));
        String username = env.getProperty("rno.ftp.username", "anonymous");
        String password = env.getProperty("rno.ftp.password", "");
        String remotePath = env.getProperty("rno.ftp.remote-path", "");

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(host, port);
            ftpClient.login(username, password);

            File file = new File(localFullPath);
            String filename = file.getName();
            FileInputStream fis = new FileInputStream(file);

            String remoteModulePath = remotePath + "/" + moduleName;

            // 切换到远程目录
            if (!ftpClient.changeWorkingDirectory(remoteModulePath)) {
                // 如果不成功，则创建远程目录
                if (ftpClient.makeDirectory(remoteModulePath)) {
                    ftpClient.changeWorkingDirectory(remoteModulePath);
                } else {
                    // 创建目录失败，返回 null
                    return null;
                }
            }

            boolean bool = ftpClient.storeFile(filename, fis);
            fis.close();

            if (bool) {
                log.debug("文件发送到FTP成功。");
                if (deleteAfterSent) {
                    if (file.delete()) {
                        log.debug("文件删除成功。");
                    } else {
                        log.debug("文件删除失败。");
                    }
                }
                return
//                        "ftp:///" + username + ":" + password +
//                        "@" + host + ":" + port + "/" +
                          remoteModulePath + "/" + filename;
            } else {
                log.error("文件发送到FTP失败。");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
