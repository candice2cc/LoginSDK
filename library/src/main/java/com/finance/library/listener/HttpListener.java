package com.finance.library.listener;

import java.io.IOException;

public interface HttpListener {
    void onFailure(IOException e);

    void onResponse(String responseStr);
}
