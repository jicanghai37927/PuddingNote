package com.haiyunshan.pudding.ftp;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.impl.DefaultFtpServer;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.listener.ListenerFactory;

import java.io.File;

public class FtpHelper {

    File mDir;
    int mPort;

    FtpServer mFtpServer;

    public FtpHelper(File dir) {
        this(dir, 6666);
    }

    public FtpHelper(File dir, int port) {
        this.mDir = dir;
        this.mPort = port;
    }

    public int getPort() {
        return mPort;
    }

    public FtpServer getFtpServer() {
        return mFtpServer;
    }

    public Listener getFtpListener() {
        if (mFtpServer == null) {
            return null;
        }

        DefaultFtpServer server = (DefaultFtpServer)mFtpServer;
        return server.getListener("default");
    }

    public UserManager getUserManager() {
        if (mFtpServer == null) {
            return null;
        }

        DefaultFtpServer server = (DefaultFtpServer)mFtpServer;
        return server.getUserManager();
    }

    public void start() {
        if (mFtpServer != null) {
            return;
        }

        FtpServer server;
        FtpServerFactory serverFactory = new FtpServerFactory();

        // Listener
        {
            ListenerFactory factory = new ListenerFactory();

            // set the port of the listener
            factory.setPort(mPort);

            Listener listener = factory.createListener();

            // replace the default listener
            serverFactory.addListener("default", listener);        // replace the default listener
        }

        // Factory
        {
            FtpUserManager manager = new FtpUserManager(mDir);
            serverFactory.setUserManager(manager);
        }


        // 启动服务器
        {
            server = serverFactory.createServer();
            try {
                server.start();
            } catch (FtpException e) {
                server = null;
            }
        }

        this.mFtpServer = server;
    }

    public void close() {
        if (mFtpServer != null) {
            mFtpServer.stop();
            mFtpServer = null;
        }
    }

    public boolean isRunning() {
        return (mFtpServer != null);
    }
}
