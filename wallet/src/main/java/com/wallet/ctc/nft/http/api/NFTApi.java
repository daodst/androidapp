

package com.wallet.ctc.nft.http.api;

import com.wallet.ctc.db.NftBean;
import com.wallet.ctc.nft.bean.MetadataBean;
import com.wallet.ctc.nft.bean.NtfPage;
import com.wallet.ctc.nft.http.annotation.Host;

import common.app.BuildConfig;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

@Host(BuildConfig.NFT_URL + "api/v2/")
public interface NFTApi {

    
    @GET("{address}/nft")
    @Headers("X-API-Key:" + BuildConfig.NFT_API_KEY)
    Observable<NtfPage<NftBean>> getOwnedNtf(@Path("address") String address, @Query("chain") String chain, @Query("format") String format);


    
    @GET("{address}/nft/{token_address}")
    @Headers("X-API-Key:" + BuildConfig.NFT_API_KEY)
    Observable<NtfPage<NftBean>> getNftAssets(@Path("address") String address, @Path("token_address") String token_address, @Query("chain") String chain, @Query("format") String format);

    
    @GET("nft/{address}/metadata")
    @Headers("X-API-Key:" + BuildConfig.NFT_API_KEY)
    Observable<NftBean> getNftMetadata(@Path("address") String address, @Query("chain") String chain);

    
    @GET
    Observable<MetadataBean> getMetadata(@Url String url);

}
