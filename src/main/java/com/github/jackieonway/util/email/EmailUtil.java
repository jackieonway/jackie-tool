package com.github.jackieonway.util.email;

import com.sun.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author Jackie
 */
public enum  EmailUtil {

    /**
     * EmailUtil 实例
     */
    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(EmailUtil.class);

    /**
     * 初始化参数
     */
    private static Properties initProperties(MailConfig mailConfig) {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", mailConfig.getProtocol());
        properties.setProperty("mail.smtp.host", mailConfig.getHost());
        properties.setProperty("mail.smtp.port", mailConfig.getPort());
        // 使用smtp身份验证
        properties.put("mail.smtp.auth", "true");
        if (mailConfig.isSsl()){
            // 使用SSL,企业邮箱必需  开启安全协议
            MailSSLSocketFactory mailSslSocketFactory = null;
            try {
                mailSslSocketFactory = new MailSSLSocketFactory();
                mailSslSocketFactory.setTrustAllHosts(true);
            } catch (GeneralSecurityException e) {
                log.error("获取 MailSSLSocketFactory 失败", e);
            }
            properties.put("mail.smtp.ssl.socketFactory", mailSslSocketFactory);
            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            properties.put("mail.smtp.ssl.checkserveridentity", true);
        }
        properties.put("mail.smtp.enable", "true");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.put("mail.smtp.socketFactory.port", mailConfig.getPort());
        return properties;
    }
    public static void send(MailConfig mailConfig, MailAccount mailAccount, MailContent mailContent)
            throws IOException, MessagingException {
        Session session = initSession(mailAccount.getSender(), mailAccount.getPassword(), mailConfig);
        MimeMessage mimeMessage = new MimeMessage(session);
        // 发件人,可以设置发件人的别名
        mimeMessage.setFrom(new InternetAddress(mailAccount.getSender(), mailAccount.getSenderName()));
        // 收件人,多人接收
        InternetAddress[] internetAddressTo = InternetAddress.parse(mailAccount.getReceiverList());
        mimeMessage.setRecipients(Message.RecipientType.TO, internetAddressTo);
        // 主题
        mimeMessage.setSubject(mailContent.getSubject());
        // 时间
        mimeMessage.setSentDate(new Date());
        // 容器类 附件
        MimeMultipart mimeMultipart = new MimeMultipart();
        if (mailContent.isHtml()){
            addHtml(mailContent.getContent(), mimeMultipart);
        }else {
            addText(mailContent.getContent(), mimeMultipart);
        }
        addFiles(mailContent.getFiles(), mimeMultipart);
        mimeMessage.setContent(mimeMultipart);
        mimeMessage.saveChanges();
        Transport.send(mimeMessage);
    }

    private static void addFiles(List<File> fileList, MimeMultipart mimeMultipart) throws MessagingException, IOException {
        if (CollectionUtils.isEmpty(fileList)){
            return;
        }
        for (File file : fileList) {
            MimeBodyPart bodyPart = new MimeBodyPart();
            // 设置内容
            bodyPart.attachFile(file);
            mimeMultipart.addBodyPart(bodyPart);
        }
    }

    private static void addHtml(String content, MimeMultipart mimeMultipart) throws MessagingException {
        // 可以包装文本,图片,附件
        MimeBodyPart bodyPart = new MimeBodyPart();
        // 设置内容
        bodyPart.setContent(content, "text/html; charset=UTF-8");
        mimeMultipart.addBodyPart(bodyPart);
    }

    private static void addText(String content, MimeMultipart mimeMultipart) throws MessagingException {
        // 可以包装文本,图片,附件
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(content, StandardCharsets.UTF_8.name());
        mimeMultipart.addBodyPart(bodyPart);
    }

    public static Session initSession(String account, String password, MailConfig mailConfig) {
        Session session = Session.getDefaultInstance(initProperties(mailConfig), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, password);
            }
        });
        // 显示debug信息 正式环境注释掉
        session.setDebug(true);
        return session;
    }

    public static class MailConfig {

        /**
         * 主机地址
         */
        private String host;

        /**
         * 端口
         */
        private String port;

        /**
         * 协议
         */
        private String protocol;

        private boolean ssl;

        public MailConfig(String host, String port, String protocol, boolean ssl) {
            this.host = host;
            this.port = port;
            this.protocol = protocol;
            this.ssl = ssl;
        }

        public MailConfig() {
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public boolean isSsl() {
            return ssl;
        }

        public void setSsl(boolean ssl) {
            this.ssl = ssl;
        }
    }

    public static class MailAccount{

        /**
         * 发送人账号
         */
        private String sender;

        /**
         * 发送人别名
         */
        private String senderName;

        /**
         * 发送人密码
         */
        private String password;

        /**
         * 接收人列表 用英文逗号分隔
         */
        private String receiverList;

        public MailAccount(String sender, String senderName, String password, String receiverList) {
            this.sender = sender;
            this.senderName = senderName;
            this.password = password;
            this.receiverList = receiverList;
        }

        public MailAccount() {
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getReceiverList() {
            return receiverList;
        }

        public void setReceiverList(String receiverList) {
            this.receiverList = receiverList;
        }
    }

    public static class MailContent{

        /**
         * 发送主题
         */
        private String subject;

        /**
         * 发送内容
         */
        private String content;

        /**
         * 是否是HTML
         */
        private boolean html;

        /**
         * 附件列表
         */
        private List<File> files;

        public MailContent(String subject, String content, boolean html, List<File> files) {
            this.subject = subject;
            this.content = content;
            this.html = html;
            this.files = files;
        }

        public MailContent() {
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public boolean isHtml() {
            return html;
        }

        public void setHtml(boolean html) {
            this.html = html;
        }

        public List<File> getFiles() {
            return files;
        }

        public void setFiles(List<File> files) {
            this.files = files;
        }
    }
}
