

package com.wallet.ctc.DMTransaction;

import com.wallet.ctc.util.LogUtil;

import org.web3j.crypto.ECKeyPair;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.List;



public class DMTransactionEncoder {
    public static String signMessage(String rawTransaction, ECKeyPair credentials) {
        byte[] encodedTransaction = rawTransaction.getBytes();
        DMSign.SignatureData signatureData = DMSign.signMessage(
                encodedTransaction, credentials);
        byte[] bt3 = new byte[1];
        int recId =signatureData.getV() - 27;
        LogUtil.d("recId"+recId);
        bt3[0] = (byte)recId;
        byte[] byte_3 = new byte[signatureData.getR().length + signatureData.getS().length + 1];
        System.arraycopy(signatureData.getR(), 0, byte_3, 0, signatureData.getR().length);
        System.arraycopy(signatureData.getS(), 0, byte_3, signatureData.getR().length, signatureData.getS().length);
        System.arraycopy(bt3, 0, byte_3, signatureData.getS().length + signatureData.getR().length, 1);
        return Numeric.toHexString(byte_3);
    }





    public static byte[] encode(String rawTransaction) {
        return encode(rawTransaction, null);
    }

    public static byte[] encode(String rawTransaction, byte chainId) {
        DMSign.SignatureData signatureData = new DMSign.SignatureData(
                chainId, new byte[] {}, new byte[] {});
        return encode(rawTransaction, signatureData);
    }

    private static byte[] encode(String rawTransaction, DMSign.SignatureData signatureData) {
        List<RlpType> values = asRlpValues(rawTransaction, signatureData);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    static List<RlpType> asRlpValues(
            String rawTransaction, DMSign.SignatureData signatureData) {
        List<RlpType> result = new ArrayList<RlpType>();
        
        byte[] data = Numeric.hexStringToByteArray(rawTransaction);
        result.add(RlpString.create(data));

        if (signatureData != null) {
            result.add(RlpString.create(signatureData.getV()));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
            result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
        }

        return result;
    }
}
