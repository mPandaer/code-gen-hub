package com.pandaer.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pandaer.web.model.dto.PageResponse;
import com.pandaer.web.model.dto.order.AddOrderRequest;
import com.pandaer.web.model.dto.order.EditOrderRemarkRequest;
import com.pandaer.web.model.dto.order.SearchOrderParams;
import com.pandaer.web.model.entity.Order;
import com.pandaer.web.model.vo.OrderVO;

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
