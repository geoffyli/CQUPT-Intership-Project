package com.sensonet.controller;


import com.sensonet.dto.QuotaAllInfo;
import com.sensonet.exception.BussinessException;
import com.sensonet.vo.AlarmVO;
import com.sensonet.vo.Pager;

import com.sensonet.entity.AlarmEntity;
import com.sensonet.service.AlarmService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/alarm")
public class AlarmController {
    @Autowired
    private AlarmService alarmService;


    @PostMapping
    public boolean create(@RequestBody AlarmVO vo) {
        try {
            AlarmEntity entity = new AlarmEntity();
            BeanUtils.copyProperties(vo, entity);

            return alarmService.save(entity);
        } catch (DuplicateKeyException e) {
            throw new BussinessException("已存在该名称");
        }

    }

    @GetMapping
    public Pager<AlarmEntity> queryPage(@RequestParam(value = "page", required = false, defaultValue = "1") Long page,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Long pageSize,
                                        @RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "quotaId", required = false) Integer quotaId) {
        return new Pager<>(alarmService.queryPage(page, pageSize, name, quotaId));
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        return alarmService.removeById(id);
    }

    @PutMapping
    public Boolean update(@RequestBody AlarmVO vo) {
        try {
            if (vo.getId() == null) return false;
            AlarmEntity entity = new AlarmEntity();
            BeanUtils.copyProperties(vo, entity);

            return alarmService.updateById(entity);
        } catch (DuplicateKeyException e) {
            throw new BussinessException("已存在该名称");
        }
    }


    /**
     * Query alarm log from influxdb
     *
     * @param page      The page number
     * @param pageSize  The page size
     * @param start     The start time
     * @param end       The end time
     * @param alarmName The alarm name
     * @param deviceId  The device id
     * @return The alarm log in pager
     */
    @GetMapping("/log")
    public Pager<QuotaAllInfo> alarmLog(@RequestParam(value = "page", required = false, defaultValue = "1") Long page,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Long pageSize,
                                        @RequestParam(value = "start", required = false) String start,
                                        @RequestParam(value = "end", required = false) String end,
                                        @RequestParam(value = "alarmName", required = false, defaultValue = "") String alarmName,
                                        @RequestParam(value = "deviceId", required = false, defaultValue = "") String deviceId) {

        return alarmService.queryAlarmLog(page, pageSize, start, end, alarmName, deviceId);
    }


}
