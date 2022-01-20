package cunw.file.examples;

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
import java.net.InetAddress;

/**
 * This example shows some how to share a folder between two users.
 * 
 * @author Nico
 *
 */
public class ShareExampleTestTwo {

	public static void main(String[] args) throws Exception {
		IFileConfiguration fileConfiguration = FileConfiguration.createDefault();

		// Create two nodes and open a new overlay network
		IH2HNode master = H2HNode.createNode(fileConfiguration);
		IH2HNode slave = H2HNode.createNode(fileConfiguration);
		master.connect(NetworkConfiguration.createInitial());
		slave.connect(NetworkConfiguration.create(InetAddress.getLocalHost()));

		// These two file agents are used to configure the root directory of the logged in users
		ExampleFileAgent node1FileAgent = new ExampleFileAgent("Master");
		ExampleFileAgent node2FileAgent = new ExampleFileAgent("Slave");

		// Register and login user 'Master' at node 1
		UserCredentials Master = new UserCredentials("Master", "password", "pin");
		master.getUserManager().createRegisterProcess(Master).execute();
		master.getUserManager().createLoginProcess(Master, node1FileAgent).execute();

		// Register and login user 'Slave' at node 2
		UserCredentials Slave = new UserCredentials("Slave", "password", "pin");
		slave.getUserManager().createRegisterProcess(Slave).execute();
		slave.getUserManager().createLoginProcess(Slave, node2FileAgent).execute();

		// Let's create a folder at Master and upload it
		IFileManager fileManager1 = master.getFileManager(); // The file management of Master's peer
		File folderAtMaster = new File(node1FileAgent.getRoot(), "shared-folder");
		folderAtMaster.mkdirs();
		fileManager1.createAddProcess(folderAtMaster).execute();

		// Slave could for example upload a new file to the shared folder
		File fileAtMaster = new File("/Users/xiaotijun/Downloads/通达信金融终端(开心果整合版)V2021.11.rar");
		File fileMaster = new File(folderAtMaster, "通达信金融终端(开心果整合版)V2021.11.rar");
		FileUtils.copyFile(fileAtMaster, fileMaster);
		fileManager1.createAddProcess(fileMaster).execute();

		// Let's share the folder with Slave giving him write permission
		fileManager1.createShareProcess(folderAtMaster, "Slave", PermissionType.WRITE).execute();

		// Wait some time in order to get the file share event propagated to Slave
		System.out.println("Master shared a folder with Slave. We'll wait some time for its propagation...");
		Thread.sleep(20000);

		// Slave can now 'download' the folder (yes, sounds a little bit silly...)
		IFileManager fileManager2 = slave.getFileManager(); // The file management of Slave's peer
		File folderAtSlave = new File(node2FileAgent.getRoot(), "shared-folder");
		fileManager2.createDownloadProcess(folderAtSlave).execute();

		// Master now can obtain the shared file
		File fileAtSlave = new File(folderAtSlave, "通达信金融终端(开心果整合版)V2021.11.rar");
		fileManager2.createDownloadProcess(fileAtSlave).execute();
		System.out.println("Content of the file in the shared folder at Master: OK!");
	}
}
