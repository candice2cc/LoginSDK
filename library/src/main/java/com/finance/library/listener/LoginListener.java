package com.finance.library.listener;

import com.finance.library.weixin.UserResp;

public interface LoginListener {
    void onSuccess(UserResp response);

    void onError(UserResp response);

    void onCancel(UserResp response);
}
