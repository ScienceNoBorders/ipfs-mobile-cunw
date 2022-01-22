package cunw.file.examples.test;

import cunw.file.examples.ExampleFileAgent;
import cunw.file.examples.monitor.ConsoleListener;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.configs.FileConfiguration;
import org.hive2hive.core.api.configs.NetworkConfiguration;
import org.hive2hive.core.api.interfaces.IFileConfiguration;
import org.hive2hive.core.api.interfaces.IFileManager;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.security.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * @author XiaoTiJun
 * @date 1/20/22 9:44 AM
 */
public class Tom {

    static Logger logger = LoggerFactory.getLogger(Tom.class);

    public static void main(String[] args) {
        try {
            IFileConfiguration fileConfiguration = FileConfiguration.createDefault();
            // Create two nodes and open a new overlay network
            IH2HNode master = H2HNode.createNode(fileConfiguration);
            master.connect(NetworkConfiguration.create(InetAddress.getLocalHost()));

            ExampleFileAgent nodeFileAgent = new ExampleFileAgent("tom");

            UserCredentials tom = new UserCredentials("tom", "password", "pin");
            master.getUserManager().createRegisterProcess(tom).execute();
            master.getUserManager().createLoginProcess(tom, nodeFileAgent).execute();

            ConsoleListener cs = new ConsoleListener(new Scanner(System.in), msg -> {
                logger.info("Console: " + msg);
                try {
                    IFileManager fileManager = master.getFileManager();
                    File folderAtMaster = new File(nodeFileAgent.getRoot(), "shared");
                    fileManager.createDownloadProcess(folderAtMaster).execute();

                    File fileMaster = new File(folderAtMaster, "通达信金融终端(开心果整合版)V2021.11.rar");
                    fileManager.createDownloadProcess(fileMaster).execute();

                    logger.info("Content of the file in the shared folder at Tom: OK!");
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
