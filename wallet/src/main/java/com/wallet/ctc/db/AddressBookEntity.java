

package com.wallet.ctc.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;


@Entity
public class AddressBookEntity {
    @Id(autoincrement = true)
    private Long id;  
    private int logo;
    private String name;
    private String address;
    private String remark;

    @Generated(hash = 1599330940)
    public AddressBookEntity(Long id, int logo, String name, String address,
            String remark) {
        this.id = id;
        this.logo = logo;
        this.name = name;
        this.address = address;
        this.remark = remark;
    }

    @Generated(hash = 808216715)
    public AddressBookEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
