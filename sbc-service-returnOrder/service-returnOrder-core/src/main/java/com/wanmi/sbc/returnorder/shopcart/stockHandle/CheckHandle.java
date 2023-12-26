package com.wanmi.sbc.returnorder.shopcart.stockHandle;

import com.wanmi.sbc.returnorder.shopcart.request.ShopCartRequest;

public abstract class CheckHandle {
    private CheckHandle next;

    public CheckHandle(CheckHandle next) {
        this.next = next;
    }

    public CheckHandle getNext() {
        return next;
    }

    public void setNext(CheckHandle next) {
        this.next = next;
    }

    abstract boolean process(ShopCartRequest request);
}
