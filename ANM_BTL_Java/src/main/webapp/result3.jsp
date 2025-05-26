<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta charset="UTF-8">
<title>Kết quả DSA</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
</head>
<body class="bg-light">
	<div class="container mt-5">
		<div class="card shadow p-4">
			<h2 class="text-center mb-4">Kết quả ký và xác thực DSA</h2>

			<div class="mb-3">
				<label class="form-label"><strong>Văn bản:</strong></label>
				<pre class="bg-light border p-3">${message}</pre>
			</div>

			<!-- Hiển thị các giá trị p, q, g, x, y, k -->
			<div class="mb-3">
				<label class="form-label"><strong>Giá trị p, q, g,
						x, y, k:</strong></label>
				<div class="bg-light border p-3"
					style="white-space: nowrap; overflow-x: auto;">p = ${p}, q =
					${q}, g = ${g}, x = ${x}, y = ${y}, k = ${k}</div>
			</div>

			<div class="mb-3">
				<label class="form-label"><strong>Giá trị r:</strong></label>
				<pre class="bg-light border p-3">${r}</pre>
			</div>

			<div class="mb-3">
				<label class="form-label"><strong>Giá trị s:</strong></label>
				<pre class="bg-light border p-3">${s}</pre>
			</div>

			<div class="mb-3">
				<label class="form-label"><strong>Hash chữ ký
						(SHA-256 của r || s):</strong></label>
				<pre class="bg-light border p-3">${signatureHash}</pre>
			</div>

			<form action="xtsign" method="post">
				<input type="hidden" name="p" value="${p}"> <input
					type="hidden" name="q" value="${q}"> <input type="hidden"
					name="g" value="${g}"> <input type="hidden" name="x"
					value="${x}"> <input type="hidden" name="y" value="${y}">
				<input type="hidden" name="k" value="${k}"> <input
					type="hidden" name="r" value="${r}"> <input type="hidden"
					name="s" value="${s}">

				<div class="mb-3">
					<label for="signatureHash" class="form-label"><strong>Nhập
							chữ ký</strong></label> <input type="text" name="signatureHash" id="signatureHash"
						class="form-control" value="${signatureHash}" required>
				</div>
				<div class="mb-3">
					<textarea name="message" rows="5" id="message" class="form-control"
						placeholder="Nhập nội dung cần ký ở đây..." required>${message}</textarea>
				</div>

	

				<button type="submit" class="btn btn-success">Xác thực</button>
			</form>

			<!-- Hiển thị kết quả xác thực -->


			<a href="index.jsp" class="btn btn-secondary mt-3">Quay lại</a>
		</div>
	</div>
</body>
</html>
