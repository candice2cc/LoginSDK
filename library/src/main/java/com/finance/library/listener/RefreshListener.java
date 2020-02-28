package com.finance.library.listener;

import com.finance.library.entity.UserRespEntity;

public interface RefreshListener {
    void onSuccess(UserRespEntity response);

    void onError(UserRespEntity response);

}
