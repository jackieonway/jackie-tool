package com.github.jackieonway.util.http;

import javax.servlet.http.HttpServletRequest;

public enum  WebUtils {
        /**
         * WebUtils 实例
         */
        INSTANCE;

        /**
         * Get the request IP
         *
         * @param request request
         * @return IP
         */
        public static String getIpAddr(HttpServletRequest request) {
            String ip = "0.0.0.0";
            ip = request.getHeader("x-forwarded-for");
            if (isInvalidIP(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (isInvalidIP(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (isInvalidIP(ip)) {
                ip = request.getRemoteAddr();
            }
            // get X-real-ip from nginx
            if (isInvalidIP(ip)) {
                ip = request.getHeader("X-real-ip");
            }
            if (isInvalidIP(ip) && null != request.getAttribute("X-real-ip")) {
                ip = request.getAttribute("X-real-ip").toString();
            }
            if (null == ip) {
                ip = "unknown";
            }
            return ip;
        }

        public static boolean isInvalidIP(String ip) {
            return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip) || "127.0.0.1".equals(ip);
        }

        /**
         * Get the base path
         *
         * @param request request
         * @return base path
         */
        public static String getBasePath(HttpServletRequest request) {
            String path = request.getContextPath();
            return request.getScheme() + "://"
                    + request.getServerName() + ":" + request.getServerPort()
                    + path;
        }

        /**
         * Get the base path haven't port
         *
         * @param request request
         * @return base path haven't port
         */
        public static String getBasePathNotPort(HttpServletRequest request) {
            String path = request.getContextPath();
            return request.getScheme() + "://"
                    + request.getServerName() + path;
        }

        /**
         * Get the url
         *
         * @param request request
         * @return url
         */
        public static String getUrl(HttpServletRequest request) {
            return request.getRequestURL().toString();
        }

        /**
         * Get the context path
         *
         * @param request request
         * @return context path
         */
        public static String getContextPath(HttpServletRequest request) {
            return request.getContextPath();
        }

        public static String getUserAgent(HttpServletRequest request) {
            return request.getHeader("user-agent");
        }

        public static String getMethod(HttpServletRequest request) {
            return request.getMethod();
        }
}