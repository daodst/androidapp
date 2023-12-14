

package com.wallet.ctc.ui.dapp.contract;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;


@SuppressWarnings("rawtypes")
public class CosmosToken_sol_CosmosToken extends Contract {
    private static final String BINARY = "60806040523480156200001157600080fd5b5060405162001e4a38038062001e4a8339810160408190526200003491620002da565b8251839083906200004d9060039060208501906200017d565b508051620000639060049060208401906200017d565b5050506000620000786200010760201b60201c565b600580546001600160a01b0319166001600160a01b038316908117909155604051919250906000907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908290a3506005805460ff60a01b1916600160a01b60ff841602179055620000fd6006620000ec3390565b6200010b60201b62000d2f1760201c565b50505050620003b2565b3390565b600062000122836001600160a01b0384166200012b565b90505b92915050565b6000818152600183016020526040812054620001745750815460018181018455600084815260208082209093018490558454848252828601909352604090209190915562000125565b50600062000125565b8280546200018b906200035f565b90600052602060002090601f016020900481019282620001af5760008555620001fa565b82601f10620001ca57805160ff1916838001178555620001fa565b82800160010185558215620001fa579182015b82811115620001fa578251825591602001919060010190620001dd565b50620002089291506200020c565b5090565b5b808211156200020857600081556001016200020d565b600082601f8301126200023557600080fd5b81516001600160401b03808211156200025257620002526200039c565b604051601f8301601f19908116603f011681019082821181831017156200027d576200027d6200039c565b816040528381526020925086838588010111156200029a57600080fd5b600091505b83821015620002be57858201830151818301840152908201906200029f565b83821115620002d05760008385830101525b9695505050505050565b600080600060608486031215620002f057600080fd5b83516001600160401b03808211156200030857600080fd5b620003168783880162000223565b945060208601519150808211156200032d57600080fd5b506200033c8682870162000223565b925050604084015160ff811681146200035457600080fd5b809150509250925092565b600181811c908216806200037457607f821691505b602082108114156200039657634e487b7160e01b600052602260045260246000fd5b50919050565b634e487b7160e01b600052604160045260246000fd5b611a8880620003c26000396000f3fe608060405234801561001057600080fd5b50600436106101735760003560e01c8063715018a6116100de578063983b2d5611610097578063aa271e1a11610071578063aa271e1a1461033a578063c2acc5cf1461034d578063dd62ed3e14610360578063f2fde38b1461039957600080fd5b8063983b2d5614610301578063a457c2d714610314578063a9059cbb1461032757600080fd5b8063715018a6146102a557806379c65068146102af57806379cc6790146102c25780637cfb1255146102d55780638da5cb5b146102e857806395d89b41146102f957600080fd5b8063313ce56711610130578063313ce567146101f9578063395093511461021857806340c10f191461022b57806342966c681461023e5780635b7121f81461025157806370a082311461027c57600080fd5b80630323aac71461017857806306fdde0314610193578063095ea7b3146101a857806318160ddd146101cb57806323338b88146101d357806323b872dd146101e6575b600080fd5b6101806103ac565b6040519081526020015b60405180910390f35b61019b6103bd565b60405161018a9190611843565b6101bb6101b6366004611608565b61044f565b604051901515815260200161018a565b600254610180565b6101bb6101e136600461157e565b610466565b6101bb6101f43660046115cc565b610513565b600554600160a01b900460ff1660405160ff909116815260200161018a565b6101bb610226366004611608565b6105c5565b6101bb610239366004611608565b6105fc565b6101bb61024c3660046116f9565b61062d565b61026461025f3660046116f9565b610641565b6040516001600160a01b03909116815260200161018a565b61018061028a36600461157e565b6001600160a01b031660009081526020819052604090205490565b6102ad6106dc565b005b6101bb6102bd366004611608565b610750565b6101bb6102d0366004611608565b6107ea565b6101bb6102e3366004611712565b610872565b6005546001600160a01b0316610264565b61019b6108ed565b6101bb61030f36600461157e565b6108fc565b6101bb610322366004611608565b610998565b6101bb610335366004611608565b610a29565b6101bb61034836600461157e565b610a36565b6101bb61035b366004611632565b610a43565b61018061036e366004611599565b6001600160a01b03918216600090815260016020908152604080832093909416825291909152205490565b6102ad6103a736600461157e565b610c44565b60006103b86006610d4b565b905090565b6060600380546103cc906119a4565b80601f01602080910402602001604051908101604052809291908181526020018280546103f8906119a4565b80156104455780601f1061041a57610100808354040283529160200191610445565b820191906000526020600020905b81548152906001019060200180831161042857829003601f168201915b5050505050905090565b600061045c338484610d55565b5060015b92915050565b6005546000906001600160a01b0316331461049c5760405162461bcd60e51b8152600401610493906118b6565b60405180910390fd5b6001600160a01b0382166105005760405162461bcd60e51b815260206004820152602560248201527f546f6b656e3a205f64656c4d696e74657220697320746865207a65726f206164604482015264647265737360d81b6064820152608401610493565b61050b600683610e7a565b90505b919050565b6000610520848484610e8f565b6001600160a01b0384166000908152600160209081526040808320338452909152902054828110156105a55760405162461bcd60e51b815260206004820152602860248201527f45524332303a207472616e7366657220616d6f756e74206578636565647320616044820152676c6c6f77616e636560c01b6064820152608401610493565b6105ba85335b6105b5868561198d565b610d55565b506001949350505050565b3360008181526001602090815260408083206001600160a01b0387168452909152812054909161045c9185906105b5908690611975565b600061060733610a36565b6106235760405162461bcd60e51b81526004016104939061187f565b61045c8383611067565b60006106393383611146565b506001919050565b6005546000906001600160a01b0316331461066e5760405162461bcd60e51b8152600401610493906118b6565b60016106786103ac565b610682919061198d565b8211156106d15760405162461bcd60e51b815260206004820152601b60248201527f546f6b656e3a205f696e646578206f7574206f6620626f756e647300000000006044820152606401610493565b61050b600683611295565b6005546001600160a01b031633146107065760405162461bcd60e51b8152600401610493906118b6565b6005546040516000916001600160a01b0316907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e0908390a3600580546001600160a01b0319169055565b600061075b33610a36565b6107775760405162461bcd60e51b81526004016104939061187f565b600082116107975760405162461bcd60e51b815260040161049390611856565b6107a18383611067565b60405182815230906001600160a01b038516907fb2c0ac17769dc4009cb3bb7bc94bf96c53610105f662c66592910dd10319dcf89060200160405180910390a350600192915050565b6000806107f7843361036e565b9050828110156108545760405162461bcd60e51b815260206004820152602260248201527f6275726e46726f6d205f616d6f756e74206578636565647320616c6c6f77616e604482015261636560f01b6064820152608401610493565b61085e84336105ab565b6108688484611146565b5060019392505050565b60008084116108935760405162461bcd60e51b815260040161049390611856565b61089d3385611146565b30336001600160a01b03167fa7fcce4e8fc824b4e1a280a370d72898aaea3579723f46f5e5a5786082239f618686866040516108db939291906118eb565b60405180910390a35060019392505050565b6060600480546103cc906119a4565b6005546000906001600160a01b031633146109295760405162461bcd60e51b8152600401610493906118b6565b6001600160a01b03821661098d5760405162461bcd60e51b815260206004820152602560248201527f546f6b656e3a205f6164644d696e74657220697320746865207a65726f206164604482015264647265737360d81b6064820152608401610493565b61050b600683610d2f565b3360009081526001602090815260408083206001600160a01b038616845290915281205482811015610a1a5760405162461bcd60e51b815260206004820152602560248201527f45524332303a2064656372656173656420616c6c6f77616e63652062656c6f77604482015264207a65726f60d81b6064820152608401610493565b61086833856105b5868561198d565b600061045c338484610e8f565b600061050b6006836112a1565b6000610a4e33610a36565b610a6a5760405162461bcd60e51b81526004016104939061187f565b8151835114610ac65760405162461bcd60e51b815260206004820152602260248201527f5f746f7320616e64205f616d6f756e7473206c656e677468206e6f7420657175604482015261185b60f21b6064820152608401610493565b6000835111610b0d5760405162461bcd60e51b81526020600482015260136024820152725f746f73206c656e677468206973207a65726f60681b6044820152606401610493565b60005b8351811015610bf7576000848281518110610b2d57610b2d611a26565b6020026020010151905060006001600160a01b0316816001600160a01b03161415610b9a5760405162461bcd60e51b815260206004820152601760248201527f5f746f20697320746865207a65726f20616464726573730000000000000000006044820152606401610493565b6000848381518110610bae57610bae611a26565b6020026020010151905060008111610bd85760405162461bcd60e51b815260040161049390611856565b610be28282611067565b50508080610bef906119df565b915050610b10565b50306001600160a01b03167fdb473e57456ea5eaf2d3512239ea384e20fa7c9ace9d9d06e62d6cda9b7538e58484604051610c339291906117cc565b60405180910390a250600192915050565b6005546001600160a01b03163314610c6e5760405162461bcd60e51b8152600401610493906118b6565b6001600160a01b038116610cd35760405162461bcd60e51b815260206004820152602660248201527f4f776e61626c653a206e6577206f776e657220697320746865207a65726f206160448201526564647265737360d01b6064820152608401610493565b6005546040516001600160a01b038084169216907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a3600580546001600160a01b0319166001600160a01b0392909216919091179055565b6000610d44836001600160a01b0384166112c3565b9392505050565b600061050b825490565b6001600160a01b038316610db75760405162461bcd60e51b8152602060048201526024808201527f45524332303a20617070726f76652066726f6d20746865207a65726f206164646044820152637265737360e01b6064820152608401610493565b6001600160a01b038216610e185760405162461bcd60e51b815260206004820152602260248201527f45524332303a20617070726f766520746f20746865207a65726f206164647265604482015261737360f01b6064820152608401610493565b6001600160a01b0383811660008181526001602090815260408083209487168084529482529182902085905590518481527f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b92591015b60405180910390a3505050565b6000610d44836001600160a01b038416611312565b6001600160a01b038316610ef35760405162461bcd60e51b815260206004820152602560248201527f45524332303a207472616e736665722066726f6d20746865207a65726f206164604482015264647265737360d81b6064820152608401610493565b6001600160a01b038216610f555760405162461bcd60e51b815260206004820152602360248201527f45524332303a207472616e7366657220746f20746865207a65726f206164647260448201526265737360e81b6064820152608401610493565b6001600160a01b03831660009081526020819052604090205481811015610fcd5760405162461bcd60e51b815260206004820152602660248201527f45524332303a207472616e7366657220616d6f756e7420657863656564732062604482015265616c616e636560d01b6064820152608401610493565b610fd7828261198d565b6001600160a01b03808616600090815260208190526040808220939093559085168152908120805484929061100d908490611975565b92505081905550826001600160a01b0316846001600160a01b03167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef8460405161105991815260200190565b60405180910390a350505050565b6001600160a01b0382166110bd5760405162461bcd60e51b815260206004820152601f60248201527f45524332303a206d696e7420746f20746865207a65726f2061646472657373006044820152606401610493565b80600260008282546110cf9190611975565b90915550506001600160a01b038216600090815260208190526040812080548392906110fc908490611975565b90915550506040518181526001600160a01b038316906000907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9060200160405180910390a35050565b6001600160a01b0382166111a65760405162461bcd60e51b815260206004820152602160248201527f45524332303a206275726e2066726f6d20746865207a65726f206164647265736044820152607360f81b6064820152608401610493565b6001600160a01b0382166000908152602081905260409020548181101561121a5760405162461bcd60e51b815260206004820152602260248201527f45524332303a206275726e20616d6f756e7420657863656564732062616c616e604482015261636560f01b6064820152608401610493565b611224828261198d565b6001600160a01b0384166000908152602081905260408120919091556002805484929061125290849061198d565b90915550506040518281526000906001600160a01b038516907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef90602001610e6d565b6000610d4483836113ff565b6001600160a01b03811660009081526001830160205260408120541515610d44565b600081815260018301602052604081205461130a57508154600181810184556000848152602080822090930184905584548482528286019093526040902091909155610460565b506000610460565b600081815260018301602052604081205480156113f557600061133660018361198d565b855490915060009061134a9060019061198d565b9050600086600001828154811061136357611363611a26565b906000526020600020015490508087600001848154811061138657611386611a26565b6000918252602080832090910192909255828152600189019091526040902084905586548790806113b9576113b9611a10565b60019003818190600052602060002001600090559055866001016000878152602001908152602001600020600090556001945050505050610460565b6000915050610460565b8154600090821061145d5760405162461bcd60e51b815260206004820152602260248201527f456e756d657261626c655365743a20696e646578206f7574206f6620626f756e604482015261647360f01b6064820152608401610493565b82600001828154811061147257611472611a26565b9060005260206000200154905092915050565b80356001600160a01b038116811461050e57600080fd5b600082601f8301126114ad57600080fd5b813560206114c26114bd83611951565b611920565b80838252828201915082860187848660051b89010111156114e257600080fd5b60005b85811015611501578135845292840192908401906001016114e5565b5090979650505050505050565b600082601f83011261151f57600080fd5b813567ffffffffffffffff81111561153957611539611a3c565b61154c601f8201601f1916602001611920565b81815284602083860101111561156157600080fd5b816020850160208301376000918101602001919091529392505050565b60006020828403121561159057600080fd5b610d4482611485565b600080604083850312156115ac57600080fd5b6115b583611485565b91506115c360208401611485565b90509250929050565b6000806000606084860312156115e157600080fd5b6115ea84611485565b92506115f860208501611485565b9150604084013590509250925092565b6000806040838503121561161b57600080fd5b61162483611485565b946020939093013593505050565b6000806040838503121561164557600080fd5b823567ffffffffffffffff8082111561165d57600080fd5b818501915085601f83011261167157600080fd5b813560206116816114bd83611951565b8083825282820191508286018a848660051b89010111156116a157600080fd5b600096505b848710156116cb576116b781611485565b8352600196909601959183019183016116a6565b50965050860135925050808211156116e257600080fd5b506116ef8582860161149c565b9150509250929050565b60006020828403121561170b57600080fd5b5035919050565b60008060006060848603121561172757600080fd5b83359250602084013567ffffffffffffffff8082111561174657600080fd5b6117528783880161150e565b9350604086013591508082111561176857600080fd5b506117758682870161150e565b9150509250925092565b6000815180845260005b818110156117a557602081850181015186830182015201611789565b818111156117b7576000602083870101525b50601f01601f19169290920160200192915050565b604080825283519082018190526000906020906060840190828701845b8281101561180e5781516001600160a01b0316845292840192908401906001016117e9565b5050508381038285015284518082528583019183019060005b8181101561150157835183529284019291840191600101611827565b602081526000610d44602083018461177f565b6020808252600f908201526e5f616d6f756e74206973207a65726f60881b604082015260600190565b60208082526018908201527f63616c6c6572206973206e6f7420746865206d696e7465720000000000000000604082015260600190565b6020808252818101527f4f776e61626c653a2063616c6c6572206973206e6f7420746865206f776e6572604082015260600190565b838152606060208201526000611904606083018561177f565b8281036040840152611916818561177f565b9695505050505050565b604051601f8201601f1916810167ffffffffffffffff8111828210171561194957611949611a3c565b604052919050565b600067ffffffffffffffff82111561196b5761196b611a3c565b5060051b60200190565b60008219821115611988576119886119fa565b500190565b60008282101561199f5761199f6119fa565b500390565b600181811c908216806119b857607f821691505b602082108114156119d957634e487b7160e01b600052602260045260246000fd5b50919050565b60006000198214156119f3576119f36119fa565b5060010190565b634e487b7160e01b600052601160045260246000fd5b634e487b7160e01b600052603160045260246000fd5b634e487b7160e01b600052603260045260246000fd5b634e487b7160e01b600052604160045260246000fdfea2646970667358221220079751988bde48bd3c39619f678f847a2e52a839aac3164108dfd632f96219e964736f6c63430008060033";

    public static final String FUNC_ADDMINTER = "addMinter";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_BURNFROM = "burnFrom";

    public static final String FUNC_BURNTOKEN = "burnToken";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_DECREASEALLOWANCE = "decreaseAllowance";

    public static final String FUNC_DELMINTER = "delMinter";

    public static final String FUNC_GETMINTER = "getMinter";

    public static final String FUNC_GETMINTERLENGTH = "getMinterLength";

    public static final String FUNC_INCREASEALLOWANCE = "increaseAllowance";

    public static final String FUNC_ISMINTER = "isMinter";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_MINTTOKEN = "mintToken";

    public static final String FUNC_MINTTOKENS = "mintTokens";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final String FUNC_TRANSFEROWNERSHIP = "transferOwnership";

    public static final Event APPROVAL_EVENT = new Event("Approval",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event BURNTOKEN_EVENT = new Event("BurnToken",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    public static final Event MINTTOKEN_EVENT = new Event("MintToken",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event MINTTOKENS_EVENT = new Event("MintTokens",
            Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}, new TypeReference<Address>(true) {}, new TypeReference<DynamicArray<Uint256>>() {}));
    ;

    public static final Event OWNERSHIPTRANSFERRED_EVENT = new Event("OwnershipTransferred",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected CosmosToken_sol_CosmosToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Override
    protected <T extends Type> RemoteFunctionCall<T> executeRemoteCallSingleValueReturn(org.web3j.abi.datatypes.Function function) {
        return super.executeRemoteCallSingleValueReturn(function);
    }

    protected CosmosToken_sol_CosmosToken(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CosmosToken_sol_CosmosToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CosmosToken_sol_CosmosToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public List<BurnTokenEventResponse> getBurnTokenEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(BURNTOKEN_EVENT, transactionReceipt);
        ArrayList<BurnTokenEventResponse> responses = new ArrayList<BurnTokenEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            BurnTokenEventResponse typedResponse = new BurnTokenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.token = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.targetAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.targetChain = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<BurnTokenEventResponse> burnTokenEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, BurnTokenEventResponse>() {
            @Override
            public BurnTokenEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(BURNTOKEN_EVENT, log);
                BurnTokenEventResponse typedResponse = new BurnTokenEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.token = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.targetAddress = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.targetChain = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<BurnTokenEventResponse> burnTokenEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BURNTOKEN_EVENT));
        return burnTokenEventFlowable(filter);
    }

    public List<MintTokenEventResponse> getMintTokenEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(MINTTOKEN_EVENT, transactionReceipt);
        ArrayList<MintTokenEventResponse> responses = new ArrayList<MintTokenEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            MintTokenEventResponse typedResponse = new MintTokenEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.token = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<MintTokenEventResponse> mintTokenEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, MintTokenEventResponse>() {
            @Override
            public MintTokenEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(MINTTOKEN_EVENT, log);
                MintTokenEventResponse typedResponse = new MintTokenEventResponse();
                typedResponse.log = log;
                typedResponse.to = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.token = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<MintTokenEventResponse> mintTokenEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINTTOKEN_EVENT));
        return mintTokenEventFlowable(filter);
    }

    public List<MintTokensEventResponse> getMintTokensEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(MINTTOKENS_EVENT, transactionReceipt);
        ArrayList<MintTokensEventResponse> responses = new ArrayList<MintTokensEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            MintTokensEventResponse typedResponse = new MintTokensEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.token = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.tos = (List<String>) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amounts = (List<BigInteger>) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<MintTokensEventResponse> mintTokensEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, MintTokensEventResponse>() {
            @Override
            public MintTokensEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(MINTTOKENS_EVENT, log);
                MintTokensEventResponse typedResponse = new MintTokensEventResponse();
                typedResponse.log = log;
                typedResponse.token = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.tos = (List<String>) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amounts = (List<BigInteger>) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<MintTokensEventResponse> mintTokensEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINTTOKENS_EVENT));
        return mintTokensEventFlowable(filter);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(OWNERSHIPTRANSFERRED_EVENT, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse.log = log;
                typedResponse.previousOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.newOwner = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<OwnershipTransferredEventResponse> ownershipTransferredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSHIPTRANSFERRED_EVENT));
        return ownershipTransferredEventFlowable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse apply(Log log) {
                EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> addMinter(String _addMinter) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ADDMINTER,
                Arrays.<Type>asList(new Address(160, _addMinter)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> allowance(String owner, String spender) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ALLOWANCE,
                Arrays.<Type>asList(new Address(160, owner),
                new Address(160, spender)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String spender, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_APPROVE,
                Arrays.<Type>asList(new Address(160, spender),
                new Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> balanceOf(String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BALANCEOF,
                Arrays.<Type>asList(new Address(160, account)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> burn(BigInteger _amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURN,
                Arrays.<Type>asList(new Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> burnFrom(String _from, BigInteger _amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURNFROM,
                Arrays.<Type>asList(new Address(160, _from),
                new Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> burnToken(BigInteger _amount, String targetAddress, String targetChain) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURNTOKEN,
                Arrays.<Type>asList(new Uint256(_amount),
                new Utf8String(targetAddress),
                new Utf8String(targetChain)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> decimals() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DECIMALS,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> decreaseAllowance(String spender, BigInteger subtractedValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DECREASEALLOWANCE,
                Arrays.<Type>asList(new Address(160, spender),
                new Uint256(subtractedValue)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> delMinter(String _delMinter) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DELMINTER,
                Arrays.<Type>asList(new Address(160, _delMinter)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> getMinter(BigInteger _index) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GETMINTER,
                Arrays.<Type>asList(new Uint256(_index)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> getMinterLength() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_GETMINTERLENGTH,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> increaseAllowance(String spender, BigInteger addedValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INCREASEALLOWANCE,
                Arrays.<Type>asList(new Address(160, spender),
                new Uint256(addedValue)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> isMinter(String account) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ISMINTER,
                Arrays.<Type>asList(new Address(160, account)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> mint(String _to, BigInteger _amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_MINT,
                Arrays.<Type>asList(new Address(160, _to),
                new Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> mintToken(String _to, BigInteger _amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_MINTTOKEN,
                Arrays.<Type>asList(new Address(160, _to),
                new Uint256(_amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> mintTokens(List<String> _tos, List<BigInteger> _amounts) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_MINTTOKENS,
                Arrays.<Type>asList(new DynamicArray<Address>(
                        Address.class,
                        org.web3j.abi.Utils.typeMap(_tos, Address.class)),
                new DynamicArray<Uint256>(
                        Uint256.class,
                        org.web3j.abi.Utils.typeMap(_amounts, Uint256.class))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> name() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_NAME,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_OWNER,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_RENOUNCEOWNERSHIP,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> symbol() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SYMBOL,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> totalSupply() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOTALSUPPLY,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String recipient, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFER,
                Arrays.<Type>asList(new Address(160, recipient),
                new Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String sender, String recipient, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERFROM,
                Arrays.<Type>asList(new Address(160, sender),
                new Address(160, recipient),
                new Uint256(amount)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferOwnership(String newOwner) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFEROWNERSHIP,
                Arrays.<Type>asList(new Address(160, newOwner)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static CosmosToken_sol_CosmosToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CosmosToken_sol_CosmosToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CosmosToken_sol_CosmosToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CosmosToken_sol_CosmosToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CosmosToken_sol_CosmosToken load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CosmosToken_sol_CosmosToken(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CosmosToken_sol_CosmosToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CosmosToken_sol_CosmosToken(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<CosmosToken_sol_CosmosToken> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String name_, String symbol_, BigInteger decimals_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Utf8String(name_),
                new Utf8String(symbol_),
                new org.web3j.abi.datatypes.generated.Uint8(decimals_)));
        return deployRemoteCall(CosmosToken_sol_CosmosToken.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<CosmosToken_sol_CosmosToken> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String name_, String symbol_, BigInteger decimals_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Utf8String(name_),
                new Utf8String(symbol_),
                new org.web3j.abi.datatypes.generated.Uint8(decimals_)));
        return deployRemoteCall(CosmosToken_sol_CosmosToken.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<CosmosToken_sol_CosmosToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String name_, String symbol_, BigInteger decimals_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Utf8String(name_),
                new Utf8String(symbol_),
                new org.web3j.abi.datatypes.generated.Uint8(decimals_)));
        return deployRemoteCall(CosmosToken_sol_CosmosToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<CosmosToken_sol_CosmosToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String name_, String symbol_, BigInteger decimals_) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new Utf8String(name_),
                new Utf8String(symbol_),
                new org.web3j.abi.datatypes.generated.Uint8(decimals_)));
        return deployRemoteCall(CosmosToken_sol_CosmosToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class BurnTokenEventResponse extends BaseEventResponse {
        public String from;

        public String token;

        public BigInteger amount;

        public String targetAddress;

        public String targetChain;
    }

    public static class MintTokenEventResponse extends BaseEventResponse {
        public String to;

        public String token;

        public BigInteger amount;
    }

    public static class MintTokensEventResponse extends BaseEventResponse {
        public String token;

        public List<String> tos;

        public List<BigInteger> amounts;
    }

    public static class OwnershipTransferredEventResponse extends BaseEventResponse {
        public String previousOwner;

        public String newOwner;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }
}
