package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import com.itheima.service.ReportService;
import com.itheima.service.SetmealService;
import com.itheima.utils.DateUtils;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;

    @Reference
    private SetmealService setmealService;

    @Reference
    private ReportService reportService;


    //运营数据统计导出表格
    @RequestMapping("/exportBusinessReport2")
    public Result exportBusinessReport2(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Map<String, Object> result = reportService.getBusinessReport();

            //取出返回结果数据，准备将报表数据写入到PDF文件中
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            //动态获取模板文件绝对磁盘路径
//            String jrxmlPath =
//                    request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jrxml";
            String jasperPath =
                    request.getSession().getServletContext().getRealPath("template") + File.separator + "health_business3.jasper";
            //编译模板
            //JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);

            //填充数据---使用JavaBean数据源方式填充
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(jasperPath,result,
                            new JRBeanCollectionDataSource(hotSetmeal));

            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/pdf");
            response.setHeader("content-Disposition", "attachment;filename=report.pdf");

            //输出文件
            JasperExportManager.exportReportToPdfStream(jasperPrint,out);

            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }


    //运营数据统计导出表格
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            //获取运营统计数据数据
            Map<String, Object> result = reportService.getBusinessReport();

            //取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            //获得Excel模板文件绝对路径
            String templatePath = request.getSession().getServletContext().getRealPath("template")
                                    + File.separator + "report_template.xlsx";
            System.out.println("templatePath = " + templatePath);
            //创建模板的excel对象
            XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File(templatePath)));

            //获取工作簿
            XSSFSheet sheet = excel.getSheetAt(0);

            //填充日期数据, 3行6列
            sheet.getRow(2).getCell(5).setCellValue(reportDate);
            //填充本日新增会员数, 5行6列
            sheet.getRow(4).getCell(5).setCellValue(todayNewMember);
            //填充总会员数, 5行8列
            sheet.getRow(4).getCell(7).setCellValue(totalMember);
            //填充本周新增会员数, 6行6列
            sheet.getRow(5).getCell(5).setCellValue(thisWeekNewMember);
            //填充本月新增会员数, 6行8列
            sheet.getRow(5).getCell(7).setCellValue(thisMonthNewMember);

            //填充今日预约数, 8行6列
            sheet.getRow(7).getCell(5).setCellValue(todayOrderNumber);
            //填充今日到诊数, 8行8列
            sheet.getRow(7).getCell(7).setCellValue(todayVisitsNumber);
            //填充本周预约数, 9行6列
            sheet.getRow(8).getCell(5).setCellValue(thisWeekOrderNumber);
            //填充本周预约数, 9行8列
            sheet.getRow(8).getCell(7).setCellValue(thisWeekVisitsNumber);
            //填充本月预约数, 10行6列
            sheet.getRow(9).getCell(5).setCellValue(thisMonthOrderNumber);
            //填充本月预约数, 10行8列
            sheet.getRow(9).getCell(7).setCellValue(thisWeekVisitsNumber);

            for (int i = 0; i < hotSetmeal.size(); i++) {
                Map map = hotSetmeal.get(i);
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                //填充套餐名称
                sheet.getRow(13 + i - 1).getCell(4).setCellValue(name);
                //填充预约数量
                sheet.getRow(13 + i - 1).getCell(5).setCellValue(setmeal_count);
                //填充占比
                sheet.getRow(13 + i - 1).getCell(6).setCellValue(proportion.doubleValue());
            }

            ServletOutputStream out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");

            excel.write(out);
            out.flush();
            out.close();
            excel.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL,null);
        }
    }



    //运营数据统计
    @RequestMapping("getBusinessReportData")
    public Result getBusinessReportData(){
        try {
            Map<String,Object> map = reportService.getBusinessReport();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

    //会员近一年注册数量折线图
    @RequestMapping("/getMemberReport")
    public Result getMemberReport() throws Exception {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH,-12);
            List<String> months = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                calendar.add(Calendar.MONTH,1);
                months.add(DateUtils.parseDate2String(calendar.getTime(),"yyyy.MM"));
            }

            Map<String, Object> map = new HashMap<>();
            List<Integer> memberCount = memberService.findMemberCountByMonth(months);
            map.put("months",months);
            map.put("memberCount",memberCount);
            return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_MEMBER_NUMBER_REPORT_FAIL);
        }
    }

    //会员近一年注册数量折线图
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport() throws Exception {
        /*
         {
			"setmealNames":["套餐1","套餐2","套餐3"],
			"setmealCount":[
							{"name":"套餐1","value":10},
							{"name":"套餐2","value":30},
							{"name":"套餐3","value":25}
						   ]
		   }
         */
        try {
            List<Map<String,Object>> setmealCount = setmealService.findSetmealCount();
            Map<String, Object> map = new HashMap<>();
            ArrayList<String> list = new ArrayList<>();
            if (setmealCount != null && !setmealCount.isEmpty()){
                for (Map<String, Object> o : setmealCount) {
                    String name = (String) o.get("name");
                    list.add(name);
                }
            }
            map.put("setmealCount",setmealCount);
            map.put("setmealNames",list);
            return new Result(true, MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }
    }
}
