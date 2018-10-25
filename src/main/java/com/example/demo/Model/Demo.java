package com.example.demo.Model;

import org.bcos.channel.client.TransactionSucCallback;
import org.bcos.web3j.abi.EventValues;
import org.bcos.web3j.abi.TypeReference;
import org.bcos.web3j.abi.datatypes.Event;
import org.bcos.web3j.abi.datatypes.Function;
import org.bcos.web3j.abi.datatypes.Type;
import org.bcos.web3j.abi.datatypes.Utf8String;
import org.bcos.web3j.crypto.Credentials;
import org.bcos.web3j.protocol.Web3j;
import org.bcos.web3j.protocol.core.methods.response.TransactionReceipt;
import org.bcos.web3j.tx.Contract;
import org.bcos.web3j.tx.TransactionManager;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;


public final class Demo extends Contract {
    private static String BINARY = "6060604052341561000c57fe5b5b604060405190810160405280600a81526020017f776f7368692064656d6f0000000000000000000000000000000000000000000081525060009080519060200190610059929190610060565b505b610105565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106100a157805160ff19168380011785556100cf565b828001600101855582156100cf579182015b828111156100ce5782518255916020019190600101906100b3565b5b5090506100dc91906100e0565b5090565b61010291905b808211156100fe5760008160009055506001016100e6565b5090565b90565b6102e2806101146000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680634ed3885e146100465780636d4ce63c146100a0575bfe5b341561004e57fe5b61009e600480803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091905050610139565b005b34156100a857fe5b6100b0610154565b60405180806020018281038252838181518152602001915080519060200190808383600083146100ff575b8051825260208311156100ff576020820191506020810190506020830392506100db565b505050905090810190601f16801561012b5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b806000908051906020019061014f9291906101fd565b505b50565b61015c61027d565b60008054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156101f25780601f106101c7576101008083540402835291602001916101f2565b820191906000526020600020905b8154815290600101906020018083116101d557829003601f168201915b505050505090505b90565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061023e57805160ff191683800117855561026c565b8280016001018555821561026c579182015b8281111561026b578251825591602001919060010190610250565b5b5090506102799190610291565b5090565b602060405190810160405280600081525090565b6102b391905b808211156102af576000816000905550600101610297565b5090565b905600a165627a7a723058203e53621ee09e1d484d0a41404aa3541070cfbb0bce473a8f6de2151852acfad20029";

    public static final String ABI = "[{\"constant\":false,\"inputs\":[{\"name\":\"temp_name\",\"type\":\"string\"}],\"name\":\"set\",\"outputs\":[],\"payable\":false,\"type\":\"function\"},{\"constant\":true,\"inputs\":[],\"name\":\"get\",\"outputs\":[{\"name\":\"\",\"type\":\"string\"}],\"payable\":false,\"type\":\"function\"},{\"inputs\":[],\"payable\":false,\"type\":\"constructor\"}]";

    private Demo(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, isInitByName);
    }

    private Demo(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, Boolean isInitByName) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, isInitByName);
    }

    private Demo(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    private Demo(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    public Future<TransactionReceipt> set(Utf8String temp_name) {
        Function function = new Function("set", Arrays.<Type>asList(temp_name), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

    public void set(Utf8String temp_name, TransactionSucCallback callback) {
        Function function = new Function("set", Arrays.<Type>asList(temp_name), Collections.<TypeReference<?>>emptyList());
        executeTransactionAsync(function, callback);
    }

    public Future<Utf8String> get() {
        Function function = new Function("get", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeCallSingleValueReturnAsync(function);
    }

    public static Future<Demo> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue) {
        return deployAsync(Demo.class, web3j, credentials, gasPrice, gasLimit, BINARY, "", initialWeiValue);
    }

    /**
     * 增加一个名字变化后的函数，做监听
     *
     *
     * */
    public static List<ChangenameEventResponse> getChangenameEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("changename",
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ChangenameEventResponse> responses = new ArrayList<ChangenameEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ChangenameEventResponse typedResponse = new ChangenameEventResponse();
            typedResponse.oldname = (Utf8String) eventValues.getNonIndexedValues().get(0);
            responses.add(typedResponse);
        }
        return responses;
    }

    public static Future<Demo> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, BigInteger initialWeiValue) {
        return deployAsync(Demo.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "", initialWeiValue);
    }

    public static Demo load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Demo(contractAddress, web3j, credentials, gasPrice, gasLimit, false);
    }

    public static Demo load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Demo(contractAddress, web3j, transactionManager, gasPrice, gasLimit, false);
    }

    public static Demo loadByName(String contractName, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Demo(contractName, web3j, credentials, gasPrice, gasLimit, true);
    }

    public static Demo loadByName(String contractName, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Demo(contractName, web3j, transactionManager, gasPrice, gasLimit, true);
    }

    public static class ChangenameEventResponse {
        public Utf8String oldname;
    }
}
