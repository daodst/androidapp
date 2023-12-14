

package com.wallet.ctc.api.me;



public class MeHttpUtil {
    public static final String GET_ARTICLE_LIST = "basic/article/Lists";
    public static final String GET_HELP_LIST = "basic/help/Lists";
    public static final String GET_POST_ARTICLE_LIST = "user/article/Lists";
    public static final String GET_POST_GROUP_ARTICLE_LIST = "user/grouparticle/ListsMy";
    public static final String ADD_POST_ARTICLE = "user/article/Add";
    public static final String EDIT_POST_ARTICLE = "user/article/Edit";
    public static final String GET_TXID = "user/candy/GetMccTxid";
    public static final String ADD_POST_GROUP_ARTICLE = "user/grouparticle/Add";
    public static final String EDIT_POST_GROUP_ARTICLE = "user/grouparticle/Edit";
    public static final String GET_LIHAOLIKONG="basic/article/ArticleOperate";
    public static final String GET_POST_ARTICLE_DETAIL = "user/article/Info";
    public static final String LOGIN_REGIST="user/register/SignInByKey";
    public static final String DEL_POST_ARTICLE = "user/article/Del";
    public static final String GET_ARTICLE = "user/user/GetAgreement";
    public static final String GET_ARTICLE_INFO = "basic/article/Info";
    public static final String GET_HELP_INFO = "basic/help/Info";
    public static final String GET_BANLANCE="purse/getbalance";
    public static final String GET_BANLANCES="purse/getbalances";

    public static final String ADD_BTC_ADDRESS="purse/omni/addaddress";
    public static final String GET_BTC_BANLANCE="purse/getbtcbalance";
    public static final String GET_BTC_WEEK_DAY="purse/getbtcdayshistroy";
    public static final String GET_BTC_TRADSACTION_LIST="purse/getbtcaddresshistory";
    public static final String GET_BTC_TRANSFER="purse/sendbtcrawtransaction";
    public static final String GET_BTC_FEES="purse/getbtcfeebase";
    public static final String GET_BTC_TRANSFER_DATA="purse/getbtctransferdata";

    public static final String GET_BTC_MINI_BANLANCE="purse/omni/getbalance";
    public static final String GET_BTC_MINI_TRADSACTION_LIST="purse/omni/getAddressHistory";
    public static final String GET_BTC_MINI_WEEK_DAY="purse/omni/getdayshistroy";


    public static final String GET_PRICE="market/getprice";
    public static final String GET_MARKET_PRICE="market/index";
    public static final String GET_TRANSFER="purse/sendRawTransaction";
    public static final String GET_KLINE="market/kline";
    public static final String PUSH_CON="user/feedback/Add";
    public static final String SEARCH_TOKEN="purse/searchcontract";
    public static final String GET_TRADSACTION_LIST ="purse/getaddresshistory";
    public static final String GET_NONCE="purse/gettransactioncount";
    public static final String NOTICE_LIST = "basic/notice/Lists";
    public static final String NOTICE_DETAIL = "basic/notice/Info";

    public static final String GET_GAS_PRICE = "purse/getextremegasprice";
    public static final String GET_GAS_DEF_PRICE = "purse/getsuggestgasprice";
    public static final String GET_GAS_DEF_PRICE2 = "purse/getsuggestgasprice?send=yes";
    public static final String GET_GAS="purse/getestimategas";
    public static final String CREAT_ETH ="purse/getethcontractcode";


    public static final String GET_TRANSFER_BTC="basic/coin/TakeCash";

    public static final String GET_WEEK_TRANSFER="purse/getdayshistroy";
    public static final String ADD_ADDRESS="purse/addaddress";

    public static final String GET_CHAINS="purse/getchains";


    public static final String GET_WALLET_QUOTES_LIST ="market/index";
    public static final String GET_DEF_ASSETS ="basic/asset/Lists";

    public static final String GET_SHARE_DATA="user/share/GetShareData";


    public static final String GET_ADDRESS="user/address/GetAddress";
    public static final String SET_ADDRESS="user/address/SetAddress";
    public static final String GET_SYS_ADDRESS="user/address/SetAddress";

    public static final String GET_CANDY_LIST="user/candy/Lists";
    public static final String GET_LINCANDY_LIST="user/candy/ReceiveLists";
    public static final String GET_SEND_CANDY_LIST="user/candy/IssueLists";
    public static final String GET_CANDY_INFO="user/candy/Preview";
    public static final String GET_CANDY_SEND="user/candy/Issue";
    public static final String GET_CANDY="user/candy/Receive";
    public static final String GET_CANDY_DETAIL="user/candy/Candy";
    public static final String GET_CANDY_SXF="user/candy/ReplenishPoundage";
    public static final String GET_CANDY_SYDB="user/candy/Withdraw";

    public static final String GET_POSTER_LIST="basic/poster/Lists";

    public static final String GET_ETH_CREATE_PRICE="user/coin/EthGasLimit";

    public static final String GET_TRANSFER_HASH="purse/gettranrecbyhash";

    public static final String GET_COMMAND_LIST="basic/article/ArticleRemarkLists";
    public static final String ADD_ARTICLE_LIKE="basic/article/ArticleLike";
    public static final String ADD_COMMAND="basic/article/ArticleRemark";
    public static final String DEL_COMMAND="basic/article/ArticleRemarkDelete";
    public static final String DEL_COMMAND_REPLY="basic/article/ArticleReplyDelete";
    public static final String ADD_COMMAND_REPLY="basic/article/ArticleReply";
    public static final String ADD_COMMAND_REPLY_REPLY="basic/article/ArticleReplyReply";
    public static final String GET_COMMAND_REPLY_LIST="basic/article/ArticleReplyLists";
    public static final String GET_COMMAND_DETAIL="basic/article/ArticleRemarkInfo";

    

    public static final String CHECK_USER_AUTH="user/auth/ChkUserAuth";
    public static final String GET_USER_AUTH="user/auth/GetUserAuth";


    public static final String GET_LOCALS="market/getlocals";
    public static final String GET_CURRENCYS="market/getcurrencys";


    
    public static final String GET_XRP_BALANCE = "purse/xrp/getbalance";
    public static final String GET_XRP_TOKEN_BALANCE = "purse/xrp/gettokenbalance";
    public static final String GET_XRP_TRNAS_HISTORY = "purse/xrp/getaccounttxs";
    public static final String GET_XRP_WEEK_TRANSFER = "purse/xrp/getdayshistroy";
    public static final String GET_XRP_TOKEN_WEEK_TRANSFER = "purse/xrp/getcurrencydayshistroy";
    public static final String ADD_XRP_ADDRESS = "purse/xrp/addaddress";
    public static final String GET_XRP_TRANS_FEE = "purse/xrp/getfeeinfo";
    public static final String GET_XRP_TRANS_SEQUENCE = "purse/xrp/getsequence";
    public static final String SEND_XRP_TRANSFER = "purse/xrp/sendtransaction";
    public static final String SEARCH_XRP_TOKEN = "purse/xrp/searchcontract";

    
    public static final String ETHGASPRICE = "purse/ethgasprice";
    public static final String ETHGASLIMIT = "purse/ethgaslimit";
}
