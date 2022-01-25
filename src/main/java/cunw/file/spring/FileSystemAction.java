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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author XiaoTiJun
 * @date 1/21/22 9:42 AM
 */
@SpringBootApplication
@RestController
public class FileSystemAction {

    private static Logger logger =  LoggerFactory.getLogger(FileSystemAction.class);
    private static IPFS ipfs = null;

    static {
        try {
            ipfs = new IPFS("/ip4/159.75.113.238/tcp/5001");
            ipfs.refs.local();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(FileSystemAction.class, args);

    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload() throws IOException {
        NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File("/Users/xiaotijun/Downloads/share/交底书-epub文件处理0713.docx"));
        MerkleNode addResult = ipfs.add(file).get(0);
        String addHash = addResult.hash.toBase58();
        return StringUtils.isEmpty(addHash) ? "upload fail!" : addHash;
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public String download(@RequestParam("hash") String hash) throws IOException {
        Path path = new File("/Users/xiaotijun/Downloads/share", "testone.docx").toPath();
        Multihash multihash = Multihash.fromBase58(hash);
        InputStream inputData = ipfs.catStream(multihash);
        long copy = Files.copy(inputData, path);
        return copy > 0 ? "download success!" : "download fail!";
    }


}
