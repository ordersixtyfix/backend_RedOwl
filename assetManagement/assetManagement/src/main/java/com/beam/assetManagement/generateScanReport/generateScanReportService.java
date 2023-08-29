package com.beam.assetManagement.generateScanReport;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class generateScanReportService {

    private final TemplateEngine templateEngine;

    public byte[] generatePdfFromData(List<scanResultRequest> scanResultRequestList) throws Exception {
        Context context = new Context();
        context.setVariable("scanResultRequestList", scanResultRequestList);

        String htmlContent = templateEngine.process("pdf-template", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }
}
