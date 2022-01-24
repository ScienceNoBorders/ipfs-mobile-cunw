package cunw.file.spring;


import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

/**
 * @author XiaoTiJun
 * @date 1/21/22 9:42 AM
 */
@SpringBootApplication
public class FileSystemAction {

    private static Logger logger =  LoggerFactory.getLogger(FileSystemAction.class);

    public static void main(String[] args) {

        SpringApplication.run(FileSystemAction.class, args);

        try {
            IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");

            ipfs.refs.local();

            NamedStreamable.FileWrapper addFile = new NamedStreamable.FileWrapper(new File("/Users/xiaotijun/Downloads/share", "hello.txt"));
            MerkleNode addResult = ipfs.add(addFile).get(0);
            String addHash = addResult.hash.toBase58();


            NamedStreamable.ByteArrayWrapper editFile = new NamedStreamable.ByteArrayWrapper("/Users/xiaotijun/Downloads/share/hello.txt", "G'day world! IPFS rocks!".getBytes());
            MerkleNode editResult = ipfs.add(editFile).get(0);
            String editHash = addResult.hash.toBase58();

            logger.info("add hash is ${}, edit hash iss  ${}", addHash, editHash);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
