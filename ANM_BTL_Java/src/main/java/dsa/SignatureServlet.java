package dsa;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/sign")
public class SignatureServlet extends HttpServlet {
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
            BigInteger x = new BigInteger(request.getParameter("x")); // private key
            BigInteger y = new BigInteger(request.getParameter("y")); // public key
            BigInteger k = new BigInteger(request.getParameter("k")); // số ngẫu nhiên

            // Ký số
            BigInteger[] signature = DSASignature.sign(message, p, q, g, x, k);
            BigInteger r = signature[0];
            BigInteger s = signature[1];

            // Băm chữ ký
            String signatureHash = DSASignature.hashSignature(r, s);

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
            request.setAttribute("status", isValid ? "Hợp lệ" : "Không hợp lệ");

            RequestDispatcher dispatcher = request.getRequestDispatcher("result3.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Lỗi xử lý DSA", e);
        }
    }
}

