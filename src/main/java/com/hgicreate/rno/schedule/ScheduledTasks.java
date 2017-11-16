package com.hgicreate.rno.schedule;

import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class ScheduledTasks {

    @Value("${rno.scheduler.enabled:false}")
    private Boolean enabled;

    private final DataJobRepository dataJobRepository;

    public ScheduledTasks(DataJobRepository dataJobRepository) {
        this.dataJobRepository = dataJobRepository;
    }

    // 10秒检查一次
    @Scheduled(fixedDelayString = "${rno.scheduler.fixed-delay:30000}")
    public void runParseTask() {
        if (enabled) {
            log.debug("当前用户：{}", SecurityUtils.getCurrentUserLogin());
            log.debug("检查任务表，当前时间为：{}", LocalDateTime.now());
            DataJob job = dataJobRepository.findTopByStatusOrderById("等待处理");
            log.debug("等待处理的任务：{}", job);
            if (job != null) {

            }
        }
    }
}
