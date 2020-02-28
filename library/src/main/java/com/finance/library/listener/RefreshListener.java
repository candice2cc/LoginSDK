package com.finance.library.listener;

import com.finance.library.weixin.UserResp;

public interface RefreshListener {
    void onSuccess(UserResp response);

    void onError(UserResp response);

}
