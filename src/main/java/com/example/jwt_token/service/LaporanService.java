package com.example.jwt_token.service;

import com.example.jwt_token.model.Peserta;
import com.example.jwt_token.model.Test;
import com.example.jwt_token.model.TrxTestAttempt;
import com.example.jwt_token.repository.TestRepository;
import com.example.jwt_token.repository.TrxTestAttemptRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class LaporanService {
    private final TestRepository testRepository;
    private final TrxTestAttemptRepository attemptRepository;

    public byte[] exportToExcel() throws IOException {
        List<Test> allTests = testRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle bodyStyle = createBodyStyle(workbook);

        for (Test test : allTests) {
            // jika nama sheet sama, tambahkan suffix agar tidak error
            String sheetName = sanitizeSheetName(test.getName());
            if (workbook.getSheet(sheetName) != null) {
                sheetName = sheetName + "_" + test.getId();
            }

            Sheet sheet = workbook.createSheet(sheetName);
            createHeaderRow(sheet, headerStyle);

            List<TrxTestAttempt> attempts = attemptRepository.findAllByTest(test);
            int rowIdx = 1;
            int no = 1;

            for (TrxTestAttempt attempt : attempts) {
                Peserta p = attempt.getPeserta();
                Row row = sheet.createRow(rowIdx++);

                createCell(row, 0, no++, bodyStyle);
                createCell(row, 1, p.getNo_peserta() != null ? p.getNo_peserta() : "-", bodyStyle);
                createCell(row, 2, p.getNama(), bodyStyle);
                createCell(row, 3, p.getEmail(), bodyStyle);
                createCell(row, 4, attempt.getScore() != null ? attempt.getScore() : 0.0, bodyStyle);
                createCell(row, 5, attempt.getStatus() != null ? attempt.getStatus().name() : "-", bodyStyle);
            }

            // auto-size kolom agar rapi
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        // Convert workbook ke byte[] untuk dikembalikan ke response
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row header = sheet.createRow(0);
        String[] columns = {"NO", "NO PESERTA", "NAMA PESERTA", "EMAIL", "NILAI", "KETERANGAN"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    // membuat cell dengan nilai dan style
    private void createCell(Row row, int columnIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }

    // membuat style untuk header dengan border)
    private CellStyle createHeaderStyle(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(false);

        CellStyle style = workbook.createCellStyle();
        style.setFont(headerFont);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // tambahkan border di semua sisi
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    // membuat style untuk isi/body
    private CellStyle createBodyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        // tambahkan border di semua sisi
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    // untuk memastikan nama sheet aman (tidak error di Excel)
    private String sanitizeSheetName(String name) {
        String safeName = name.replaceAll("[\\\\/?*\\[\\]:]", "_");
        return safeName.length() > 31 ? safeName.substring(0, 31) : safeName;
    }

}
