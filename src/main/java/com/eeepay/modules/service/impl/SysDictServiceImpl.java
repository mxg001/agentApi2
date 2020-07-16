package com.eeepay.modules.service.impl;

import com.eeepay.modules.dao.SysDictDao;
import com.eeepay.modules.service.SysDictService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.function.Function;

@Service
public class SysDictServiceImpl implements SysDictService {
    @Resource
    SysDictDao sysDictDao;

    @Override
    public Map<String, Object> getSysDictByKey(String paramKey) {
        return sysDictDao.getDictValue(paramKey);
    }

    @Override
    public String getDictSysValue(String key) {
        return sysDictDao.getDictSysValue(key);
    }

    @Override
    public String getSysDictValueByKey(String paramKey) {
        return sysDictDao.getDictSysValue(paramKey);
    }

    @Override
    public <T> T getSysDictValueByKey(String paramKey, T defaultValue, Function<String, T> function) {
        return null;
    }
}
