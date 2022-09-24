package com.itheima.test;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class POITest {

    //从excel文件中读取数据
    @Test
    public void readExcel1() throws IOException {
        //创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook("d:\\abc.xlsx");
        //获取工作表：可以根据顺序获取，也可以根据名称获取
        XSSFSheet sheet = workbook.getSheetAt(0);
        //获取每一行
        for (Row row : sheet) {
            for (Cell cell : row) {
                Object value = null;
                //获取单元格中的数据类型
                int cellType = cell.getCellType();
                if(cellType == Cell.CELL_TYPE_STRING){
                    value = cell.getStringCellValue();
                }else if(cellType == Cell.CELL_TYPE_NUMERIC) {
                    value = cell.getNumericCellValue();
                }
                System.out.println("value = " + value);
            }
        }
        workbook.close();
    }


    //从excel文件中读取数据
    @Test
    public void readExcel2() throws IOException {
        //创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook("d:\\abc.xlsx");
        //获取工作表：可以根据顺序获取，也可以根据名称获取
        XSSFSheet sheet = workbook.getSheetAt(0);
        //此处获得的行号为索引值，即实际行数-1
        int lastRowNum = sheet.getLastRowNum();
        System.out.println("lastRowNum = " + lastRowNum);
        for (int i = 0; i < lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            //此处获取到的单元格数就是，实际的数量
            short lastCellNum = row.getLastCellNum();
            System.out.println("lastCellNum = " + lastCellNum);
            for (int j = 0; j < lastCellNum; j++) {
                XSSFCell cell = row.getCell(j);
                Object value = null;
                //获取单元格中的数据类型
                int cellType = cell.getCellType();
                if(cellType == Cell.CELL_TYPE_STRING){
                    value = cell.getStringCellValue();
                }else if(cellType == Cell.CELL_TYPE_NUMERIC) {
                    value = cell.getNumericCellValue();
                }
                System.out.println("value = " + value);
            }
        }
        workbook.close();
    }

    //写入数据到excel文件
    @Test
    public void writeExcel() throws IOException {
        //在内存中创建一个excel表格
        XSSFWorkbook workbook = new XSSFWorkbook();
        //创建一个工作簿
        XSSFSheet sheet = workbook.createSheet("体检报告");
        //创建行，0表示第一行
        XSSFRow row0 = sheet.createRow(0);
        //创建单元格，0表示第一个单元格
        row0.createCell(0).setCellValue("编号");
        row0.createCell(1).setCellValue("名称");
        row0.createCell(2).setCellValue("年龄");

        //创建行，1表示第二行
        XSSFRow row1 = sheet.createRow(1);
        //创建单元格，0表示第一个单元格
        row1.createCell(0).setCellValue(1);
        row1.createCell(1).setCellValue("张三");
        row1.createCell(2).setCellValue(23);

        //创建行，2表示第三行
        XSSFRow row2 = sheet.createRow(2);
        //创建单元格，0表示第一个单元格
        row2.createCell(0).setCellValue(2);
        row2.createCell(1).setCellValue("李四");
        row2.createCell(2).setCellValue(24);

        //创建一个输出流，将内存中的excel文件写到磁盘
        FileOutputStream fileOutputStream = new FileOutputStream("d:\\ccd.xlsx");
        workbook.write(fileOutputStream);
        //刷新流
        fileOutputStream.flush();
        //关闭流
        fileOutputStream.close();
        workbook.close();
    }
}
