package com.hgicreate.rno.service;


import com.hgicreate.rno.domain.Area;
import com.hgicreate.rno.domain.DataJob;
import com.hgicreate.rno.repository.DataJobRepository;
import com.hgicreate.rno.service.dto.LteCapicityOptimizationDTO;
import com.hgicreate.rno.service.mapper.LteKpiDataFileMapper;
import com.hgicreate.rno.web.rest.vm.LteKpiDataFileVM;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LteCapicityOptimizationService {

    public List<LteCapicityOptimizationDTO> queryAllCells(String districtName, int cellIdLowerLimit) {
        List<LteCapicityOptimizationDTO> list = new ArrayList<>();
        Random random = new Random();
        int totalCount = random.nextInt(150) + 150;
        double problemCellRate = 0.2;
        String[] baseChinese = {"华穗路", "金穗路", "华夏路", "华强路", "天荣路", "天强路", "广利路", "天河路", "悦新街", "天尚街",
                "亿泉街", "天河直街", "天河北路", "萌横路", "泰兴路", "泰昌街", "林和西路", "林和西横路", "紫荆路",
                "林和中路", "林乐路", "林和东路", "天寿路", "沾溢直街", "瘦狗岭路", "禺东西路", "兴华路", "燕岭路",
                "粤垦路", "侨燕街", "侨源大街", "金坤街", "燕都路", "银定塘前街", "燕富路", "科韵路", "燕塘路",
                "马蹄岗大街", "陶庄路", "东燕街", "东莞庄路", "伟逸街", "长江南路", "长福路", "紫薇西路", "粤汉路",
                "华山路", "黄河路", "黄河西路", "长江路", "洪泽路", "巢湖路", "岳洲路", "大丰一街", "大丰路",
                "华南东侧路", "科韵路", "岑村东街、岑村新南街、行云街", "红花岗西街", "沐陂西街", "沐陂西路",
                "沐陂南路", "沐陂中路、沐陂大街", "沐陂东路", "合景路", "新塘大街", "大观南路"};
        String[] relativePosition = {"东", "南", "西", "北", "中"};
        String baseUperEnglish = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String baseLowerEnglish = "qwertyuiopasdfghjklzxcvbnm";
        String baseNumber = "1234567890";

        for (String roadName : baseChinese) {
            for (String relPosition : relativePosition) {
            int randomID = 0;
            int randomPreId = 0;
            boolean checkID = true;
            while (checkID) {
                //循环检查ID是否重复
                randomPreId = random.nextInt(2000) + cellIdLowerLimit;
                if (list.size() == 0) {
                    checkID = false;
                    randomID = randomPreId;
                } else {
                    for (LteCapicityOptimizationDTO aList : list) {
                        if (randomPreId != Integer.parseInt(aList.getCellId())) {
                            checkID = false;
                            randomID = randomPreId;
                            break;
                        }
                    }
                }
            }
            //            int randomSubNameLength = random.nextInt(2) + 2;

            //            while (randomSubNameLength > 0) {
            //                subName += baseChinese[(new Random().nextInt(baseChinese.length))];
            //                randomSubNameLength -= 1;
            //            }
            String subName = "";
            subName += roadName;
            String subNameCell = "";
            subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
            subNameCell += "-";
            subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
            subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
            subNameCell += baseUperEnglish.charAt(new Random().nextInt(baseUperEnglish.length())) + "";
            subNameCell += "-";
            subNameCell += baseNumber.charAt(new Random().nextInt(baseNumber.length())) + "";
            subNameCell += baseNumber.charAt(new Random().nextInt(baseNumber.length())) + "";
            subNameCell += baseNumber.charAt(new Random().nextInt(baseNumber.length())) + "";

            double randomAdviceRate = Math.random();
            int randomAdvice = 0;
            int randomUpRate = 0;
            int randomDownRate = 0;
            int randomUpFlow = 0;
            int randomDownFlow = 0;
            //小区有问题概率
            double randomUpProblemRate = 0.5;
            int[] randomOperation = new int[8];
            if (randomAdviceRate < problemCellRate) {
                //有问题
                randomAdvice = 1;
                double randomProblemOnUpOrDown = Math.random();
                if (randomProblemOnUpOrDown < randomUpProblemRate) {
                    randomUpRate = 100;
                    randomDownRate = random.nextInt(80);
                    randomUpFlow = random.nextInt(2) + 4;
                    randomDownFlow = random.nextInt(2)+1;
                } else {
                    randomUpRate = random.nextInt(80);
                    randomDownRate = 100;
                    randomUpFlow = random.nextInt(2)+1;
                    randomDownFlow = random.nextInt(2) + 4;
                }
                for (int i = 0; i < randomOperation.length; i++) {
                    randomOperation[i] = random.nextInt(2);
                }

            } else if (randomAdviceRate > problemCellRate) {
                //无问题
                randomAdvice = 0;
                randomUpRate = random.nextInt(70);
                randomDownRate = random.nextInt(70);
                randomUpFlow = random.nextInt(2)+1;
                randomDownFlow = random.nextInt(2)+1;
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
            int randomRRC = random.nextInt(40)+40;
            list.add(new LteCapicityOptimizationDTO(randomID + "", districtName + subName + relPosition + subNameCell, randomAdvice + "", randomRRC+"",
                    randomUpRate + "", randomDownRate + "", randomUpFlow + "", randomDownFlow + "", randomAdvice + "",
                    "ABC", random.nextInt(100000) + "", 113+Float.parseFloat(new BigDecimal(Math.random()).setScale(4,BigDecimal.ROUND_HALF_UP)+"")+"", 22+Float.parseFloat(new BigDecimal(Math.random()).setScale(4,BigDecimal.ROUND_HALF_UP)+"")+"",
                    "40", "90", "5", "4G", "20", "10", "indoor",
                    "10", "5", "400", "3", isVipStation + "", "123",
                    randomRRC+"", "20", randomUpRate+"", randomDownRate+"", "10", "ABC-C1",
                    "2017-11-17", "100", "是", "roof",
                    randomOperation[0] + "", randomOperation[1] + "", randomOperation[2] + "", randomOperation[3] + "",
                    randomOperation[4] + "", randomOperation[5] + "", randomOperation[6] + "", randomOperation[7] + ""));
            }
        }
        return list;
    }
}
