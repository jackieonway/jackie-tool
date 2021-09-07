/**
 * Jackie.
 * Copyright (c)) 2019 - 2021 All Right Reserved
 */
package com.github.jackieonway.util.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Jackie
 * @version $id: TestDo.java v 0.1 2021-09-07 17:11 Jackie Exp $$
 */
public class TestDo implements BaseBean, Serializable {
    @Override
    public Map<String, String> configMap() {
        return null;
    }

    @Override
    public List<String> excludeFields() {
        return null;
    }
}
