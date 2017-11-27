package com.hgicreate.rno.service;

import com.hgicreate.rno.service.dto.LteCapicityOptimizationDTO;
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

    public List<LteParameterSelfAdaptionOptimizationDTO> queryAllTargetCells(String districtName, String cellName) {
        List<LteParameterSelfAdaptionOptimizationDTO> list = new ArrayList<>();
//        Random random = new Random();
//        double problemCellRate = 0.2;
//        String[] baseChinese = getDistrictRoadName(districtName);
//        String[] relativePosition = {"东", "南", "西", "北", "中"};
//        String baseUperEnglish = "QWERTYUIOPASDFGHJKLZXCVBNM";
//        String baseNumber = "1234567890";
//
//        for (String roadName : baseChinese) {
//            for (String relPosition : relativePosition) {
//                int randomID = 0;
//                int randomPreId = 0;
//                boolean checkID = true;
//                while (checkID) {
//                    //循环检查ID是否重复
//                    randomPreId = random.nextInt(2000) + cellIdLowerLimit;
//                    if (list.size() == 0) {
//                        checkID = false;
//                        randomID = randomPreId;
//                    } else if (list.size() > 0) {
//                        int eqCount = 0;
//                        for (LteCapicityOptimizationDTO aList : list) {
//                            if (randomPreId == Integer.parseInt(aList.getCellId())) {
//                                eqCount += 1;
//                            }
//                        }
//                        if (eqCount == 0) {
//                            checkID = false;
//                            randomID = randomPreId;
//                            break;
//                        }
//                    }
//                }
//                String subName = "";
//                subName += roadName;
//                String subNameCell = "";
//                subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
//                subNameCell += "-";
//                subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
//                subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
//                subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
//                subNameCell += "-";
//                subNameCell += baseNumber.charAt(new Random().nextInt(baseNumber.length())) + "";
//                subNameCell += baseNumber.charAt(new Random().nextInt(baseNumber.length())) + "";
//                subNameCell += baseNumber.charAt(new Random().nextInt(baseNumber.length())) + "";
//
//                double randomAdviceRate = Math.random();
//                int randomAdvice = 0;
//                int randomUpRate = 0;
//                int randomDownRate = 0;
//                int randomUpFlow = 0;
//                int randomDownFlow = 0;
//                //小区有问题概率
//                double randomUpProblemRate = 0.5;
//                int[] randomOperation = new int[8];
//                if (randomAdviceRate < problemCellRate) {
//                    //有问题
//                    randomAdvice = 1;
//                    double randomProblemOnUpOrDown = Math.random();
//                    if (randomProblemOnUpOrDown < randomUpProblemRate) {
//                        randomUpRate = 100;
//                        randomDownRate = random.nextInt(80);
//                        randomUpFlow = random.nextInt(2) + 4;
//                        randomDownFlow = random.nextInt(2) + 1;
//                    } else {
//                        randomUpRate = random.nextInt(80);
//                        randomDownRate = 100;
//                        randomUpFlow = random.nextInt(2) + 1;
//                        randomDownFlow = random.nextInt(2) + 4;
//                    }
//                    for (int i = 0; i < randomOperation.length; i++) {
//                        randomOperation[i] = random.nextInt(2);
//                    }
//                    int operationCount = 0;
//                    for (int one : randomOperation) {
//                        if (one == 0) {
//                            operationCount += 1;
//                        }
//                    }
//                    if (operationCount == randomOperation.length) {
//                        randomAdvice = 0;
//                    }
//
//                } else if (randomAdviceRate > problemCellRate) {
//                    //无问题
//                    randomAdvice = 0;
//                    randomUpRate = random.nextInt(70);
//                    randomDownRate = random.nextInt(70);
//                    randomUpFlow = random.nextInt(2) + 1;
//                    randomDownFlow = random.nextInt(2) + 1;
//                    for (int i = 0; i < randomOperation.length; i++) {
//                        randomOperation[i] = 0;
//                    }
//                }
//                String isVipStation = "";
//                if (Math.random() > 0.5) {
//                    isVipStation += "是";
//                } else {
//                    isVipStation += "否";
//                }
//                int randomRRC = random.nextInt(40) + 40;
//                list.add(new LteParameterSelfAdaptionOptimizationDTO());
//            }
//        }
        list.add(new LteParameterSelfAdaptionOptimizationDTO("1","name","50",
                "80", "85","10","12345",
                "15", "45678","43210"));
        return list;
    }

    private String[] getDistrictRoadName(String districtName) {
        String[] resultRoads = {};
        if ("广州增城区".equals(districtName)) {
            resultRoads = new String[]{"塘头", "横岭", "郑田", "仙塘", "顾屋", "基岗", "麻车", "修屋", "石头村", "街心村",
                    "明星村", "金星村", "陆村", "尾岗", "城丰村", "金竹岗", "莲塘村", "陈桥头", "对田",
                    "马屋", "汤屋", "三联村", "光明村", "增城广场", "荔枝文化公园", "东汇城", "增城公园", "夏街村",
                    "挂绿广场", "光明车站", "泰富广场", "人人乐", "城西市场", "大1978文化创意园", "增城体育馆", "郑中钧中学", "高级中学",
                    "荔城中学", "东湖", "礁石岭"};
        }
        return resultRoads;
    }
}
