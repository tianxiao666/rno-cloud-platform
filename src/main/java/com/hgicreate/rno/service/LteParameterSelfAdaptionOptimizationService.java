package com.hgicreate.rno.service;

import com.hgicreate.rno.domain.LteCell;
import com.hgicreate.rno.repository.LteParameterSelfAdaptionOptimizationRepository;
import com.hgicreate.rno.service.dto.LteParameterSelfAdaptionOptimizationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class LteParameterSelfAdaptionOptimizationService {

    /**
     * @param districtName     小区名
     * @param cellIdLowerLimit 小区Id 的开始值
     * @param cellName         查询指定的小区名
     * @return
     */
    private LteParameterSelfAdaptionOptimizationRepository lteParameterSelfAdaptionOptimizationRepository;

    public LteParameterSelfAdaptionOptimizationService(
            LteParameterSelfAdaptionOptimizationRepository lteParameterSelfAdaptionOptimizationRepository) {
        this.lteParameterSelfAdaptionOptimizationRepository = lteParameterSelfAdaptionOptimizationRepository;
    }

    public List<LteParameterSelfAdaptionOptimizationDTO> queryAllTargetCells(
            String districtName, int cellIdLowerLimit, String cellName) {
        List<LteParameterSelfAdaptionOptimizationDTO> list = new ArrayList<>();
        List<LteCell> lteCells = null;
        if (cellName.length() > 0) {
            if (cellName.contains(districtName)) {
                lteCells = lteParameterSelfAdaptionOptimizationRepository
                        .findTop1000ByCellNameLike(cellName + "%");

            } else {
                lteCells = lteParameterSelfAdaptionOptimizationRepository
                        .findTop1000ByCellNameLike(districtName + "%" + cellName + "%");
            }
        } else {
            lteCells = lteParameterSelfAdaptionOptimizationRepository
                    .findTop1000ByCellNameLike(districtName + "%");
        }
        Random random = new Random();
        for (LteCell roadName : lteCells) {
            int randomId = 0;
            int randomPreId = 0;
            boolean checkID = true;
            while (checkID) {
                // 循环检查ID是否重复
                randomPreId = random.nextInt(2000) + cellIdLowerLimit;
                if (list.size() == 0) {
                    checkID = false;
                    randomId = randomPreId;
                } else if (list.size() > 0) {
                    int eqCount = 0;
                    for (LteParameterSelfAdaptionOptimizationDTO aList : list) {
                        if (randomPreId == Integer.parseInt(aList.getCellId())) {
                            eqCount += 1;
                        }
                    }
                    if (eqCount == 0) {
                        randomId = randomPreId;
                        break;
                    }
                }
            }
            float anotherHalf = (float) Math.random() - 0.5f;
            if (anotherHalf < 0) {
                anotherHalf = 0f;
            }
            float radioAccessRate = (float) (98 + Math.random() + anotherHalf);
            float erabSetUpSuccessRate = (float) (98 + Math.random() + anotherHalf);
            float rrcConnectionSetUpSuccessRate = (float) (98 + Math.random() + anotherHalf);

            float radioDropRate = (float) (0.5f + Math.random() + anotherHalf);
            if ((radioAccessRate + radioDropRate) < 100) {
                float gap = 100 - radioAccessRate - radioDropRate;
                radioAccessRate += gap * 0.5f;
                radioDropRate += gap * 0.5f;
            }

            int radioDropCount = (int) ((random.nextInt(15000) + 70000) * radioDropRate / 100);

            float erabDropRate = (float) (0.5f + Math.random() + anotherHalf);
            int switchRequestCount = random.nextInt(2000) + 3000;

            float switchSuccessRate = (float) (98 + Math.random() + anotherHalf);
            int switchSuccessCount = (int) (switchRequestCount * switchSuccessRate / 100);
            switchSuccessRate = (new BigDecimal(100f * switchSuccessCount / switchRequestCount)
                    .setScale(2, BigDecimal.ROUND_HALF_UP)).floatValue();

            String cellPriority =
                    random.nextInt(2) == 1 ? "小区重选优先级已从7级调整为" + (random.nextInt(4) + 3) + "级" : "";

            String cellChangeSwitchDifficulty =
                    random.nextInt(2) == 1 ? "小区切换难易度已从1级调整到" + (random.nextInt(2) + 2) + "级" : "";

            String decreaseHighStressCellRechooseDelay =
                    random.nextInt(2) == 1 ? (random.nextInt(2) == 1 ? "降低高负荷小区的重选迟滞" : "升高低负荷小区重选迟滞") : "";

            String decreaseHighStressCellFrequencyGapFrequencyOffset =
                    random.nextInt(2) == 1 ? "降低高负荷小区频间频率偏移" : "";

            boolean ifAllOboveIsNegative = cellPriority.length() == 0 &&
                    cellChangeSwitchDifficulty.length() == 0 &&
                    decreaseHighStressCellRechooseDelay.length() == 0 &&
                    decreaseHighStressCellFrequencyGapFrequencyOffset.length() == 0;

            if (ifAllOboveIsNegative) {
                //如果上述所有都为0，即为真时，则指定下面语句出现问题
                decreaseHighStressCellFrequencyGapFrequencyOffset = "降低高负荷小区频间频率偏移";
            }
            list.add(new LteParameterSelfAdaptionOptimizationDTO(
                    randomId + "",
                    roadName.getCellName() + "",
                    String.format("%.2f", radioAccessRate) + "",
                    String.format("%.2f", erabSetUpSuccessRate) + "",
                    String.format("%.2f", rrcConnectionSetUpSuccessRate) + "",
                    String.format("%.2f", radioDropRate) + "",
                    radioDropCount + "",
                    String.format("%.2f", erabDropRate) + "",
                    switchRequestCount + "",
                    switchSuccessCount + "",
                    switchSuccessRate + "",
                    "1",
                    cellPriority + "",
                    cellChangeSwitchDifficulty + "",
                    decreaseHighStressCellRechooseDelay + "",
                    decreaseHighStressCellFrequencyGapFrequencyOffset + "")
            );
        }
        return list;
    }
}