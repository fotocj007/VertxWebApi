package com.webser.db;

import java.io.Serializable;

/**
 *
 * @param <ID> 主键
 * @param <K>  查询key名称
 */

public abstract class BaseEntity<ID extends Serializable,K extends Serializable> {
    public abstract ID getId();

    public abstract void setId(ID id);
    // 分库分表依赖id
    public abstract K splitId();
}
