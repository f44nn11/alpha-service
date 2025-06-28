package com.alpha.service.util;


import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import java.io.File;
import java.io.IOException;

/*
 * Created by: fkusu
 * Date: 5/6/2025
 */
public class SshjSftpUploader {
    public static void main(String[] args) throws IOException {
        String host = "192.168.2.2"; // IP server/NAS
        String user = "userapp";
        String password = "Alphaproject24";




        String localFilePath = "/document/support/dev/BK01250010/memberlist/";
        String fileName = "memberlist_edit_rev1.xlsx";
        String remoteDir = "/ALPHA-APP/document/support/dev/";


        SSHClient sshClient = new SSHClient();
        try {
            sshClient.addHostKeyVerifier(new PromiscuousVerifier()); // Non-production use
            sshClient.connect(host);
            sshClient.authPassword(user, password);

            SFTPClient sftpClient = sshClient.newSFTPClient();
            sftpClient.mkdirs(remoteDir + new File(localFilePath));
            sftpClient.put(localFilePath + fileName, remoteDir + new File(localFilePath));

            System.out.println("Upload berhasil!");
            sftpClient.close();
            sshClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
