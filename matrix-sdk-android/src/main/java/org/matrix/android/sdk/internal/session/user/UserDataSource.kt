

package org.matrix.android.sdk.internal.session.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.zhuinden.monarchy.Monarchy
import io.realm.Case
import io.realm.kotlin.where
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.api.util.Optional
import org.matrix.android.sdk.api.util.toOptional
import org.matrix.android.sdk.internal.database.RealmSessionProvider
import org.matrix.android.sdk.internal.database.mapper.asDomain
import org.matrix.android.sdk.internal.database.model.IgnoredUserEntity
import org.matrix.android.sdk.internal.database.model.IgnoredUserEntityFields
import org.matrix.android.sdk.internal.database.model.RemarkEntity
import org.matrix.android.sdk.internal.database.model.RemarkEntityFields
import org.matrix.android.sdk.internal.database.model.UserEntity
import org.matrix.android.sdk.internal.database.model.UserEntityFields
import org.matrix.android.sdk.internal.database.query.where
import org.matrix.android.sdk.internal.di.SessionDatabase
import javax.inject.Inject

internal class UserDataSource @Inject constructor(@SessionDatabase private val monarchy: Monarchy,
                                                  private val realmSessionProvider: RealmSessionProvider) {

    private val realmDataSourceFactory: Monarchy.RealmDataSourceFactory<UserEntity> by lazy {
        monarchy.createDataSourceFactory { realm ->
            realm.where(UserEntity::class.java)
                    .isNotEmpty(UserEntityFields.USER_ID)
                    .sort(UserEntityFields.DISPLAY_NAME)
        }
    }

    private val domainDataSourceFactory: DataSource.Factory<Int, User> by lazy {
        realmDataSourceFactory.map {
            it.asDomain()
        }
    }

    private val livePagedListBuilder: LivePagedListBuilder<Int, User> by lazy {
        LivePagedListBuilder(domainDataSourceFactory, PagedList.Config.Builder().setPageSize(100).setEnablePlaceholders(false).build())
    }

    fun getUser(userId: String): User? {
        return realmSessionProvider.withRealm {
            val userEntity = UserEntity.where(it, userId).findFirst()
            userEntity?.asDomain()
        }
    }

    fun getUserLive(userId: String): LiveData<Optional<User>> {
        val liveData = monarchy.findAllMappedWithChanges(
                { UserEntity.where(it, userId) },
                { it.asDomain() }
        )
        return Transformations.map(liveData) { results ->
            results.firstOrNull().toOptional()
        }
    }

    fun getUsersLive(): LiveData<List<User>> {
        return monarchy.findAllMappedWithChanges(
                { realm ->
                    realm.where(UserEntity::class.java)
                            .isNotEmpty(UserEntityFields.USER_ID)
                            .sort(UserEntityFields.DISPLAY_NAME)
                },
                { it.asDomain() }
        )
    }

    fun getPagedUsersLive(filter: String?, excludedUserIds: Set<String>?): LiveData<PagedList<User>> {
        realmDataSourceFactory.updateQuery { realm ->
            val query = realm.where(UserEntity::class.java)
            if (filter.isNullOrEmpty()) {
                query.isNotEmpty(UserEntityFields.USER_ID)
            } else {
                query
                        .beginGroup()
                        .contains(UserEntityFields.DISPLAY_NAME, filter, Case.INSENSITIVE)
                        .or()
                        .contains(UserEntityFields.USER_ID, filter)
                        .or()
                        .contains(UserEntityFields.TEL_NUMBERS.`$`, filter)
                        .endGroup()
            }
            excludedUserIds
                    ?.takeIf { it.isNotEmpty() }
                    ?.let {
                        query.not().`in`(UserEntityFields.USER_ID, it.toTypedArray())
                    }
            query.sort(UserEntityFields.DISPLAY_NAME)
        }
        return monarchy.findAllPagedWithChanges(realmDataSourceFactory, livePagedListBuilder)
    }

    fun getIgnoredUsersLive(): LiveData<List<User>> {
        return monarchy.findAllMappedWithChanges(
                { realm ->
                    realm.where(IgnoredUserEntity::class.java)
                            .isNotEmpty(IgnoredUserEntityFields.USER_ID)
                            .sort(IgnoredUserEntityFields.USER_ID)
                },
                { getUser(it.userId) ?: User(userId = it.userId) }
        )
    }

    
    fun updateRemark(userId: String, remark: String)  {
        realmSessionProvider.withRealm{realm ->realm.executeTransactionAsync { it.insertOrUpdate(RemarkEntity(userId, remark,0)) } }
    }

    
    fun getRemark(userId: String): String{
       return realmSessionProvider.withRealm {
           var remarkEntity = it.where<RemarkEntity>().equalTo(RemarkEntityFields.USER_ID, userId).findFirst()
           remarkEntity?.remark?:""
        }
    }
}
