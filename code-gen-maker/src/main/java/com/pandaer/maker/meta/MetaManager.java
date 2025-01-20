package com.pandaer.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.pandaer.maker.constants.MakerConstant;

public class MetaManager {

    private static volatile Meta meta;

    public static Meta getMetaObject() {
        if (meta == null) {
            synchronized (MetaManager.class) {
                if (meta == null) {
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static Meta initMeta() {
        String metaJsonStr = ResourceUtil.readUtf8Str(MakerConstant.META_FILE_NAME);
        Meta meta = JSONUtil.toBean(metaJsonStr, Meta.class);
        MetaValidator.validate(meta);
        return meta;
    }


    // 验证代码
    public static void main(String[] args) {
        Meta metaObject = MetaManager.getMetaObject();
        System.out.println(metaObject);
    }
}
