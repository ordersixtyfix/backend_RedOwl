package com.beam.assetManagement.generateScanReport;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class generateScanReportController {

    private final generateScanReportService generateScanReportService;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestBody List<scanResultRequest> scanResultRequestList) {
        try {
            byte[] pdfBytes = generateScanReportService.generatePdfFromData(scanResultRequestList);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "scan-report.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exception appropriately
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
