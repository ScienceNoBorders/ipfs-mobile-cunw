package cunw.file.examples.test;

import cunw.file.examples.ExampleFileAgent;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.configs.FileConfiguration;
import org.hive2hive.core.api.configs.NetworkConfiguration;
import org.hive2hive.core.api.interfaces.IFileConfiguration;
import org.hive2hive.core.api.interfaces.IFileManager;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.model.PermissionType;
import org.hive2hive.core.security.UserCredentials;

import java.io.File;
import java.net.InetAddress;

/**
 * @author XiaoTiJun
 * @date 1/20/22 9:43 AM
 */
public class SlaveTom {

    public static void main(String[] args) {
        try {
            IFileConfiguration fileConfiguration = FileConfiguration.createDefault();

            // Create two nodes and open a new overlay network
            IH2HNode tom = H2HNode.createNode(fileConfiguration);
            tom.connect(NetworkConfiguration.create(InetAddress.getByName("192.168.79.82")));

            System.out.println("tom is connect: " + tom.isConnected());

            ExampleFileAgent nodeFileAgent = new ExampleFileAgent("Tom");

            UserCredentials tomSlave = new UserCredentials("Tom", "password", "pin");
            if ( !tom.getUserManager().isRegistered("Tom") ) {
                tom.getUserManager().createRegisterProcess(tomSlave).execute();
            }
            tom.getUserManager().createLoginProcess(tomSlave, nodeFileAgent).execute();

            IFileManager fileManager = tom.getFileManager();
            File folderAtTom = new File(nodeFileAgent.getRoot(), "shared-folder");
            if (!folderAtTom.exists()) {
                boolean mkdirs = folderAtTom.mkdirs();
            }
            fileManager.createDownloadProcess(folderAtTom).execute();
            fileManager.createShareProcess(folderAtTom, "Master", PermissionType.WRITE);

            File fileAtTom = new File(folderAtTom, "通达信金融终端(开心果整合版)V2021.11.rar");
            fileManager.createDownloadProcess(fileAtTom).execute();
            System.out.println("Content of the file in the shared folder at Tom: OK!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
