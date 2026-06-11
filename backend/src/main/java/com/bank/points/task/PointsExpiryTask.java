package com.bank.points.task;

import com.bank.points.service.PointsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PointsExpiryTask {

    private static final Logger logger = LoggerFactory.getLogger(PointsExpiryTask.class);

    @Autowired
    private PointsService pointsService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void checkExpiredPoints() {
        logger.info("开始检查过期积分...");
        try {
            pointsService.checkExpiredPoints();
            logger.info("过期积分检查完成");
        } catch (Exception e) {
            logger.error("检查过期积分时发生错误", e);
        }
    }
}
