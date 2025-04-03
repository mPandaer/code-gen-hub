package com.pandaer.web.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.web.common.dto.resp.PageResponse;
import com.pandaer.web.order.dto.req.AddOrderRequest;
import com.pandaer.web.order.dto.req.EditOrderRemarkRequest;
import com.pandaer.web.order.dto.req.SearchOrderParams;
import com.pandaer.web.order.dto.resp.OrderVO;
import com.pandaer.web.order.entity.Order;

/**
* @author pandaer
* @description 针对表【order】的数据库操作Service
* @createDate 2025-03-12 10:00:41
*/
public interface OrderService extends IService<Order> {


    OrderVO addOrder(AddOrderRequest addOrderRequest);

    OrderVO getOrderById(String orderId);

    PageResponse<OrderVO> pageOrders(SearchOrderParams searchOrderRequest);

    void editOrderRemark(EditOrderRemarkRequest editOrderRemarkRequest);

    OrderVO queryOrderStatus(String orderId);
}
