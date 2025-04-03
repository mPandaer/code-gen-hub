package com.pandaer.web.common.validate;

import lombok.Data;

/**
 * 验证结果
 *
 * @author pandaer
 */

@Data
public class ValidatedResult {

    /**
     * 参数校验的状态
     */
    private boolean success;


    /**
     * 校验失败的原因
     */
    private String message;


    public static ValidatedResult success() {
        ValidatedResult result = new ValidatedResult();
        result.setSuccess(true);
        result.setMessage("ok");
        return result;
    }

    public static ValidatedResult fail(String message) {
        ValidatedResult result = new ValidatedResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

}
