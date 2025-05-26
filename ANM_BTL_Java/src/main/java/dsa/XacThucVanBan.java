package dsa;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/xtsign")
public class XacThucVanBan extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	   response.setContentType("text/html;charset=UTF-8");
           request.setCharacterEncoding("UTF-8");
        try {
            // Lấy dữ liệu từ form
            String message = request.getParameter("message");
        	BigInteger p = new BigInteger(request.getParameter("p"));
        	BigInteger q = new BigInteger(request.getParameter("q"));
        	BigInteger g = new BigInteger(request.getParameter("g"));
        	BigInteger x = new BigInteger(request.getParameter("x")); // khóa bí mật
        	BigInteger y = new BigInteger(request.getParameter("y")); // khóa công khai
        	BigInteger k = new BigInteger(request.getParameter("k"));
        	BigInteger r = new BigInteger(request.getParameter("r"));
        	BigInteger s = new BigInteger(request.getParameter("s"));
        	 String signatureHash = request.getParameter("signatureHash");
            // Ký số
          
            // Xác thực chữ ký
            boolean isValid = DSASignature.verifyWithSignatureHash(message, p, q, g, y, r, s, signatureHash);

            // Gửi dữ liệu sang JSP
            request.setAttribute("message", message);
            request.setAttribute("p", p.toString());
            request.setAttribute("q", q.toString());
            request.setAttribute("g", g.toString());
            request.setAttribute("x", x.toString());
            request.setAttribute("y", y.toString());
            request.setAttribute("k", k.toString());
            request.setAttribute("r", r.toString());
            request.setAttribute("s", s.toString());
            request.setAttribute("signatureHash", signatureHash);
            if (isValid) {
                request.setAttribute("xacThucKetQua", "Chữ ký HỢP LỆ.");
            } else {
                request.setAttribute("xacThucKetQua", "Chữ ký KHÔNG HỢP LỆ.");
            }
            request.getRequestDispatcher("result4.jsp").forward(request, response);
  
        } catch (Exception e) {
            throw new ServletException("Lỗi xử lý DSA", e);
        }
    }
}

