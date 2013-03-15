/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bitcoin;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.DownloadListener;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Peer;
import com.google.bitcoin.core.PeerAddress;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.discovery.DnsDiscovery;
import com.google.bitcoin.discovery.PeerDiscoveryException;
import com.google.bitcoin.store.BlockStore;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.BoundedOverheadBlockStore;
import com.google.bitcoin.store.DiskBlockStore;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author leijurv
 */
public class Bitcoin {

    /**
     * @param args the command line arguments
     */
    public static Wallet loadOrGenerateWallet(String filename, NetworkParameters params,String start){
        Wallet wallet=null;
        File walletFile=new File("/Users/leijurv/Desktop/jBitcoin/wallet.dat");
        
        try {
            wallet=Wallet.loadFromFile(walletFile);
            
        } catch (IOException ex) {
            wallet = new Wallet(params);
            wallet.addKey(get(start,params));
            try {
                wallet.saveToFile(walletFile);
            } catch (IOException ex1) {
                 }
        }
        return wallet;
    }
    
    public static void main(String[] args)  {
        final String filePrefix="/Users/leijurv/Desktop/jBitcoin/";
        //(new File(filePrefix)).mkdirs();
        //try {
            NetworkParameters params=NetworkParameters.prodNet();
            Wallet wallet=loadOrGenerateWallet(filePrefix+"wallet.dat",params,"1LRF");
            System.out.println("Reading block store from disk");
    /*BlockStore blockStore = new BoundedOverheadBlockStore(params, new File(filePrefix + "blocks.blockchain"));
    BlockChain chain = new BlockChain(params, wallet, blockStore);
    System.out.println("Connecting ...");
final PeerGroup peerGroup = new PeerGroup(params,chain);
peerGroup.setUserAgent("MyApp", "1.2");
peerGroup.addWallet(wallet);
peerGroup.addAddress(new PeerAddress(InetAddress.getLocalHost()));
DnsDiscovery dns=new DnsDiscovery(params);

peerGroup.addPeerDiscovery(dns);
peerGroup.start();
dns.getPeers();
System.out.println(peerGroup.getConnectedPeers());
/*for (InetSocketAddress i: dns.getPeers()){
    System.out.println(i.toString());
}
peerGroup.startBlockChainDownload(new DownloadListener());
        
        } catch (PeerDiscoveryException ex) {
            Logger.getLogger(Bitcoin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Bitcoin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BlockStoreException ex) {
            Logger.getLogger(Bitcoin.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    public static ECKey get(String start, NetworkParameters params){
        String res="";
        ECKey ek=new ECKey();
        while(!res.startsWith(start)){
            ek=new ECKey();
            if (res.toUpperCase().startsWith("1L"))
            System.out.println(res);
            res=ek.toAddress(params).toString();
        }
        return ek;
    }
}
