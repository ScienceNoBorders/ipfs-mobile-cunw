package cunw.file.examples.test;

import cunw.file.examples.ExampleFileAgent;
import cunw.file.examples.monitor.ConsoleListener;
import org.apache.commons.io.FileUtils;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.configs.FileConfiguration;
import org.hive2hive.core.api.configs.NetworkConfiguration;
import org.hive2hive.core.api.interfaces.IFileConfiguration;
import org.hive2hive.core.api.interfaces.IFileManager;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.model.PermissionType;
import org.hive2hive.core.security.UserCredentials;

import java.io.File;
import java.util.Scanner;

/**
 * @author XiaoTiJun
 * @date 1/20/22 9:43 AM
 */
public class Master {

    public static void main(String[] args) {
        try {
            IFileConfiguration fileConfiguration = FileConfiguration.createDefault();
            // Create two nodes and open a new overlay network
            IH2HNode master = H2HNode.createNode(fileConfiguration);
            master.connect(NetworkConfiguration.createInitial());

            ExampleFileAgent nodeFileAgent = new ExampleFileAgent("Master");

            UserCredentials userMaster = new UserCredentials("Master", "password", "pin");
            master.getUserManager().createRegisterProcess(userMaster).execute();
            master.getUserManager().createLoginProcess(userMaster, nodeFileAgent).execute();

            ConsoleListener cs = new ConsoleListener(new Scanner(System.in), msg -> {
                System.out.println("Console: " + msg);
                try {
                    IFileManager fileManager = master.getFileManager();
                    File folderAtMaster = new File(nodeFileAgent.getRoot(), "shared");
                    if (!folderAtMaster.exists()) {
                        boolean mkdirs = folderAtMaster.mkdirs();
                    }
                    fileManager.createAddProcess(folderAtMaster).execute();

                    /*File file = new File("/Users/xiaotijun/Downloads/通达信金融终端(开心果整合版)V2021.11.rar");
                    File fileMaster = new File(folderAtMaster, "通达信金融终端(开心果整合版)V2021.11.rar");*/
                    File file = new File("/Users/xiaotijun/Downloads/交底书-epub文件处理0713.docx");
                    File fileMaster = new File(folderAtMaster, "交底书-epub文件处理0713.docx");
                    if ( !fileMaster.exists() ) {
                        FileUtils.copyFile(file, fileMaster);
                    }
                    fileManager.createAddProcess(fileMaster).execute();

                    fileManager.createShareProcess(folderAtMaster, "slave", PermissionType.WRITE).execute();
//                    fileManager.createShareProcess(folderAtMaster, "tom", PermissionType.WRITE).execute();
                    System.out.println("Content of the file in the shared folder at Master: OK!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            cs.listenInNewThread();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
