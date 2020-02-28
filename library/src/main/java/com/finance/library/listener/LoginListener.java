package com.finance.library.listener;

import com.finance.library.entity.UserRespEntity;

public interface LoginListener {
    void onSuccess(UserRespEntity response);

    void onError(UserRespEntity response);

    void onCancel(UserRespEntity response);
}
