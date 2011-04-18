package com.taobao.top.pageapi.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class WhiteListDomainService {
    private static final Log log = LogFactory.getLog(WhiteListDomainService.class);
    private List<String> whiteListDomain;
    private String filePath;
    private static String FILE_NAME = "pageApiWhiteListDomain.txt";
    private List<String> prefixs;


    public void init() {
        prefixs = Arrays.asList(new String[] {"com.cn","net.cn","org.cn","gov.cn"});
        // 初始化时，做一次加载
        if (whiteListDomain == null || whiteListDomain.isEmpty()) {
            loadWhiteList();
        }
    }


    /**
     * 判断给定的url，域名是否在白名单里面
     * 
     * @param url
     * @return true是合法，false不合法
     */
    public boolean isInWhiteList(String url) {
        // 若url未传或者内部路径，则不验证
        if (url == null || (!url.startsWith("http://") && !url.startsWith("https://"))) {
            return true;
        }

        if (whiteListDomain == null || whiteListDomain.isEmpty()) {
            loadWhiteList();
        }

        String domain = parseDomain(url);

        return (whiteListDomain != null && whiteListDomain.contains(domain));
    }


    private String parseDomain(String url) {
        String servletPath = null;
        if(url.startsWith("http://")) {
            servletPath = url.substring("http://".length());
        }else if(url.startsWith("https://")) {
            servletPath = url.substring("https://".length());
        }

        // 就一个http://的情况
        if (StringUtils.isBlank(servletPath)) {
            return null;
        }

        String fullDomain = null;
        if (servletPath.contains("/")) {
            // 包含子路径的情况。例如：www.item.taobao.com/xx
            fullDomain = servletPath.substring(0, servletPath.indexOf("/"));
        }
        else {
            // 不包含子路径的情况。例如：www.item.taobao.com
            fullDomain = servletPath.substring(0);
        }
        boolean isMutilPrefix = false;
        if(prefixs != null && StringUtils.isNotBlank(fullDomain)) {
            for (String prefix : prefixs) {
                if(fullDomain.endsWith(prefix)) {
                    isMutilPrefix = true;
                    break;
                }
            }
        }
        String[] strs = fullDomain.split("\\.");
        if (strs.length >= 2) {
            if(isMutilPrefix && strs.length > 3) {
                return strs[strs.length - 3] + "." + strs[strs.length - 2] + "." + strs[strs.length - 1];
            }else {
                return strs[strs.length - 2] + "." + strs[strs.length - 1];
            }
        }
        return fullDomain;
    }


    /**
     * 读取文件
     * 
     */
    private synchronized void loadWhiteList() {
        String fullFileName = filePath + File.separator + FILE_NAME;
        File file = new File(fullFileName);

        if (!file.exists()) {
            return;
        }
        whiteListDomain = new ArrayList<String>();
        // 文件已经存在
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                if (StringUtils.isNotBlank(line)) {
                    whiteListDomain.add(line);
                }
                line = reader.readLine();
            }
        }
        catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    if (log.isErrorEnabled()) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }


    public void setWhiteListDomain(List<String> whiteListDomain) {
        this.whiteListDomain = whiteListDomain;
        // 保存到文件
        save2File(whiteListDomain);
    }


    /**
     * 将域名保存到文件
     * 
     * @param whiteListDomain
     */
    private void save2File(List<String> whiteListDomain) {
        if (whiteListDomain == null || whiteListDomain.isEmpty()) {
            return;
        }
        String fullFileName = filePath + File.separator + FILE_NAME;
        File file = new File(fullFileName);
        BufferedWriter writer = null;

        try {
            // 若文件不存在，则创建文件
            if (!file.exists() && !file.createNewFile()) {
                if (log.isErrorEnabled()) {
                    log.error(fullFileName + " createNewFile failed!");
                }
                return;
            }

            // 循环写入
            writer = new BufferedWriter(new FileWriter(file));
            for (String domain : whiteListDomain) {
                writer.write(domain + "\n");
            }
        }
        catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
        finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                }
                catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }


    public List<String> getWhiteListDomain() {
        return whiteListDomain;
    }


    public static void main(String[] args) {
        WhiteListDomainService service = new WhiteListDomainService();
        service.init();
        System.out.println("localhost:8080".equals(service.parseDomain("http://localhost:8080")));
        System.out.println("www.taobao".equals(service.parseDomain("http://www.taobao")));
        System.out.println("taobao.com".equals(service.parseDomain("http://www.taobao.com")));
        System.out.println("taobao.com".equals(service.parseDomain("http://www.item.taobao.com")));
        System.out.println("taobao.com".equals(service.parseDomain("http://www.taobao.com/item")));
        System.out.println("taobao.com".equals(service.parseDomain("http://www.item.taobao.com/item")));
        System.out.println("taobao.com".equals(service.parseDomain("http://www.taobao.com/item.get")));
        System.out.println("taobao.com".equals(service.parseDomain("http://www.taobao.com/api/item.get")));
        System.out.println("taobao.com.cn".equals(service.parseDomain("http://www.taobao.com.cn/api/item.get")));
        System.out.println("net.cn".equals(service.parseDomain("http://www.net.cn/api/item.get")));
        System.out.println("net.cn".equals(service.parseDomain("https://www.net.cn/api/item.get")));
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public String getFilePath() {
        return filePath;
    }


    public void setPrefixs(List<String> prefixs) {
        this.prefixs = prefixs;
    }


    public List<String> getPrefixs() {
        return prefixs;
    }
}
