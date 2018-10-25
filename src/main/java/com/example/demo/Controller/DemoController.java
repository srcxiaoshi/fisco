package com.example.demo.Controller;


import com.example.demo.DemoClient;
import com.example.demo.Model.Demo;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.crypto.ECKeyPair;
import org.bcos.web3j.crypto.Keys;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.channel.ChannelEthereumService;
import org.bcos.web3j.protocol.core.methods.response.EthBlockNumber;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

/**
 * Created by xiaoshi on 2018/10/25.
 */
@Service
@RestController
@RequestMapping({"/demo"})
@CrossOrigin
public class DemoController {

    // 初始化交易参数
    public static ECKeyPair keyPair;
    public static Credentials credentials;
    public static Web3j web3j;
    public static BigInteger gasPrice = new BigInteger("1");
    public static BigInteger gasLimit = new BigInteger("30000000");
    public static BigInteger initialWeiValue = new BigInteger("0");

    private void initWeb3j()throws Exception
    {
        // init the Service
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        org.bcos.channel.client.Service service = context.getBean(org.bcos.channel.client.Service.class);
        service.run(); // run the daemon service
        // init the client keys
        keyPair = Keys.createEcKeyPair();
        credentials = Credentials.create(keyPair);

        System.out.println("我启动了 !");
        ChannelEthereumService channelEthereumService = new ChannelEthereumService();
        channelEthereumService.setChannelService(service);

        // 初始化webj
        web3j = Web3j.build(channelEthereumService);
    }

    @RequestMapping(value  = "/test",method = RequestMethod.GET)
    public String test(String uuid) throws Exception
    {
        if(web3j==null)
        {
            this.initWeb3j();
        }

        // 测试区块数量，可选的步骤
        EthBlockNumber ethBlockNumber = web3j.ethBlockNumber().sendAsync().get();
        int startBlockNumber = ethBlockNumber.getBlockNumber().intValue();
        System.out.println("-->区块数目: " + startBlockNumber);

        DemoClient democlient=new DemoClient(web3j, credentials, gasPrice, gasLimit, initialWeiValue);
        String address=democlient.deployDemo();
        System.out.println("地址："+address);

        democlient.testDemo(address);

        //添加了交易记录后的，区块数量
        ethBlockNumber = web3j.ethBlockNumber().sendAsync().get();
        int finishBlockNumber = ethBlockNumber.getBlockNumber().intValue();
        System.out.println("<--开始数量 = " + startBlockNumber + ",结束数量=" + finishBlockNumber);


        Demo demo = Demo.load(address, web3j, credentials, gasPrice, gasLimit);
        // 获取当前name的值
        String name = demo.get().get().getValue();
        System.out.println("重新查询合约的值name=:" + name);

        return "我是demo";
    }

}
