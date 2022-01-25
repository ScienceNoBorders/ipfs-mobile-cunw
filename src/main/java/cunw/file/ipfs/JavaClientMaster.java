package cunw.file.ipfs;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * @author XiaoTiJun
 * @date 1/24/22 9:19 AM
 */
public class JavaClientMaster {
    static Logger logger = LoggerFactory.getLogger(JavaClientMaster.class);

    public static void main(String[] args) {
        try {
            IPFS ipfs = new IPFS("/ip4/159.75.113.238/tcp/5001");

            ipfs.refs.local();

            String hash = "QmU2RXfwwJgNYvKvkZsgYYxLd4FUx8aogUn2yQztz82VKc";
            Path path = new File("/Users/xiaotijun/Downloads/share", "testone.docx").toPath();
            Multihash multihash = Multihash.fromBase58(hash);

            //打印文件内容
            byte[] data = ipfs.cat(multihash);
            InputStream inputData = ipfs.catStream(multihash);

            //下载文件
            byte[] byteData = ipfs.get(multihash);
            Files.copy(inputData, path);

//            NamedStreamable.FileWrapper file = new NamedStreamable.FileWrapper(new File("/Users/xiaotijun/Downloads/share/交底书-epub文件处理0713.docx"));
//            MerkleNode merkleNode = ipfs.add(file).get(0);
//            System.out.println(merkleNode.hash);

            //查看文件信息
            List<MerkleNode> nodes = ipfs.ls(multihash);
            System.out.println(Arrays.toString(nodes.toArray()));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
