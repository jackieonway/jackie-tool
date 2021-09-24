package com.github.jackieonway.util.ftp;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.github.jackieonway.util.collection.CollectionUtils;

/**
 * sftp工具类
 * @version 1.0
 */
public class SFTPUtils{
    private static Logger log = Logger.getLogger(SFTPUtils.class.getName());
    //服务器连接ip
    private String host;
    //用户名
    private String username;
    //密码
    private String password;
    //端口号
    private int port = 22;
    private ChannelSftp sftp = null;
    private Session sshSession = null;

    public SFTPUtils(){}
    
    /**
     * 带端口的构造方法
     * @param host 主机IP
     * @param port 端口号
     * @param username 用户名
     * @param password 密码
     */
    public SFTPUtils(String host, int port, String username, String password){
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    /**
     * 不带端口的构造方法 默认端口为22
     * @param host 主机Ip
     * @param username 用户名
     * @param password 密码
     */
    public SFTPUtils(String host, String username, String password){
        this.host = host;
        this.username = username;
        this.password = password;
    }

    /**
     * 通过SFTP连接服务器
     */
    public void connect(){
        try{
            JSch jsch = new JSch();
            sshSession = jsch.getSession(username, host, port);
            if (log.isInfoEnabled()){
                log.info("Session created.");
            }
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            if (log.isInfoEnabled()){
                log.info("Session connected.");
            }
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            if (log.isInfoEnabled()){
                log.info("Opening Channel.");
            }
            sftp = (ChannelSftp) channel;
            if (log.isInfoEnabled()){
                log.info("Connected to " + host + ".");
            }
        }catch (Exception e){
            log.error("Session Connected error.", e);
        }
    }

    /**
     * 关闭连接
     */
    private void disconnect(){
        if (this.sftp != null){
            if (this.sftp.isConnected()){
                this.sftp.disconnect();
                if (log.isInfoEnabled()){
                    log.info("sftp is closed already");
                }
            }
        }
        if (this.sshSession != null){
            if (this.sshSession.isConnected()){
                this.sshSession.disconnect();
                if (log.isInfoEnabled()){
                    log.info("sshSession is closed already");
                }
            }
        }
    }

    /**
     * 批量下载文件
     * @param remotePath：远程下载目录(以路径符号结束,可以为相对路径eg:/assess/sftp/)
     * @param localPath：本地保存目录(以路径符号结束,D:\Duansha\sftp\)
     * @param fileFormat：下载文件格式(以特定字符开头,为空不做检验)
     * @param fileEndFormat：下载文件格式(文件格式)
     * @param del：下载后是否删除sftp文件
     * @return files
     */
    public List<String> batchDownLoadFile(String remotePath, String localPath,
            String fileFormat, String fileEndFormat, boolean del) {
        List<String> filenames = new ArrayList<String>();
        try{
            Vector v = listFiles(remotePath);
            if (!CollectionUtils.isEmpty(v)) {
                log.info("本次处理文件个数不为零,开始下载...fileSize=" + v.size());
                for (Object o : v) {
                    LsEntry entry = (LsEntry) o;
                    String filename = entry.getFilename();
                    SftpATTRS attrs = entry.getAttrs();
                    if (!attrs.isDir()) {
                        boolean flag;
                        String localFileName = localPath + filename;
                        fileFormat = fileFormat == null ? "" : fileFormat
                                .trim();
                        fileEndFormat = fileEndFormat == null ? ""
                                : fileEndFormat.trim();
                        // 三种情况
                        if (fileFormat.length() > 0 && fileEndFormat.length() > 0) {
                            if (filename.startsWith(fileFormat) && filename.endsWith(fileEndFormat)) {
                                flag = downloadFile(remotePath, filename, localPath, filename);
                                if (flag) {
                                    filenames.add(localFileName);
                                    if (del) {
                                        deleteSFTP(remotePath, filename);
                                    }
                                }
                            }
                        } else if (fileFormat.length() > 0 && "".equals(fileEndFormat)) {
                            if (filename.startsWith(fileFormat)) {
                                flag = downloadFile(remotePath, filename, localPath, filename);
                                if (flag) {
                                    filenames.add(localFileName);
                                    if (del) {
                                        deleteSFTP(remotePath, filename);
                                    }
                                }
                            }
                        } else if (fileEndFormat.length() > 0 && "".equals(fileFormat)) {
                            if (filename.endsWith(fileEndFormat)) {
                                flag = downloadFile(remotePath, filename, localPath, filename);
                                if (flag) {
                                    filenames.add(localFileName);
                                    if (del) {
                                        deleteSFTP(remotePath, filename);
                                    }
                                }
                            }
                        } else {
                            flag = downloadFile(remotePath, filename, localPath, filename);
                            if (flag) {
                                filenames.add(localFileName);
                                if (del) {
                                    deleteSFTP(remotePath, filename);
                                }
                            }
                        }
                    }
                }
            }
            if (log.isInfoEnabled()){
                log.info("download file is success:remotePath=" + remotePath
                        + "and localPath=" + localPath + ",file size is"
                        + v.size());
            }
        }
        catch (SftpException e){
            log.error("download file error.", e);
        }
        return filenames;
    }

    /**
     * 下载单个文件
     * @param remotePath：远程下载目录(以路径符号结束)
     * @param remoteFileName：下载文件名
     * @param localPath：本地保存目录(以路径符号结束)
     * @param localFileName：保存文件名
     * @return result
     */
    public boolean downloadFile(String remotePath, String remoteFileName,String localPath, String localFileName){
        FileOutputStream fieloutput = null;
        try{
        	//本地保存路径不存在则创建本地保存路径
        	File fileDir = new File(localPath);
        	if (!fileDir.exists()) {
				if (fileDir.mkdirs()) {
					log.info("本地保存路径 "+localPath+" 创建成功");
				}else {
					log.info("本地保存路径 "+localPath+" 创建失败");
					return false;
				}
			}
            File file = new File(localPath + localFileName);
            // mkdirs(localPath + localFileName);
            fieloutput = new FileOutputStream(file);
            sftp.get(remotePath + remoteFileName, fieloutput);
            if (log.isInfoEnabled()){
                log.info("===DownloadFile:" + remoteFileName + " success from sftp.");
            }
            return true;
        }
        catch (Exception e){
            log.error("download file error.", e);
        }finally{
            if (null != fieloutput) {
                try{
                    fieloutput.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 下载单个文件输出到流
     * @param remotePath：远程下载目录(以路径符号结束)
     * @param remoteFileName：下载文件名
     * @param outputStream 输出流
     */
    public void downloadFileOs(String remotePath, String remoteFileName, OutputStream outputStream){
        try{
            sftp.get(remotePath + remoteFileName, outputStream);
            if (log.isInfoEnabled()){
                log.info("===DownloadFile:" + remoteFileName + " success from sftp.");
            }
        }
        catch (SftpException e){
            log.error("download file error.", e);
        }
    }

    /**
     * 上传单个文件
     * @param remotePath：远程保存目录
     * @param remoteFileName：保存文件名
     * @param localPath：本地上传目录(以路径符号结束)
     * @param localFileName：上传的文件名
     * @return result
     */
    public boolean uploadFile(String remotePath, String remoteFileName,String localPath, String localFileName){
        FileInputStream in = null;
        try{
            createDir(remotePath);
            File file = new File(localPath + localFileName);
            in = new FileInputStream(file);
            sftp.put(in, remoteFileName);
            return true;
        }catch (Exception e){
            log.error("upload file error.", e);
        }finally{
            if (in != null){
                try{
                    in.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 上传单个文件
     * @param remotePath：远程保存目录
     * @param remoteFileName：保存文件名
     * @param is 文件输入流
     * @return result
     */
    public boolean uploadFile(String remotePath, String remoteFileName,InputStream is){
        try{
            createDir(remotePath);
            sftp.put(is, remoteFileName);
            return true;
        }catch (SftpException e){
            log.error("upload file error.", e);
        }
        return false;
    }
    
    /**
     * 批量上传文件
     * @param remotePath：远程保存目录
     * @param localPath：本地上传目录(以路径符号结束)
     * @param del：上传后是否删除本地文件
     * @return result
     */
    public boolean batchUploadFile(String remotePath, String localPath, boolean del){
        try{
            connect();
            File file = new File(localPath);
            File[] files = file.listFiles();
            assert files != null;
            for (File file1 : files) {
                if (file1.isFile()
                        && !file1.getName().contains("bak")) {
                    if (this.uploadFile(remotePath, file1.getName(),
                            localPath, file1.getName())
                            && del) {
                        deleteFile(localPath + file1.getName());
                    }
                }
            }
            if (log.isInfoEnabled()){
                log.info("upload file is success:remotePath=" + remotePath
                        + "and localPath=" + localPath + ",file size is "
                        + files.length);
            }
            return true;
        }catch (Exception e){
            log.error("upload file error.", e);
        }finally{
            this.disconnect();
        }
        return false;
    }

    /**
     * 删除本地文件
     * @param filePath filePath
     * @return result
     */
    public boolean deleteFile(String filePath){
        File file = new File(filePath);
        if (!file.exists()){
            return false;
        }
        if (!file.isFile()){
            return false;
        }
        boolean rs = file.delete();
        if (rs && log.isInfoEnabled()){
            log.info("delete file success from local.");
        }
        return rs;
    }

    /**
     * 创建目录
     * @param createpath createpath
     * @return result
     */
    public boolean createDir(String createpath){
        try{
        	//判断远程目录是否存在
        	//若存在则切换到该目录下
        	//若不存在则创建该目录并切换到该目录下
            if (isDirExist(createpath)){
            	//切换到远程目录下
                this.sftp.cd(createpath);
                return true;
            }
            String[] pathArray = createpath.split("/");
            StringBuilder filePath = new StringBuilder("/");
            for (String path : pathArray){
                if ("".equals(path)){
                    continue;
                }
                filePath.append(path).append("/");
                if (isDirExist(filePath.toString())){
                    sftp.cd(filePath.toString());
                }else{
                    // 建立目录
                    sftp.mkdir(filePath.toString());
                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }
            }
            this.sftp.cd(createpath);
            return true;
        }catch (SftpException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断远程目录是否存在
     * @param directory directory
     * @return dir is exist or not
     */
    public boolean isDirExist(String directory){
        boolean isDirExistFlag = false;
        try{
            SftpATTRS sftpAttrs = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpAttrs.isDir();
        }catch (Exception e){
            if ("no such file".equalsIgnoreCase(e.getMessage())){
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }

    /**
     * 删除stfp文件
     * @param remoteDirectory：要删除文件所在目录
     * @param deleteRemoteFile：要删除的文件
     */
    public void deleteSFTP(String remoteDirectory, String deleteRemoteFile){
        try{
            sftp.rm(remoteDirectory + deleteRemoteFile);
            if (log.isInfoEnabled()){
                log.info("delete remote file success from sftp.");
            }
        }catch (Exception e){
            log.error("delete remote file error.", e);
        }
    }

    /**
     * 列出目录下的文件
     * 
     * @param directory：要列出的目录
     * @return files
     * @throws SftpException exception
     */
    public Vector listFiles(String directory) throws SftpException{
        return sftp.ls(directory);
    }

    public String getHost(){
        return host;
    }

    public void setHost(String host){
        this.host = host;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public ChannelSftp getSftp(){
        return sftp;
    }

    public void setSftp(ChannelSftp sftp){
        this.sftp = sftp;
    }
}

