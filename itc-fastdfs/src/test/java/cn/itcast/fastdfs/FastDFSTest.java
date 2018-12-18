package cn.itcast.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.IOException;

public class FastDFSTest {
    @Test
    public void test() throws IOException, MyException {
        //1.获取追踪服务器配置文件路径并设置配置
        String confFilename = ClassLoader.getSystemResource("fastdfs/tracker.conf").getPath();
        ClientGlobal.init(confFilename);
        //2.创建TrackerClient,直接实例化
        TrackerClient trackerClient = new TrackerClient();
        //3.利用TrackerClient获取到TrackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        //4.创建一个储存服务器StorageServer,可以为空
        StorageServer storageServer = null;
        //5.利用TrackerServer和StorageServer创建一个StorageClient对象并上传
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);


        /**
         * 上传图片
         * 参数1:文件路径
         * 参数2:文件扩展名(后缀)
         * 参数3:文件信息
         *
         */
        //要上传的文件路径
        String[] uploadFile = storageClient.upload_file("e:\\itcast\\pics\\fengling01.jpg", "jpg", null);
        if (uploadFile!=null&&uploadFile.length>0){
            for (String s : uploadFile) {
                System.out.println(s);
            }

            //组名
            String groupName = uploadFile[0];
            //文件路径
            String filename = uploadFile[1];
            System.out.println("groupName= "+groupName);
            System.out.println("filename= "+filename);
            //获取存储服务器的地址
            ServerInfo[] serverInfos = trackerClient.getFetchStorages(trackerServer, groupName, filename);
            for (ServerInfo serverInfo : serverInfos) {
                System.out.println("ip = "+serverInfo.getIpAddr()+"; port = "+serverInfo.getPort());
            }
            String url = "http://" + serverInfos[0].getIpAddr() + "/" + groupName + "/" + filename;
            System.out.println("图片可访问地址为: "+url);
        }
    }
}
