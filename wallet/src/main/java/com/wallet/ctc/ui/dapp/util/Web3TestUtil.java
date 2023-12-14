

package com.wallet.ctc.ui.dapp.util;

import static org.web3j.crypto.SignatureDataOperations.LOWER_REAL_V;

import android.util.Log;

import com.wallet.ctc.BuildConfig;
import com.wallet.ctc.crypto.WalletDBUtil;
import com.wallet.ctc.crypto.WalletUtil;
import com.wallet.ctc.db.WalletEntity;
import com.wallet.ctc.ui.dapp.contract.PancakeSwap_sol_PancakeRouter;
import com.wallet.ctc.util.LogUtil;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainIdLong;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import common.app.AppApplication;
import common.app.utils.ThreadManager;


public class Web3TestUtil {

    private WalletEntity walletEntity;
    private BigDecimal gasCount = new BigDecimal("100000");
    private BigDecimal gasprice = new BigDecimal("25000");
    private void testContract() {
        String defAddress = "0x8aB9f0f74B39211c3D26101367010896E905baaC";
        walletEntity= WalletDBUtil.getInstent(AppApplication.getContext()).getWalletInfoByAddress(defAddress, WalletUtil.ETH_COIN);
        LogUtil.i("--------------------");
        ECKeyPair pair = ECKeyPair.create(new BigInteger(WalletUtil.getDecryptionKey(walletEntity.getmPrivateKey(), "12345678"), 16));
        Credentials credentials = Credentials.create(pair);

        String contractAddr = "0xb0AD0E7648bCD92Ff0C369a8e5d9f435530d5bdc";
        
        Web3j web3j =  Web3j.build(new HttpService("https://ropsten.infura.io/v3/b4064cbac20e4740ba2e9acef569d129"));

        LogUtil.i("gasprice:"+gasprice+", gasCount:"+gasCount);

        StaticGasProvider gasProvider = new StaticGasProvider(BigInteger.valueOf(gasprice.longValue()), BigInteger.valueOf(gasCount.longValue()));

        TransactionManager transactionManager = new ReadonlyTransactionManager(web3j, walletEntity.getAllAddress());
        PancakeSwap_sol_PancakeRouter contract = PancakeSwap_sol_PancakeRouter.load(contractAddr, web3j, transactionManager, new DefaultGasProvider());

        BigInteger _amount = new BigInteger("100000000");
        String targetAddress = "m1agepcpj56s0py8q9sk076n9h0zz64dyqluc444";
        String targetChain = "MIP";
        String FUNC_BURNTOKEN = "burnToken";

        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.i("burnToken");
                    List<String> tokens = new ArrayList<>();


                    TransactionReceipt receipt = contract.getAmountsOut(new BigInteger("10000000"), tokens).send();
                    LogUtil.i("hash:"+receipt.getTransactionHash());
                    if (receipt.isStatusOK()) {
                        LogUtil.i("");
                    } else {
                        LogUtil.i("");
                    }
                } catch (Exception e) {
                    LogUtil.e("："+e);
                    e.printStackTrace();
                }
            }
        });


    }


    
    private void getBqlPancakeSwapPrice() {
        
        String pancakeSwapContract = "0x10ED43C718714eb63d5aA57B78B54704E256024E";

        ECKeyPair pair = ECKeyPair.create(new BigInteger("7a769ae26c9605c84b602593c919915498fd5923fc8b9ed9403d9165a3e8b8c1", 16));
        Credentials credentials = Credentials.create(pair);

        
        Web3j web3j =  Web3j.build(new HttpService(BuildConfig.HOST_JSONRPC_BNB));
        
        TransactionManager transactionManager = new ClientTransactionManager(web3j, "0x8aB9f0f74B39211c3D26101367010896E905baaC");

        PancakeSwap_sol_PancakeRouter contract = PancakeSwap_sol_PancakeRouter.load(pancakeSwapContract, web3j, transactionManager, new DefaultGasProvider());

        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtil.i("");
                    List<String> tokens = new ArrayList<>();
                    tokens.add("0xE9c7a827a4bA133b338b844C19241c864E95d75f");
                    tokens.add("0x55d398326f99059fF775485246999027B3197955");
                    




                    final Function function = new Function(
                            PancakeSwap_sol_PancakeRouter.FUNC_GETAMOUNTSOUT,
                            Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(new BigInteger("1000000")),
                                    new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                                            org.web3j.abi.datatypes.Address.class,
                                            org.web3j.abi.Utils.typeMap(tokens, org.web3j.abi.datatypes.Address.class))),
                            Arrays.asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));

                    String encodedFunction = FunctionEncoder.encode(function);
                    org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                            Transaction.createEthCallTransaction("0x8aB9f0f74B39211c3D26101367010896E905baaC", pancakeSwapContract, encodedFunction),
                            DefaultBlockParameterName.LATEST).sendAsync().get();

                    List<Type> someTypes = FunctionReturnDecoder.decode(
                            response.getValue(), function.getOutputParameters());
                    if (null != someTypes && someTypes.size() == 4) {
                        Uint256 value = (Uint256) someTypes.get(3);
                        String price = new BigDecimal(value.getValue()).divide(new BigDecimal(Math.pow(10,18)), 5, RoundingMode.HALF_UP).toPlainString();
                    }

                    LogUtil.i("someTypes:"+someTypes+", "+someTypes.size()+"-----"+response.getValue());
                } catch (Exception e) {
                    LogUtil.e("："+e);
                    e.printStackTrace();
                }
            }
        });

    }


    public void signData() {
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {




                String message = "0x7762343436646531386164396461646536613263313463663461356633356333";
                String message2 = "wb446de18ad9dade6a2c14cf4a5f35c3";


                String hash = Hash.sha3String("wb446de18ad9dade6a2c14cf4a5f35c3");
                String hash2 = Hash.sha3("0x7762343436646531386164396461646536613263313463663461356633356333");

                String sign1 = WalletUtil.customSign("7a769ae26c9605c84b602593c919915498fd5923fc8b9ed9403d9165a3e8b8c1", "0x7762343436646531386164396461646536613263313463663461356633356333", WalletUtil.ETH_COIN);
                String sign2 = WalletUtil.customSign("7a769ae26c9605c84b602593c919915498fd5923fc8b9ed9403d9165a3e8b8c1", "wb446de18ad9dade6a2c14cf4a5f35c3", WalletUtil.ETH_COIN);
                Log.i("testSign", "hash="+hash+", "+hash2);
                Log.i("testSign", "sign1="+sign1+", "+sign2);

                ECKeyPair pair = ECKeyPair.create(new BigInteger("7a769ae26c9605c84b602593c919915498fd5923fc8b9ed9403d9165a3e8b8c1", 16));


                Sign.SignatureData signatureData = Sign.signPrefixedMessage(Numeric.hexStringToByteArray(message), pair);

                Log.i("testSign", "sign1="+sign1+", "+sign2);

                byte[] r = signatureData.getR();
                byte[] s = signatureData.getS();

                String rS = Numeric.toHexString(r);
                String sS = Numeric.toHexStringNoPrefix(s);
                String vS = Numeric.toHexStringNoPrefix(signatureData.getV());
                String siganature = rS+sS+vS;
                Log.i("testSign", rS+"---"+sS+", "+vS);
                boolean result = validate(siganature, message2, "0x8aB9f0f74B39211c3D26101367010896E905baaC");
                Log.i("testSign", "result="+result);


                
                String signature="0x53ea88d24f4ef8cdcc4bcc843912510b065cd6014c453ff61316c4cd75162f0a38f83a2103da028fb8e5181292ba194b0c8aa21a9ddacdf6783ebfa608889d121c";
                
                String message3="Hello Dapp";
                
                String address="0xc290436b3da897115493a1547B52783c50f0Bef3";
                boolean result2 = validate(signature, message3, address);
                Log.i("testSign", "result2="+result2);


                
                Web3j web3j =  Web3j.build(new HttpService("https://mainnet.infura.io/v3/cf728b87bfed4390ade4ea1ab885c5bb"));
                try {

                    String signagturne = web3j.ethSign("0x8aB9f0f74B39211c3D26101367010896E905baaC", Hash.sha3(message)).send().getSignature();
                    Log.i("testSign", "signagturne="+signagturne);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("testSign", "ssss"+e);
                }





            }
        });


    }

    static byte getRealV(BigInteger bv) {
        long v = bv.longValue();
        if (v == LOWER_REAL_V || v == (LOWER_REAL_V + 1)) {
            return (byte) v;
        }
        byte realV = LOWER_REAL_V;
        int inc = 0;
        if ((int) v % 2 == 0) {
            inc = 1;
        }
        return (byte) (realV + inc);
    }

    
    public static final String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    public static boolean validate(String signature, String message, String address) {
        
        
        
        
        
        
        
        
        String prefix = PERSONAL_MESSAGE_PREFIX + message.length();
        byte[] msgHash = Hash.sha3((prefix + message).getBytes());

        byte[] signatureBytes = Numeric.hexStringToByteArray(signature);
        byte v = signatureBytes[64];
        if (v < 27) {
            v += 27;
        }

        Sign.SignatureData sd = new Sign.SignatureData(
                v,
                Arrays.copyOfRange(signatureBytes, 0, 32),
                Arrays.copyOfRange(signatureBytes, 32, 64));

        String addressRecovered = null;
        boolean match = false;

        
        for (int i = 0; i < 4; i++) {
            BigInteger publicKey = Sign.recoverFromSignature(
                    (byte) i,
                    new ECDSASignature(new BigInteger(1, sd.getR()), new BigInteger(1, sd.getS())),
                    msgHash);

            if (publicKey != null) {
                addressRecovered = "0x" + Keys.getAddress(publicKey);

                if (addressRecovered.equals(address)) {
                    match = true;
                    break;
                }
            }
        }
        return match;
    }


    public static void testWeb3jTransfer () {
        String privateKey = "1382245507db020aa1017fcc1de638331cc5d1f0bc67972c39de392708cfaf2a";
        LogUtil.i("--------------------");
        ECKeyPair pair = ECKeyPair.create(new BigInteger(privateKey, 16));
        Credentials credentials = Credentials.create(pair);

        String contractAddr = "0xb0AD0E7648bCD92Ff0C369a8e5d9f435530d5bdc";
        
        Web3j web3j =  Web3j.build(new HttpService("https://ropsten.infura.io/v3/b4064cbac20e4740ba2e9acef569d129"));
        TransactionManager transactionManager = new RawTransactionManager(web3j, credentials, ChainIdLong.ROPSTEN);
        Transfer transfer = new Transfer(web3j, transactionManager);
        String to = "0x8b4d3a59F621d885475b3Bce231baE4f94E6488d";
        try {
            TransactionReceipt transactionReceiptRemoteCall = transfer.sendFunds(to, new BigDecimal("0.1"), Convert.Unit.ETHER).send();
            Log.i("testDappSen", "："+transactionReceiptRemoteCall.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
