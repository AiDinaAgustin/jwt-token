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

        for (Test test : allTests) {
            Sheet sheet = workbook.createSheet(test.getName());
            createHeaderRow(sheet, headerStyle);

            List<TrxTestAttempt> attempts = attemptRepository.findAllByTest(test);
            int rowIdx = 1;
            int no = 1;

            for (TrxTestAttempt attempt : attempts) {
                Peserta p = attempt.getPeserta();
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(no++);
                row.createCell(1).setCellValue(p.getNo_peserta() != null ? p.getNo_peserta() : "-");
                row.createCell(2).setCellValue(p.getNama());
                row.createCell(3).setCellValue(p.getEmail());
                row.createCell(4).setCellValue(attempt.getScore() != null ? attempt.getScore() : 0.0);
            }

            // auto-size
            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }
        }

        // Convert workbook ke byte[]
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }

    private void createHeaderRow(Sheet sheet, CellStyle headerStyle) {
        Row header = sheet.createRow(0);
        String[] columns = {"No", "No Peserta", "Nama Peserta", "Email", "Score"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(headerFont);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
}
