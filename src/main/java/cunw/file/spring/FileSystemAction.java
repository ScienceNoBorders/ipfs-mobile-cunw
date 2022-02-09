package cunw.file.spring;


import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author XiaoTiJun
 * @date 1/21/22 9:42 AM
 */
@SpringBootApplication
@RestController
public class FileSystemAction {

    private static Logger logger = LoggerFactory.getLogger(FileSystemAction.class);
    private static IPFS ipfs = null;

    static {
        try {
            ipfs = new IPFS("/ip4/192.168.79.82/tcp/5001");
            List<Multihash> local = ipfs.refs.local();
//            local.forEach(multihash -> System.out.println("Peer ID: " + multihash));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(FileSystemAction.class, args);

    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload(@RequestParam("filePath") String filePath) throws IOException {
        NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File(filePath));
        MerkleNode addResult = ipfs.add(file).get(0);
        String addHash = addResult.hash.toBase58();
        return StringUtils.isEmpty(addHash) ? "upload fail!" : addHash;
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public String download(@RequestParam("hash") String hash, @RequestParam("fileName") String fileName) throws IOException {
        Multihash multihash = Multihash.fromBase58(hash);
        byte[] data = ipfs.cat(multihash);
        File file = new File("/Users/xiaotijun/Downloads/share", fileName);
        if (file.exists()) {
            boolean delete = file.delete();
        }
        FileOutputStream fos = new FileOutputStream(file);
        try {
            if (data != null) {
                fos.write(data, 0, data.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fos.flush();
            fos.close();
        }
        return "download success!";
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public String remove(@RequestParam("hash") String hash) throws IOException {
        Multihash filePointer = Multihash.fromBase58(hash);
        List<Multihash> rm = ipfs.pin.rm(filePointer);
        return StringUtils.isEmpty(rm.get(0)) ? "remove fail!" : "remove seccess!";
    }


}
