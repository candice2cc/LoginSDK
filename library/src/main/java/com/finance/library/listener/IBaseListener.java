package com.finance.library.listener;

import com.finance.library.entity.IBaseRespEntity;

public interface IBaseListener {
    // 成功
    void onSuccess(IBaseRespEntity response);

    // 错误
    void onError(IBaseRespEntity response);

}
