package com.haiyunshan.pudding.ftp;

import android.text.TextUtils;

import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FtpUserManager implements UserManager {

    File mDir;

    public FtpUserManager(File dir) {
        this.mDir = dir;
    }

    @Override
    public User getUserByName(String userName) {
        if (!doesExist(userName)) {
            return null;
        }

        BaseUser user = new BaseUser();

        {
            user.setName(userName);
            user.setEnabled(true);
            user.setHomeDirectory(mDir.getAbsolutePath());
            user.setMaxIdleTime(60 * 60);
        }

        {
            List<Authority> authorities = new ArrayList<>();
            {
                authorities.add(new WritePermission());
            }

            {
                int maxLogin = 11;
                int maxLoginPerIP = 11;

                authorities.add(new ConcurrentLoginPermission(maxLogin, maxLoginPerIP));
            }

            {
                int uploadRate = 1000 * 1024 * 1024;
                int downloadRate = 1000 * 1024 * 1024;

                authorities.add(new TransferRatePermission(downloadRate, uploadRate));
            }

            user.setAuthorities(authorities);
        }

        return user;
    }

    @Override
    public String[] getAllUserNames() {
        return new String[] { "admin", "anonymous" };
    }

    @Override
    public void delete(String username) {

    }

    @Override
    public void save(User user) {

    }

    @Override
    public boolean doesExist(String username) {
        return true;
    }

    @Override
    public User authenticate(Authentication authentication) {
        String username = "anonymous"; // 默认为匿名用户

        if (authentication instanceof UsernamePasswordAuthentication) {

            UsernamePasswordAuthentication upauth = (UsernamePasswordAuthentication) authentication;

            String user = upauth.getUsername();
            if (!TextUtils.isEmpty(user)) {
                username = user;
            }

        } else if (authentication instanceof AnonymousAuthentication) {

        } else {

        }

        return getUserByName(username);
    }

    @Override
    public String getAdminName() {
        return "admin";
    }

    @Override
    public boolean isAdmin(String username) {
        return true;
    }
}
