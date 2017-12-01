package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.LteCell;
import com.hgicreate.rno.repository.LteCapicityOptimizationRepository;
import com.hgicreate.rno.service.dto.LteCapicityOptimizationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class LteCapicityOptimizationService {

    private LteCapicityOptimizationRepository lteCapicityOptimizationRepository;

    public LteCapicityOptimizationService(LteCapicityOptimizationRepository lteCapicityOptimizationRepository) {
        this.lteCapicityOptimizationRepository = lteCapicityOptimizationRepository;
    }

    public List<LteCapicityOptimizationDTO> queryAllCells(String districtName, int cellIdLowerLimit) {
        List<LteCapicityOptimizationDTO> list = new ArrayList<>();
        Random random = new Random();
        final double problemCellRate = 0.2;
        List<LteCell> lteCells = lteCapicityOptimizationRepository
                .findTop1000ByCellNameLike(districtName + "%");
        for (LteCell oneCell : lteCells) {
            int randomId = 0;
            int randomPreId = 0;
            boolean checkId = true;
            while (checkId) {
                // 循环检查ID是否重复
                randomPreId = random.nextInt(2000) + cellIdLowerLimit;
                if (list.size() == 0) {
                    // 当list刚初始化完，添加第一条记录时
                    checkId = false;
                    randomId = randomPreId;
                } else if (list.size() > 0) {
                    // 当list已存在记录时
                    // eqCount 是找到相同Id的个数
                    int eqCount = 0;
                    for (LteCapicityOptimizationDTO aList : list) {
                        if (randomPreId == Integer.parseInt(aList.getCellId())) {
                            eqCount += 1;
                        }
                    }
                    if (eqCount == 0) {
                        // 当当前Id 是唯一时，则为可用Id ，跳出循环
                        checkId = false;
                        randomId = randomPreId;
                        break;
                    }
                }
            }
            double randomAdviceRate = Math.random();
            int randomAdvice = 0;
            int randomUpRate = 0;
            int randomDownRate = 0;
            int randomUpFlow = 0;
            int randomDownFlow = 0;
            // 小区有问题概率
            double randomUpProblemRate = 0.5;
            int[] randomOperation = new int[8];
            if (randomAdviceRate < problemCellRate) {
                // 当小区有问题时
                randomAdvice = 1;
                double randomProblemOnUpOrDown = Math.random();
                if (randomProblemOnUpOrDown < randomUpProblemRate) {
                    randomUpRate = 100;
                    randomDownRate = random.nextInt(80);
                    randomUpFlow = random.nextInt(2) + 4;
                    randomDownFlow = random.nextInt(2) + 1;
                } else {
                    randomUpRate = random.nextInt(80);
                    randomDownRate = 100;
                    randomUpFlow = random.nextInt(2) + 1;
                    randomDownFlow = random.nextInt(2) + 4;
                }
                for (int i = 0; i < randomOperation.length; i++) {
                    randomOperation[i] = random.nextInt(2);
                }
                int operationCount = 0;
                for (int one : randomOperation) {
                    if (one == 0) {
                        operationCount += 1;
                    }
                }
                if (operationCount == randomOperation.length) {
                    randomAdvice = 0;
                }
            } else if (randomAdviceRate > problemCellRate) {
                // 当小区无问题时
                randomAdvice = 0;
                randomUpRate = random.nextInt(70);
                randomDownRate = random.nextInt(70);
                randomUpFlow = random.nextInt(2) + 1;
                randomDownFlow = random.nextInt(2) + 1;
                for (int i = 0; i < randomOperation.length; i++) {
                    randomOperation[i] = 0;
                }
            }
            String isVipStation = "";
            if (Math.random() > 0.5) {
                isVipStation += "是";
            } else {
                isVipStation += "否";
            }
            int randomRRC = random.nextInt(40) + 40;
            list.add(new LteCapicityOptimizationDTO(randomId + "", oneCell.getCellName(), randomAdvice + "", randomRRC + "",
                    randomUpRate + "", randomDownRate + "", randomUpFlow + "", randomDownFlow + "", randomAdvice + "",
                    "ABC", random.nextInt(100000) + "", String.format("%.4f", random.nextInt(2) + 112 + Math.random()) + "",
                    String.format("%.4f", random.nextInt(2) + 22 + Math.random()) + "",
                    "40", "90", "5", "4G", "20", "10", "indoor",
                    "10", "5", "400", "3", isVipStation + "", "123",
                    randomRRC + "", "20", randomUpRate + "", randomDownRate + "", "10", "ABC-C1",
                    "2017-11-17", "100", "是", "roof",
                    randomOperation[0] + "", randomOperation[1] + "", randomOperation[2] + "", randomOperation[3] + "",
                    randomOperation[4] + "", randomOperation[5] + "", randomOperation[6] + "", randomOperation[7] + ""));

        }
        return list;
    }
}
