package com.sensonet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sensonet.emq.EmqClient;
import com.sensonet.mapper.entity.AlarmEntity;
import com.sensonet.exception.BussinessException;
import com.sensonet.service.AlarmService;
import com.sensonet.vo.Pager;
import com.sensonet.vo.QuotaVO;
import com.sensonet.mapper.entity.QuotaEntity;
import com.sensonet.service.QuotaService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quota")
public class QuotaController {

    @Autowired
    private EmqClient emqClient;

    @Autowired
    private QuotaService quotaService;
    @Autowired
    private AlarmService alarmService;


    /**
     * This method is used to create a new quota.
     *
     * @param vo the quota to be created, which is received from the request body
     * @return true if the quota is created successfully, false otherwise
     */
    @PostMapping
    public boolean create(@RequestBody QuotaVO vo) {
        try {
            // Create a new QuotaEntity object
            QuotaEntity quotaEntity = new QuotaEntity();
            // Copy properties from the QuotaVO (DTO) to the QuotaEntity (entity) using BeanUtils
            BeanUtils.copyProperties(vo, quotaEntity);
            // Subscribe to a topic based on the 'subject' field in the QuotaVO
            try {
                emqClient.subscribe("$queue/" + vo.getSubject());
            } catch (MqttException e) {
                e.printStackTrace();
            }
            // Save the QuotaEntity using the QuotaService object
            // The QuotaService object is responsible for handling CRUD operations for quotas
            return quotaService.save(quotaEntity);
        } catch (DuplicateKeyException e) {
            // If a DuplicateKeyException occurs, it means there is already a quota with the same name
            throw new BussinessException("Quota name already exists");
        }
    }

    /**
     * Update quota
     *
     * @param vo the quota to be updated, which is received from the request body
     * @return true if the quota is updated successfully, false otherwise
     */
    @PutMapping
    public Boolean update(@RequestBody QuotaVO vo) {
        try {
            QuotaEntity entity = new QuotaEntity();
            BeanUtils.copyProperties(vo, entity);

            return quotaService.updateById(entity);
        } catch (DuplicateKeyException e) {
            throw new BussinessException("Quota name already exists");
        }

    }

    /**
     * Get all quotas based on the page number, page size, and quota name
     *
     * @param page      the page number
     * @param pageSize  the page size
     * @param quotaName the quota name
     * @return a Pager object containing the list of quotas
     */
    @GetMapping
    public Pager<QuotaEntity> queryPage(@RequestParam(value = "page", required = false, defaultValue = "1") Long page,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Long pageSize,
                                        @RequestParam(value = "quotaName", required = false) String quotaName) {
        return new Pager<>(quotaService.queryPage(page, pageSize, quotaName));
    }

    /**
     * Delete quota
     *
     * @param id the id of the quota to be deleted
     * @return true if the quota is deleted successfully, false otherwise
     */
    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Integer id) {
        QueryWrapper<AlarmEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(AlarmEntity::getQuotaId, id);
        Integer count = alarmService.count(queryWrapper);
        if (count > 0)
            throw new BussinessException("Quota is in use");
        return quotaService.removeById(id);
    }
}
