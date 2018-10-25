package com.example.demo;

import com.example.demo.Model.Demo;
import org.bcos.web3j.abi.datatypes.Utf8String;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DemoClient {

    private ECKeyPair keyPair;
    private Credentials credentials;
    private Web3j web3j;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private BigInteger initialWeiValue;

    public DemoClient(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue)
    {
        this.web3j=web3j;
        this.credentials=credentials;
        this.gasPrice=gasPrice;
        this.gasLimit=gasLimit;
        this.initialWeiValue=initialWeiValue;
    }

    public ECKeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(ECKeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public Web3j getWeb3j() {
        return web3j;
    }

    public void setWeb3j(Web3j web3j) {
        this.web3j = web3j;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public BigInteger getInitialWeiValue() {
        return initialWeiValue;
    }

    public void setInitialWeiValue(BigInteger initialWeiValue) {
        this.initialWeiValue = initialWeiValue;
    }

    /* 部署合同，从链中获取地址,这里是合约的地址 */
    public String deployDemo() throws InterruptedException, ExecutionException {

        Future<Demo> futureDeploy = Demo.deploy(this.getWeb3j(), this.getCredentials(),
                this.getGasPrice(), this.getGasLimit(), this.getInitialWeiValue());
        Demo demo = futureDeploy.get();
        System.out.println(demo.get().get().getValue());
        String contractAddress = demo.getContractAddress();
        demo.getContractName();
        System.out.println("部署合约 :" + demo.getContractName() + ",地址 :" + contractAddress);
        return contractAddress;
    }

    /* 做简单读写测试 */
    public void testDemo(String contractAddr) throws InterruptedException, ExecutionException {

        Demo demo = Demo.load(contractAddr, this.getWeb3j(), this.getCredentials(), this.getGasPrice(), this.getGasLimit());

        // 获取当前name的值
        String name = demo.get().get().getValue();
        System.out.println("在交易之前的name:" + name);

        Future<TransactionReceipt> futureSetname = demo.set(new Utf8String("史瑞昌"));

        // 等待回调
        TransactionReceipt receiptSetname = futureSetname.get();
        // 提交之后
        String curName = demo.get().get().getValue();
        System.out.println("在交易之后的name:" + curName);

        /* process setname receipt */
        List<Demo.ChangenameEventResponse> lstCN = Demo.getChangenameEvents(receiptSetname);
        for (int i = 0; i < lstCN.size(); i++) {
            Demo.ChangenameEventResponse response = lstCN.get(i);
            System.out.println("设置名字->oldname:[" + response.oldname.getValue() + "]," + "newname=[" + curName + "]");
        }

    }


}
