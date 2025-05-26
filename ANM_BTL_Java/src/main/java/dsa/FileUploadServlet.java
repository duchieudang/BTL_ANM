package dsa;

import java.io.*;
import java.math.BigInteger;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

@WebServlet("/upload")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy p, q, g, x từ form
            BigInteger p = new BigInteger(request.getParameter("p"));
            BigInteger q = new BigInteger(request.getParameter("q"));
            BigInteger g = new BigInteger(request.getParameter("g"));
            BigInteger x = new BigInteger(request.getParameter("x")); // khóa bí mật
            BigInteger y = new BigInteger(request.getParameter("y")); // public key
            BigInteger k = new BigInteger(request.getParameter("k"));

            // Đọc nội dung file upload
            Part filePart = request.getPart("file");
            String fileName = filePart.getSubmittedFileName();
            InputStream fileContent = filePart.getInputStream();
            String content = readFileContent(fileName, fileContent);

            // Ký nội dung
            BigInteger[] signature = DSASignature.sign(content, p, q, g, x, k);
            BigInteger r = signature[0];
            BigInteger s = signature[1];

            // Băm chữ ký
            String signatureHash = DSASignature.hashSignature(r, s);

            // Xác thực chữ ký
            boolean isValid = DSASignature.verifyWithSignatureHash(content, p, q, g, y, r, s, signatureHash);

            // Gửi dữ liệu sang JSP
            request.setAttribute("message", content);
            request.setAttribute("r", r.toString());
            request.setAttribute("s", s.toString());
            request.setAttribute("p", p.toString());
            request.setAttribute("q", q.toString());
            request.setAttribute("g", g.toString());
            request.setAttribute("x", x.toString());
            request.setAttribute("y", y.toString());
            request.setAttribute("k", k.toString());
            request.setAttribute("signatureHash", signatureHash);
            request.setAttribute("status", isValid ? "Hợp lệ" : "Không hợp lệ");

            RequestDispatcher dispatcher = request.getRequestDispatcher("result.jsp");
            dispatcher.forward(request, response);


        } catch (Exception e) {
            throw new ServletException("Lỗi xử lý file hoặc tham số", e);
        }
    }

    private String readFileContent(String fileName, InputStream fileContent) throws Exception {
        String content = "";
        if (fileName.endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(fileContent)) {
                StringBuilder sb = new StringBuilder();
                for (XWPFParagraph para : doc.getParagraphs()) {
                    sb.append(para.getText()).append("\n");
                }
                content = sb.toString();
            }
        } else if (fileName.endsWith(".pdf")) {
            try (PDDocument pdf = PDDocument.load(fileContent)) {
                PDFTextStripper stripper = new PDFTextStripper();
                content = stripper.getText(pdf);
            }
        } else if (fileName.endsWith(".xlsx")) {
            try (XSSFWorkbook workbook = new XSSFWorkbook(fileContent)) {
                StringBuilder sb = new StringBuilder();
                XSSFSheet sheet = workbook.getSheetAt(0);
                for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                    XSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        for (int j = 0; j < row.getLastCellNum(); j++) {
                            if (row.getCell(j) != null) {
                                switch (row.getCell(j).getCellType()) {
                                    case STRING:
                                        sb.append(row.getCell(j).getStringCellValue());
                                        break;
                                    case NUMERIC:
                                        sb.append(row.getCell(j).getNumericCellValue());
                                        break;
                                    case BOOLEAN:
                                        sb.append(row.getCell(j).getBooleanCellValue());
                                        break;
                                    case FORMULA:
                                        sb.append(row.getCell(j).getCellFormula());
                                        break;
                                    default:
                                        sb.append("");
                                }
                                sb.append("\t");
                            }
                        }
                        sb.append("\n");
                    }
                }
                content = sb.toString();
            }
        } else {
            throw new ServletException("Chỉ hỗ trợ file .docx, .pdf hoặc .xlsx");
        }
        return content;
    }
}
